package com.memory.service;

import java.util.List;

import com.memory.dao.GameLog;
import com.memory.model.PlayerBadge;

/*┌────────────────────────────────────────────────────────────┐
│                    PlayerBadgeService                        │
│--------------------------------------------------------------│
│ - Analyser les performances globales d’un joueur             │
│ - Déterminer un niveau de badge (progression)                │
│ - Comparer les rangs et scores historiques                   │
│ - Centraliser les règles de classification                   │
└──────────────────────────────────────────────────────────────┘*/

/*****************************************************************
 * Service de calcul des badges joueurs basé sur les performances
 * historiques (score, niveau, rang).
 *****************************************************************/
public class PlayerBadgeService

{
    public PlayerBadgeService(){}

    /**************************************************************
     * Calcule le badge du joueur à partir de ses logs de partie.
     *
     * @param logs historique des parties du joueur
     * @return badge correspondant au niveau de performance global
     **************************************************************/
    public PlayerBadge computePlayerBadge(List<GameLog> logs)
    {
        if (logs == null || logs.isEmpty()) return PlayerBadge.BEGINNER;
        int totalScore = logs.stream().mapToInt(GameLog::getScore).sum();
        int maxLevel = logs.stream().mapToInt(GameLog::getLevel).max().orElse(0);
        String bestRank = logs.stream()
                              .map(GameLog::getRank)
                              .filter(r -> r != null && !r.equals("Not Ranked"))
                              .max((r1, r2) -> Integer.compare(rankValue(r1), rankValue(r2)))
                              .orElse("Not Ranked");
        if (bestRank.equals("S") && totalScore > 10000000) return PlayerBadge.LEGEND;
        if (bestRank.equals("S") || totalScore > 1000000) return PlayerBadge.MASTER;
        if (bestRank.equals("A") || totalScore > 500000) return PlayerBadge.ELITE;
        if (maxLevel >= 10 || totalScore > 200000) return PlayerBadge.ADVANCED;
        if (maxLevel >= 6) return PlayerBadge.INTERMEDIATE;
        if (totalScore > 50000) return PlayerBadge.APPRENTICE;
        return PlayerBadge.BEGINNER;
    }

    /******************************************************************
     * Convertit un rang textuel en valeur numérique pour comparaison.
     *
     * @param rank rang (S, A, B, C)
     * @return valeur hiérarchique du rang
     ******************************************************************/
    public static int rankValue(String rank)
    {
        return switch (rank) 
        {
            case "S" -> 4;
            case "A" -> 3;
            case "B" -> 2;
            case "C" -> 1;
            default -> 0;
        };
    }

    /************************************************
     * Compare deux rangs selon leur hiérarchie.
     *
     * @param r1 premier rang
     * @param r2 second rang
     * @return résultat de comparaison
     ************************************************/
    public static int compareRanks(String r1, String r2)
    {return Integer.compare(rankValue(r1), rankValue(r2));}
}