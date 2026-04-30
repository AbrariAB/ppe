package com.memory.dao;

import java.util.List;

/*┌────────────────────────────────────────┐
 │           MultiLogger                  │
 │----------------------------------------│
 │  Utile pour enregistrer les logs       │
 │  à la fois dans un fichier, une base  │
 │  de données, ou d’autres supports.    │
└────────────────────────────────────────┘*/

/***************************************************************************
 * Logger composite qui permet d’écrire simultanément un {@link GameLog}  
 * vers plusieurs {@link GameLogger}. 
 ***************************************************************************/
public class MultiLogger implements GameLogger
{
    private List<GameLogger> loggers;

    /************************************************************
     * Constructeur.
     *
     * @param loggers la liste des {@link GameLogger} à notifier
     ************************************************************/
    public MultiLogger(List<GameLogger> loggers) {this.loggers = loggers;}

    /**************************************************************
     * Enregistre le {@link GameLog} fourni dans tous les loggers
     * de la liste.
     *
     * @param log le {@link GameLog} à enregistrer
     **************************************************************/
    @Override
    public void log(GameLog log)
    {for (GameLogger logger : loggers) {logger.log(log);}}
}