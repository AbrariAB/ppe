package com.memory.app;

import java.util.function.Consumer;

import com.memory.animation.AnimationManager;
import com.memory.animation.FxCoordinator;
import com.memory.model.Card;
import com.memory.service.GameFacadeService;
import com.memory.ui.CardViewFactory;
import com.memory.ui.HudManager;
import com.memory.ui.UIManager;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

/*┌────────────────────────────────────────┐
│              TurnManager               │
│----------------------------------------│
│ - Capture les sélections joueur        │
│ - Déclenche les animations (flip, fx)  │
│ - Coordonne la résolution d’un tour    │
│ - Synchronise UI et logique métier     │
└────────────────────────────────────────┘*/

/*******************************
 * Contrôleur de tour côté UI .
 ******************************/
public class TurnManager 
{
    private final GameFacadeService game;
    private final AnimationManager fx;
    private final FxCoordinator fxCore;
    private final HandleTurn handler;
    private final HudManager hud;
    private boolean locked = false;
    private int firstIndex = -1;
    private int secondIndex = -1;
    private final PauseTransition pause = new PauseTransition(Duration.millis(600));

    /**************************************************
     * Initialise le gestionnaire de tour UI.
     *
     * @param game façade métier du jeu
     * @param hud gestionnaire HUD
     * @param fx gestionnaire d’animations
     * @param fxCore coordinateur des effets visuels
     **************************************************/
    public TurnManager(GameFacadeService game,
                       HudManager hud,
                       AnimationManager fx,
                       FxCoordinator fxCore) 
    {
        this.game = game;
        this.fx = fx;
        this.hud = hud;
        this.fxCore = fxCore;
        this.handler = new HandleTurn(game);
    }

    /***********************************************
     * Traite une sélection de carte utilisateur.
     *
     * @param index index de la carte sélectionnée
     * @param ui gestionnaire UI
     * @return résultat intermédiaire du tour
     ***********************************************/
    public TurnResult handleSelection(int index, UIManager ui) 
    {
        if (locked) return TurnResult.continueTurn();
        Card card = ui.getModel().getCards().get(index);
        Button btn = ui.getButtons()[index];
        Node front = CardViewFactory.createFront(card, btn.getPrefWidth());
        fx.flip(btn, front);
        btn.setDisable(true);
        if (firstIndex == -1) 
        {
            firstIndex = index;
            return handler.select(index);
        }
        secondIndex = index;
        handler.select(index);
        return TurnResult.resolved(false, 0, 0);
    }

    /***************************************************************
     * Résout un tour après sélection de deux cartes.
     *
     * @param ui gestionnaire UI
     * @param callback callback recevant le résultat final
     **************************************************************/
    public void resolve(UIManager ui, Consumer<TurnResult> callback) 
    {
        if (firstIndex == -1 || secondIndex == -1) return;
        locked = true;
        ui.setInputEnabled(false);
        pause.setOnFinished(e -> {
            TurnResult result = handler.resolve();
            applyVisualEffects(ui, result);
            if (assertGameIsOver(ui, callback)) return;
            finishTurn(ui, callback, result);
        });
        pause.play();
    }

    /*******************************************************
     * Applique les effets visuels liés au résultat du tour.
     ********************************************************/
    private void applyVisualEffects(UIManager ui, TurnResult result) 
    {
        if (result.isMatch()) 
        {
            fxCore.onMatch(ui, firstIndex, secondIndex, result.getGain());
            fxCore.onCombo(ui, result.getCombo());
        } 
        else { fxCore.onError(ui);}
        hud.updateProgress(game.getProgress());
        if (result.getType() == TurnResult.Type.GAME_ENDED) {hud.updateProgress(1.0);}
        ui.updateUI();
    }

    /********************************************************************
     * Finalise le tour en réinitialisant l’état et appelant le callback.
     ********************************************************************/
    private void finishTurn(UIManager ui,
                            Consumer<TurnResult> callback,
                            TurnResult result) 
    {
        reset();
        locked = false;
        ui.setInputEnabled(true);
        if (callback != null) {callback.accept(result);}
    }

    /********************************************************
     * Vérifie si la partie est terminée (toutes conditions).
     *
     * @return true si fin de partie
     *******************************************************/
    private boolean isGameOver() {return game.isDefeat() || game.isTimeUp();}

    /***************************************
     * Réinitialise l’état interne du tour.
     **************************************/
    public void reset() 
    {
        firstIndex = -1;
        secondIndex = -1;
    }

    /****************************************************
     * Annule un tour en cours (ex : changement d’écran).
     ****************************************************/
    public void cancel() 
    {
        pause.stop();
        reset();
        locked = false;
    }

    private boolean assertGameIsOver(UIManager ui,Consumer<TurnResult> callback)
    {
        if (isGameOver()) 
        {finishTurn(ui, callback, TurnResult.gameEnded(game.isEndGame(), game.isHardcore()));return true;}
        return false;
    } 
}

