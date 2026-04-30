package com.memory.app;

import com.memory.model.CardType;
import com.memory.model.IconTheme;
import com.memory.service.GameFacadeService;

/**┌───────────────────────────────────────┐
│             ChangeTheme                  │
│-----------------------------------------│
│  - le thème graphique (IconTheme)       │
│  - le type de cartes (CardType)         │
└────────────────────────────────────────┘**/

/************************************************************************************
 *Classe utilitaire permettant de modifier dynamiquement la configuration visuelle
 ************************************************************************************/
public class ChangeTheme 
{
    private final GameFacadeService game;

    /*********************************************************
     * Constructeur initialisant le service façade du jeu.
     *
     * @param game service principal permettant de manipuler
     *             l'état et la configuration du jeu
     * @throws NullPointerException si {@code game} est null
     *********************************************************/
    public ChangeTheme(GameFacadeService game) {this.game = game;}

    /************************************************************
     * Modifie le thème graphique du jeu.
     *
     * @param theme nouveau thème à appliquer
     *  aucune action n'est effectuée si {@code theme} est null
     ***********************************************************/
    public void changeTheme(IconTheme theme) 
    {
        if (theme == null) return;
        game.setTheme(theme);
    }

    /************************************************************
     * Modifie le type de cartes utilisé dans le jeu.
     *
     * @param type type de cartes à appliquer
     *  aucune action n'est effectuée si {@code type} est null
     ************************************************************/
    public void changeProvider(CardType type) 
    {
        if (type == null) return;
        game.setType(type);
        if (type == CardType.FONT_AWESOME || type == CardType.IKONLI) 
        {game.setTheme(IconTheme.ALL);} 
    }
}