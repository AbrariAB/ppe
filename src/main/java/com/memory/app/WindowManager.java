package com.memory.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*┌─────────────────────────────────────┐
│            WindowManager              │
│---------------------------------------│
│  - ouvrir des modales                 │
│  - changer de scène                   │
│  - gérer le cycle de vie des fenêtres │
└───────────────────────────────────────┘*/

/*******************************************************************
 * Gestionnaire de fenêtres JavaFX.
 ********************************************************************/
public final class WindowManager 
{
    private static final WindowManager INSTANCE = new WindowManager();

    /******************************
     * Retourne l’instance unique.
     *
     * @return instance singleton
     ******************************/
    public static WindowManager getInstance() {return INSTANCE;}

    /*********************
     * Constructeur privé 
     ********************/
    private WindowManager() {}

    private final Map<String, WindowContext<?>> windows = new HashMap<>();

    /***********************************************************************
     * Ouvre une fenêtre modale ou ramène au premier plan si déjà ouverte.
     *
     * @param id identifiant unique de la fenêtre
     * @param fxml chemin FXML
     * @param title titre de la fenêtre
     * @param parent stage parent (nullable)
     * @param controllerClass type du contrôleur
     * @param <T> type du contrôleur
     * @return contexte de fenêtre
     * @throws RuntimeException en cas d’échec de chargement
     ***********************************************************************/
    public <T> WindowContext<T> openModal(
            String id,
            String fxml,
            String title,
            Stage parent,
            Class<T> controllerClass) 
    {
        try 
        {
            if (windows.containsKey(id)) 
            {
                @SuppressWarnings("unchecked")
                WindowContext<T> existing = (WindowContext<T>) windows.get(id);
                existing.stage.toFront();
                return existing;
            }
            FXMLLoader loader = loadFXML(fxml);
            Parent root = loader.load();
            T controller = controllerClass.cast(loader.getController());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            URL iconUrl = getClass().getResource("/icon.png");
            if (iconUrl != null) {stage.getIcons().add(new Image(iconUrl.toExternalForm()));}
            if (parent != null) 
            {
                stage.initOwner(parent);
                stage.initModality(Modality.WINDOW_MODAL);
            }
            WindowContext<T> context = new WindowContext<>(stage, controller, controllerClass);
            stage.setOnHidden(e -> {
                windows.remove(id);
                if (context.onClose != null) context.onClose.run();
            });
            windows.put(id, context);
            stage.show();
            return context;
        }
        catch (Exception e) {throw new RuntimeException("Failed to open modal: " + id, e);}
    }

    /********************************************
     * Remplace la scène d’un stage existant.
     *
     * @param fxml chemin FXML
     * @param stage stage cible
     * @param controllerClass type du contrôleur
     * @param <T> type du contrôleur
     * @return contexte de fenêtre
     * @throws RuntimeException en cas d’échec
     ********************************************/
    public <T> WindowContext<T> switchScene(
            String fxml,
            Stage stage,
            Class<T> controllerClass) 
    {
        try 
        {
            FXMLLoader loader = loadFXML(fxml);
            Parent root = loader.load();
            T controller = controllerClass.cast(loader.getController());
            stage.setScene(new Scene(root));
            return new WindowContext<>(stage, controller, controllerClass);
        } 
        catch (Exception e) {throw new RuntimeException("Failed to switch scene: " + fxml, e);}
    }

    /**************************************
     * Ferme une fenêtre identifiée.
     *
     * @param id identifiant de la fenêtre
     **************************************/
    public void close(String id) 
    {
        WindowContext<?> ctx = windows.remove(id);
        if (ctx != null) ctx.stage.close();
    }

    /**************************************
     * Ferme toutes les fenêtres ouvertes.
     **************************************/
    public void closeAll() 
    {
        windows.values().forEach(c -> c.stage.close());
        windows.clear();
    }

    /*******************************************************************
     * Charge un fichier FXML.
     *
     * @param fxml chemin de la ressource
     * @return loader configuré
     * @throws IllegalArgumentException si la ressource est introuvable
     ********************************************************************/
    private FXMLLoader loadFXML(String fxml) 
    {
        URL url = getClass().getClassLoader().getResource(fxml);
        if (url == null) {throw new IllegalArgumentException("FXML not found: " + fxml);}
        return new FXMLLoader(url);
    }
}