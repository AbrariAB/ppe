package com.memory;

import com.memory.app.MainApp;

/*┌───────────────────────────────────────────┐
 │           Memory                           │
 │--------------------------------------------│
 │  Point d’entrée principal de l’application │
 │  Lance la classe {@link MainApp}          │
 │                                           │
 │            @author AB                     │
 │                                           │
 └───────────────────────────────────────────┘*/

/*********************************************************************************
 * Classe de lancement de l’application {@link com.memory.app.MainApp}.
 * Contient uniquement la méthode main qui délègue l’exécution à {@link MainApp}.
 *********************************************************************************/
public class Launcher 
{
    /************************************************************
     * Point d’entrée principal de l’application.
     * Délègue immédiatement à {@link MainApp#main(String[])}.
     *
     * @param args 
     ************************************************************/
    public static void main(String[] args) {MainApp.main(args);}
}
