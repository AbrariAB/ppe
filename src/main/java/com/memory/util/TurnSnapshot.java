package com.memory.util;

/*┌──────────────────────────────────────────────────────────────┐
│                        TurnSnapshot                            │
│--------------------------------------------------------------│
│ - Encapsuler le résultat d’un coup (match / échec)          │
│ - Transporter les états de fin de jeu                        │
│ - Exposer les gains et combos du tour                       │
└──────────────────────────────────────────────────────────────┘*/

/************************************************************************
 * Snapshot immuable décrivant l’état résultant d’un tour de jeu.
 * Utilisé pour transporter les informations de gameplay entre services.
 ************************************************************************/
public class TurnSnapshot 
{
    public final boolean match;
    public final boolean gameOver;
    public final boolean victory;
    public final boolean hardcore;
    public final int gain;
    public final int combo;

    /**************************************************************
     * Construit un snapshot de tour de jeu.
     *
     * @param match true si une paire est trouvée
     * @param gameOver true si la partie est terminée par défaite
     * @param victory true si la victoire est atteinte
     * @param hardcore true si le mode hardcore est actif
     * @param gain points gagnés durant ce tour
     * @param combo multiplicateur de combo actif
     **************************************************************/
    public TurnSnapshot(boolean match,
                        boolean gameOver,
                        boolean victory,
                        boolean hardcore,
                        int gain,
                        int combo) 
    {
        this.match = match;
        this.gameOver = gameOver;
        this.victory = victory;
        this.hardcore = hardcore;
        this.gain = gain;
        this.combo = combo;
    }

    /****************************************************
     * Indique si le tour correspond à une paire valide.
     *
     * @return true si match trouvé
     ****************************************************/
    public boolean isMatch() {return match;}

    /***********************************
     * Indique si la partie est perdue.
     *
     * @return true si game over atteint
     ***********************************/
    public boolean isGameOver() {return gameOver;}

    /*************************************
     * Indique si la partie est gagnée.
     *
     * @return true si victoire atteinte
     **************************************/
    public boolean isVictory() {return victory;}

    /*****************************************
     * Indique si le mode hardcore est actif.
     *
     * @return true si hardcore activé
     ******************************************/
    public boolean isHardcore() {return hardcore;}

    /*****************************************
     * Retourne les points gagnés sur ce tour.
     *
     * @return gain de score
     ******************************************/
    public int getGain() {return gain;}

    /*********************************************
     * Retourne le multiplicateur de combo actif.
     *
     * @return valeur du combo
     *********************************************/
    public int getCombo() {return combo;}
}