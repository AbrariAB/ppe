package com.memory.ui;

import java.util.*;
import java.util.function.Consumer;

import com.memory.animation.AnimationManager;
import com.memory.animation.NiceFX;
import com.memory.fsm.PlayState;
import com.memory.model.Card;
import com.memory.model.GameModel;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

/*┌────────────────────────────────────────────────────────────┐
│                         UIManager                            │
│--------------------------------------------------------------│
│ - Construction et gestion dynamique de la grille             │
│ - Liaison modèle (GameModel) ↔ UI                            │
│ - Gestion des interactions utilisateur                       │
│ - Optimisation des performances (pooling + cache FX)         │
│ - Adaptation responsive automatique                          │
└──────────────────────────────────────────────────────────────┘*/

/************************************************************
 * Manager UI responsable de l’affichage et de l’interaction
 * du plateau de jeu Memory.
 ************************************************************/
public class UIManager 
{
    private final Grid gameGrid = new Grid();
    private final AnimationManager fx = new AnimationManager(new NiceFX());

    private final BorderPane root;
    private final StackPane gameContainer;
    private final GridPane grid;
    private final StackPane fxLayer;
    private final HudManager hud;
    private StackPane gridOverlay;

    private GameModel model;
    private Button[] buttons;
    private Consumer<Integer> clickHandler;

    private GridSize lastSize;
    private int lastTotalCards = -1;

    private final List<Node> backCache = new ArrayList<>();
    private final List<Button> buttonPool = new ArrayList<>();
    private final List<Button> gameButtons = new ArrayList<>();

    private final PauseTransition resizeDelay =
            new PauseTransition(Duration.millis(100));

    private boolean isUIReady = false;
    private boolean isReloading = false;

    /***************************************************
     * Constructeur du UIManager.
     *
     * @param root conteneur principal de scène
     * @param gameContainer conteneur du plateau de jeu
     * @param grid grille JavaFX des cartes
     * @param hud interface HUD (score, timer, etc.)
     ***************************************************/
    public UIManager(BorderPane root,
                     StackPane gameContainer,
                     GridPane grid,
                     HudManager hud) 
    {
        this.root = root;
        this.gameContainer = gameContainer;
        this.grid = grid;
        this.hud = hud;
        this.fxLayer = new StackPane();
        this.fxLayer.setMouseTransparent(true);
        this.gameContainer.getChildren().add(fxLayer);
        StackPane.setAlignment(grid, Pos.CENTER);
    }

    /**************************************************************
     * Initialise les bindings UI et les comportements utilisateur.
     *
     * @param play state de jeu principal
     **************************************************************/
    public void initialize(PlayState play) 
    {
        configureGridLayout();
        configureFxLayer();
        setupGridOverlay();
        configureSceneAutoStart(play);
        hud.initialize(play);
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) 
            {
                hud.toggleUI();
                e.consume();
            }
        });
    }

    /*******************************************
     * Configure la grille (cache + alignement).
     *******************************************/
    private void configureGridLayout() 
    {
        grid.setCache(true);
        grid.setCacheHint(CacheHint.SPEED);
        grid.setAlignment(Pos.CENTER);
    }

    /*********************************************************
     * Configure la couche FX overlay et le redimensionnement.
     *********************************************************/
    private void configureFxLayer() 
    {
        Platform.runLater(() -> {
            fxLayer.toFront();
            fxLayer.prefWidthProperty().bind(gameContainer.widthProperty());
            fxLayer.prefHeightProperty().bind(gameContainer.heightProperty());
        });
        gameContainer.widthProperty().addListener((o, a, b) -> scheduleResize());
        gameContainer.heightProperty().addListener((o, a, b) -> scheduleResize());
    }

    /**************************************************************
     * Démarre automatiquement la partie à l’affichage de la scène.
     *
     * @param play state de jeu
     **************************************************************/
    private void configureSceneAutoStart(PlayState play) 
    {
        root.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) 
            {
                Platform.runLater(() -> play.startNewGame(false));
                Platform.runLater(() -> {applyResponsiveLayout(); updateUI();});
            }
        });
    }

    /***********************************************
     * Initialise ou reconstruit la grille de jeu.
     *
     * @param totalCards nombre total de cartes
     * @param handler callback de clic carte
     ***********************************************/
    public void initGrid(int totalCards, Consumer<Integer> handler) 
    {
        if (gameContainer.getWidth() <= 0 || gameContainer.getHeight() <= 0) 
        {
            Platform.runLater(() -> initGrid(totalCards, handler));
            return;
        }
        if (totalCards <= 0) return;
        this.clickHandler = handler;
        GridSize size = gameGrid.computeSquareGrid(gameContainer, totalCards);
        boolean rebuild = buttons == null || buttons.length != totalCards;
        if (rebuild) buildGrid(totalCards, size);
        applyCardSizes(size);
        buildBackCache(size.cardSize);
        lastSize = size;
        lastTotalCards = totalCards;
    }

    /*****************************************
     * Construit la grille de boutons.
     *
     * @param totalCards nombre de cartes
     * @param size taille de grille calculée
     ******************************************/
    private void buildGrid(int totalCards, GridSize size) 
    {
        buttons = new Button[totalCards];
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        for (int i = 0; i < totalCards; i++) 
        {
            Button btn = getPooledButton(i);
            int index = i;
            btn.setOnAction(e -> {if (clickHandler != null) clickHandler.accept(index);});
            buttons[i] = btn;
            grid.add(btn, i % size.cols, i / size.cols);
        }
    }

    /***************************************
     * Applique la taille des cartes.
     *
     * @param size configuration de grille
     ***************************************/
    private void applyCardSizes(GridSize size) 
    {
        double s = size.cardSize;
        for (Button btn : buttons) {btn.setPrefSize(s, s);}
    }

    /*****************************************************
     * Récupère un bouton du pool ou en crée un nouveau.
     *
     * @param index index du bouton
     * @return bouton réutilisé ou créé
     ****************************************************/
    private Button getPooledButton(int index) 
    {
        if (index < buttonPool.size()) return buttonPool.get(index);
        Button btn = new Button();
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(Insets.EMPTY);
        btn.getStyleClass().add("card");
        buttonPool.add(btn);
        return btn;
    }

    /*****************************************
     * Génère le cache des dos de cartes.
     *
     * @param size taille visuelle des cartes
     ****************************************/
    private void buildBackCache(double size) 
    {
        if (buttons == null) return;
        backCache.clear();
        for (int i = 0; i < buttons.length; i++) 
        {backCache.add(CardViewFactory.createBack(size));}
    }

    /**************************************
     * Met à jour l’UI à partir du modèle.
     **************************************/
    public void updateUI() 
    {
        if (model == null || buttons == null) return;
        List<Card> cards = model.getCards();
        for (int i = 0; i < cards.size(); i++) 
        {render(i, buttons[i], cards.get(i));}
    }

    /*******************************************
     * Affiche une carte sur son bouton associé.
     -
     * @param i index carte
     * @param btn bouton UI
     * @param c carte modèle
     ********************************************/
    private void render(int i, Button btn, Card card)
    {
        if (card.isMatched()) return;
        double size = btn.getPrefWidth();
        if (card.isMatched() || card.isRevealed()) 
        {
            btn.setGraphic(CardViewFactory.createFront(card, size));
            btn.setDisable(false);
        } 
        else 
        {
            btn.setGraphic(backCache.get(i));
            btn.setDisable(false);
        }
    }

    /*********************************************
     * Réinitialise toutes les cartes face cachée.
     *********************************************/
    public void resetAllCards() 
    {
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++) 
        {
            buttons[i].setGraphic(backCache.get(i));
            buttons[i].setDisable(false);
        }
    }

    /*******************************************
     * Active ou désactive l’entrée utilisateur.
     *
     * @param enabled état des interactions
     *******************************************/
    public void setInputEnabled(boolean enabled) 
    {
        if (model == null) return;
        List<Card> cards = model.getCards();
        for (int i = 0; i < buttons.length; i++) 
        {buttons[i].setDisable(cards.get(i).isMatched() || !enabled);}
    }

    /*****************************************
     * Programme un redimensionnement différé.
     ****************************************/
    private void scheduleResize() 
    {
        resizeDelay.stop();
        resizeDelay.setOnFinished(e -> applyResponsiveLayout());
        resizeDelay.playFromStart();
    }

    /****************************************
     * Applique une mise en page responsive.
     ****************************************/
    private void applyResponsiveLayout() 
    {
        if (model == null) return;
        GridSize size = gameGrid.computeSquareGrid
        (
            gameContainer,
            model.getCards().size()
        );
        applyCardSizes(size);
        buildBackCache(size.cardSize);
        updateUI();
    }


    /******************************************
     * Désactive toutes les cartes du plateau.
     ******************************************/
    public void disableButtons()
    {
        Button[] buttons = getButtons();
        if (buttons != null) 
        {for (Button b : buttons) {b.setDisable(true);}}
    }

    /*****************************************************
     * Initialise la couche d’overlay dédiée à la grille.
     ****************************************************/
    private void setupGridOverlay()
    {
        gridOverlay = new StackPane();
        gridOverlay.setMouseTransparent(true);
        gridOverlay.setOpacity(0);
        gridOverlay.setStyle("-fx-background-color: black;");
        gridOverlay.prefWidthProperty().bind(grid.widthProperty());
        gridOverlay.prefHeightProperty().bind(grid.heightProperty());
        StackPane.setAlignment(gridOverlay, Pos.CENTER);
        gameContainer.getChildren().add(gridOverlay);
    }

    /*************************************************
     * Réinitialise l’état d’interaction utilisateur.
     *************************************************/
    public void resetInteractionState() 
    {
        setInputEnabled(true);
        disableButtons();
        gameContainer.setMouseTransparent(false);
    }

    public void setModel(GameModel m) { this.model = m; }

    public GameModel getModel() { return model; }

    public Button[] getButtons() { return buttons; }

    public StackPane getGameContainer() { return gameContainer; }

    public GridPane getGrid() { return grid; }

    public AnimationManager getFx() { return fx; }

    public Grid getGameGrid() {return gameGrid;}

    public BorderPane getRoot() {return root;}

    public StackPane getFxLayer() {return fxLayer;}

    public HudManager getHud() {return hud;}

    public void setButtons(Button[] buttons) {this.buttons = buttons;}

    public Consumer<Integer> getClickHandler() {return clickHandler;}

    public void setClickHandler(Consumer<Integer> clickHandler) {this.clickHandler = clickHandler;}

    public GridSize getLastSize() {return lastSize;}

    public void setLastSize(GridSize lastSize) {this.lastSize = lastSize;}

    public int getLastTotalCards() {return lastTotalCards;}

    public void setLastTotalCards(int lastTotalCards) {this.lastTotalCards = lastTotalCards;}

    public List<Node> getBackCache() {return backCache;}

    public List<Button> getButtonPool() {return buttonPool;}

    public PauseTransition getResizeDelay() {return resizeDelay;}

    public boolean isUIReady() {return isUIReady;}

    public void setUIReady(boolean isUIReady) {this.isUIReady = isUIReady; }

    public boolean isReloading() {return isReloading;}

    public void setReloading(boolean isReloading) {this.isReloading = isReloading;}

    public List<Button> getGameButtons() 
    {
        gameButtons.clear(); 
        Button[] buttons = getButtons();
        if (buttons != null) 
        {for (Button b : buttons) {gameButtons.add(b);}}
        return gameButtons;
    }

    public StackPane getGridOverlay() {return gridOverlay;}

}