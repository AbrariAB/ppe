package com.memory.controller;

import java.time.format.DateTimeFormatter;

import com.memory.dao.GameLog;
import com.memory.dao.HistoryDAO;
import com.memory.app.GameSession;
import com.memory.app.WindowContext;
import com.memory.app.WindowManager;

import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.collections.transformation.SortedList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;


/*┌────────────────────────────────────────┐
│           HistoryController             │
│----------------------------------------│
│  - Chargement des données (DAO)        │
│  - Affichage dans une TableView        │
│  - Recherche et filtrage dynamique     │
│  - Tri des résultats                   │
│  - Navigation vers le classement       │
└────────────────────────────────────────┘*/

/***************************************************************
 * Contrôleur de l’écran d’historique des parties.
 ***************************************************************/
public class HistoryController
{
    @FXML private TableView<GameLog> table;
    @FXML private TableColumn<GameLog, String> colName;
    @FXML private TableColumn<GameLog, Integer> colScore;
    @FXML private TableColumn<GameLog, Integer> colLevel;
    @FXML private TableColumn<GameLog, Integer> colTime;
    @FXML private TableColumn<GameLog, Integer> colBonus;
    @FXML private TableColumn<GameLog, String> colRank;
    @FXML private TableColumn<GameLog, String> colDate;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchColumn;

    private FilteredList<GameLog> filteredData;
    private final HistoryDAO dao = new HistoryDAO();
    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm:ss");

    private final WindowManager windowManager = WindowManager.getInstance();
    private final PauseTransition debounce = new PauseTransition(Duration.millis(100));
    private GameSession session;

    /*************************************************
     * Initialise les colonnes et charge les données.
     *************************************************/
    @FXML
    public void initialize()
    {
        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPlayerName()));
        colScore.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getScore()));
        colLevel.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getLevel()));
        colBonus.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getBonus()));
        colRank.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRank()));
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate().format(dateFormatter)));
        loadData();
    }

    /****************************************************
     * Ouvre la fenêtre de classement.
     ***************************************************/
    @FXML
    private void openChart()
    {
        try 
        {
            WindowContext<ChartController> ctx =
                windowManager.openModal
                (
                   "chart",
                   "fxml/chart.fxml",
                   "Classement",
                   getStage(),
                   ChartController.class
                );
            if (ctx != null) {ctx.controller.setSession(getSession());}
        }
        catch (Exception e) {e.printStackTrace();}
    }

    /**************************************************
     * Charge les données depuis les logs
     *************************************************/
    private void loadData()
    {
        try 
        {
            ObservableList<GameLog> data =
                    FXCollections.observableArrayList(dao.getAllLogs());
            if (data.isEmpty()) 
            {
                showInfo("Aucune donnée trouvée",
                        "La base de données est vide.\nAucun score n’a encore été enregistré.");
                table.setItems(FXCollections.observableArrayList());
                return;
            }
            table.setItems(data);
            setupSearch(data);
        } 
        catch (Exception e) 
        {
            showError
            (
                "Erreur lors de la récupération des données.\nLa base de données est peut-être absente ou corrompue.",
                e
            );
            table.setItems(FXCollections.observableArrayList());
        }
    }

    /*******************************
     * Retourne le stage courant.
     *
     * @return stage JavaFX
     ******************************/
    private Stage getStage() {return (Stage) table.getScene().getWindow();}

    /******************************************
     * Affiche une boîte de dialogue d’erreur.
     *
     * @param message message utilisateur
     * @param e exception associée
     ******************************************/
    private void showError(String message, Exception e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(message + "\n\nDétails : " + e.getMessage());
        alert.showAndWait();
    }

    /****************************************
     * Affiche une information utilisateur.
     *
     * @param title titre
     * @param message contenu
     ***************************************/
    private void showInfo(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*****************************
     * Ferme la fenêtre courante.
     *****************************/
    @FXML
    private void closeWindow() {getStage().close();}

    /***********************************************
     * Initialise le système de recherche et tri.
     *
     * @param data données source
     **********************************************/
    private void setupSearch(ObservableList<GameLog> data)
    {
        filteredData = new FilteredList<>(data, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            debounce.setOnFinished(event -> applyFilter(newVal));
            debounce.playFromStart();
        });
        SortedList<GameLog> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }
    
    /******************************************
     * Applique le filtre de recherche.
     *
     * @param searchText texte saisi (nullable)
     ******************************************/
    private void applyFilter(String searchText)
    {
        String lower = searchText == null ? "" : searchText.toLowerCase();
        filteredData.setPredicate(log -> {
            if (lower.isEmpty()) return true;
            return
                    log.getPlayerName().toLowerCase().contains(lower) ||
                    String.valueOf(log.getScore()).contains(lower)    ||
                    String.valueOf(log.getLevel()).contains(lower)    ||
                    String.valueOf(log.getBonus()).contains(lower)    ||
                    log.getRank().toLowerCase().contains(lower)       ||
                    log.getDate().format(dateFormatter).toLowerCase().contains(lower);
        });
    }

    /********************************
     * Définit la session courante.
     *
     * @param session session active
     ********************************/
    public void setSession(GameSession session) {this.session = session;}

    /**********************************
     * Retourne la session courante.
     *
     * @return session
     ************************************/
    private GameSession getSession(){return this.session;}
}
