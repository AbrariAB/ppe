package com.memory.ui;

import javafx.scene.layout.StackPane;

/*┌────────────────────────────────────────┐
│                Grid                      │
│ -----------------------------------------│
│ Cette classe représente une configuration│
│ de grille utilisée dans l'application.  │
└────────────────────────────────────────┘*/

/***************************************************************
 * Cette classe est un simple conteneur de données (data class)
 ***************************************************************/
public class Grid
{
    private static final int[][] LAYOUTS = 
    {
        {2, 2},   
        {3, 2},   
        {4, 2},   
        {4, 3},   
        {4, 4},   

        {5, 4},   
        {6, 4},   
        {6, 5},   
        {6, 6},   

        {7, 6},   
        {8, 6},   
        {8, 7},   
        {8, 8},   

        {9, 8},   
        {10, 8},  
        {10, 9},  
        {10, 10}, 

        {11, 10}, 
        {12, 10}, 
        {12, 11}, 
        {12, 12}, 

        {13, 12}, 
        {14, 12}, 
        {14, 13}, 
        {15, 14}, 
    };

    private static final int CARD_GAP = 30;
    private static final double CARD_SIZE = 300;

    public Grid(){}

    /*******************************************************************
     * Calcule une grille “carrée” pour le nombre total de cartes,
     * en adaptant la taille des cartes.
     *
     * @param totalCards nombre total de cartes
     * @return configuration optimale de grille (cols, rows, cardSize)
     *******************************************************************/
    public GridSize computeSquareGrid(StackPane sp, int totalCards)
    {
        double width = sp.getWidth();
        double height = sp.getHeight();
        if (width <= 0 || height <= 0){return new GridSize(1, totalCards, CARD_SIZE);}
        int bestCols = 2;
        int bestRows = totalCards;
        double bestScore = Double.MAX_VALUE;
        for (int[] layout : LAYOUTS)
        {
            int cols = layout[0];
            int rows = layout[1];
            if (cols * rows < totalCards) continue;
            double cellW = (width - (cols - 1) * CARD_GAP) / cols;
            double cellH = (height - (rows - 1) * CARD_GAP) / rows;
            double cardSize = Math.min(cellW, cellH);
            double emptyPenalty = (cols * rows - totalCards) * 0.3;
            double score = -cardSize + emptyPenalty;
            if (score < bestScore)
            {
                bestScore = score;
                bestCols = cols;
                bestRows = rows;
            }
        }
        double sizeByWidth = (width - (bestCols - 1) * CARD_GAP) / bestCols;
        double sizeByHeight = (height - (bestRows - 1) * CARD_GAP) / bestRows;
        double cardSize = Math.min(sizeByWidth, sizeByHeight);
        cardSize = Math.min(cardSize, CARD_SIZE);
        return new GridSize(bestCols, bestRows, cardSize);
    }

    public double getCardSize(){return CARD_SIZE;}

    public double getCardGap(){return CARD_GAP;}

}
