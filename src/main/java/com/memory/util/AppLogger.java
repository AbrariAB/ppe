package com.memory.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*┌──────────────────────────────────────┐
│           AppLogger                    │
│----------------------------------------│
│  - LOG : informations générales        │
│  - WARN : avertissements               │
│  - ERROR : erreurs non critiques       │
│  - CRITICAL : erreurs graves           │
└────────────────────────────────────────┘*/

/********************
 * Logger utilitaire .
 *******************/
public final class AppLogger
{
    private static boolean ENABLED = true;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private AppLogger() {}

    /**************************************
     * Log standard (information).
     *
     * @param message message à afficher
     *************************************/
    public static void log(String message) {print("LOG", message, BLUE);}

    public static void log(String context, String message)
    {print("LOG", "[" + context + "] " + message, CYAN);}

    /************************************
     * Log d'avertissement.
     *
     * @param message message à afficher
     ************************************/
    public static void warn(String message) {print("WARN", message, YELLOW);}

    /*************************************
     * Log d'erreur non critique.
     *
     * @param message message à afficher
     *************************************/
    public static void error(String message) {print("ERROR", message, RED);}

    /*************************************
     * Log critique (erreur grave).
     *
     * @param message message à afficher
     *************************************/
    public static void critical(String message) {print("CRITICAL", message, PURPLE);}

    /*************************************************
     * Active ou désactive complètement les logs.
     *
     * @param enabled true pour activer, false sinon
     *************************************************/
    public static void setEnabled(boolean enabled) {ENABLED = enabled;}

     /*************************************************
     * Affiche les logs.
     * @param level 
     * @param message
     * @param color
     *************************************************/
    private static void print(String level, String message, String color)
    {
        if (!ENABLED) return;
        String time = LocalDateTime.now().format(FORMAT);
        String thread = Thread.currentThread().getName();
        System.out.println
        (
            color +
            "[" + time + "]" +
            " [" + level + "]" +
            " [" + thread + "] " +
            message +
            RESET
        );
    }
}
