package com.memory.ui;

import javafx.stage.Stage;

/*┌──────────────────────────────────────────────────────────────┐
│                          UIContext                             │
│--------------------------------------------------------------│
│ - Fournir un accès centralisé à la Stage courante            │
│ - Simplifier le passage de contexte UI entre composants      │
└──────────────────────────────────────────────────────────────┘*/

/********************************************
 * Contexte UI encapsulant une {@link Stage}.
 ********************************************/
public class UIContext
{
    private final Stage stage;

    /**********************************************************
     * Construit un contexte UI à partir d’une fenêtre JavaFX.
     *
     * @param stage fenêtre associée au contexte
     ********************************************************/
    public UIContext(Stage stage) {this.stage = stage;}

    /*******************************************
     * Retourne la fenêtre associée au contexte.
     *
     * @return instance de {@link Stage}
     *******************************************/
    public Stage getStage() {return stage;}
}