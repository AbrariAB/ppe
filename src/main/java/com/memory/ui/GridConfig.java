package com.memory.ui;

import javafx.scene.layout.StackPane;

/*┌──────────────────────────────────────────────────────────────┐
│                          GridConfig                            │
│--------------------------------------------------------------│
│ - Calcul et délégation de la grille                          │
│ - Détection des changements structurels                      │
│ - Cache des dernières dimensions (optimisation UI)          │
└──────────────────────────────────────────────────────────────┘*/

/***************************************************************
 * Gestionnaire de configuration et de cache pour la grille UI.
 * Optimise les recalculs de layout du plateau de jeu.
 ***************************************************************/
public class GridConfig 
{
    private GridSize lastSize;
    private int lastTotalCards = -1;
    private double lastCardSize = -1;

    /****************************************************************************************
     * Calcule la grille carrée optimale en fonction du conteneur et du nombre de cartes.
     *
     * @param calculator moteur de calcul de grille
     * @param container conteneur UI (StackPane)
     * @param totalCards nombre total de cartes
     * @return configuration de grille calculée
     ***************************************************************************************/
    public GridSize compute(Grid calculator,
                            StackPane container,
                            int totalCards) 
    {return calculator.computeSquareGrid(container, totalCards);}

    /************************************************************
     * Indique si une reconstruction de grille est nécessaire.
     *
     * @param totalCards nombre actuel de cartes
     * @return true si le nombre de cartes a changé
     ************************************************************/
    public boolean needsRebuild(int totalCards) {return lastTotalCards != totalCards;}

    /********************************************************
     * Met à jour le cache après construction de la grille.
     *
     * @param totalCards nombre de cartes utilisé
     * @param size configuration de grille calculée
     *******************************************************/
    public void markBuilt(int totalCards, GridSize size) 
    {
        this.lastTotalCards = totalCards;
        this.lastSize = size;
        this.lastCardSize = size.cardSize;
    }

    /****************************************************************************
     * Vérifie si la taille des cartes est identique à la dernière configuration.
     *
     * @param size taille à comparer
     * @return true si identique à la taille en cache
     ****************************************************************************/
    public boolean isSameCardSize(double size) {return lastCardSize == size;}

    /*********************************************************
     * Retourne la dernière configuration de grille calculée.
     *
     * @return dernier {@link GridSize} utilisé
     *********************************************************/
    public GridSize getLastSize() {return lastSize;}

    /******************************************************
     * Réinitialise complètement le cache de configuration.
     ******************************************************/
    public void reset() 
    {
        lastSize = null;
        lastTotalCards = -1;
        lastCardSize = -1;
    }
}