package com.memory.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

/*┌────────────────────────────────────────┐
 │           Player                       │
 │----------------------------------------│
 │  Représente un joueur,                 │
 │  avec son nom, son score, et           │
 │  son icône de profil. Fournit aussi    │
 │  un accès singleton pour le joueur     │
 │  courant et un pool d’icônes.          │
 └────────────────────────────────────────┘*/

/*************************************
 * Classe représentant un joueur .
 *************************************/
public class Player 
{
    private String name;
    private int score;
    private FontAwesomeIcon profileIcon;
    private static final List<FontAwesomeIcon> ICON_POOL =
            Arrays.stream(FontAwesomeIcon.values())
                  // optionnel : filtrer certaines icônes si besoin
                  //.filter(icon -> !icon.name().startsWith("FILE"))
                  .filter(icon -> !icon.name().contains("ALT"))
                  .filter(icon -> !icon.name().contains("OUTLINE"))
                  .collect(Collectors.toList());

    private static final Player instance = new Player();

    /*******************************************
     * Constructeur privé pour le singleton.
     * Initialise l’icône de profil par défaut.
     ******************************************/
    private Player() {profileIcon = FontAwesomeIcon.USER;}

    /***************************************************
     * Retourne l’instance singleton du joueur courant.
     *
     * @return instance unique de Player
     ***************************************************/
    public static Player getInstance() {return instance;}

    /****************************************************
     * Crée un nouveau joueur avec un nom et une icône.
     *
     * @param name Nom du joueur
     * @param icon Icône de profil du joueur
     ****************************************************/
    public Player(String name, FontAwesomeIcon icon) 
    {
        this.name = name;
        this.profileIcon = icon;
        this.score = 0;
    }

    /******************************
     * Retourne le nom du joueur.
     *
     * @return Nom du joueur
     *****************************/
    public String getName() {return name;}

    /***************************************
     * Retourne l’icône de profil du joueur.
     *
     * @return Icône FontAwesome du joueur
     ***************************************/
    public FontAwesomeIcon getProfileIcon() {return profileIcon;}

    /**************************************
     * Retourne le score actuel du joueur.
     *
     * @return score du joueur
     **************************************/
    public int getScore() {return score;}

    /***************************************
     * Ajoute des points au score du joueur.
     *
     * @param s Nombre de points à ajouter
     ***************************************/
    public void addScore(int s) {score += s;}

    /******************************
     * Définit le score du joueur.
     *
     * @param score Nouveau score
     *****************************/
    public void setScore(int score) {this.score = score;}

    /*****************************************
     * Réinitialise le score du joueur à zéro.
     *****************************************/
    public void resetScore() {this.score = 0;}

    /*******************************************************************
     * Retourne la liste des icônes disponibles, mélangée aléatoirement.
     *
     * @return Liste d’icônes FontAwesome
     *******************************************************************/
    public List<FontAwesomeIcon> getIconPool() 
    {
        Collections.shuffle(ICON_POOL, new Random());
        return ICON_POOL;
    }
}