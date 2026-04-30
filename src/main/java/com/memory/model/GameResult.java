package com.memory.model;

/*┌──────────────────────────────────────────────────────────────┐
│                         GameResult                             │
│--------------------------------------------------------------│
│ Énumération représentant l’issue d’une partie.               │
└──────────────────────────────────────────────────────────────┘*/

/***************************************************
 * Représente le résultat d’une partie de jeu.
 ***************************************************/
public enum GameResult 
{
    /**
     * Indique qu’une partie est gagnée.
     */
    WIN,

    /**
     * Indique qu’une partie est perdue.
     */
    LOSE
}