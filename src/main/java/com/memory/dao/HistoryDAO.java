package com.memory.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*┌────────────────────────────────────────┐
 │           HistoryDAO                   │
 │----------------------------------------│
 │  Fournit des méthodes pour récupérer   │
 │  les logs de jeu depuis une base       │
 │  SQLite et les transformer en objets   │
└────────────────────────────────────────┘*/

/********************************************************
 * DAO pour accéder à l’historique des  parties jouées.
 * {@link GameLog}.   
 *********************************************************/
public class HistoryDAO
{
    private static final String URL = "jdbc:sqlite:game.db";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm:ss");

    /*********************************************************************
     * Récupère tous les logs de parties depuis la base de données,
     * triés par date décroissante.
     *
     * @return une liste de {@link GameLog} représentant toutes les parties
     **********************************************************************/
    public List<GameLog> getAllLogs()
    {
        String sql = 
        """
            SELECT player_name, score, level, time_left, bonus, rank, date
            FROM game_logs
            ORDER BY date DESC
        """;
        return executeQuery(sql);
    }

    /**********************************************************************
     * Récupère tous les logs de parties depuis la base de données,
     * triés par date croissante, utile pour générer des graphiques.
     *
     * @return une liste de {@link GameLog} représentant toutes les parties
     **********************************************************************/
    public List<GameLog> getLogsForChart()
    {
        String sql = 
        """
            SELECT player_name, score, level, time_left, bonus, rank, date
            FROM game_logs
            ORDER BY date ASC
        """;
        return executeQuery(sql);
    }

    /**********************************************************************
     * Exécute une requête SQL et mappe chaque ligne du {@link ResultSet}
     * en {@link GameLog}.
     *
     * @param sql la requête SQL à exécuter
     * @return la liste des {@link GameLog} résultants
     **********************************************************************/
    private List<GameLog> executeQuery(String sql)
    {
        List<GameLog> logs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {while (rs.next()) {logs.add(mapResultSetToGameLog(rs));}} 
        catch (SQLException e) {e.printStackTrace();}
        return logs;
    }

    /****************************************************************************
     * Transforme une ligne du {@link ResultSet} en un objet {@link GameLog}.
     *
     * @param rs le {@link ResultSet} contenant les données d’une ligne
     * @return un {@link GameLog} correspondant à cette ligne
     * @throws SQLException si une erreur de lecture survient
     ****************************************************************************/
    private GameLog mapResultSetToGameLog(ResultSet rs) throws SQLException
    {
        LocalDateTime date = parseDate(rs.getString("date"));
        return new GameLog
        (
            rs.getString("player_name"),
            rs.getInt("score"),
            rs.getInt("level"),
            rs.getInt("time_left"),
            rs.getInt("bonus"),
            rs.getString("rank"),
            date
        );
    }

    /**********************************************************************************
     * Parse une chaîne de caractères représentant une date en {@link LocalDateTime}.
     * Tente d’abord le format ISO, puis un format custom si échec.
     *
     * @param rawDate la date brute sous forme de chaîne
     * @return la {@link LocalDateTime} correspondante
     *********************************************************************************/
    private LocalDateTime parseDate(String rawDate)
    {
        try {return LocalDateTime.parse(rawDate);} 
        catch (Exception e) {return LocalDateTime.parse(rawDate, FORMATTER); }
    }
}