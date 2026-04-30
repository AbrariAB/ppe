package com.memory.ui;

import org.kordamp.ikonli.javafx.FontIcon;

import com.memory.model.Card;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/*┌──────────────────────────────────────┐
│             CardViewFactory            │
│----------------------------------------│
│ - Créer la face arrière standardisée   │
│ - Générer dynamiquement la face avant  │
│   selon le type de carte (FontAwesome, │
│   Ikonli, Image)                       │
│ - Adapter le rendu et le scaling       │
│   automatiquement à la taille demandée │
└────────────────────────────────────────┘*/

/*******************************************************************************
 * Classe stateless, fabrique utilitaire de vues JavaFX  pour les cartes du jeu.  
 *******************************************************************************/
public class CardViewFactory 
{
    private static final Image CARD_BACK = new Image
    (CardViewFactory.class.getResource("/assets/card-back.png").toExternalForm());

    /********************************************************
     * Crée la vue de la face arrière d'une carte.
     *
     * @param size taille (largeur = hauteur) du composant
     * @return noeud JavaFX prêt à être affiché
     ********************************************************/
    public static Node createBack(double size) 
    {
        ImageView iv = new ImageView(CARD_BACK);
        iv.setPreserveRatio(false);
        StackPane wrapper = new StackPane(iv);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPrefSize(size, size);
        wrapper.setMinSize(size, size);
        wrapper.setMaxSize(size, size);
        iv.fitWidthProperty().bind(wrapper.widthProperty());
        iv.fitHeightProperty().bind(wrapper.heightProperty());
        return wrapper;
    }

    /******************************************************
     * Crée la vue de la face avant d'une carte.
     *
     * @param card carte métier à représenter
     * @param size taille (largeur = hauteur) du composant
     * @return noeud JavaFX représentant la carte
     ******************************************************/
    public static Node createFront(Card card, double size) 
    {
        Node icon = createIconNode(card);
        return wrapIcon(icon, size);
    }

    /******************************************************************
     * Génère le noeud graphique correspondant au contenu de la carte.
     *
     * @param card carte source
     * @return noeud graphique adapté au type de carte
     *****************************************************************/
    private static Node createIconNode(Card card) 
    {
        return switch (card.getType()) 
        {
                case FONT_AWESOME -> {
                    FontAwesomeIconView view = new FontAwesomeIconView(card.getFaIcon());
                    view.setFill(card.getColor());
                    yield view;
                }
                case IKONLI -> {
                    FontIcon view = new FontIcon(card.getIkonIcon());
                    view.setIconColor(card.getColor());
                    yield view;
                }
                case PNG, SVG -> {
                    ImageView iv = new ImageView(card.getImage());
                    iv.setPreserveRatio(true);
                    yield iv;
                }
            };
    }


    /*****************************************************************
     * Encapsule une icône dans un conteneur dimensionné et applique
     * une logique de scaling adaptatif en fonction du type de noeud.
     *
     * @param icon noeud graphique à afficher
     * @param size taille cible du conteneur
     * @return noeud encapsulé et redimensionnable
     ****************************************************************/
    private static Node wrapIcon(Node icon, double size) 
    {
        StackPane wrapper = new StackPane(icon);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPrefSize(size, size);
        wrapper.setMinSize(size, size);
        wrapper.setMaxSize(size, size);
        double ratio = 0.65;
        double initialSize = Math.min(size * ratio, 140);
        if (icon instanceof ImageView iv) 
        {
            iv.setFitWidth(initialSize);
            iv.setFitHeight(initialSize);
            iv.fitWidthProperty().bind(wrapper.widthProperty().multiply(ratio));
            iv.fitHeightProperty().bind(wrapper.heightProperty().multiply(ratio));
        }
        else if (icon instanceof FontAwesomeIconView fa) 
        {
            fa.setSize((int) initialSize + "px");
            wrapper.widthProperty().addListener((obs, oldV, newV) ->
                fa.setSize((int) Math.min(newV.doubleValue() * ratio, 140) + "px"));
        }
        else if (icon instanceof FontIcon fi) 
        {
            fi.setIconSize((int) initialSize);
            wrapper.widthProperty().addListener((obs, oldV, newV) ->
                fi.setIconSize((int) Math.min(newV.doubleValue() * ratio, 140)));
        }
        else 
        {
            icon.setScaleX(initialSize / 50.0);
            icon.setScaleY(initialSize / 50.0);
            wrapper.widthProperty().addListener((obs, oldV, newV) -> {
                double iconSize = newV.doubleValue() * ratio;
                icon.setScaleX(iconSize / 50.0);
                icon.setScaleY(iconSize / 50.0);
            });
        }
        return wrapper;
    }
}