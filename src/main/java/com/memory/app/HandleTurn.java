package com.memory.app;

import com.memory.service.GameFacadeService;
import com.memory.service.GameFlowService;

/*┌──────────────────────────────────────┐
│              HandleTurn                │
│----------------------------------------│
│ - Gestion des sélections joueur        │
│ - Vérification des correspondances     │
│ - Application des règles (score, combo)│
│ - Détection fin de partie / niveau     │
└────────────────────────────────────────┘*/

/*********************************
 * Encapsule la logique d’un tour. 
 ********************************/
public class HandleTurn 
{
    private final GameFacadeService game;

/*************************************************
 * Initialise le gestionnaire d’un tour.
 *
 * @param game façade du jeu
 *************************************************/
    public HandleTurn(GameFacadeService game) {this.game = game;}

    /***********************************************
     * Traite une sélection de carte côté métier.
     *
     * @param index index de la carte sélectionnée
     * @return résultat intermédiaire du tour
     ***********************************************/
    public TurnResult select(int index) 
    {
        boolean accepted = game.selectCard(index);
        if (!accepted) {return TurnResult.continueTurn();}
        return TurnResult.continueTurn();
    }

    /******************************************************
     * Résout un tour après deux sélections.
     * Applique les règles de match, score et progression.
     *
     * @return résultat final du tour
     ******************************************************/
    public TurnResult resolve() 
    {
        boolean match = game.checkMatch();
        if (match) 
        {
            game.onMatch();
            GameFlowService.getInstance().registerMatch();
        } 
        else {game.onError();}
        if (game.isEndGame()) 
        {
            if (game.nextLevel()) {return TurnResult.nextLevel();}
            return TurnResult.gameEnded(game.isVictory(), game.isHardcore());
        }
        return TurnResult.resolved
        (
            match,
            game.getLastGain(),
            game.getCombo()
        );
    }
}