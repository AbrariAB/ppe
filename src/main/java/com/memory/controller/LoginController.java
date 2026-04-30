package com.memory.controller;

import com.memory.model.Player;
import com.memory.dao.HistoryDAO;
import com.memory.app.WindowManager;
import com.memory.app.WindowContext;
import com.memory.app.GameSession;
import com.memory.app.MainApp;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

/*┌────────────────────────────────────────┐
│           LoginController               │
│----------------------------------------│
│  - Authentification du joueur          │
│  - Création de session de jeu          │
│  - Navigation vers le jeu              │
│  - Accès à l’historique des parties    │
│  - Validation de l’environnement (DB)  │
└────────────────────────────────────────┘*/

/*************************************************************
 * Contrôleur de l’écran de connexion.
 *************************************************************/
public class LoginController 
{
    @FXML private TextField nameField;
    @FXML private ComboBox<FontAwesomeIcon> iconCombo;
    
    private final HistoryDAO dao = new HistoryDAO();
    private final WindowManager windowManager = WindowManager.getInstance();

    private GameSession session;
    private MainApp mainApp;

    /***************************************************
     * Injecte l’instance principale de l’application.
     *
     * @param app instance de {@link MainApp}
     ***************************************************/
    public void setMainApp(MainApp app) {this.mainApp = app;}

    /*****************************************************
     * Lance une nouvelle partie.
     *****************************************************/
    @FXML
    private void startGame() 
    {
        String name = nameField.getText().trim();
        FontAwesomeIcon icon = Player.getInstance().getProfileIcon();
        if (name.isEmpty() || icon == null) return;
        Player player = new Player(name, icon);
        mainApp.startGame(player);
    }

    /***********************************************************
     * Ouvre l’écran d’historique des parties.
     ************************************************************/
    @FXML
    private void openHistory() 
    {
        if (!databaseFileExists()) 
        {
            showError
            (
                "Base de données introuvable",
                "Le fichier SQLite est manquant.\nImpossible d’ouvrir l’historique."
            );
            return;
        }
        try 
        {
            if (dao.getAllLogs().isEmpty()) 
            {
                showInfo
                (
                    "Historique vide",
                    "Aucune partie enregistrée.\nL’historique ne peut pas être affiché."
                );return;
            }
        } 
        catch (Exception e) 
        {
            showError
            (
                "Erreur de lecture",
                "Impossible de lire la base de données.\nElle est peut-être corrompue."
            );return;
        }
        try 
        {
            WindowContext<HistoryController> ctx =
                    windowManager.openModal
                    (
                        "history",
                        "fxml/history.fxml",
                        "Historique des parties",
                        getStage(),
                        HistoryController.class
                    );
            if (ctx != null) {ctx.controller.setSession(getSession());}
        } 
        catch (Exception e) 
        {
            showError
            (
                "Erreur d’ouverture",
                "Impossible d’ouvrir la fenêtre d’historique."
            );
        }
    }

    /*****************************
     * Retourne le stage courant.
     *
     * @return stage JavaFX
     *****************************/
    private Stage getStage() {return (Stage) nameField.getScene().getWindow();}

    /*********************************************
     * Vérifie l’existence de la base de données.
     *
     * @return true si le fichier existe
     *********************************************/
    private boolean databaseFileExists() {return new File("game.db").exists();}

    /************************************
     * Affiche une alerte d’erreur.
     *
     * @param title titre de la fenêtre
     * @param message message affiché
     ************************************/
    private void showError(String title, String message) 
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /***************************************
     * Affiche une information utilisateur.
     *
     * @param title titre de la fenêtre
     * @param message message affiché
     ***************************************/
    private void showInfo(String title, String message) 
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /********************************
     * Définit la session courante.
     *
     * @param session session active
     *********************************/
    public void setSession(GameSession session) {this.session = session;}

    /*********************************
     * Retourne la session courante.
     *
     * @return session
     **********************************/
    private GameSession getSession(){return this.session;}
}
