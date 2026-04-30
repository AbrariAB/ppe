package com.memory.service;

import com.memory.controller.ConfirmQuitController;
import com.memory.controller.GameOverController;
import com.memory.controller.VictoryController;
import com.memory.model.Player;
import com.memory.app.WindowManager;
import com.memory.app.WindowContext;

import javafx.application.Platform;
import javafx.stage.Stage;

/*┌──────────────────────────────────────────────────────────────┐
│                    NavigationService                           │
│--------------------------------------------------------------│
│ - Centraliser la navigation UI                               │
│ - Ouvrir des fenêtres modales (Game Over, Victory, etc.)     │
│ - Injecter les données dans les contrôleurs                  │
│ - Garantir l’exécution sur le thread JavaFX                  │
└──────────────────────────────────────────────────────────────┘*/

/******************************************************************
 * Service applicatif chargé de gérer la navigation et l’ouverture
 * des différentes fenêtres de l’interface JavaFX.
 ******************************************************************/
public class NavigationService 
{
    private final WindowManager windowManager = WindowManager.getInstance();

    /**************************************************************
     * Affiche la fenêtre de fin de partie (défaite).
     *
     * @param parentStage fenêtre parente (obligatoire)
     * @param game façade de jeu pour récupérer les données finales
     * @param player joueur courant
     * @param restart action à exécuter pour relancer une partie
     * @throws IllegalStateException si le stage parent est null
     ***************************************************************/
    public void showGameOver(Stage parentStage,
                             GameFacadeService game,
                             Player player,
                             Runnable restart) 
    {
        if (parentStage == null) 
        {throw new IllegalStateException("Parent stage is null");}

        Platform.runLater(() -> {

            WindowContext<GameOverController> ctx =
                    windowManager.openModal
                    (
                        "gameOver",
                        "fxml/game-over.fxml",
                        "",
                        parentStage,
                        GameOverController.class
                    );

            if (ctx != null) 
            {
                ctx.controller.initData
                (
                    player.getName(),
                    game.getFinalScore(),
                    game.isHardcore() ? game.getScoreProgression() : game.getScoreTemps(),
                    game.getHardcoreRank(),
                    game.isHardcore(),
                    player.getProfileIcon(),
                    restart
                );
            }
        });
    }

    /***************************************************************
     * Affiche la fenêtre de victoire.
     *
     * @param parentStage fenêtre parente (obligatoire)
     * @param game façade de jeu pour récupérer les données finales
     * @param player joueur courant
     * @param restart action de redémarrage
     * @throws IllegalStateException si le stage parent est null
     ***************************************************************/
    public void showGameWin(Stage parentStage,
                            GameFacadeService game,
                            Player player,
                            Runnable restart) 
    {
        if (parentStage == null) 
        {throw new IllegalStateException("Parent stage is null");}

        Platform.runLater(() -> {

            WindowContext<VictoryController> ctx =
                    windowManager.openModal
                    (
                        "gameWin",
                        "fxml/victory.fxml",
                        "",
                        parentStage,
                        VictoryController.class
                    );

            if (ctx != null) 
            {
                ctx.controller.initData
                (
                    player.getName(),
                    game.getFinalScore(),
                    game.isHardcore() ? game.getScoreProgression() : game.getScoreTemps(),
                    game.getHardcoreRank(),
                    game.isHardcore(),
                    player.getProfileIcon(),
                    restart
                );
            }
        });
    }

    /*****************************************************************
     * Ouvre une fenêtre modale générique à partir d’un fichier FXML.
     *
     * @param fxmlPath chemin du fichier FXML
     * @param title titre de la fenêtre
     * @param parentStage stage parent (optionnel)
     * @return stage de la fenêtre ouverte ou null si échec
     ****************************************************************/
    public Stage showWindow(String fxmlPath,
                            String title,
                            Stage parentStage) 
    {
        WindowContext<Object> ctx =
                windowManager.openModal
                (
                    "window_" + fxmlPath,
                    fxmlPath,
                    title,
                    parentStage,
                    Object.class
                );

        return (ctx != null) ? ctx.stage : null;
    }

    /*****************************************************************
     * Ouvre une fenêtre de confirmation.
     *
     * @param parentStage stage parent
     * @param onConfirm action si confirmation
     * @param onCancel action sinon
     ****************************************************************/
    public void showConfirmQuit(Stage parentStage,
                                Runnable onConfirm,
                                Runnable onCancel)
    {
        if (parentStage == null) 
        {throw new IllegalStateException("Parent stage is null");}
        Platform.runLater(() -> {
            WindowContext<ConfirmQuitController> ctx =
                    windowManager.openModal
                    (
                        "confirmQuit",
                        "fxml/confirm-quit.fxml",
                        "",
                        parentStage,
                        ConfirmQuitController.class
                    );
            if (ctx != null) {ctx.controller.initData(onConfirm, onCancel);}
        });
    }

    /*************************************************
     * Fournit l’instance du gestionnaire de fenêtres.
     *
     * @return instance de {@link WindowManager}
     *************************************************/
    public WindowManager getWindowManager() {return windowManager;}
}