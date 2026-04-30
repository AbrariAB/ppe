package com.memory.fsm;

import java.util.List;
import java.util.Map;

import com.memory.animation.*;
import com.memory.app.*;
import com.memory.dao.*;
import com.memory.model.*;
import com.memory.module.TurnModule;
import com.memory.module.GameLifecycleModule;
import com.memory.module.MenuModule;
import com.memory.module.ThemeModule;
import com.memory.module.LevelModule;
import com.memory.service.*;
import com.memory.ui.*;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*┌────────────────────────────────────────┐
│              PlayState                 │
│----------------------------------------│
│ - Coordonne les modules spécialisés     │
│ - Délègue la logique métier             │
└────────────────────────────────────────┘*/

/****************************************************************
 * Orchestrateur principal du mode jeu.
 ****************************************************************/
public class PlayState 
{
    private final GameSession session;
    private final GameFacadeService game;
    private final GameStateMachine fsm;
    private final NavigationService nav;
    private final HudManager hud;

    private final UIManager ui;
    private final AnimationManager fx;
    private final TurnManager turnManager;
    private final TimerService timer;
    private final Stage primaryStage;
    private final FxCoordinator fxCore;

    private Player player;

    private GameLifecycleModule lifecycleModule;
    private MenuModule menuModule;
    private ThemeModule themeModule;
    private LevelModule levelModule;
    private TurnModule turnModule;

    private final GameFlow flow;
    private final EndGameHandler endHandler;

    private final Map<String, IconTheme> themeMap = Map.of
    (
        "Emojis", IconTheme.EMOJIS,
        "Symbols", IconTheme.SYMBOLS,
        "Animaux", IconTheme.ANIMALS
    );

    private final Map<String, CardType> providerMap = Map.of
    (
        "Ikon", CardType.IKONLI,
        "OpenMoji", CardType.SVG,
        "Default", CardType.FONT_AWESOME
    );

    /****************************************************************
     * Construit le PlayState et initialise les services core du jeu.
     *
     * @param session session de jeu courante
     * @param game façade métier du jeu
     * @param root conteneur racine UI JavaFX
     * @param hud gestionnaire HUD (score, timer, UI)
     * @param gameContainer conteneur principal du plateau
     * @param grid grille de cartes
     * @param score label score UI
     * @param timerLabel label timer UI
     * @param primaryStage stage principal JavaFX
     ****************************************************************/
    public PlayState(GameSession session,
                     GameFacadeService game,
                     BorderPane root,
                     HudManager hud,
                     StackPane gameContainer,
                     GridPane grid,
                     Label score,
                     Label timerLabel,
                     Stage primaryStage) 
    {
        this.session = session;
        this.game = game;
        this.player = session.getPlayer();
        this.primaryStage = primaryStage;
        this.fsm = new GameStateMachine();
        this.nav = new NavigationService();
        this.hud = hud;
        this.ui = new UIManager(root, gameContainer, grid, hud);
        this.fx = new AnimationManager(new NiceFX());
        this.fxCore = new FxCoordinator(fx);
        this.turnManager = new TurnManager(game, hud, fx, fxCore);
        this.timer = new TimerService();
        this.flow = new GameFlow(game, fsm, ui, hud, timer, turnManager, fxCore);
        this.endHandler = new EndGameHandler
        (
            game, timer, ui, fxCore, fx, nav,
            new EndGame(game, new MultiLogger(List.of(new FileLogger(), new SQLiteLogger()))),
            fsm,
            primaryStage
        );
    }

    /**********************************************************************
     * Initialise le moteur de jeu et les modules associés.
     **********************************************************************/
    public void init() 
    {
        validatePlayer();
        ui.initialize(this);
        fsm.set(GameState.INIT);
        initModules();
        flow.initTimer(() -> endHandler.handleGameEnd(player, this::restart));
        timer.setOnTimeUp(() -> Platform.runLater(() -> {
            ui.disableButtons();
            fsm.set(GameState.GAME_OVER);
            endHandler.handleGameEnd(player, this::restart);
        }));
        startNewGame(false);
    }

    /***********************************************
     * Initialise les modules fonctionnels du jeu.
     ***********************************************/
    private void initModules() 
    {
        lifecycleModule = new GameLifecycleModule(flow, endHandler, ui, fx, fxCore);
        menuModule = new MenuModule(timer, fsm, ui, nav);
        themeModule = new ThemeModule
        (
            new ThemeChanger
            (
                new ChangeTheme(game),
                themeMap,
                providerMap
            )
        );
        levelModule = new LevelModule(game, hud);
        turnModule = new TurnModule
        (
            game,
            turnManager,
            endHandler,
            ui,
            player,
            this::restart,
            this::handleNextLevel
        );
    }

    /*************************************************
     * Démarre une nouvelle partie.
     *
     * @param hardcore mode difficulté hardcore activé
     **************************************************/
    public void startNewGame(boolean hardcore) 
    {
        if (flow.isGameInProgress()) 
        {
            menuModule.pause();
            menuModule.confirmQuit
            (
                primaryStage,
                () -> lifecycleModule.startNewGame(hardcore, this::loadLevel),
                () -> menuModule.resume()
            );
        } 
        else {lifecycleModule.startNewGame(hardcore, this::loadLevel);}
    }

    /***************************************
     * Redémarre la partie en mode normal.
     ***************************************/
    public void restart() {startNewGame(false);}

    /************************************
     * Lance une partie en mode hardcore.
     ************************************/
    public void startHardcore() {startNewGame(true);}

    /**************************************************
     * Charge un niveau et initialise la grille de jeu.
     **************************************************/
    private void loadLevel() 
    {
        levelModule.configureThemeAvailability();
        flow.loadLevel(turnModule::onCardClicked);
        fx.fadeInGrid(ui.getGrid());
    }

    /********************************************************
     * Gère la transition vers le niveau suivant.
     ********************************************************/
    private void handleNextLevel() 
    {
        if (game.isEndGame()) return;
        if (game.getTier() != GameFlowService.Tier.EASY
            && game.consumeTierChanged()) 
        {
            Platform.runLater(() ->
                fxCore.onLevelChange
                (
                    ui,
                    game.getTier() + " Tier Unlocked",
                    true,
                    this::loadLevel
                )
            );
        } 
        else {Platform.runLater(this::loadLevel);}
    }

    /**********************
     * Met le jeu en pause.
     **********************/
    public void pauseGame() {menuModule.pause();}

    /*****************
     * Reprend le jeu.
     *****************/
    public void resumeGame() {menuModule.resume();}

    /********************************************
     * Change le thème visuel du jeu.
     *
     * @param themeName nom du thème sélectionné
     ********************************************/
    public void changeTheme(String themeName) {themeModule.changeTheme(themeName, this::restart);}

    /***********************************
     * Change le provider d’icônes.
     *
     * @param themeName nom du provider
     ************************************/
    public void changeProviderTheme(String themeName) {themeModule.changeProvider(themeName, this::restart);}

    /*******************************
     * Change la difficulté du jeu.
     *
     * @param mode HARD ou NORMAL
     *******************************/
    public void changeDifficulty(String mode) 
    {
        if ("HARD".equals(mode)) game.setDifficulty(GameFlowService.Difficulty.HARD);
        else game.setDifficulty(GameFlowService.Difficulty.NORMAL);
        restart();
    }

    /*********************************************************
     * Vérifie la validité du joueur courant.
     *
     * @throws IllegalStateException si joueur non initialisé
     *********************************************************/
    private void validatePlayer() 
    {if (player == null) throw new IllegalStateException("Player must be set before init()");}

    /*********************************
     * @return stage JavaFX principal
     *********************************/
    public Stage getPrimaryStage() { return primaryStage; }

    /*********************************
     * @return session de jeu courante
     *********************************/
    public GameSession getSession() { return session; }
}