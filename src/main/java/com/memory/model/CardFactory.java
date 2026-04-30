package com.memory.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.function.BiFunction;

import com.memory.service.*;

import java.util.*;

/*┌──────────────────────────────────────────────────────────────┐
 │                         CardFactory                           │
 │--------------------------------------------------------------│
 │ - Générer des paires de cartes homogènes                     │
 │ - Supporter plusieurs sources d’icônes (Font, PNG, SVG)      │
 │ - Appliquer une coloration cohérente par paire               │
 │ - Garantir l’intégrité des données (taille, fallback)        │
 └──────────────────────────────────────────────────────────────┘*/

/***************************************************************************
 * Fabrique utilitaire permettant de générer des cartes pour le jeu Memory.
 * Produit des paires homogènes selon un thème et un type d’icône.
 **************************************************************************/
public class CardFactory 
{
    /*****************************************************************
     * Crée une liste de paires de cartes selon le type et le thème.
     * Les cartes sont mélangées avant retour.
     *
     * @param pairs nombre de paires à générer
     * @param theme thème d’icônes
     * @param type type de source (Font, PNG, SVG…)
     * @return liste mélangée de cartes (taille = pairs * 2)
     * @throws IllegalArgumentException si ressources insuffisantes
     *****************************************************************/
    public static List<Card> createPairs(int pairs, IconTheme theme, CardType type) 
    {
        List<Card> cards = switch (type) 
        {
            case FONT_AWESOME -> createFromList
            (
                pairs,
                IconProviderService.getFontAwesomeIcons(theme),
                (i, icon) -> new Card(i, icon, generateColor(i, pairs))
            );
            case IKONLI -> createFromList
            (
                pairs,
                IconProviderService.getIkonliIcons(theme),
                (i, ikon) -> new Card(i, ikon, generateColor(i, pairs))
            );
            case PNG -> createFromList
            (
                pairs,
                ImageIconService.getIcons(theme),
                (i, img) -> new Card(i, img, generateColor(i, pairs))
            );
            case SVG -> createSVG(pairs, theme);
        };
        Collections.shuffle(cards);
        return cards;
    }

    /***************************************************************
     * Génère des paires de cartes à partir d’une source générique.
     *
     * @param pairs nombre de paires
     * @param source liste source d’éléments
     * @param factory fonction de création de carte
     * @param <T> type des éléments source
     * @return liste de cartes générées
     * @throws IllegalArgumentException si taille insuffisante
     ****************************************************************/
    private static <T> List<Card> createFromList(
        int pairs,
        List<T> source,
        BiFunction<Integer, T, Card> factory)
    {
        List<T> items = new ArrayList<>(source);
        Collections.shuffle(items);
        checkSize(pairs, items.size(), "items");
        List<Card> cards = new ArrayList<>(pairs * 2);
        for (int i = 0; i < pairs; i++) 
        {
            T item = items.get(i);
            Card card1 = factory.apply(i, item);
            Card card2 = factory.apply(i, item);
            cards.add(card1);
            cards.add(card2);
        }
        return cards;
    }

    /**************************************************************************
     * Génère des cartes à partir de fichiers SVG avec colorisation dynamique.
     * Utilise un fallback en cas d’échec de chargement.
     *
     * @param pairs nombre de paires
     * @param theme thème SVG
     * @return liste de cartes SVG
     **************************************************************************/
    private static List<Card> createSVG(int pairs, IconTheme theme) 
    {
        List<String> files = new ArrayList<>(SvgIconService.getSvgFileNames(theme));
        Collections.shuffle(files);
        List<Card> cards = new ArrayList<>(pairs * 2);
        int index = 0;
        for (int i = 0; i < pairs; i++) 
        {
            Image img = null;
            while (img == null && index < files.size()) 
            {
                String file = files.get(index++);
                String svg = SvgIconService.getSvgContent(file);
                if (svg == null) continue;
                Color tempColor = generateColor(i, pairs);
                String hex = toHex(tempColor);
                img = SvgColorService.colorize(svg, hex);
            }

            if (img == null) {img = createFallbackImage(i);}
            Color color = generateColor(i, pairs);
            cards.add(new Card(i, img, color));
            cards.add(new Card(i, img, color));
        }
        return cards;
    }

    /********************************************************************
     * Génère une image de secours en cas d’échec de chargement d’icône.
     *
     * @param index index de la carte (influence la couleur)
     * @return image fallback
     ********************************************************************/
    private static Image createFallbackImage(int index)
    {
        int size = 64;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Color color = generateColor(index, 10);
        gc.setFill(color);
        gc.fillRect(0, 0, size, size);
        gc.setFill(Color.WHITE);
        gc.fillText("?", size / 2.5, size / 1.8);
        return canvas.snapshot(null, null);
    }

    /************************************************************
     * Vérifie que la source contient suffisamment d’éléments.
     *
     * @param pairs nombre de paires demandées
     * @param available nombre d’éléments disponibles
     * @param type type de ressource (message)
     * @throws IllegalArgumentException si insuffisant
     ************************************************************/
    private static void checkSize(int pairs, int available, String type) 
    {
        if (pairs > available) 
        {throw new IllegalArgumentException ("Pas assez d'éléments " + type + 
                                             " (" + available + ") pour " + pairs + " paires");}
    }

    /*******************************************************
     * Génère une couleur distincte basée sur la position.
     *
     * @param index index de la carte
     * @param total nombre total de paires
     * @return couleur HSB
     ********************************************************/
    private static Color generateColor(int index, int total) 
    {return Color.hsb((index * 360.0) / total, 0.75, 0.9);}

    /****************************************************
     * Convertit une couleur JavaFX en code hexadécimal.
     *
     * @param c couleur
     * @return chaîne hex (#RRGGBB)
     ***************************************************/
    private static String toHex(Color c) 
    {
        return String.format
        (
            "#%02X%02X%02X",
            (int)(c.getRed() * 255),
            (int)(c.getGreen() * 255),
            (int)(c.getBlue() * 255)
        );
    }
}