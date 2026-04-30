package com.memory.app;

import com.memory.controller.GameController;
import com.memory.controller.LoginController;
import com.memory.model.Player;
import com.memory.ui.UIContext;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/*┌─────────────────────────────────────┐
│              MainApp                  │
│---------------------------------------│
│  - bootstrap application              │
│  - navigation login / jeu             │
│  - injection des contrôleurs          │
│  - création des sessions de jeu       │
└───────────────────────────────────────┘*/

/*******************************************************************
 * Classe principale de lancement de l’application.
 ******************************************************************/
public class MainApp extends Application 
{
    private Stage primaryStage;
    private final WindowManager windowManager = WindowManager.getInstance();

    /*********************************************************
     * Point d’entrée JavaFX.
     *
     * @param stage stage principal fourni par la plateforme
     ********************************************************/
    @Override
    public void start(Stage stage) 
    {
        this.primaryStage = stage;
        stage.setTitle("Mnemo");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        showLogin();
        stage.centerOnScreen();
        stage.show();
    }

    /*******************************************************
     * Affiche l’écran de connexion.
     ******************************************************/
    public void showLogin() 
    {
        WindowContext<LoginController> ctx =
                windowManager.switchScene
                (
                    "fxml/login.fxml",
                    primaryStage,
                    LoginController.class
                );
        if (ctx != null) 
        {ctx.controller.setMainApp(this);}
    }

    /*******************************************
     * Initialise et lance une session de jeu.
     *
     * @param player joueur courant
     ******************************************/
    public void startGame(Player player) 
    {
        try 
        {
            GameSession session = new GameSession(player,new UIContext(primaryStage));
            WindowContext<GameController> ctx =
                    windowManager.switchScene
                    (
                        "fxml/game.fxml",
                        primaryStage,
                        GameController.class
                    );
            if (ctx != null) 
            {
                ctx.controller.setSession(session);
                primaryStage.setMaximized(true);
                primaryStage.setFullScreen(false);
                primaryStage.centerOnScreen();
            }
        } 
        catch (Exception e) {e.printStackTrace();}
    }

    /*************************************
     * Point d’entrée standard JVM.
     *
     * @param args arguments de lancement
     *************************************/
    public static void main(String[] args) {launch();}
}