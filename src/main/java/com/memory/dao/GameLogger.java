package com.memory.dao;

/*┌────────────────────────────────────────┐
 │           GameLogger                   │
 │----------------------------------------│
 │  Permet de découpler le système de     │
 │  log (fichier, base de données, etc.)  │
 │  de son implémentation concrète.       │
└────────────────────────────────────────┘*/

/*****************************************************************************
 * Interface définissant le contrat pour l’enregistrement des logs de parties.  
 ****************************************************************************/
public interface GameLogger
{
    /************************************************************************
     * Enregistre un log de partie.
     *
     * @param log l’objet {@link GameLog} contenant les données de la partie
     *************************************************************************/
    void log(GameLog log);
}