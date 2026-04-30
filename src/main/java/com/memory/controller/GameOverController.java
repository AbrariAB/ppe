package com.memory.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.application.Platform;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

/*┌────────────────────────────────────────┐
 │           GameOverController           │
 │----------------------------------------│
 │  Permet d’afficher les informations    │
 │  du joueur (nom, score, bonus temps)   │
 │  ainsi que de gérer les actions        │
 │  utilisateur (rejouer ou quitter).     │
└────────────────────────────────────────┘*/

/************************************************
 *  Gére l’écran de  fin de partie (Game Over).         
 ***********************************************/
public class GameOverController 
{
    @FXML private Label playerLabel;
    @FXML private Label scoreLabel;
    @FXML private Label bonusLabel;
    @FXML private FontAwesomeIconView playerIcon;

    private Runnable onReplayCallback;

    /************************************************************************************
     * Initialise les données affichées dans l’écran de fin de partie.
     *
     * @param player le nom du joueur
     * @param score le score final du joueur
     * @param timeBonus le bonus de temps obtenu
     * @param rank le rang final du joueur
     * @param isRanked verifier si ranked
     * @param icon l’icône {@link FontAwesomeIcon} représentant le joueur
     * @param replayCallback action à exécuter lorsque l’utilisateur choisit de rejouer
     ***********************************************************************************/
    public void initData(String player,
                         int score,
                         int timeBonus,
                         String rank,
                         boolean isRanked,
                         FontAwesomeIcon icon,
                         Runnable replayCallback) 
    {
        playerLabel.setText("👤 " + player);
        scoreLabel.setText("🏆 Score : " + score);
        bonusLabel.setText("✰ Bonus  : " + timeBonus);
        playerIcon.setIcon(icon);
        this.onReplayCallback = replayCallback;
    }

    /****************************************************************
     * Gère l’action de rejouer une partie.
     ****************************************************************/
    @FXML
    private void onReplay() 
    {
        closeWindow();
        if (onReplayCallback != null) {onReplayCallback.run();}
    }

    /***********************************************************************
     * Gère l’action de quitter l’application.
     ***********************************************************************/
    @FXML
    private void onQuit() {Platform.exit();}

    /***************************************************************
     * Ferme la fenêtre JavaFX courante.
     ***************************************************************/
    private void closeWindow() 
    {
        Stage stage = (Stage) playerLabel.getScene().getWindow();
        stage.close();
    }
}