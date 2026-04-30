package com.memory.module;

import com.memory.fsm.*;
import com.memory.service.NavigationService;
import com.memory.service.TimerService;
import com.memory.ui.UIManager;

import javafx.stage.Stage;

/*┌────────────────────────────────────────┐
│          MenuModule                     │
│-----------------------------------------│
│ Centralise la logique de               │
│ pause/reprise et garantir la cohérence │
│ entre UI, timer et état applicatif.    │
└────────────────────────────────────────┘*/

/*******************************************
 * Module de gestion du menu et de la pause.
 *******************************************/
public class MenuModule 
{
    private final TimerService timer;
    private final GameStateMachine fsm;
    private final UIManager ui;
    private final NavigationService navigation;

    /*******************************************************************
     * Initialise le module de gestion du menu.
     *
     * @param timer service de gestion du temps de jeu
     * @param fsm machine à états du jeu
     * @param ui gestionnaire de l’interface utilisateur
     * @param navigation service de navigation (dialogues, transitions)
     ******************************************************************/
    public MenuModule(TimerService timer,
                      GameStateMachine fsm,
                      UIManager ui,
                      NavigationService navigation) 
    {
        this.timer = timer;
        this.fsm = fsm;
        this.ui = ui;
        this.navigation = navigation;
    }

    /*************************************************************
     * Met le jeu en pause.
     *************************************************************/
    public void pause() 
    {
        timer.pause();
        fsm.set(GameState.LOCKED);
    }

    /*******************************************************
     * Reprend la partie après une pause.
     ******************************************************/
    public void resume() 
    {
        timer.resume();
        fsm.set(GameState.PLAYING);
    }

    /***************************************************************************
     * Affiche une boîte de dialogue de confirmation de sortie.
     *
     * @param parentStage fenêtre parente pour l’ancrage de la modale (non null)
     * @param onConfirm action exécutée en cas de confirmation
     * @param onCancel action exécutée en cas d’annulation
     * @throws IllegalArgumentException si {@code parentStage} est null
     ***************************************************************************/
    public void confirmQuit(Stage parentStage,
                        Runnable onConfirm,
                        Runnable onCancel) 
    {
        if (parentStage == null) 
        {throw new IllegalArgumentException("Parent stage is null");}
        navigation.showConfirmQuit
        (
            parentStage,
            onConfirm,
            onCancel
        );
    }

    /******************************************
     * Retourne le service de gestion du temps.
     *
     * @return instance du {@code TimerService}
     *****************************************/
    public TimerService getTimer() {return timer;}

    /**********************************************
     * Retourne la machine à états du jeu.
     *
     * @return instance du {@code GameStateMachine}
     *********************************************/
    public GameStateMachine getFsm() {return fsm;}

    /***************************************
     * Retourne le gestionnaire UI.
     *
     * @return instance du {@code UIManager}
     ***************************************/
    public UIManager getUi() {return ui;}
}