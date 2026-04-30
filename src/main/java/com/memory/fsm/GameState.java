package com.memory.fsm;

/*┌────────────────────────────────────────┐
│           GameState                     │
│-----------------------------------------│
│ Elle est utilisée par la machine à      │
│ états pour piloter le flux d’exécution  │
│ et contrôler les interactions selon le │
│ contexte courant.                       │
└────────────────────────────────────────┘*/

/*********************************************************************************
 *Enumération représentant les différents états possibles du cycle de vie du jeu.
 *********************************************************************************/
public enum GameState
{
    /***************************************************
     * État initial du jeu, avant toute configuration.
     ***************************************************/
    INIT,

    /************************************************
     * Jeu prêt à démarrer, initialisé mais inactif.
     ************************************************/
    READY,

    /*****************************************************
     * Jeu en cours, interactions utilisateur autorisées.
     *****************************************************/
    PLAYING,

    /*******************************************************
     * Jeu temporairement verrouillé (ex: attente logique).
     *******************************************************/
    LOCKED,

    /*****************************************************
     * Une animation est en cours, interactions limitées.
     ****************************************************/
    ANIMATING,

    /********************************************
     * Phase de transition (niveau, écran, etc.).
     ********************************************/
    TRANSITION,

    /**********************************
     * FIN DU JEU (animations finales)
     *********************************/
    ENDING, 

    /**********************
     * Partie terminée.
     *********************/
    GAME_OVER
}