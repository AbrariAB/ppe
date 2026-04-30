package com.memory.ui;

/*┌────────────────────────────────────────┐
│           GridSize                       │
│------------------------------------------│
│ - Le nombre de colonnes                 │
│ - Le nombre de lignes                   │
│ - La taille des cartes                  │
└────────────────────────────────────────┘*/

/*********************************************************************************
 * Représente une configuration optimale de grille (lignes / colonnes / taille).
 ********************************************************************************/
public class GridSize
{
    public final int cols;
    public final int rows;
    public final double cardSize;

    /***********************************************
     * Constructeur de la configuration de grille.
     *
     * @param cols nombre de colonnes
     * @param rows nombre de lignes
     * @param cardSize taille des cartes
     **********************************************/
    public GridSize(int cols, int rows, double cardSize)
    {
        this.cols = cols;
        this.rows = rows;
        this.cardSize = cardSize;
    }
}