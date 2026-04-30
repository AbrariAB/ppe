package com.memory.dao;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/*┌────────────────────────────────────────┐
 │           FileLogger                   │
 │----------------------------------------│
 │  Chaque entrée est formatée et ajoutée │
 │  à la fin du fichier pour conserver    │
 │  un historique persistant.             │
└────────────────────────────────────────┘*/

/*********************************************************************
 * Implémentation de l’interface GameLogger permettant d’enregistrer 
 * les logs de parties dans un fichier texte.             
 *********************************************************************/
public class FileLogger implements GameLogger
{
    private static final String FILE = "game_logs.txt";
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    /****************************************************************************
     * Enregistre un log de partie dans un fichier texte.
     *
     * @param log l’objet {@link GameLog} contenant les informations de la partie
     * @throws RuntimeException en cas d’erreur d’écriture (capturée et affichée)
     *****************************************************************************/
    @Override
    public void log(GameLog log)
    {
        try (FileWriter fw = new FileWriter(FILE, true))
        {fw.write(format(log) + "\n");}
        catch (IOException e) {e.printStackTrace();}
    }

    /*****************************************************************
     * Formate un objet {@link GameLog} en chaîne de caractères.
     *
     * @param log l’objet {@link GameLog} à formater
     * @return une chaîne représentant les données du log
     ********************************************************************/
    private String format(GameLog log)
    {
        String formattedDate = log.getDate().format(FORMATTER);
        return String.format
        (
            "%s - [ Player: %s ] - [ Score: %d ] - [ Level: %d ] - [ TimeLeft: %d ] - [ bonus: %d ] - [ Rank: %s ]",
            formattedDate,
            log.getPlayerName(),
            log.getScore(),
            log.getLevel(),
            log.getTimeLeft(),
            log.getBonus(),
            log.getRank()
        );
    }
}