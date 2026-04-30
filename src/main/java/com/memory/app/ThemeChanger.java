package com.memory.app;

import java.util.Map;

import com.memory.model.*;

/*┌────────────────────────────────────────┐
│            ThemeController             │
│----------------------------------------│
│ - Conversion nom UI → modèle métier    │
│ - Changement de thème et provider      │
└────────────────────────────────────────┘*/

/******************************************
 *  Gère les changements de thème du jeu.
 ******************************************/
public class ThemeChanger 
{
    private static final long COOLDOWN_MS = 300;
    private final ChangeTheme changeThemeUseCase;
    private final Map<String, IconTheme> themeMap;
    private final Map<String, CardType> providerMap;
    private long lastChange = 0;
    
    /****************************************************************
     * Initialise le contrôleur de thèmes.
     *
     * @param changeThemeUseCase cas d’usage de changement de thème
     * @param themeMap mapping nom → thème
     * @param providerMap mapping nom → provider
     ****************************************************************/
    public ThemeChanger(ChangeTheme changeThemeUseCase,
                           Map<String, IconTheme> themeMap,
                           Map<String, CardType> providerMap) 
    {
        this.changeThemeUseCase = changeThemeUseCase;
        this.themeMap = themeMap;
        this.providerMap = providerMap;
    }

    /****************************************
     * Change le thème visuel actif.
     *
     * @param themeName nom du thème (UI)
     * @param restart action de redémarrage
     ****************************************/
    public void changeTheme(String themeName, Runnable restart) 
    {
        if (!canChange()) return;
        IconTheme theme = themeMap.getOrDefault(themeName, IconTheme.ALL);
        changeThemeUseCase.changeTheme(theme);
        restart.run();
    }

    /***************************************************
     * Change le provider de cartes (source d’icônes).
     *
     * @param themeName nom du provider (UI)
     * @param restart action de redémarrage
     **************************************************/
    public void changeProvider(String themeName, Runnable restart) 
    {
        if (!canChange()) return;
        CardType type = providerMap.getOrDefault(themeName, CardType.FONT_AWESOME);
        changeThemeUseCase.changeProvider(type);
        if (type == CardType.FONT_AWESOME || type == CardType.IKONLI) 
        {changeThemeUseCase.changeTheme(IconTheme.ALL);} 
        restart.run();
    }

    /****************************************************
     * Vérifie si un changement est autorisé (cooldown).
     *
     * @return true si changement autorisé
     ***************************************************/
    private boolean canChange() 
    {
        long now = System.currentTimeMillis();
        if (now - lastChange < COOLDOWN_MS) return false;
        lastChange = now;
        return true;
    }
}