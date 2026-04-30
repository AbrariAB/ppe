package com.memory.app;

/*┌──────────────────────────────────────┐
│              TurnResult                │
│----------------------------------------│
│ - Type de transition (flow)            │
│ - Résultat métier (match, score)       │
│ - Informations de fin (victory, mode)  │
└────────────────────────────────────────┘*/

/************************************************************
 * Objet de transfert décrivant le résultat d’un tour de jeu.
 ************************************************************/
public class TurnResult 
{
    /*******************************************
     * Types de résultats possibles d’un tour.
     *******************************************/
    public enum Type 
    {
        CONTINUE,
        RESOLVED,
        NEXT_LEVEL,
        GAME_ENDED
    }
    
    private final Type type;
    private final boolean match;
    private final int gain;
    private final int combo;
    private final boolean victory;
    private final boolean hardcore;

    /********************************************************
     * Constructeur privé pour usage.
     *
     * @param type type de résultat
     * @param match indique si les cartes correspondent
     * @param gain gain de score
     * @param combo multiplicateur de combo
     * @param victory indique une victoire
     * @param hardcore indique le mode hardcore
     ********************************************************/
    private TurnResult(Type type,
                       boolean match,
                       int gain,
                       int combo,
                       boolean victory,
                       boolean hardcore) 
    {
        this.type = type;
        this.match = match;
        this.gain = gain;
        this.combo = combo;
        this.victory = victory;
        this.hardcore = hardcore;
    }

    /********************************************
     * Résultat indiquant que le tour continue.
     *
     * @return instance CONTINUE
     ********************************************/
    public static TurnResult continueTurn() 
    {return new TurnResult(Type.CONTINUE, false, 0, 0, false, false);}

    /*****************************************************
     * Résultat d’un tour résolu (match ou non).
     *
     * @param match indique si les cartes correspondent
     * @param gain gain de score
     * @param combo valeur du combo
     * @return instance RESOLVED
     *****************************************************/
    public static TurnResult resolved(boolean match, int gain, int combo) 
    {return new TurnResult(Type.RESOLVED, match, gain, combo, false, false);}

    /****************************************************
     * Résultat indiquant un passage au niveau suivant.
     *
     * @return instance NEXT_LEVEL
     ****************************************************/
    public static TurnResult nextLevel() 
    {return new TurnResult(Type.NEXT_LEVEL, false, 0, 0, false, false);}

    /*****************************************
     * Résultat indiquant la fin de partie.
     *
     * @param victory true si victoire
     * @param hardcore true si mode hardcore
     * @return instance GAME_ENDED
     *****************************************/
    public static TurnResult gameEnded(boolean victory, boolean hardcore) 
    {return new TurnResult(Type.GAME_ENDED, false, 0, 0, victory, hardcore);}

    /****************************
     * @return type de résultat
     ***************************/
    public Type getType() { return type; }

    /*********************************
     * Vérifie si le type correspond.
     *
     * @param t type attendu
     * @return true si identique
     *******************************/
    public boolean is(Type t) { return type == t; }

    /************************
     * @return true si match
     ************************/
    public boolean isMatch() { return match; }

    /************************
     * @return gain de score
     ************************/
    public int getGain() { return gain; }

    /***************************
     * @return valeur du combo
     ***************************/
    public int getCombo() { return combo; }

    /****************************
     * @return true si victoire
     ***************************/
    public boolean isVictory() { return victory; }

    /********************************
     * @return true si mode hardcore
     ********************************/
    public boolean isHardcore() { return hardcore; }
}