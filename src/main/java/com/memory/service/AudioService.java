package com.memory.service;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*┌────────────────────────────────────────┐
 │           AudioService                 │
 │----------------------------------------│
 │  Service singleton pour la gestion     │
 │  des sons du jeu. Permet de jouer,     │
 │  précharger, couper le son, ajuster    │
 │  le volume et stopper tous les sons.  │
 └────────────────────────────────────────┘*/

/****************************************************
 * Service de gestion audio pour le jeu Memory.
 * Singleton thread-safe utilisant JavaFX AudioClip.
 ***************************************************/
public final class AudioService 
{
    private static volatile AudioService instance;
    private final Map<String, AudioClip> sounds = new ConcurrentHashMap<>();
    private double masterVolume = 1.0;
    private boolean muted = false;

    /**************************************
     * Constructeur privé pour singleton.
     * Précharge les sons du jeu.
     *************************************/
    private AudioService() {preload();}

    /****************************************
     * Retourne l'instance unique du service.
     *
     * @return instance unique d'AudioService
     ****************************************/
    public static AudioService getInstance()
    {
        if (instance == null) 
        {
            synchronized (AudioService.class) 
            {if (instance == null) {instance = new AudioService();}}
        }
        return instance;
    }

    /*******************************
     * Enumération des sons du jeu.
     *******************************/
    public enum Sound {FLIP, MATCH, ERROR, WIN, TICK, BONUS}

    /**********************************************************
     * Joue un son identifié par l'énumération {@link Sound}.
     *
     * @param sound son à jouer
     *********************************************************/
    public void play(Sound sound) {play(sound.name().toLowerCase());}

    /************************************************
     * Précharge tous les fichiers audio dans la map.
     ************************************************/
    private void preload() 
    {
        load("flip", "/sounds/flip.wav");
        load("match", "/sounds/match.wav");
        load("error", "/sounds/error.wav");
        load("win", "/sounds/win.wav");
        load("tick", "/sounds/tick.wav");
        load("bonus", "/sounds/bonus.wav");
    }

    /***********************************************************
     * Charge un son depuis une ressource et l'ajoute à la map.
     *
     * @param key  clé d'identification du son
     * @param path chemin de la ressource audio
     ***********************************************************/
    private void load(String key, String path) 
    {
        try 
        {
            URL resource = getClass().getResource(path);
            if (resource == null) 
            {
                System.err.println("[AudioService] Fichier introuvable : " + path);
                return;
            }
            AudioClip clip = new AudioClip(resource.toExternalForm());
            sounds.put(key, clip);
        } 
        catch (Exception e) 
        {
            System.err.println("[AudioService] Erreur chargement : " + path);
            e.printStackTrace();
        }
    }

    /******************************************************************************
     * Joue un son identifié par sa clé.
     * <p>
     * Si le son n'existe pas ou que le service est en mode muet, rien n'est joué.
     * Le son est joué de manière thread-safe sur le thread JavaFX.
     *
     * @param key clé du son à jouer
     ******************************************************************************/
    public void play(String key) 
    {
        if (muted) return;
        AudioClip clip = sounds.get(key);
        if (clip == null) return;
        Runnable playTask = () -> {
            try {clip.play(masterVolume);} 
            catch (Exception e) {System.err.println("[Audio ERROR] " + key);}
        };
        if (Platform.isFxApplicationThread()) {playTask.run();} 
        else {Platform.runLater(playTask);}
    }

    /******************************************
     * Définit le volume global.
     *
     * @param volume volume entre 0.0 et 1.0
     ******************************************/
    public void setVolume(double volume) {this.masterVolume = Math.max(0, Math.min(1, volume));}

    /*******************************************
     * Retourne le volume global.
     *
     * @return volume global entre 0.0 et 1.0
     *******************************************/
    public double getVolume() {return masterVolume;}

    /**********************************************************
     * Active ou désactive le mode muet.
     *
     * @param value true pour couper le son, false pour activer
     **********************************************************/
    public void mute(boolean value) {this.muted = value;}

    /*********************************************
     * Indique si le son est actuellement coupé.
     *
     * @return true si muet, false sinon
     *********************************************/
    public boolean isMuted() {return muted;}

    /*******************************************
     * Arrête tous les sons en cours de lecture.
     *******************************************/
    public void stopAll() {sounds.values().forEach(AudioClip::stop);}

    /***********************************************
     * Recharge tous les sons depuis les ressources.
     ***********************************************/
    public void reload() 
    {
        sounds.clear();
        preload();
    }
}