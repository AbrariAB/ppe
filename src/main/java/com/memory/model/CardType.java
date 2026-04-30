package com.memory.model;

/*┌────────────────────────────────────────┐
 │           CardType                     │
 │----------------------------------------│
 │  Enumération des types de cartes       │
 │  possibles pour le jeu.               │
 │  Définit les formats d’icônes         │
 │  utilisables par la CardFactory :     │
 │  FontAwesome, Ikonli, PNG ou SVG.     │
 └────────────────────────────────────────┘*/

/************************************************************************************
 * Représente les types de cartes pouvant être générées.
 * Utilisé par {@link CardFactory} pour déterminer le format et la source des icônes.
 ************************************************************************************/
public enum CardType
{
    /** Carte utilisant des icônes FontAwesome */
    FONT_AWESOME,

    /** Carte utilisant des icônes Ikonli */
    IKONLI,

    /** Carte utilisant des images PNG */
    PNG,

    /** Carte utilisant des icônes SVG */
    SVG
}