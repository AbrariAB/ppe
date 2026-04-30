package com.memory.fsm;

/**┌────────────────────────────────────────┐
│             GameStateMachine             │
│-----------------------------------------│
│ transitions entre les différents états  │
│ (initialisation, jeu en cours, etc.).   │
└────────────────────────────────────────┘**/

/*************************************************************
 * Machine à états simple représentant l’état courant du jeu.    
 ************************************************************/
public class GameStateMachine
{
    private GameState state = GameState.INIT;

    /**********************************
     * Retourne l’état courant du jeu.
     *
     * @return état courant
     **********************************/
    public GameState get() {return state;}

    /***********************************************************************************
     * Vérifie si l’état courant correspond à un état donné.
     *
     * @param s état à comparer
     * @return {@code true} si l’état courant est égal à {@code s}, sinon {@code false}
     ***********************************************************************************/
    public boolean is(GameState s) {return state == s;}

    /***********************************
     * Définit un nouvel état courant.
     *
     * @param next état suivant
     ***********************************/
    public void set(GameState next) {state = next;}

    /***************************************************************************
     * Indique si le jeu est dans un état permettant de jouer.
     *
     * @return {@code true} si l’état est {@code PLAYING}, sinon {@code false}
     **************************************************************************/
    public boolean canPlay() {return state == GameState.PLAYING;}
}