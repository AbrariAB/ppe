package com.memory.animation;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Circle;

/*┌────────────────────────────────────────┐
 │         NiceFX                         │
 │----------------------------------------│
 │  Fournit un ensemble d'effets visuels  │
 │  JavaFX prêts à l'emploi pour enrichir │
 │  l’expérience utilisateur : feedback,  │
 │  animations de score, transitions,     │
 │  alertes visuelles et effets de fin.   │
└────────────────────────────────────────┘*/

/****************************************************
 * Utilitaire centralisant des effets visuels JavaFX.
 **************************************************/
public class NiceFX
{
    private final List<Animation> activeHighlights = new ArrayList<>();
    private static final PseudoClass HIGHLIGHT = 
    PseudoClass.getPseudoClass("highlight");

    /********************************************************
     * Applique une animation de secousse horizontale.
     *
     * @param node nœud cible
     * @param amplitude amplitude du déplacement en pixels
     * @param durationMillis durée d’une étape (ms)
     ********************************************************/
    public void shakeNode(Node node, double amplitude, double durationMillis) 
    {
        if (node == null) return;
        Duration dur = Duration.millis(durationMillis);
        TranslateTransition t1 = new TranslateTransition(dur, node);
        t1.setByX(amplitude);
        TranslateTransition t2 = new TranslateTransition(dur, node);
        t2.setByX(-amplitude * 2);
        TranslateTransition t3 = new TranslateTransition(dur, node);
        t3.setByX(amplitude);
        TranslateTransition t4 = new TranslateTransition(dur, node);
        t4.setByX(0);
        SequentialTransition seq = new SequentialTransition(t1, t2, t3, t4);
        seq.play();
    }

    /*******************************************************************
     * Joue une animation de gain de score (zoom + fade + déplacement).
     *
     * @param scoreLabel label cible
     * @param points valeur gagnée (doit être > 0)
     *******************************************************************/
    public void playBonusEffect(Label scoreLabel, int points) 
    {
        if (scoreLabel == null || points <= 0) return;
        scoreLabel.setText("+" + points + " pts");
        if (points > 800) scoreLabel.setStyle("-fx-text-fill: gold;");
        else if (points > 400) scoreLabel.setStyle("-fx-text-fill: orange;");
        else scoreLabel.setStyle("-fx-text-fill: white;");
        ScaleTransition up = new ScaleTransition(Duration.millis(120), scoreLabel);
        up.setToX(1.6); up.setToY(1.6);
        ScaleTransition down = new ScaleTransition(Duration.millis(120), scoreLabel);
        down.setToX(1); down.setToY(1);
        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), scoreLabel);
        fade.setFromValue(1); fade.setToValue(0);
        TranslateTransition move = new TranslateTransition(Duration.seconds(1.2), scoreLabel);
        move.setByY(-40);
        ParallelTransition anim = new ParallelTransition
        (
            new SequentialTransition(up, down),
            fade,
            move
        );
        anim.setOnFinished(e -> {
            scoreLabel.setText("");
            scoreLabel.setOpacity(1);
            scoreLabel.setTranslateY(0);
        });
        anim.play();
    }

    /***********************************************
     * Réinitialise l’état visuel du label de score.
     *
     * @param scoreLabel label cible
     ***********************************************/
    public void clearBonusEffect(Label scoreLabel) 
    {
        if (scoreLabel == null) return;
        scoreLabel.setText("");
        scoreLabel.setOpacity(1);
        scoreLabel.setTranslateY(0);
    }

    /**************************************************
     * Applique un effet visuel de validation (match).
     *
     * @param cardButton bouton cible
     **************************************************/
    public void playMatchEffect(Button cardButton) 
    {
        if (cardButton == null) return;
        DropShadow glow = new DropShadow();
        glow.setRadius(25);
        glow.setSpread(0.5);
        glow.setColor(Color.GOLD);
        cardButton.setEffect(glow);
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(250), cardButton);
        scaleUp.setToX(1.3); scaleUp.setToY(1.3);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(250), cardButton);
        scaleDown.setToX(1); scaleDown.setToY(1);
        SequentialTransition seq = new SequentialTransition(scaleUp, scaleDown);
        seq.setOnFinished(e -> cardButton.setEffect(null));
        seq.play();
    }

    /*************************************************
     * Déclenche une alerte visuelle sur un timer.
     *
     * @param timerLabel label du timer
     ************************************************/
    public void playTimerAlert(Label timerLabel) 
    {
        if (timerLabel == null) return;
        TranslateTransition shake = new TranslateTransition(Duration.millis(80), timerLabel);
        shake.setByX(5);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
        ScaleTransition pulse = new ScaleTransition(Duration.millis(400), timerLabel);
        pulse.setFromX(1); pulse.setFromY(1);
        pulse.setToX(1.4); pulse.setToY(1.4);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
        timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    /*******************************************
     * Effet flash rouge.
     *
     * @param fxLayer conteneur cible
     *******************************************/
    public void playDangerEffect(Pane fxLayer)
    {
        if (fxLayer == null) return;
        Pane overlay = new Pane();
        overlay.setMouseTransparent(true);
        overlay.setStyle("-fx-background-color: red;");
        overlay.setOpacity(0);
        overlay.prefWidthProperty().bind(fxLayer.widthProperty());
        overlay.prefHeightProperty().bind(fxLayer.heightProperty());
        fxLayer.getChildren().add(overlay);
        FadeTransition flash = new FadeTransition(Duration.millis(120), overlay);
        flash.setFromValue(0);
        flash.setToValue(0.35);
        flash.setAutoReverse(true);
        flash.setCycleCount(2);
        ParallelTransition anim = new ParallelTransition(flash);
        anim.setOnFinished(e -> fxLayer.getChildren().remove(overlay));
        anim.play();
    }

    /**********************************
     * Réinitialise le style du timer.
     *
     * @param timerLabel label cible
     **********************************/
    public void clearTimerAlert(Label timerLabel) 
    {
        if (timerLabel == null) return;
        timerLabel.setStyle("-fx-text-fill: white;");
    }

    /*************************************************
     * Animation de transition de niveau avec overlay.
     *
     * @param container conteneur parent
     * @param levelText texte affiché
     * @param onFinish callback de fin (nullable)
     ************************************************/
    public void playLevelTransition(Pane container, String levelText, Runnable onFinish)
    {
        if (container == null) return;
        Label label = new Label(levelText);
        label.setStyle
        (
            "-fx-font-family: 'Segoe UI Black', sans-serif; " +
            "-fx-font-size: 64px; " +
            "-fx-text-fill: linear-gradient(from 0% 0% to 100% 100%, #e0e0ff 0%, #a0a0ff 50%, #6060ff 100%); " +
            "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 255, 0.7), 10, 0, 0, 2);"
        );
        Pane overlay = new Pane(label);
        overlay.setStyle
        (
            "-fx-background-color: rgba(10, 10, 30, 0.85); " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 5);"
        );
        overlay.setOpacity(0);
        overlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.getChildren().add(overlay);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), overlay);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        ScaleTransition scalePulse = new ScaleTransition(Duration.millis(600), label);
        scalePulse.setFromX(0.8); scalePulse.setFromY(0.8);
        scalePulse.setToX(1.1); scalePulse.setToY(1.1);
        ScaleTransition scaleSettle = new ScaleTransition(Duration.millis(300), label);
        scaleSettle.setToX(1); scaleSettle.setToY(1);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), overlay);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        SequentialTransition seq = new SequentialTransition
        (
            new ParallelTransition(fadeIn, scalePulse),
            scaleSettle,
            pause,
            fadeOut
        );
        seq.setOnFinished(e -> {
            container.getChildren().remove(overlay);
            if (onFinish != null) onFinish.run();
        });
        seq.play();
    }

    /*****************************************
     * Lance une transition de tier déléguée.
     *
     * @param container conteneur cible
     * @param text texte affiché
     * @param callback callback fin
     ****************************************/
    public void playTierTransition(StackPane container, String text, Runnable callback) 
    {
        TierTransitionFX.play(container, text, callback);
    }

    /*******************************************
     * Lance animation de victoire + explosion.
     *
     * @param text message
     * @param container conteneur cible
     * @param callback callback fin
     *******************************************/
    public void playWin(String text, StackPane container, Runnable callback) 
    {
        HardcoreWinFX.play(text,container, callback);
        Platform.runLater(() -> playWinExplosion(container));
    }

    /***********************************
     * Lance animation de défaite.
     *
     * @param container conteneur cible
     * @param callback callback fin
     ************************************/
    public void playLoose(StackPane container, Runnable callback) 
    {
        Platform.runLater(() -> playGameOverFade(container, callback));
    }

    /****************************************
     * Affiche un bonus flottant indépendant.
     *
     * @param fxLayer couche FX
     * @param scoreLabel référence style
     * @param gain valeur
     *****************************************/
    public void playFloatingBonus(Pane fxLayer, Label scoreLabel, int gain)
    {
        if (gain <= 0 || fxLayer == null) return;
        Label fxLabel = new Label();
        fxLabel.getStyleClass().addAll(scoreLabel.getStyleClass());
        fxLabel.setMouseTransparent(true);
        StackPane.setAlignment(fxLabel, Pos.TOP_CENTER);
        fxLabel.setTranslateY(50);
        fxLayer.getChildren().add(fxLabel);
        playBonusEffect(fxLabel, gain);
        PauseTransition cleanup = new PauseTransition(Duration.seconds(1.3));
        cleanup.setOnFinished(e -> fxLayer.getChildren().remove(fxLabel));
        cleanup.play();
    }

    /****************************************
     * Affiche un timer flottant temporaire.
     *
     * @param fxLayer couche FX
     * @param timerLabel label source
     ****************************************/
    public void playFloatingTimer(Pane fxLayer, Label timerLabel)
    {
        if (timerLabel == null || fxLayer == null) return;
        Label fxLabel = new Label(timerLabel.getText());
        fxLabel.getStyleClass().addAll(timerLabel.getStyleClass());
        fxLabel.setMouseTransparent(true);
        StackPane.setAlignment(fxLabel, Pos.TOP_RIGHT);
        fxLabel.setTranslateY(50);
        fxLayer.getChildren().add(fxLabel);
        PauseTransition cleanup = new PauseTransition(Duration.seconds(1));
        cleanup.setOnFinished(e -> {
            clearTimerAlert(fxLabel);
            fxLayer.getChildren().remove(fxLabel);
        });
        cleanup.play();
    }

    /**************************************************
     * Met en évidence plusieurs cartes.
     *
     * @param cards liste de boutons
     **************************************************/
    public void playMatchHighlight(List<Button> cards)
    {
        if (cards == null || cards.isEmpty()) return;
        stopMatchHighlight();
        for (int i = 0; i < cards.size(); i++)
        {
            Button btn = cards.get(i);
            PauseTransition delay = new PauseTransition(Duration.millis(i * 80));
            delay.setOnFinished(e -> {
                btn.pseudoClassStateChanged(HIGHLIGHT, true);
                ScaleTransition pulse = new ScaleTransition(Duration.millis(300), btn);
                pulse.setToX(1.1);
                pulse.setToY(1.1);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(Animation.INDEFINITE);
                activeHighlights.add(pulse);
                pulse.play();
            });
            activeHighlights.add(delay);
            delay.play();
        }
    }

    /***********************************
     * Supprime le highlight des cartes.
     *
     * @param cards liste de boutons
     **********************************/

    public void clearMatchHighlight(List<Button> cards)
    {
        if (cards == null) return;
        stopMatchHighlight();
        for (Button btn : cards)
        {
            btn.setEffect(null);
            btn.pseudoClassStateChanged(HIGHLIGHT, false);
            btn.applyCss();
            ScaleTransition reset = new ScaleTransition(Duration.millis(150), btn);
            reset.setToX(1);
            reset.setToY(1);
            reset.play();
        }
    }
    
    /*********************************************
     * Stop les animations (highlight) des cartes.
     ********************************************/
    public void stopMatchHighlight()
    {
        for (Animation anim : activeHighlights){if (anim != null) anim.stop();}
        activeHighlights.clear();
    }

    /***********************************************
     * Effet burst circulaire selon niveau de combo.
     *
     * @param fxLayer couche FX
     * @param level intensité
     ***********************************************/
    public void playComboBurst(StackPane fxLayer, int level) 
    {
        Circle circle = new Circle(50 * level);
        circle.setOpacity(0.6);
        circle.setManaged(false);
        circle.setMouseTransparent(true);
        circle.setCache(true);
        circle.setCacheHint(CacheHint.SPEED);
        circle.setStyle
        (
            "-fx-fill: radial-gradient(center 50% 50%, radius 50%, " +
            (level == 1 ? "cyan" : level == 2 ? "gold" : "red") +
            ", transparent);"
        );
        fxLayer.getChildren().add(circle);
        FadeTransition fade = new FadeTransition(Duration.millis(600),circle);
        fade.setToValue(0);
        fade.setOnFinished(e -> fxLayer.getChildren().remove(circle));
        fade.play();
    }

    /**********************************
     * Explosion lumineuse de victoire.
     *
     * @param fxLayer couche FX
     **********************************/
    public void playWinExplosion(StackPane fxLayer) 
    {
        Circle flash = new javafx.scene.shape.Circle(200);
        flash.setOpacity(0.8);
        flash.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 80%, gold, transparent);");
        fxLayer.getChildren().add(flash);
        FadeTransition fade = new FadeTransition
        (
            Duration.millis(1200),
            flash
        );
        fade.setToValue(0);
        fade.setOnFinished(e -> fxLayer.getChildren().remove(flash));
        fade.play();
    }

    /**********************************************************
     * Fade écran noir (game over).
     *
     * @param gridOverlay couche overlay associée à la grille
     * @param callback callback fin
     **********************************************************/
    public void playGameOverFade(StackPane gridOverlay, Runnable callback)
    {
        gridOverlay.setOpacity(0);
        gridOverlay.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(800), gridOverlay);
        fade.setToValue(0.6);
        fade.setOnFinished(e -> {if (callback != null) callback.run();});
        fade.play();
    }

    /*******************************
     * Pulse global de l’écran.
     *
     * @param root conteneur racine
     *******************************/
    public void playScreenPulse(StackPane root) 
    {
        ScaleTransition st = new ScaleTransition(Duration.millis(600), root);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.03);
        st.setToY(1.03);
        st.setAutoReverse(true);
        st.setCycleCount(6);
        st.play();
    }

    /*******************************
     * Fade-in rapide d’une grille.
     *
     * @param grid grille cible
     ******************************/
    public void fadeInGrid(GridPane grid) 
    {
        grid.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(250), grid);
        fade.setToValue(1);
        fade.play();
    }

    /**************************************
     * Animation d’introduction de niveau.
     *
     * @param fxLayer couche FX
     * @param level numéro
     * @param cb callback fin
     ***************************/
    public void playLevelIntro(StackPane fxLayer, int level, Runnable cb) 
    {
        Label label = new Label("LEVEL " + level);
        label.getStyleClass().add("level-intro");
        label.setOpacity(0);
        label.setScaleX(0.5);
        label.setScaleY(0.5);
        fxLayer.getChildren().add(label);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), label);
        fadeIn.setToValue(1);
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(300), label);
        zoomIn.setToX(1.2);
        zoomIn.setToY(1.2);
        PauseTransition hold = new PauseTransition(Duration.millis(600));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), label);
        fadeOut.setToValue(0);
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(300), label);
        zoomOut.setToX(0.8);
        zoomOut.setToY(0.8);
        ParallelTransition in = new ParallelTransition(fadeIn, zoomIn);
        ParallelTransition out = new ParallelTransition(fadeOut, zoomOut);
        SequentialTransition seq = new SequentialTransition
        (
            in,
            hold,
            out
        );
        seq.setOnFinished(e -> {
            fxLayer.getChildren().remove(label);
            if (cb != null) cb.run();
        });
        seq.play();
    }
}