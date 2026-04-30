package com.memory.app;

import com.memory.model.Player;
import com.memory.service.GameFacadeService;
import com.memory.ui.UIContext;

/*┌─────────────────────────────────────┐
│              GameSession              │
│---------------------------------------│
│  - le joueur courant                  │
│  - le moteur de jeu (GameFacade)      │
│  - le contexte UI                     │
│  - l’état de difficulté (hardcore)    │
│  - le suivi du temps de session       │
└───────────────────────────────────────┘*/

/***************************************************************
 * Représente une session de jeu.
 ***************************************************************/
public class GameSession
{
    private final Player player;
    private final GameFacadeService game;
    private final UIContext uiContext;
    private boolean hardcore;
    private long sessionStartTime;
    private long accumulatedTime;
    private boolean running;

    /****************************************************************
     * Crée une nouvelle session de jeu.
     *
     * @param player joueur associé (non null)
     * @param uiContext contexte UI
     * @throws IllegalArgumentException si {@code player} est null
     ****************************************************************/
    public GameSession(Player player, UIContext uiContext)
    {
        if (player == null) {throw new IllegalArgumentException("Player cannot be null");}
        this.player = player;
        this.game = new GameFacadeService();
        this.uiContext = uiContext;
    }

    /*****************************************
     * Démarre le suivi du temps de session.
     *****************************************/
    public void startSession() 
    {
        if (!running) 
        {
            sessionStartTime = System.currentTimeMillis();
            running = true;
        }
    }

    /************************************
     * Met en pause le suivi du temps.
     ************************************/
    public void pauseSession() 
    {
        if (running) 
        {
            accumulatedTime += (System.currentTimeMillis() - sessionStartTime) / 1000;
            running = false;
        }
    }

    /****************************
     * Reprend le suivi du temps.
     ***************************/
    public void resumeSession() 
    {
        if (!running) 
        {
            sessionStartTime = System.currentTimeMillis();
            running = true;
        }
    }

    /***************************************
     * Termine la session temporelle.
     **************************************/
    public void endSession() {pauseSession();}

    /**************************************
     * Retourne le temps total de session.
     *
     * @return durée en secondes
     *************************************/
    public int getSessionTime() 
    {
        if (running) 
        {
            long current = (System.currentTimeMillis() - sessionStartTime) / 1000;
            return (int) (accumulatedTime + current);
        }
        return (int) accumulatedTime;
    }

    /********************************
     * Retourne le joueur courant.
     *
     * @return joueur (non null)
     *******************************/
    public Player getPlayer() {return player;}

    /************************************************
     * Accès au moteur de jeu.
     *
     * @return instance de {@link GameFacadeService}
     ************************************************/
    public GameFacadeService getGame() {return game;}

    /*****************************************
     * Indique si le mode hardcore est actif.
     *
     * @return true si actif
     *****************************************/
    public boolean isHardcore() {return hardcore;}

    /********************************
     * Définit le mode hardcore.
     *
     * @param hardcore état souhaité
     ********************************/
    public void setHardcore(boolean hardcore) {this.hardcore = hardcore;}

    /*************************************
     * Retourne le contexte UI associé.
     *
     * @return contexte UI
     ************************************/
    public UIContext getUiContext() {return uiContext;}
}