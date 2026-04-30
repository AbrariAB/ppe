package com.memory.module;

import com.memory.model.Player;
import com.memory.service.GameFacadeService;
import com.memory.ui.UIManager;
import com.memory.app.TurnManager;
import com.memory.app.TurnResult;
import com.memory.app.EndGameHandler;

/*┌────────────────────────────────────────┐
│            TurnModule                   │
│-----------------------------------------│
│ Centralise le flux des tours            │
│ et garanti une exécution cohérente.    │
└────────────────────────────────────────┘*/

/****************************************************
 * Module responsable de la gestion des tours de jeu.
 ****************************************************/
public class TurnModule 
{
    private final GameFacadeService game;
    private final TurnManager turnManager;
    private final EndGameHandler endHandler;
    private final UIManager ui;
    private final Player player;
    private final Runnable restartCallback;
    private final Runnable nextLevelCallback;

    /****************************************************************************
     * Initialise le module de gestion des tours.
     *
     * @param game service façade du jeu (état global)
     * @param turnManager gestionnaire de sélection et résolution des tours
     * @param endHandler gestionnaire de fin de partie
     * @param ui gestionnaire de l’interface utilisateur
     * @param player joueur courant
     * @param restartCallback callback de redémarrage de partie
     * @param nextLevelCallback callback de passage au niveau suivant (optionnel)
     *****************************************************************************/
    public TurnModule(GameFacadeService game,
                      TurnManager turnManager,
                      EndGameHandler endHandler,
                      UIManager ui,
                      Player player,
                      Runnable restartCallback,
                      Runnable nextLevelCallback) 
    {
        this.game = game;
        this.turnManager = turnManager;
        this.endHandler = endHandler;
        this.ui = ui;
        this.player = player;
        this.restartCallback = restartCallback;
        this.nextLevelCallback = nextLevelCallback;
    }

    /*************************************************************
     * Traite le clic utilisateur sur une carte.
     *
     * @param index index de la carte sélectionnée dans la grille
     *************************************************************/
    public void onCardClicked(int index) 
    {
        if (game.isEndGame()) return;
        TurnResult result = turnManager.handleSelection(index, ui);
        if (result.is(TurnResult.Type.CONTINUE)) return;
        turnManager.resolve(ui, this::handleResult);
    }

    /************************************************************
     * Traite le résultat d’un tour après résolution.
     *
     * @param result résultat produit par le {@code TurnManager}
     ************************************************************/
    private void handleResult(TurnResult result) 
    {
        if (game.isDefeat() || game.isTimeUp() || game.isEndGame()) 
        {
            endHandler.handleGameEnd(player, restartCallback);
            return;
        }
        switch (result.getType()) 
        {
            case CONTINUE:
            case RESOLVED:
                break;
            case NEXT_LEVEL:
                if (nextLevelCallback != null)
                    nextLevelCallback.run();
                break;
            case GAME_ENDED:
                endHandler.handleGameEnd(player, restartCallback);
                break;
        }
    }
}