package com.memory.module;

import com.memory.animation.*;
import com.memory.app.GameFlow;
import com.memory.app.EndGameHandler;
import com.memory.ui.UIManager;

/*┌────────────────────────────────────────┐
│             GameLifecycleModule         │
│-----------------------------------------│
│ Garanti un environnement               │
│ de jeu sain à chaque redémarrage.       │
└────────────────────────────────────────┘*/

/**************************************************
 * Module de gestion du cycle de vie du jeu.
 ****************************************************/
public class GameLifecycleModule
{
    private final GameFlow flow;
    private final EndGameHandler endHandler;
    private final UIManager ui;
    private final AnimationManager fx;
    private final FxCoordinator fxCore;

    /*********************************************************
     * Initialise le module avec ses dépendances principales.
     *
     * @param flow moteur de déroulement du jeu
     * @param endHandler gestionnaire de fin de partie
     * @param ui gestionnaire de l'interface utilisateur
     * @param fx gestionnaire des animations
     * @param fxCore coordinateur des effets avancés
     ********************************************************/
    public GameLifecycleModule(GameFlow flow,
                               EndGameHandler endHandler,
                               UIManager ui,
                               AnimationManager fx,
                               FxCoordinator fxCore) 
    {
        this.flow = flow;
        this.endHandler = endHandler;
        this.ui = ui;
        this.fx = fx;
        this.fxCore = fxCore;
    }

    /***************************************************************************
     * Démarre une nouvelle partie après réinitialisation complète.
     *
     * @param hardcore active le mode de jeu hardcore
     * @param loadLevel callback de chargement du niveau (exécuté par le flow)
     ***************************************************************************/
    public void startNewGame(boolean hardcore, Runnable loadLevel)
    {
        reset();
        flow.startNewGame(hardcore,loadLevel);
    }

    /*********************************************************************
     * Réinitialise l'ensemble des états du jeu et de l'interface.
     *********************************************************************/
    private void reset()
    {
        endHandler.reset();
        fx.stopMatchHighlight();
        fxCore.clearGameOverFade(ui.getGameContainer());
        fxCore.clearMatchHighlight(ui.getGameButtons());
        ui.getGridOverlay().setOpacity(0);
        ui.getGridOverlay().setVisible(false);
        ui.resetInteractionState();
    }
}