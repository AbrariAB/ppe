package com.memory.service;

import com.memory.dao.GameLog;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*┌────────────────────────────────────────────────────────────┐
│                        StatsService                          │
│--------------------------------------------------------------│
│ - Calcul des scores et bonus globaux                         │
│ - Agrégation du temps de jeu (par session / total)           │
│ - Détermination des niveaux max et rangs                     │
│ - Structuration des données pour affichage UI                │
└──────────────────────────────────────────────────────────────┘*/

/****************************************************************
 * Service métier responsable du calcul des statistiques joueur
 * (score, temps, bonus, rangs et progression).
 ****************************************************************/
public class StatsService 
{
    private static final int DEFAULT_MAX_TIME = 400;

    /**************************************************
     * Calcule le temps effectivement joué pour un log.
     *
     * @param log entrée de partie
     * @return durée jouée en secondes
     **************************************************/
    private int computePlayedTime(GameLog log) 
    {return Math.max(0, DEFAULT_MAX_TIME - log.getTimeLeft());}

    /*************************************************************
     * Calcule le temps total de jeu à partir de l’historique.
     *
     * @param logs historique des parties
     * @return durée totale en secondes
     ************************************************************/
    public int totalPlayTime(List<GameLog> logs) 
    {
        return logs.stream()
                   .mapToInt(this::computePlayedTime)
                   .sum();
    }

    /*********************************************************
     * Calcule le temps total incluant une session en cours.
     *
     * @param logs historique des parties
     * @param sessionTime temps de la session active
     * @return durée totale en secondes
     *********************************************************/
    public int totalPlayTime(List<GameLog> logs, int sessionTime) 
    {return totalPlayTime(logs) + sessionTime;}

    /******************************************************
     * Retourne une représentation lisible du temps total.
     *
     * @param logs historique des parties
     * @param sessionTime temps de session courant
     * @return format texte (ex: "2h 15min")
     ******************************************************/
    public String formattedTime(List<GameLog> logs, int sessionTime) 
    {
        int seconds = totalPlayTime(logs, sessionTime);
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        if (h > 0) return h + "h " + m + "min";
        return m + " min";
    }

    /*************************************
     * Calcule le score total cumulé.
     *
     * @param logs historique des parties
     * @return score global
     *************************************/
    public int totalScore(List<GameLog> logs) 
    {return logs.stream().mapToInt(GameLog::getScore).sum();}

    /**************************************
     * Calcule le bonus total cumulé.
     *
     * @param logs historique des parties
     * @return bonus total
     **************************************/
    public int totalBonus(List<GameLog> logs) 
    {return logs.stream().mapToInt(GameLog::getBonus).sum();}

    /*****************************************************
     * Agrège le temps de jeu par session (format date).
     *
     * @param logs historique des parties
     * @return map date → temps total joué
     *****************************************************/
    public Map<String, Integer> timePerSession(List<GameLog> logs) 
    {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (logs == null || logs.isEmpty()) return result;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        logs.forEach(log -> {
            String dateKey = log.getDate().format(formatter);
            result.putIfAbsent(dateKey, 0);
        });
        logs.forEach(log -> {
            String dateKey = log.getDate().format(formatter);
            result.put(dateKey, result.get(dateKey) + computePlayedTime(log));
        });
        return result;
    }

    /*********************************************************
     * Détermine le niveau maximum atteint dans l’historique.
     *
     * @param logs historique des parties
     * @return niveau maximal
     *********************************************************/
    public int maxlevel(List<GameLog> logs)
    {
        int maxLevel = 0;
        for (GameLog log : logs) 
        {if (log.getLevel() > maxLevel) maxLevel = log.getLevel();}
        return maxLevel;
    }

    /*****************************************************
     * Détermine le meilleur rang atteint par un joueur.
     *
     * @param logs historique global
     * @param player nom du joueur
     * @return rang le plus élevé ou "unknown"
     *****************************************************/
    public String rank(List<GameLog> logs, String player)
    {
        if (logs == null || logs.isEmpty()) return "unknown";
        List<GameLog> playerLogs = logs.stream()
                                       .filter(log -> player.equals(log.getPlayerName()))
                                       .toList();
        if (playerLogs.isEmpty()) return "unknown";
        String rank = getRank(playerLogs);
        return rank;
    }

    /*******************************************************
     * Extrait le meilleur rang d’une liste de logs joueur.
     *
     * @param playerLogs logs filtrés du joueur
     * @return meilleur rang trouvé
     *******************************************************/
    private String getRank(List<GameLog> playerLogs)
    {
        return playerLogs.stream()
                      .map(GameLog::getRank)
                      .filter(r -> r != null && !r.isBlank())
                      .filter(r -> switch (r) {case "S", "A", "B", "C" -> true; default -> false;})
                      .max((r1, r2) -> Integer.compare(PlayerBadgeService.rankValue(r1),
                                                       PlayerBadgeService.rankValue(r2)))
                      .orElse("Not Ranked");
    }
}