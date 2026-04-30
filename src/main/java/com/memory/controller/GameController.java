package com.memory.controller;

import com.memory.app.GameSession;
import com.memory.app.WindowContext;
import com.memory.app.WindowManager;
import com.memory.fsm.PlayState;
import com.memory.service.GameFacadeService;
import com.memory.ui.HudManager;
import com.memory.ui.UIManager;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/*┌────────────────────────────────────────┐
 │           GameController               │
 │----------------------------------------│
 │  - Initialisation de l’UI (FXML)       │
 │  - Gestion des interactions joueur     │
 │  - Coordination avec PlayState         │
 │  - Gestion HUD (score, timer, joueur)  │
 │  - Navigation (modales, règles, stats) │
 └────────────────────────────────────────┘*/

/*********************************************************************************
 * Contrôleur JavaFX principal du jeu .
 ***********************************************************************************/
public class GameController 
{
    @FXML private GridPane grid;
    @FXML private BorderPane root;
    @FXML private Label scoreLabel;
    @FXML private Label timerLabel;
    @FXML private Label playerName;
    @FXML private FontAwesomeIconView playerIcon;
    @FXML private StackPane gameContainer;
    @FXML private ComboBox<String> themeComboBox;
    @FXML private ComboBox<String> providerComboBox;
    @FXML private ComboBox<String> difficultyComboBox;

    @FXML private ProgressBar levelProgressBar;
    @FXML private Label levelLabel;

    private final GameFacadeService game = new GameFacadeService();
    private GameSession session;

    private UIManager ui;
    private PlayState play;
    private HudManager hud ;

    @FXML private HBox topUIContainer;
    @FXML private HBox bottomUIContainer;
    @FXML private Label hintLabel;
    @FXML private StackPane fxLayer;
    @FXML private MenuBar menuBar;

    /****************************************************************
     * Initialise le contrôleur après injection FXML.
     * Configure le HUD, le gestionnaire UI et les bindings différés.
     ****************************************************************/
    @FXML
    public void initialize() 
    {
        hud = new HudManager
        (
            scoreLabel, timerLabel, hintLabel,
            themeComboBox, providerComboBox,difficultyComboBox,
            topUIContainer, bottomUIContainer,
            playerName, playerIcon,
            menuBar
        );
        ui = new UIManager(root, gameContainer, grid, hud);
        Platform.runLater(() -> {
            if (levelProgressBar != null && levelLabel != null) 
            {hud.setLevelUI(levelProgressBar, levelLabel);}
        });
    }

    /**********************************************************
     * Injecte la session de jeu et initialise l’état de jeu.
     *
     * @param session session courante (non null)
     ***********************************************************/
    public void setSession(GameSession session) 
    {
        this.session = session;
        session.startSession();
        hud.getPlayerName().setText(session.getPlayer().getName());
        if (session.getPlayer().getProfileIcon() != null) 
        {hud.getPlayerIcon().setIcon(session.getPlayer().getProfileIcon());}
        play = new PlayState
        (
            session,
            game,
            root,
            hud,
            gameContainer,
            grid,
            scoreLabel,
            timerLabel,
            session.getUiContext().getStage()
        );
        play.init();
    }

    /******************************************
     * Redémarre une partie en mode standard.
     *****************************************/
    @FXML
    private void restartGame() {play.restart();}

    /**************************************************
     * Active le mode hardcore et initialise la partie.
     **************************************************/
    @FXML
    private void startHardcoreMode() 
    {
        play.startHardcore();
        grid.setDisable(false);
        timerLabel.setText("--:--");
    }

    /******************************************
     * Ouvre la fenêtre de classement.
     ******************************************/
    @FXML
    private void openRanking() 
    {
        try 
        {
            if (play != null) play.pauseGame();
            WindowContext<ChartController> ctx =
                    WindowManager.getInstance().openModal
                    (
                        "chart",
                        "fxml/chart.fxml",
                        "Classements",
                        session.getUiContext().getStage(),
                        ChartController.class
                    );

            if (ctx != null) 
            {
                ctx.controller.setSession(session);
                ctx.onClose = () -> {if (play != null) play.resumeGame();};
            }
        } 
        catch (Exception e) {e.printStackTrace();}
    }

    /**********************
     * Met le jeu en pause.
     **********************/
    @FXML
    private void pauseGame() 
    {
        if (play != null) 
        {
            play.pauseGame();
            session.pauseSession();
        }
    }

    /******************************
     * Reprend le jeu après pause.
     *****************************/
    @FXML
    private void resumeGame() 
    {
        if (play != null) 
        {
            play.resumeGame();
            session.resumeSession();
        }
    }

    /************************************
     * Quitte l’application.
     * <p>
     * Termine la session avant fermeture.
     *************************************/
    @FXML
    private void exitGame() 
    {
        if (session != null) {session.endSession(); }
        Platform.exit();
    }

    /******************************************
     * Ouvre la fenêtre des règles du jeu.
     * <p>
     * Met le jeu en pause pendant l’affichage.
     ******************************************/
    @FXML
    private void openRules() 
    {
        try 
        {
            if (play != null) play.pauseGame();
            WindowContext<RulesController> ctx =
                    WindowManager.getInstance().openModal
                    (
                        "rules",
                        "fxml/rules.fxml",
                        "Règles du jeu",
                        session.getUiContext().getStage(),
                        RulesController.class
                    );
            if (ctx != null) 
            {ctx.onClose = () -> {if (play != null) play.resumeGame();};}
        } 
        catch (Exception e) {e.printStackTrace();}
    }

    /********************************
     * Retourne la session courante.
     *
     * @return session active
     *******************************/
    public GameSession getSession(){return session;}

    /********************************
     * Retourne le gestionnaire UI.
     *
     * @return UIManager
     ********************************/
    public UIManager getUi(){return ui;}

}