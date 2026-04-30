package com.memory.app;

import javafx.stage.Stage;

/*┌──────────────────────────────────────┐
│            WindowContext               │
│----------------------------------------│
│ Conteneur générique représentant un    │
│ contexte de fenêtre JavaFX.            │
└────────────────────────────────────────┘*/

/***********************************************
 * Représente le contexte d’une fenêtre JavaFX.
 *
 * @param <T> type du contrôleur associé
 **********************************************/
public class WindowContext<T>
{
    /** Stage associé à la scène */
    public final Stage stage;

    /** Contrôleur JavaFX de la scène */
    public final T controller;

    /** Type du contrôleur */
    public final Class<T> type;

    /** Callback optionnel exécuté à la fermeture */
    public Runnable onClose;

    /*********************************************************
     * Crée un nouveau contexte de fenêtre.
     *
     * @param stage stage associé (non null)
     * @param controller contrôleur lié à la vue (non null)
     * @param type classe du contrôleur (non null)
     *********************************************************/
    public WindowContext(Stage stage, T controller, Class<T> type) 
    {
        this.stage = stage;
        this.controller = controller;
        this.type = type;
    }
}