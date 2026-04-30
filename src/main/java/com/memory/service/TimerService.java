package com.memory.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/*┌───────────────────────────────────────────────────────────┐
│                         TimerService                        │
│-------------------------------------------------------------│
│ - Piloter un compteur temps (tick / pause / resume)         │
│ - Fournir des callbacks métier (tick, time-up)              │
│ - Gérer l’état du timer (running, paused)                   │
│ - Garantir une exécution continue via Timeline              │
└─────────────────────────────────────────────────────────────┘*/

/*******************************************************
 * Service JavaFX responsable de la gestion d’un timer
 * basé sur {@link Timeline}.
 *******************************************************/
public class TimerService 
{
    private Timeline timeline;

    private boolean running = false;
    private boolean paused = false;

    private Runnable onTick;
    private Runnable onTimeUp;

    /****************************************************************
     * Démarre le timer et initialise la boucle de tick (1 seconde).
     * Remplace toute instance précédente du timer.
     ****************************************************************/
    public void start() 
    {
        stop();
        running = true;
        paused = false;
        timeline = new Timeline
        (
            new KeyFrame(Duration.seconds(1), e -> {
                if (!running || paused) return;
                if (onTick != null) {onTick.run();}
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();
    }

    /******************************************************
     * Met le timer en pause sans réinitialiser son état.
     * Aucun tick n’est exécuté pendant la pause.
     ******************************************************/
    public void pause() 
    {
        if (!running) return;
        paused = true;
        if (timeline != null) {timeline.pause();}
    }

    /**********************************************************
     * Reprend l’exécution du timer après une pause.
     * Redémarre automatiquement si le timer n’était pas actif.
     **********************************************************/
    public void resume() 
    {
        if (!running) 
        {
            start();
            return;
        }
        paused = false;
        if (timeline != null) {timeline.play();}
    }

    /**********************************************************
     * Arrête complètement le timer et désactive son exécution.
     * L’état reste conservé jusqu’à reset().
     **********************************************************/
    public void stop() 
    {
        running = false;
        paused = false;
        if (timeline != null) {timeline.stop();}
    }

    /***************************************************************
     * Réinitialise complètement le timer en supprimant la Timeline.
     * Utilisé lors d’un redémarrage de partie.
     ***************************************************************/
    public void reset() 
    {
        stop();
        timeline = null;
    }

    /**************************************************
     * Indique si le timer est actif et non en pause.
     *
     * @return true si en cours d’exécution
     **************************************************/
    public boolean isRunning() {return running && !paused;}

    /**************************************************
     * Indique si le timer est actuellement en pause.
     *
     * @return true si en pause
     **************************************************/
    public boolean isPaused() {return paused;}

    /********************************************************
     * Définit l’action exécutée à chaque tick (1 seconde).
     *
     * @param onTick callback exécuté périodiquement
     *******************************************************/
    public void setOnTick(Runnable onTick) {this.onTick = onTick;}

    /*************************************************************
     * Définit le callback déclenché lorsque le temps est écoulé.
     *
     * @param onTimeUp callback fin de timer
     *************************************************************/
    public void setOnTimeUp(Runnable onTimeUp) {this.onTimeUp = onTimeUp;}

    /**********************************************
     * Retourne le callback de fin de temps.
     *
     * @return runnable associé à la fin du timer
     ***********************************************/
    public Runnable getOnTimeUp() {return onTimeUp;}
}