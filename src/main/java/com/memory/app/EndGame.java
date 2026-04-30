package com.memory.app;

import com.memory.dao.GameLog;
import com.memory.dao.GameLogger;
import com.memory.model.Player;
import com.memory.service.GameFacadeService;

/**┌──────────────────────────────────────┐
│           EndGame                       │
│-----------------------------------------│
│  - génération d’un journal de partie    │
│    via le GameFacadeService             │
│  - Persistance de ce journal via        │
│    un GameLogger                        │
└────────────────────────────────────────┘**/

/************************************************************
 *Classe responsable de la finalisation d’une partie de jeu.    
 ************************************************************/
public class EndGame 
{
    private final GameFacadeService game;
    private final GameLogger logger;

    /***************************************************************************
     * Initialise le gestionnaire de fin de partie.
     *
     * @param game   service façade du jeu utilisé pour générer les logs
     * @param logger composant responsable de la persistance des logs
     * @throws NullPointerException si {@code game} ou {@code logger} est null
     ***************************************************************************/
    public EndGame(GameFacadeService game, GameLogger logger) 
    {
        this.game = game;
        this.logger = logger;
    }

    /**************************************************************************
     * Exécute le processus de fin de partie pour un joueur donné.
     *
     * <p>Cette méthode :
     * <ul>
     *   <li>génère un {@link GameLog} à partir de l'état courant du jeu</li>
     *   <li>tente de le persister via le {@code GameLogger}</li>
     * </ul>
     *
     * @param player joueur concerné par la fin de partie
     * @throws IllegalArgumentException si {@code player} est null
     * les exceptions levées lors de la journalisation sont capturées
     * afin d'éviter toute interruption de l'interface utilisateur
     ***************************************************************************/
    public void execute(Player player) 
    {
        if (player == null) 
        {throw new IllegalArgumentException("Player cannot be null");}
        GameLog log = game.buildLog(player);
        try {logger.log(log);} 
        catch (Exception e) {e.printStackTrace();}
    }
}