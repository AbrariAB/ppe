package com.memory.module;

import com.memory.model.*;
import com.memory.service.GameFacadeService;
import com.memory.ui.HudManager;

/*┌────────────────────────────────────────┐
│              LevelModule                 │
│------------------------------------------│
│ Garanti la cohérence entre               │
│ les contraintes techniques du jeu et     │
│ les contrôles accessibles à l’utilisateur│
└────────────────────────────────────────┘*/

/***************************************************
 * Module de gestion des niveaux et configuration UI.
 ***************************************************/
public class LevelModule
{
    private final GameFacadeService game;
    private final HudManager hud;

    /************************************************************************
     * Initialise le module avec ses dépendances.
     *
     * @param game service façade exposant l’état et la configuration du jeu
     * @param hud gestionnaire des composants HUD (contrôles UI)
     ************************************************************************/
    public LevelModule(GameFacadeService game, HudManager hud) 
    {
        this.game = game;
        this.hud = hud;
    }

    /***************************************************************************
     * Configure la disponibilité du sélecteur de thème selon le type de cartes.
     ***************************************************************************/
    public void configureThemeAvailability() 
    {
        if (game.getType() == CardType.IKONLI ||
            game.getType() == CardType.FONT_AWESOME ||
            game.getType() == CardType.PNG) 
        {
            game.setTheme(IconTheme.ALL);
            hud.getThemeComboBox().setDisable(true);
        } 
        else {hud.getThemeComboBox().setDisable(false);}
    }
}