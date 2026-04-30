package com.memory.animation;

import java.util.List;

import com.memory.ui.UIManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/*┌─────────────────────────────────────┐
│            FxCoordinator              │
│--------------------------------------│
│ Orchestrateur des effets visuels liés│
│ aux événements de gameplay.          │
└──────────────────────────────────────┘*/

/*************************************************************************
 * Coordinateur des effets visuels déclenchés par le gameplay.
 * 
 * Fait le lien entre l’état du jeu (via UIManager) et les animations
 * fournies par {@link AnimationManager}.
 *************************************************************************/
public class FxCoordinator 
{
    private final AnimationManager fx;

    /*********************************************************
     * Initialise le coordinateur.
     *
     * @param fx gestionnaire d’animations (non null)
     * @param audio gestionnaire d’effets sonores (non null)
     * @throws NullPointerException si {@code fx} est null
     *********************************************************/
    public FxCoordinator(AnimationManager fx) 
    {
        this.fx = fx;
    }

    /*****************************************************************
     * Déclenche les effets liés à une correspondance de cartes.
     *
     * @param ui interface utilisateur
     * @param firstIndex index première carte
     * @param secondIndex index seconde carte
     * @param gain points gagnés (première carte uniquement)
     *****************************************************************/
    public void onMatch(UIManager ui, int firstIndex, int secondIndex, int gain) 
    {
        Button[] buttons = ui.getButtons();
        fx.match
        (
            buttons[firstIndex],
            ui.getHud().getScore(),
            gain,
            ui.getHud().isHidden(),
            ui.getFxLayer()
        );
        fx.match
        (
            buttons[secondIndex],
            ui.getHud().getScore(),
            0,
            ui.getHud().isHidden(),
            ui.getFxLayer()
        );
        // audio.play(AudioService.Sound.MATCH); 
    }

     /***************************************************
     * Déclenche les effets en cas d'erreur.
     *
     * @param ui interface utilisateur
     ***************************************************/
    public void onError(UIManager ui) 
    {
        fx.error(ui.getGameContainer());
        // audio.play(AudioService.Sound.ERROR);
    }

    /***************************************************
     * Déclenche les effets de combo selon le niveau.
     *
     * @param ui interface utilisateur
     * @param comboLevel niveau de combo (>=2)
     ***************************************************/
    public void onCombo(UIManager ui, int comboLevel) 
    {
        if (comboLevel < 2) return;
        if (comboLevel == 2) {fx.comboSmall(ui.getFxLayer());}
        else if (comboLevel == 3) {fx.comboMedium(ui.getFxLayer());}
        else {fx.comboUltra(ui.getFxLayer());}

        // audio.play(AudioService.Sound.BONUS);
    }

    /**********************************************
     * Déclenche les effets de fin de partie.
     *
     * @param ui interface utilisateur
     * @param win true si victoire
     **********************************************/
    public void onGameEnd(UIManager ui, boolean win) 
    {
        StackPane layer = ui.getFxLayer();
        if (win) {fx.winExplosion(layer);}
    }

    /************************************************
     * Lance une transition de niveau standard.
     *
     * @param ui interface utilisateur
     * @param title texte affiché
     * @param cb callback de fin (nullable)
     ************************************************/
    public void onLevelTransition(UIManager ui, String title, Runnable cb) 
    {
        fx.playLevelTransition
        (
            ui.getFxLayer(),
            title,
            cb,
            false
        );
    }

    /****************************************************
     * Lance une transition de changement de palier.
     *
     * @param ui interface utilisateur
     * @param title texte affiché
     * @param cb callback de fin (nullable)
     ****************************************************/
    public void onTierTransition(UIManager ui, String title, Runnable cb) 
    {
        fx.playLevelTransition
        (
            ui.getFxLayer(),
            title,
            cb,
            true
        );
    }

    /*********************************************************
     * Lance une transition générique (niveau ou palier).
     *
     * @param ui interface utilisateur
     * @param title texte affiché
     * @param tierChanged true si changement de palier
     * @param cb callback de fin (nullable)
     ********************************************************/
    public void onLevelChange(UIManager ui,
                          String title,
                          boolean tierChanged,
                          Runnable cb) 
    {
        fx.playLevelTransition
        (
            ui.getFxLayer(),
            title,
            () -> {if (cb != null) cb.run();},
            tierChanged
        );
    }

    /************************************************************
     * Déclenche la fin de partie avec callback optionnel.
     *
     * @param ui interface utilisateur
     * @param victory true si victoire
     * @param onFinished action exécutée après effets (nullable)
     ************************************************************/
    public void onGameEnd(UIManager ui, boolean victory, Runnable onFinished) 
    {
        onGameEnd(ui, victory);
        if (onFinished != null) {onFinished.run();}
    }

    /***********************************
     * Joue un effet visuel de danger.
     *
     * @param fxPane conteneur cible
     ***********************************/
    public void danger(Pane fxPane) {fx.playDangerEffect(fxPane);/*audio.play(AudioService.Sound.TICK);*/}

    /*******************************************
     * Déclenche une alerte de timer critique.
     *
     * @param timerLabel label du timer
     *******************************************/
    public void timeout(Label timerLabel) {fx.playTimerAlert(timerLabel);}

    /**********************************************
     * Applique un effet de secousse.
     *
     * @param fxPane conteneur cible (non utilisé)
     * @param amplitude amplitude
     * @param durationMillis durée
     **********************************************/
    public void shakeNode(Pane fxPane, double amplitude, double durationMillis)
    {fx.shakeNode(null, amplitude, durationMillis);}

    /********************************************
     * Lance un fondu de défaite.
     *
     * @param fxLayer couche FX
     * @param cb callback de fin (nullable)
     ********************************************/
    public void loseFade(StackPane fxLayer, Runnable cb)
    {fx.playGameOverFade(fxLayer, cb);}

    /********************************************
     * Supprime l’overlay de fin de partie.
     *
     * @param fxLayer couche FX
     ******************************************/
    public void clearGameOverFade(StackPane fxLayer) 
    {
        fxLayer.getChildren().removeIf
        (n -> "gameover-overlay".equals(n.getId()));
    }

    /*********************************
     * Supprime la mise en évidence.
     *
     * @param cards liste des cartes
     *********************************/
    public void clearMatchHighlight(List<Button> cards) {fx.clearMatchHighlight(cards);}

    public AnimationManager getFx() {return fx;}


}