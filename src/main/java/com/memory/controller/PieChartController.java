package com.memory.controller;

import com.memory.app.GameSession;
import com.memory.dao.GameLog;
import com.memory.dao.HistoryDAO;
import com.memory.model.PlayerBadge;
import com.memory.service.PlayerBadgeService;
import com.memory.service.StatsService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;

/*┌──────────────────────────────────────────────────────────────┐
 │                    PieChartController                         │
 │--------------------------------------------------------------│
 │ - Filtrer l’historique du joueur                              │
 │ - Agréger les données (score, bonus, temps, niveau)           │
 │ - Générer les graphiques (PieChart + LineChart)               │
 │ - Afficher un résumé avec badge et rang                      │
 └──────────────────────────────────────────────────────────────┘*/

/******************************************************************
 * Contrôleur JavaFX chargé de construire et afficher
 * les statistiques visuelles d’un joueur sous forme de graphiques
 * (camembert et courbe) ainsi qu’un résumé détaillé.
 *******************************************************************/
public class PieChartController 
{
    @FXML private VBox leftBox;
    @FXML private PieChart scorePieChart;
    @FXML private LineChart<String, Number> timeLineChart;
    @FXML private NumberAxis yAxis;

    private final HistoryDAO dao = new HistoryDAO();
    private final StatsService stats = new StatsService();
    private final PlayerBadgeService badgeService = new PlayerBadgeService();

    private String player;
    private GameSession session;

    /*********************************************************************
     * Définit le joueur courant et déclenche le chargement des données.
     *
     * @param playerName nom du joueur
     *********************************************************************/
    public void setPlayer(String playerName) 
    {
        this.player = playerName;
        load();
    }

    /*************************************************
     * Injecte la session de jeu courante.
     *
     * @param session session active (peut être null)
     *************************************************/
    public void setSession(GameSession session) {this.session = session;}

    /**************************************************************
     * Charge les données du joueur, filtre les logs et initialise
     * les composants graphiques.
     *
     * Ne fait rien si aucun joueur ou aucun historique disponible.
     **************************************************************/
    private void load() 
    {
        if (player == null || player.isBlank()) return;
        List<GameLog> logs = dao.getLogsForChart().stream()
                                                  .filter(l -> player.equals(l.getPlayerName()))
                                                  .toList();
        if (logs.isEmpty()) return;
        buildScoreChart(logs);
        buildTimeChart(logs);
        leftBox.getChildren().setAll(buildOverlay(logs));
    }

    /*****************************************************************
     * Construit le graphique circulaire représentant la répartition
     * entre score total et bonus.
     *
     * @param logs historique filtré du joueur
     ****************************************************************/
    private void buildScoreChart(List<GameLog> logs) 
    {
        var data = FXCollections.observableArrayList
        (
            new PieChart.Data("Score", stats.totalScore(logs)),
            new PieChart.Data("Bonus", stats.totalBonus(logs))
        );
        scorePieChart.setData(data);
        scorePieChart.setTitle(player);
        data.forEach(d ->
                Tooltip.install(d.getNode(),
                new Tooltip(d.getName() + " : " + (int) d.getPieValue()))
        );
    }

    /**************************************************************************
     * Construit le graphique temporel des sessions de jeu.
     *
     * @param logs historique filtré du joueur
     **************************************************************************/
    private void buildTimeChart(List<GameLog> logs) 
    {
        yAxis.setTickLabelFormatter(new StringConverter<>() 
        {
            /**************************************************************
             * Convertit une valeur en secondes vers un affichage lisible.
             *
             * @param value durée en secondes
             * @return chaîne formatée (minutes ou heures)
             **************************************************************/
            @Override
            public String toString(Number value) 
            {
                double seconds = value.doubleValue();
                double hours = seconds / 3600.0;
                if (hours < 1) return String.format("%.0f min", seconds / 60);
                return String.format("%.1f h", hours);
            }
            /***********************
             * Non utilisé.
             *
             * @param s valeur texte
             * @return 0
             ************************/
            @Override
            public Number fromString(String s) { return 0; }
        });
        var series = new XYChart.Series<String, Number>();
        series.setName("Sessions");
        Map<String, Integer> sessions = stats.timePerSession(logs);
        sessions.forEach((session, time) ->
                series.getData().add(new XYChart.Data<>(session, time))
        );
        timeLineChart.getData().clear();
        timeLineChart.getData().add(series);
    }

    /********************************************************************
     * Construit le panneau de résumé affichant les statistiques globales
     * du joueur ainsi que son badge.
     *
     * @param logs historique filtré du joueur
     * @return label stylisé contenant les informations synthétiques
     ********************************************************************/
    private Label buildOverlay(List<GameLog> logs) 
    {
        int score = stats.totalScore(logs);
        int bonus = stats.totalBonus(logs);
        int maxLevel = stats.maxlevel(logs);
        String rank = stats.rank(logs, player);
        int sessionTime = (session != null) ? session.getSessionTime() : 0;
        String time = stats.formattedTime(logs, sessionTime);
        PlayerBadge badge = badgeService.computePlayerBadge(logs);
        Label label = new Label
        (                       
                        "Joueur ♕ " + badge.getDisplayName() +
                        "\nScore ⭐ " + score +
                        "\nBonus ✪ +" + bonus +
                        "\nTemps ⏱ " + time +
                        "\nNiveau ⊳ " + maxLevel+
                        "\nRang ✌ " + rank 
        );
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setWrapText(true);
        label.setStyle
        (              
                       "-fx-text-fill: " + badge.getColor() + ";" +
                       "-fx-background-color: rgba(0.4,0.1,0.6,0.7);" +
                       "-fx-padding: 12;" +
                       "-fx-background-radius: 10;" +
                       "-fx-border-color: " + badge.getColor() + ";" +
                       "-fx-border-width: 2;" +
                       "-fx-border-radius: 10;"
        );
        return label;
    }
}