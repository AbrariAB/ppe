package com.memory.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/*┌────────────────────────────────────────┐
 │           ConfirmQuitController        │
 │----------------------------------------│
 │  Contrôleur de la fenêtre de           │
 │  confirmation de fermeture            │
 └────────────────────────────────────────┘*/

/*************************************************************************
 * Contrôleur de la fenêtre de confirmation de fermeture de l'application.
 * Gère les actions de validation ou d'annulation avant fermeture.
 ************************************************************************/
public class ConfirmQuitController
{
    private Runnable onConfirm;
    private Runnable onCancel;

    /************************************************************
     * Initialise les actions associées aux choix utilisateur.
     *
     * @param onConfirm action exécutée lors de la confirmation
     * @param onCancel action exécutée lors de l'annulation
     ***********************************************************/
    public void initData(Runnable onConfirm, Runnable onCancel) 
    {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    /************************************
     * Gère la confirmation de fermeture.
     ***********************************/
    @FXML
    private void handleConfirm() 
    {
        if (onConfirm != null) onConfirm.run();
        close();
    }

    /*********************************
     * Gère l'annulation de fermeture.
     *********************************/
    @FXML
    private void handleCancel() 
    {
        if (onCancel != null) onCancel.run();
        close();
    }

    /****************************
     * Ferme la fenêtre courante.
     ***************************/
    private void close() 
    {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    /************************
     * Racine de la vue FXML.
     ************************/
    @FXML
    private javafx.scene.layout.AnchorPane root;
}