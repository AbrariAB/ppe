package com.memory.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

/*┌──────────────────────────────────────────────────────────────┐
│                        RulesController                         │
│----------------------------------------------------------------│
│ - Gérer les interactions utilisateur sur la fenêtre des règles│
│ - Permettre la fermeture de la fenêtre via action UI          │
└──────────────────────────────────────────────────────────────┘*/

/***********************************************************
 * Contrôleur JavaFX de la vue affichant les règles du jeu.
 **********************************************************/
public class RulesController
{
    /*********************************************************************
     * Ferme la fenêtre courante 
     *
     * @param event événement JavaFX provenant d’un composant de la scène
     *********************************************************************/
    @FXML
    private void close(ActionEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource())
                                           .getScene()
                                           .getWindow();
        stage.close();
    }
}