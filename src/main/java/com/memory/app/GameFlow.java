package com.memory.app;

import java.util.function.IntConsumer;

import com.memory.animation.FxCoordinator;
import com.memory.fsm.GameState;
import com.memory.fsm.GameStateMachine;
import com.memory.service.*;
import com.memory.ui.*;

import javafx.application.Platform;

/*┌────────────────────────────────────────┐
│               GameFlow                 │
│----------------------------------------│
│ - Démarrage / redémarrage de partie    │
│ - Chargement des niveaux               │
│ - Initialisation UI et grille          │
│ - Synchronisation FSM / Timer / UI     │
└────────────────────────────────────────┘*/

/***********************************
 * Orchestrateur du flux de jeu.
 * Gère les transitions entre états 
 ************************************/
public class GameFlow 
{
    private final GameFacadeService game;
    private final GameStateMachine fsm;
    private final UIManager ui;
    private final HudManager hud;
    private final TimerService timer;
    private final TurnManager turnManager;
    private final FxCoordinator fxCore;

    /***********************************************
     * Initialise le gestionnaire de flux de jeu.
     *
     * @param game façade métier du jeu
     * @param fsm machine à états
     * @param ui gestionnaire UI
     * @param hud gestionnaire HUD
     * @param timer service de timer
     * @param turnManager gestionnaire de tours
     * @param fxCore coordinateur FX
     ***********************************************/
    public GameFlow(GameFacadeService game,
                    GameStateMachine fsm,
                    UIManager ui,
                    HudManager hud,
                    TimerService timer,
                    TurnManager turnManager,
                    FxCoordinator fxCore) 
    {
        this.game = game;
        this.fsm = fsm;
        this.ui = ui;
        this.hud = hud;
        this.timer = timer;
        this.turnManager = turnManager;
        this.fxCore = fxCore;
    }

    /*************************************************
     * Lance une nouvelle partie.
     *
     * @param hardcore active le mode hardcore
     * @param loadLevel action de chargement du niveau
     *************************************************/
    public void startNewGame(boolean hardcore, Runnable loadLevel) 
    {safeStartNewGame(hardcore, loadLevel);}

    /******************************************************************
     * Démarre une nouvelle partie sans vérifications supplémentaires.
     *
     * @param hardcore mode hardcore
     * @param loadLevel action de chargement du niveau
     ******************************************************************/
    public void safeStartNewGame(boolean hardcore, Runnable loadLevel) 
    {
        timer.stop();
        fsm.set(GameState.READY);
        ui.setInputEnabled(true);
        ui.resetAllCards();
        game.startNewGame(hardcore);
        loadLevel.run();
    }

    /***********************************************
     * Redémarre la partie en réinitialisant l’UI.
     *
     * @param restartAction action de redémarrage
     **********************************************/
    public void restart(Runnable restartAction) 
    {
        ui.resetAllCards();
        restartAction.run();
    }

    /*****************************************************************
     * Charge un niveau et initialise la grille de jeu.
     *
     * @param onCardClicked callback déclenché lors d’un clic carte
     ****************************************************************/
    public void loadLevel(IntConsumer onCardClicked) 
    {
        hud.setUiHidden(false);
        timer.stop();
        ui.setModel(game.getModel());
        turnManager.reset();
        initGrid(onCardClicked);
        Platform.runLater(() -> {
            ui.updateUI();
            hud.updateProgress(game.getProgress());
        });
        hud.setUiHidden(true);
        if (!game.isHardcore()) {timer.start();}
        fsm.set(GameState.PLAYING);
    }

    /****************************************************
     * Initialise la grille avec les handlers de clic.
     *
     * @param onCardClicked callback de sélection carte
     ****************************************************/
    private void initGrid(IntConsumer onCardClicked) 
    {
        ui.initGrid
        (
            ui.getModel().getCards().size(),
            i -> {
                if (fsm.get() != GameState.PLAYING) return;
                onCardClicked.accept(i);
            }
        );
    }

    /***************************************************
     * Indique si une partie est actuellement en cours.
     *
     * @return true si une partie est active
     **************************************************/
    public boolean isGameInProgress() 
    {
        if (ui.getModel() == null || game.getModel() == null) return false;
        return game.hasPlayerStarted() &&
              !game.isEndGame()        &&
              !game.allCardsRevealed() &&
              !game.isDefeat()         &&
              !game.isTimeUp()         &&
              fsm.get() == GameState.PLAYING;
    }

    /****************************************************
     * Initialise le timer
     *
     * @param onGameOver callback de fin de partie
     ****************************************************/
    public void initTimer(Runnable onGameOver)
    {
        timer.setOnTick(() -> {
            int t = game.tickTimer();
            hud.updateTimer(t, game.isHardcore());
            if (t <= 10) {fxCore.danger(ui.getFxLayer());}
            if (game.isTimeUp()) 
            {
                timer.stop();
                Platform.runLater(() -> {
                    fxCore.timeout(hud.getTimer());
                    onGameOver.run();
                });
            }
        });
    }

    /**************************************
     * @return coordinateur des effets FX
     **************************************/
    public FxCoordinator getFxCore() {return fxCore;}

}