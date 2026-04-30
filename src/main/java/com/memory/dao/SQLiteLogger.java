package com.memory.dao;

import java.sql.*;

/*┌─────────────────────────────────────────────────┐
 │           SQLiteLogger                           │
 │--------------------------------------------------│
 │  Crée automatiquement la table si elle           │
 │  n’existe pas et permet l’insertion              │
 │  de nouveaux logs de parties.                    │
└──────────────────────────────────────────────────┘*/

/**************************************************************************
 * Logger pour enregistrer les {@link GameLog} dans une base SQLite locale.  
 **************************************************************************/
public class SQLiteLogger implements GameLogger
{
    private static final String URL = "jdbc:sqlite:game.db";

    /**************************************************************
     * Initialise le logger et crée la table SQLite si nécessaire.
     **************************************************************/
    public SQLiteLogger() {createTable();}

    /********************************************************************************
     * Crée la table {@code game_logs} si elle n’existe pas.
     * Contient les colonnes : id, player_name, score, level, time_left, bonus, date.
     ********************************************************************************/
    private void createTable()
    {
        String sql = 
        """
            CREATE TABLE IF NOT EXISTS game_logs 
            (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT,
                score INTEGER,
                level INTEGER,
                time_left INTEGER,
                bonus INTEGER,
                rank TEXT,
                date TEXT
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement())
        {stmt.execute(sql);}
        catch (SQLException e) {e.printStackTrace();}
    }

    /****************************************************
     * Enregistre un {@link GameLog} dans la base SQLite.
     *
     * @param log le {@link GameLog} à insérer
     ****************************************************/
    @Override
    public void log(GameLog log)
    {
        String sql = 
        """
            INSERT INTO game_logs(player_name, score, level, time_left, bonus, rank, date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, log.getPlayerName());
            ps.setInt(2, log.getScore());
            ps.setInt(3, log.getLevel());
            ps.setInt(4, log.getTimeLeft());
            ps.setInt(5, log.getBonus());
            ps.setString(6, log.getRank());
            ps.setString(7, log.getDate().toString());
            ps.executeUpdate();
        }
        catch (SQLException e) {e.printStackTrace();}
    }
}