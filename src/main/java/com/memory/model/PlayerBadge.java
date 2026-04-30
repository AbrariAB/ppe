package com.memory.model;

/*┌──────────────────────────────────────────────────────────────┐
│                         PlayerBadge                            │
│--------------------------------------------------------------│
│ - Classifier la progression du joueur                         │
│ - Fournir un nom d’affichage lisible (UI)                     │
│ - Associer une couleur visuelle à chaque niveau               │
└──────────────────────────────────────────────────────────────┘*/

/************************************************************************
 * Représente les niveaux de progression d’un joueur sous forme de badge.
 * Fournit des métadonnées utiles à l’affichage (nom, couleur).
 ***********************************************************************/
public enum PlayerBadge
{
    BEGINNER,
    APPRENTICE,
    INTERMEDIATE,
    ADVANCED,
    ELITE,
    MASTER,
    LEGEND;

    /***************************************************************
     * Retourne le nom lisible du badge pour affichage utilisateur.
     *
     * @return libellé localisé du badge
     ***************************************************************/
    public String getDisplayName()
    {
        return switch (this) 
        {
            case BEGINNER -> "Débutant";
            case APPRENTICE -> "Apprenti";
            case INTERMEDIATE -> "Intermédiaire";
            case ADVANCED -> "Avancé";
            case ELITE -> "Elite";
            case MASTER -> "Master";
            case LEGEND -> "Légende";
        };
    }

    /****************************************************************
     * Fournit la couleur associée au badge sous forme hexadécimale.
     *
     * @return code couleur HEX (format #RRGGBB)
     ****************************************************************/
    public String getColor() 
    {
        return switch (this) 
        {
            case BEGINNER -> "#bdc3c7";
            case APPRENTICE -> "#95a5a6";
            case INTERMEDIATE -> "#3498db";
            case ADVANCED -> "#2ecc71";
            case ELITE -> "#9b59b6";
            case MASTER -> "#e67e22";
            case LEGEND -> "#f1c40f";
        };
    }
}