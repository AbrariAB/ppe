package com.memory.dao;

import java.time.LocalDateTime;

/*┌────────────────────────────────────────┐
 │           GameLog                      │
 │----------------------------------------│
 │  Contient les informations principales │
 │  d’une partie telles que le joueur,    │
 │  le score, le niveau atteint, le temps │
 │  restant, le bonus ainsi que la date.  │
└────────────────────────────────────────┘*/

/*****************************************************************
 * Classe représentant un enregistrement (log) d’une partie jouée.   
 *****************************************************************/
public class GameLog
{
    private final String playerName;
    private final int score;
    private final int level;
    private final int timeLeft;
    private final int bonus;
    private final String rank;
    private final LocalDateTime date;

    /*******************************************************************************
     * Constructeur utilisé lors de la lecture des données depuis la base.
     *
     * @param playerName le nom du joueur
     * @param score le score obtenu
     * @param level le niveau atteint
     * @param timeLeft le temps restant
     * @param bonus le bonus obtenu
     * @param date la date de la partie
     *******************************************************************************/
    public GameLog(String playerName, int score, int level, int timeLeft, int bonus, String rank, LocalDateTime date)
    {
        this.playerName = playerName;
        this.score = score;
        this.level = level;
        this.timeLeft = timeLeft;
        this.bonus = bonus;
        this.rank = rank;
        this.date = date;
    }

    /***********************************************************
     * Constructeur utilisé pour créer un nouveau log de partie.
     *
     * @param playerName le nom du joueur
     * @param score le score obtenu
     * @param level le niveau atteint
     * @param timeLeft le temps restant
     * @param bonus le bonus obtenu
     ***********************************************************/
    public GameLog(String playerName, int score, int level, int timeLeft, int bonus, String rank)
    {this(playerName, score, level, timeLeft, bonus, rank, LocalDateTime.now());}

    /***************************
     * Retourne le nom du joueur.
     *
     * @return le nom du joueur
     *****************************/
    public String getPlayerName() { return playerName; }

    /***********************************
     * Retourne le score de la partie.
     *
     * @return le score
     ***********************************/
    public int getScore() { return score; }

    /****************************************
     * Retourne le niveau atteint.
     *
     * @return le niveau
     ***************************************/
    public int getLevel() { return level; }

    /**************************************************
     * Retourne le temps restant à la fin de la partie.
     *
     * @return le temps restant
     *************************************************/
    public int getTimeLeft() { return timeLeft; }

    /*****************************************************
     * Retourne la date de la partie.
     *
     * @return la date sous forme de {@link LocalDateTime}
     *****************************************************/
    public LocalDateTime getDate() { return date; }

    /************************************************
     * Retourne le temps maximal (alias de timeLeft).
     *
     * @return le temps maximal
     ************************************************/
    public int getMaxTime(){return timeLeft;}

    /**********************************************
     * Retourne le score maximal (alias de score).
     *
     * @return le score maximal
     **********************************************/
    public int getMaxScore(){return score;}

    /*******************************
     * Retourne le bonus obtenu.
     *
     * @return le bonus
     ******************************/
    public int getBonus() {return bonus;}

    /****************************
     * Retourne le rang obtenu.
     *
     * @return le rang
     ****************************/
    public String getRank() {return rank;}

    
}