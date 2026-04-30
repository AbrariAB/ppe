package com.memory.util;

import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*┌──────────────────────────────────────────────────────────────┐
│                        DialogService                           │
│--------------------------------------------------------------│
│ - Affichage des alertes (info, erreur, succès, confirmation)  │
│ - Uniformisation du style des dialogues                      │
│ - Gestion optionnelle du parent Stage                        │
└──────────────────────────────────────────────────────────────┘*/

/*************************************************************
 * Service singleton de gestion des boîtes de dialogue JavaFX.
 *************************************************************/
public class DialogService 
{
    private static final DialogService INSTANCE = new DialogService();

    private DialogService() {}

    /*******************************************************
     * Retourne l’instance unique du service.
     *
     * @return instance singleton {@link DialogService}
     ********************************************************/
    public static DialogService getInstance() {return INSTANCE;}

    /***********************************************************
     * Affiche une boîte d’information simple sans propriétaire.
     *
     * @param title titre de la fenêtre
     * @param message message principal
     ***********************************************************/
    public void showInfo(String title, String message) 
    {
        Alert alert = createAlert(Alert.AlertType.INFORMATION, title, null, message);
        alert.showAndWait();
    }

    /***************************************************************
     * Affiche une boîte d’information liée à une fenêtre parente.
     *
     * @param title titre de la fenêtre
     * @param message message principal
     * @param owner stage parent (peut être null)
     ****************************************************************/
    public void showInfo(String title, String message, Stage owner) 
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) 
        {
            alert.initOwner(owner);
            alert.initModality(Modality.WINDOW_MODAL);
        }
        alert.showAndWait();
    }

    /********************************************************
     * Affiche une erreur avec message optionnel d’exception.
     *
     * @param message message utilisateur
     * @param e exception associée (nullable)
     ********************************************************/
    public void showError(String message, Exception e) 
    {
        String details = (e != null) ? "\n\nDétails : " + e.getMessage() : "";
        Alert alert = createAlert
        (
            Alert.AlertType.ERROR,
            "Oups",
            "Une erreur est survenue",
            message + details
        );
        alert.showAndWait();
    }

    /***************************************
     * Affiche une notification de succès.
     *
     * @param message message à afficher
     ***************************************/
    public void showSuccess(String message) 
    {
        Alert alert = createAlert
        (
            Alert.AlertType.INFORMATION,
            "Bravo",
            null,
            message
        );
        alert.showAndWait();
    }

    /***************************************************
     * Affiche une boîte de confirmation utilisateur.
     *
     * @param title titre de la boîte
     * @param message message de confirmation
     * @return true si l’utilisateur valide (OK)
     ***************************************************/
    public boolean confirm(String title, String message) 
    {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, null, message);
        return alert.showAndWait()
                    .filter(btn -> btn == ButtonType.OK)
                    .isPresent();
    }

    /************************************************
     * Crée une instance d’Alert configurée.
     *
     * @param type type de dialogue
     * @param title titre de la fenêtre
     * @param header en-tête (nullable)
     * @param content contenu principal
     * @return instance configurée de {@link Alert}
     ************************************************/
    private Alert createAlert(Alert.AlertType type,
                              String title,
                              String header,
                              String content) 
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        return alert;
    }
}