package com.memory.app;

import com.memory.animation.*;
import com.memory.fsm.GameState;
import com.memory.fsm.GameStateMachine;
import com.memory.model.Player;
import com.memory.service.*;
import com.memory.ui.UIManager;

import javafx.application.Platform;
import javafx.stage.Stage;

/*┌──────────────────────────────────────┐
│            EndGameHandler              │
│----------------------------------------│
│  - arrêt du timer et du gameplay       │
│  - exécution de la logique métier      │
│  - animations de victoire / défaite    │
│  - navigation vers les écrans finaux   │
└────────────────────────────────────────┘*/

/*******************************************************************
 * Gère la fin de partie et ses effets.
 * Transition fluide et cohérente vers l’état final du jeu.
 ********************************************************************/
public class EndGameHandler 
{
    private final GameStateMachine fsm;
    private final GameFacadeService game;
    private final TimerService timer;
    private final UIManager ui;
    private final FxCoordinator fxCore;
    private final AnimationManager fx;
    private final NavigationService nav;
    private final EndGame endGameUseCase;
    private final Stage stage;
    private boolean endGameFxPlayed = false;

    /************************************************
     * Initialise le gestionnaire de fin de partie.
     *
     * @param game façade métier du jeu
     * @param timer service de gestion du temps
     * @param ui gestionnaire UI
     * @param fxCore coordinateur des effets FX
     * @param fx gestionnaire d’animations
     * @param nav service de navigation
     * @param endGameUseCase cas d’usage métier
     * @param stage stage principal
     ************************************************/
    public EndGameHandler(GameFacadeService game,
                          TimerService timer,
                          UIManager ui,
                          FxCoordinator fxCore,
                          AnimationManager fx,
                          NavigationService nav,
                          EndGame endGameUseCase,
                          GameStateMachine fsm,
                          Stage stage) 
    {
        this.game = game;
        this.timer = timer;
        this.ui = ui;
        this.fxCore = fxCore;
        this.fx = fx;
        this.nav = nav;
        this.endGameUseCase = endGameUseCase;
        this.fsm = fsm;
        this.stage = stage;
    }

    /********************************************************
     * Termine immédiatement la partie.
     *
     * @param player joueur courant
     * @param restart callback de redémarrage (nullable)
     *******************************************************/
    private void endGame(Player player, Runnable restart) 
    {
        timer.stop();
        ui.disableButtons();
        ui.setInputEnabled(false);
        ui.getGameContainer().setMouseTransparent(true);
        ui.updateUI();
        endGameUseCase.execute(player);
        boolean victory = game.isVictory();
        if (victory && game.isHardcore()) {nav.showGameWin(stage, game, player, restart);} 
        else if (victory) {nav.showGameWin(stage, game, player, restart);}
        else {nav.showGameOver(stage, game, player, restart);}
        endGameFxPlayed = false;
    }

    /***********************************************************
     * Gère la séquence complète de fin de partie avec effets.
     *
     * @param player joueur courant
     * @param restart callback de redémarrage (nullable)
     **********************************************************/
    public void handleGameEnd(Player player, Runnable restart) 
    {
        if (fsm.get() == GameState.GAME_OVER) return;
        if (!game.isEndGame()) return;
        if (endGameFxPlayed) return;
        timer.stop();
        fsm.set(GameState.GAME_OVER);
        endGameFxPlayed = true;
        boolean victory = game.isVictory();
        boolean defeat = game.isDefeat();
        boolean hardcore = game.isHardcore();
        if (defeat) {playDefeat(player, restart);}
        else if (victory && hardcore) 
        {ui.getHud().updateProgress(1.0);playWin("BRAVO !", player, restart);}
        else if (victory) 
        {ui.getHud().updateProgress(1.0);playWin("VICTORY !", player, restart);}
    }

    /****************************************************
     * Joue la séquence de défaite.
     *
     * @param player joueur courant
     * @param restart callback de redémarrage (nullable)
     *****************************************************/
    private void playDefeat(Player player, Runnable restart)
    {
        timer.stop();
        ui.disableButtons();
        ui.setInputEnabled(false);
        Platform.runLater(() ->
                            fx.playGameOverFade
                            (
                                ui.getGridOverlay(),
                                () -> endGame(player, restart)
                            )
        );
    }

    /***************************************************
     * Joue la séquence de victoire.
     *
     * @param text texte affiché
     * @param player joueur courant
     * @param restart callback de redémarrage (nullable)
     ****************************************************/
    private void playWin(String text, Player player, Runnable restart)
    {
        Platform.runLater(() -> {
            fx.playMatchHighlight(ui.getGameButtons(), () -> {
                Platform.runLater(() -> {
                        fx.playWin(
                            ui.getGameContainer(),
                            text,
                            () -> endGame(player, restart));});
                });
            });
    }

    /******************************************
     * Retourne le coordinateur FX.
     *
     * @return coordinateur des effets visuels
     ******************************************/
    public FxCoordinator getFxCore() {return fxCore;}

    /*************************************
     * Réinitialise l’état interne.
     **************************************/
    public void reset() {endGameFxPlayed = false;}
}