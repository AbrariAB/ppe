package com.memory.model;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import org.kordamp.ikonli.Ikon;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/*┌────────────────────────────────────────┐
│                 Card                    │
│-----------------------------------------│
│ - un identifiant unique (id)           │
│ - un type de rendu (icône ou image)    │
│ - une ressource graphique associée     │
│   (FontAwesome, Ikonli ou Image)       │
│ - une couleur                          │
│ - un état (révélée / associée)         │
└────────────────────────────────────────┘*/

/******************************************
 * Classe représentant une carte du jeu .   
 *****************************************/
public class Card 
{
    private final int id;
    private final CardType type;

    private final FontAwesomeIcon faIcon; 
    private final Ikon ikonIcon;          
    private final Image image;            
    private final Color color;

    private boolean revealed = false;
    private boolean matched = false;

    /******************************************************
     * Construit une carte basée sur une icône FontAwesome.
     *
     * @param id identifiant unique de la carte
     * @param icon icône FontAwesome associée
     * @param color couleur de la carte
     ******************************************************/
    public Card(int id, FontAwesomeIcon icon, Color color) 
    {
        this.id = id;
        this.type = CardType.FONT_AWESOME;
        this.faIcon = icon;
        this.ikonIcon = null;
        this.image = null;
        this.color = color;
    }

    /*************************************************
     * Construit une carte basée sur une icône Ikonli.
     *
     * @param id identifiant unique de la carte
     * @param ikon icône Ikonli associée
     * @param color couleur de la carte
     *************************************************/
    public Card(int id, Ikon ikon, Color color) 
    {
        this.id = id;
        this.type = CardType.IKONLI;
        this.faIcon = null;
        this.ikonIcon = ikon;
        this.image = null;
        this.color = color;
    }

    /******************************************************************
     * Construit une carte basée sur une image (PNG ou SVG rasterisé).
     *
     * @param id identifiant unique de la carte
     * @param image image associée
     * @param color couleur de la carte
     *****************************************************************/
    public Card(int id, Image image, Color color) 
    {
        this.id = id;
        this.type = CardType.PNG;
        this.faIcon = null;
        this.ikonIcon = null;
        this.image = image;
        this.color = color;
    }

    /****************************************
     * @return identifiant unique de la carte
     ***************************************/
    public int getId() { return id; }

    /***********************************************
     * @return type de la carte (source graphique)
     **********************************************/
    public CardType getType() { return type; }

    /*****************************************************
     * @return icône FontAwesome si applicable, sinon null
     ****************************************************/
    public FontAwesomeIcon getFaIcon() { return faIcon; }

    /*************************************************
     * @return icône Ikonli si applicable, sinon null
     ***********************************************/
    public Ikon getIkonIcon() { return ikonIcon; }

    /**************************************************
     * @return image associée si applicable, sinon null
     *************************************************/
    public Image getImage() { return image; }

    /*******************************
     * @return couleur de la carte
     ******************************/
    public Color getColor() { return color; }

    /****************************************************
     * @return true si la carte est actuellement révélée
     ***************************************************/
    public boolean isRevealed() { return revealed; }

    /******************************************
     * @return true si la carte a été appariée
     ******************************************/
    public boolean isMatched() { return matched; }

    /******************
     * Révèle la carte.
     ******************/
    public void reveal() { revealed = true; }

    /*****************
     * Cache la carte.
     *****************/
    public void hide() { revealed = false; }

    /*********************************
     * Marque la carte comme appariée.
     *********************************/
    public void match() { matched = true; }
}