package com.memory.animation;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/*┌────────────────────────────────────────┐
│       AnimationManager                  │
│----------------------------------------│
│ Façade centralisant les animations UI  │
│ du jeu (feedback joueur, transitions,  │
│ score, états critiques, fin de partie).│
└────────────────────────────────────────┘*/

/*****************************************************************************
 * Gestionnaire d’animations JavaFX.
 * 
 * Coordonne et délègue les effets visuels aux composants spécialisés afin de
 * garantir cohérence, réutilisabilité et isolation UI / logique métier.
 *****************************************************************************/
public class AnimationManager 
{
    private final NiceFX vfx;

    /******************************************************
     * Crée un gestionnaire d’animations.
     *
     * @param vfx moteur d’effets visuels (non null)
     * @throws NullPointerException si {@code vfx} est null
     ********************************************************/
    public AnimationManager(NiceFX vfx) {this.vfx = vfx;}

    /*******************************************************
     * Anime le retournement d’une carte avec effet visuel.
     *
     * @param btn bouton représentant la carte
     * @param icon contenu graphique associé
     ********************************************************/
    public void flip(Button btn, Node icon) 
    {
        FlipAnimation.flip3D(btn, icon);
        JuiceFX.pop(btn);
    }

    /**********************************************************
     * Joue les effets associés à une correspondance réussie.
     *
     * @param btn carte validée
     * @param score label de score
     * @param gain points gagnés
     * @param uiHidden indique si l’UI est masquée
     * @param fxLayer couche FX pour effets flottants
     **********************************************************/
    public void match(Button btn, Label score, int gain, boolean uiHidden, StackPane fxLayer) 
    {
        JuiceFX.glow(btn);
        vfx.playMatchEffect(btn);
        JuiceFX.bounce(score);
        if (gain > 0) 
        {
            if (uiHidden) vfx.playFloatingBonus(fxLayer, score, gain);
            else vfx.playBonusEffect(score, gain);
        }
    }

    /***********************************
     * Déclenche une animation d’erreur.
     *
     * @param container nœud cible
     *************************************/
    public void error(Node container) {JuiceFX.shake(container);}

    /***************************************
     * Lance une transition de niveau.
     *
     * @param container conteneur cible
     * @param title texte affiché
     * @param cb callback de fin (nullable)
     ***************************************/
    public void playLevelTransition(Pane container, String title, Runnable cb) 
    {vfx.playLevelTransition(container, title, cb);}

    /***************************************************
     * Lance une transition de niveau ou de palier.
     *
     * @param container conteneur cible
     * @param text texte affiché
     * @param onFinish callback de fin (nullable)
     * @param tierChanged true si transition de palier
     ***************************************************/
    public void playLevelTransition(StackPane container, String text, Runnable onFinish, boolean tierChanged) 
    {
        if (tierChanged) {vfx.playTierTransition(container, text, onFinish);} 
        else {vfx.playLevelTransition(container, text, onFinish);}
    }

    /***************************************
     * Lance une transition de palier.
     *
     * @param container conteneur cible
     * @param title texte affiché
     * @param cb callback de fin (nullable)
     ***************************************/
    public void playTierTransition(StackPane container, String title, Runnable cb) 
    {vfx.playTierTransition(container, title, cb);}

    /****************************************
     * Joue l’animation de victoire.
     *
     * @param container conteneur cible
     * @param text message affiché
     * @param cb callback de fin (nullable)
     ****************************************/
    public void playWin(StackPane container, String text, Runnable cb) 
    {vfx.playWin(text, container, cb);}

    /******************************************
     * Affiche ou masque l’UI avec animation.
     *
     * @param visible état cible
     * @param top barre supérieure
     * @param bot barre inférieure
     ******************************************/
    public void setUIVisible(boolean visible, HBox top, HBox bot)
    {
        if (top.getTranslateY() != 0 && !visible) return;
        slideNode(top, visible, true);
        slideNode(bot, visible, false);
    }

    /****************************************
     * Anime le slide vertical d’un nœud.
     *
     * @param node nœud cible
     * @param show afficher ou masquer
     * @param isTop position haute
     ***************************************/
    private void slideNode(Node node, boolean show, boolean isTop)
    {
        double distance = node.getBoundsInParent().getHeight();
        if (distance <= 0) {distance = node.prefHeight(-1);}

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), node);

        if (show)
        {
            node.setOpacity(1);
            node.setMouseTransparent(false);
            applyBlur(node, true);
            tt.setToY(0);
            node.setTranslateY(0);
            tt.setOnFinished(e -> applyBlur(node, false));
        }
        else
        {
            applyBlur(node, true);
            tt.setToY(isTop ? -distance : distance);
            tt.setOnFinished(e -> {
                node.setOpacity(0);
                node.setMouseTransparent(true);
                applyBlur(node, false);
            });
        }

        tt.setInterpolator(Interpolator.EASE_BOTH); 
        tt.play();
    }

    /************************************
     * Active ou désactive un flou.
     *
     * @param node nœud cible
     * @param enable true pour activer
     ************************************/
    private void applyBlur(Node node, boolean enable)
    {
        if (enable){node.setEffect(new GaussianBlur(10));}
        else{node.setEffect(null);}
    }

    /********************************************
     * Affiche un indice temporaire avec fondu.
     *
     * @param hintLabel label de l’indice
     *******************************************/
    public void showHint(Label hintLabel)
    {
        if (hintLabel == null || hintLabel.getScene() == null) return;

        hintLabel.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), hintLabel);
        fadeIn.setToValue(1);

        PauseTransition wait = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), hintLabel);
        fadeOut.setToValue(0);

        new SequentialTransition(fadeIn, wait, fadeOut).play();
    }

    /***********************************
     * Joue un effet de danger.
     *
     * @param fxPane conteneur cible
     ************************************/
    public void danger(Pane fxPane) {vfx.playDangerEffect(fxPane);}

    /***********************************
     * Joue un effet de danger.
     *
     * @param fxPane conteneur cible
     ************************************/
    public void playDangerEffect(Pane fxPane) {vfx.playDangerEffect(fxPane);}

    /**********************************
     * Joue une alerte de timer.
     *
     * @param timerLabel label du timer
     ***********************************/
    public void playTimerAlert(Label timerLabel){vfx.playTimerAlert(timerLabel);}

    /************************************
     * Alias d’alerte critique.
     *
     * @param timerLabel label du timer
     ***********************************/
    public void timeout(Label timerLabel) {vfx.playTimerAlert(timerLabel);}

    /*******************************************
     * Anime le retour (flip back) d’une carte.
     *
     * @param btn carte
     * @param icon contenu graphique
     ******************************************/
    public void flipBack(Button btn, Node icon) 
    {
        FlipAnimation.flip3D(btn, icon);
        JuiceFX.pop(btn);
    }

    /***********************************
     * Met en évidence plusieurs cartes.
     *
     * @param cards liste des cartes
     * @param onComplete callback 
     ***********************************/
    public void playMatchHighlight(List<Button> cards, Runnable onComplete) 
    {
        vfx.playMatchHighlight(cards);
        onComplete.run(); 
    }

    /*********************************
     * Supprime la mise en évidence.
     *
     * @param cards liste des cartes
     *********************************/
    public void clearMatchHighlight(List<Button> cards) {vfx.clearMatchHighlight(cards);}

    /*********************************************
     * Stop les animations de la mise en évidence.
     *********************************************/
    public void stopMatchHighlight(){vfx.stopMatchHighlight();}

    /***************************
     * Effet combo faible.
     *
     * @param fxLayer couche FX
     ***************************/
    public void comboSmall(StackPane fxLayer) {vfx.playComboBurst(fxLayer, 1);}

    /***************************
     * Effet combo moyen.
     *
     * @param fxLayer couche FX
     ***************************/
    public void comboMedium(StackPane fxLayer) {vfx.playComboBurst(fxLayer, 2);}

    /*****************************
     * Effet combo élevé.
     *
     * @param fxLayer couche FX
     *****************************/
    public void comboUltra(StackPane fxLayer) {vfx.playComboBurst(fxLayer, 3);}

    /***************************
     * Explosion de victoire.
     *
     * @param fxLayer couche FX
     ***************************/
    public void winExplosion(StackPane fxLayer) {vfx.playWinExplosion(fxLayer);}

    /**************************************
     * Fondu de fin de partie.
     *
     * @param fxLayer couche FX
     * @param cb callback de fin (nullable)
     **************************************/
    public void playGameOverFade(StackPane fxLayer, Runnable cb) {vfx.playGameOverFade(fxLayer, cb);}

    /***************************************
     * Joue les effets de fin de partie.
     *
     * @param fxLayer couche FX
     * @param cb callback de fin (nullable)
     ***************************************/
    public void playEndGameFx(StackPane fxLayer, Runnable cb) 
    {
        vfx.playGameOverFade(fxLayer, cb);
        vfx.playScreenPulse(fxLayer);
    }

    /****************************
     * Fait apparaître la grille.
     *
     * @param grid grille cible
     *****************************/
    public void fadeInGrid(GridPane grid) {vfx.fadeInGrid(grid);}

    /*************************************
     * Joue l’introduction d’un niveau.
     *
     * @param fxLayer couche FX
     * @param level niveau
     * @param cb callback fin (nullable)
     *************************************/
    public void playLevelIntro(StackPane fxLayer, int level, Runnable cb) 
    {vfx.playLevelIntro(fxLayer, level, cb);}

    /******************************
     * Secoue un nœud.
     *
     * @param node nœud cible
     * @param amplitude amplitude
     * @param durationMillis durée
     *******************************/
    public void shakeNode(Node node, double amplitude, double durationMillis)
    {vfx.shakeNode(node, amplitude, durationMillis);}

    /***********************************
     * Lance animation de défaite.
     *
     * @param container conteneur cible
     * @param callback callback fin
     ***********************************/
    public void playLoose(StackPane container, Runnable callback)
    {vfx.playLoose(container, callback);}

    /***********************************
     * Supprime l’overlay de game over.
     *
     * @param fxLayer couche FX
     ***********************************/
    public void clearGameOverFade(StackPane fxLayer) 
    {fxLayer.getChildren().removeIf(n -> "gameover-overlay".equals(n.getId()));}
}