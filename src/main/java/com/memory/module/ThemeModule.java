package com.memory.module;

import com.memory.app.ThemeChanger;

/*┌────────────────────────────────────────┐
│            ThemeModule                  │
│-----------------------------------------│
│ Isoler la logique de thème              │
│ et garantir une application cohérente   │
│ des changements visuels.                │
└────────────────────────────────────────┘*/

/********************************
 * Module de gestion des thèmes.
 *******************************/
public class ThemeModule 
{
    private final ThemeChanger changer;

    /*******************************************************************
     * Initialise le module avec le gestionnaire de thèmes.
     *
     * @param changer composant responsable de l’application des thèmes
     *******************************************************************/
    public ThemeModule(ThemeChanger changer) {this.changer = changer;}

    /***********************************************************************
     * Applique un nouveau thème visuel.
     *
     * @param theme identifiant du thème à appliquer
     * @param restart callback exécuté après le changement (ex : reload UI)
     ***********************************************************************/
    public void changeTheme(String theme, Runnable restart) {changer.changeTheme(theme, restart);}

    /************************************************************
     * Change le provider de ressources.
     *
     * @param provider identifiant du provider à utiliser
     * @param restart callback exécuté après le changement
     ************************************************************/
    public void changeProvider(String provider, Runnable restart) {changer.changeProvider(provider, restart);}
}