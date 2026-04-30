package com.memory.controller;

import com.memory.dao.GameLog;
import com.memory.dao.HistoryDAO;
import com.memory.util.DialogService;
import com.memory.app.GameSession;
import com.memory.app.WindowContext;
import com.memory.app.WindowManager;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*┌───────────────────────────────────────────────────────────┐
│                      ChartController                        │
│-------------------------------------------------------------│
│ - Charger et filtrer les données (HistoryDAO)               │
│ - Agréger les scores par joueur                             │
│ - Gérer les filtres (joueur / date)                         │
│ - Configurer dynamiquement les axes et le rendu             │
│ - Déclencher l’ouverture de vues détaillées (PieChart)      │
└─────────────────────────────────────────────────────────────┘*/

/************************************************************************
 * Contrôleur JavaFX responsable de l’affichage des statistiques joueurs
 * via un graphique en barres avec filtres dynamiques.
 ************************************************************************/
public class ChartController 
{
    @FXML private BarChart<String, Number> barChart;
    @FXML private NumberAxis yAxis;
    @FXML private ComboBox<String> playerComboBox;
    @FXML private ComboBox<LocalDate> dateComboBox;

    private final HistoryDAO dao = new HistoryDAO();
    private List<GameLog> allLogs;

    private static final String ALL_PLAYERS = "Tous les joueurs";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final WindowManager windowManager = WindowManager.getInstance();
    private Stage primaryStage;
    private GameSession session;

    /***************************************************************************
     * Initialise le contrôleur, charge les données et configure les filtres UI.
     ***************************************************************************/
    @FXML
    public void initialize() 
    {
        allLogs = dao.getLogsForChart();
        loadDateComboBox();
        setupDateFormatting();
        updatePlayerComboBox(null);
        refreshChart();
        playerComboBox.setOnAction(e -> refreshChart());
        if (dateComboBox != null) 
        {
            dateComboBox.setOnAction(e -> {
                updatePlayerComboBox(dateComboBox.getValue());
                refreshChart();
            });
        }
    }

    /**************************************************************************
     * Rafraîchit complètement le graphique en appliquant les filtres courants.
     **************************************************************************/
    private void refreshChart() 
    {
        Map<String, Integer> scores = computeScores
        (
            playerComboBox.getValue(),
            dateComboBox != null ? dateComboBox.getValue() : null
        );
        configureYAxis(scores);
        loadBarChart(scores);
    }

    /***********************************************************
     * Calcule les scores cumulés par joueur selon les filtres.
     *
     * @param selectedPlayer joueur sélectionné (ou tous)
     * @param selectedDate date sélectionnée (optionnelle)
     * @return map joueur → score total
     ************************************************************/
    private Map<String, Integer> computeScores(String selectedPlayer, LocalDate selectedDate) 
    {
        Map<String, Integer> scoresMap = new HashMap<>();
        for (GameLog log : allLogs) 
        {
            String player = log.getPlayerName();
            LocalDate date = log.getDate().toLocalDate();
            if 
            (
                selectedPlayer != null              && 
                !ALL_PLAYERS.equals(selectedPlayer) && 
                !player.equals(selectedPlayer)
            )continue;
            if (selectedDate != null && !date.equals(selectedDate))continue;
            scoresMap.merge(player, log.getScore(), (a, b) -> a + b);
        }
        return scoresMap;
    }

    /**********************************************************
     * Configure dynamiquement l’axe Y en fonction des valeurs.
     *
     * @param scoresMap données à afficher
     **********************************************************/
    private void configureYAxis(Map<String, Integer> scoresMap) 
    {
        int maxScore = scoresMap.values().stream()
                                .mapToInt(Integer::intValue)
                                .max()
                                .orElse(1000);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxScore);
        yAxis.setTickUnit(1000);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickLabelFormatter(new StringConverter<>() 
        {
            @Override
            public String toString(Number value) 
            {return value.intValue() % 1000 == 0 ? String.valueOf(value.intValue()) : "";}
            @Override
            public Number fromString(String string) {return Integer.parseInt(string);}
        });
    }

    /***********************************************************************
     * Injecte les données dans le BarChart et applique styles + animation.
     *
     * @param scoresMap données triées à afficher
     **********************************************************************/
    private void loadBarChart(Map<String, Integer> scoresMap) 
    {
        barChart.getData().clear();
        scoresMap.entrySet().stream()
                 .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                 .forEach(entry -> {
                    String player = entry.getKey();
                    Integer score = entry.getValue();
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(player);
                    XYChart.Data<String, Number> data = new XYChart.Data<>(player, score);
                    series.getData().add(data);
                    barChart.getData().add(series);
                    data.nodeProperty().addListener((obs, oldNode, node) -> {
                        if (node != null) 
                        {
                            node.setStyle("-fx-bar-fill: " + generateColorFromPlayer(player) + ";");
                            ScaleTransition st = new ScaleTransition(Duration.millis(600), node);
                            st.setFromY(0);
                            st.setToY(1);
                            st.play();
                        }
                    });
                });
    }

    /****************************************************************
     * Ouvre la vue détaillée (camembert) pour le joueur sélectionné.
     * Affiche un message si aucun joueur spécifique n’est choisi.
     ****************************************************************/
    @FXML
    private void openPieChart() 
    {
        String selectedPlayer = playerComboBox.getValue();
        if (selectedPlayer == null || ALL_PLAYERS.equals(selectedPlayer)) 
        {
            DialogService.getInstance().showInfo
            (
                "Infos",
                "Aucun joueur sélectionné",
                getStage()
            );
            return;
        }
        try 
        {
            WindowContext<PieChartController> ctx =
                    windowManager.openModal
                    (
                        "pieChart_" + selectedPlayer,
                        "fxml/piechart.fxml",
                        "Performances",
                        getStage(),
                        PieChartController.class
                    );
            if (ctx != null) 
            {
                ctx.controller.setPlayer(selectedPlayer);
                ctx.controller.setSession(getSession());
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    /*****************************************************************************
     * Génère une couleur pseudo-aléatoire déterministe à partir du nom du joueur.
     *
     * @param player nom du joueur
     * @return couleur hexadécimale
     *****************************************************************************/
    private String generateColorFromPlayer(String player) 
    {
        int hash = Math.abs(player.hashCode());
        int r = (hash >> 16) & 0xFF;
        int g = (hash >> 8) & 0xFF;
        int b = hash & 0xFF;
        return String.format
        (
            "#%02X%02X%02X",
            (r + 100) % 256,
            (g + 100) % 256,
            (b + 100) % 256
        );
    }

    /********************************************************
     * Met à jour la liste des joueurs selon la date filtrée.
     *
     * @param selectedDate date sélectionnée (optionnelle)
     ********************************************************/
    private void updatePlayerComboBox(LocalDate selectedDate) 
    {
        if (allLogs == null) return;
        List<String> players = allLogs.stream()
                                      .filter(log -> selectedDate == null ||
                                              log.getDate().toLocalDate().equals(selectedDate))
                                      .map(GameLog::getPlayerName)
                                      .filter(Objects::nonNull)
                                      .distinct()
                                      .sorted()
                                      .toList();
        List<String> finalList = new ArrayList<>(players);
        finalList.add(0, ALL_PLAYERS);
        String previous = playerComboBox.getValue();
        playerComboBox.setItems(FXCollections.observableArrayList(finalList));
        playerComboBox.setValue
        (
            previous != null && 
            finalList.contains(previous) ? previous: ALL_PLAYERS
        );
    }

    /****************************************************************
     * Initialise les valeurs disponibles dans le ComboBox des dates.
     ****************************************************************/
    private void loadDateComboBox() 
    {
        if (dateComboBox == null || allLogs == null) return;
        List<LocalDate> dates = allLogs.stream()
                                       .map(log -> log.getDate().toLocalDate())
                                       .filter(Objects::nonNull)
                                       .distinct()
                                       .sorted(Comparator.reverseOrder())
                                       .toList();
        dateComboBox.setItems(FXCollections.observableArrayList(dates));
        if (!dates.isEmpty()) 
        {
            LocalDate today = LocalDate.now();
            dateComboBox.setValue(dates.contains(today) ? today : dates.get(0));
        }
    }

    /*************************************************************
     * Configure le format d’affichage des dates dans le ComboBox.
     *************************************************************/
    private void setupDateFormatting() 
    {
        if (dateComboBox == null) return;
        dateComboBox.setCellFactory(cb -> createDateCell());
        dateComboBox.setButtonCell(createDateCell());
    }

    /*********************************************************************
     * Crée une cellule personnalisée pour afficher les dates formatées.
     *
     * @return cellule JavaFX formatée
     *********************************************************************/
    private ListCell<LocalDate> createDateCell() 
    {
        return new ListCell<>() 
        {
            @Override
            protected void updateItem(LocalDate item, boolean empty) 
            {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        };
    }

    /********************************
     * Définit la fenêtre principale.
     *
     * @param stage stage principal
     ********************************/
    public void setPrimaryStage(Stage stage) {this.primaryStage = stage;}

    /*************************
     * @return stage principal
     *************************/
    public Stage getPrimaryStage() {return primaryStage;}

    /********************************************
     * @return stage courant associé au graphique
     ********************************************/
    private Stage getStage() {return (Stage) barChart.getScene().getWindow();}

    /*************************************
     * Définit la session de jeu courante.
     *
     * @param session session active
     **************************************/
    public void setSession(GameSession session) {this.session = session;}
    
    /***********************************
     * @return session de jeu courante
     **********************************/
    private GameSession getSession(){return this.session;}
}