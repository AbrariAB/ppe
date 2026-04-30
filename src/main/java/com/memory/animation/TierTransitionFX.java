package com.memory.animation;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*┌────────────────────────────────────────┐
│           TierTransitionFX              │
│-----------------------------------------│
│ - Un texte animé avec effet 3D          │
│ - Une apparition progressive (fade/scale)│
│ - Une rotation sur l'axe Y              │
│ - Des particules type feu d'artifice    │
└────────────────────────────────────────┘*/

/********************************************************************************
 * Classe responsable de l'animation de transition de niveau (tier).
 ********************************************************************************/
public class TierTransitionFX
{
    /***************************************************************************** 
     * Générateur aléatoire utilisé pour les positions et couleurs des particules 
     *****************************************************************************/
    private static final Random random = new Random();

    /******************************************************************************************
     * Lance l'animation de transition avec un texte personnalisé.
     *
     * @param container le conteneur JavaFX dans lequel afficher l'animation
     * @param text le texte à afficher durant la transition
     * @param callback fonction optionnelle exécutée à la fin de l'animation (peut être null)
     *******************************************************************************************/
    public static void play(StackPane container, String text, Runnable callback)
    {
        StackPane overlay = new StackPane();
        overlay.setPickOnBounds(false);
        Label title = new Label(text);
        title.setFont(Font.font(48));
        title.setTextFill(Color.GOLD);
        title.setOpacity(0);
        title.setScaleX(0.2);
        title.setScaleY(0.2);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        title.getTransforms().add(rotateY);
        overlay.getChildren().add(title);
        container.getChildren().add(overlay);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), title);
        fadeIn.setToValue(1);
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), title);
        scale.setToX(1.2);
        scale.setToY(1.2);
        RotateTransition rotate = new RotateTransition(Duration.millis(800), title);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(90);
        rotate.setToAngle(0);
        ParallelTransition intro = new ParallelTransition(fadeIn, scale, rotate);
        Timeline fireworks = new Timeline();
        fireworks.setCycleCount(6);
        fireworks.getKeyFrames().add(new KeyFrame(Duration.millis(300), e -> spawnFirework(overlay)));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), title);
        fadeOut.setToValue(0);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(500), title);
        shrink.setToX(0.5);
        shrink.setToY(0.5);
        ParallelTransition outro = new ParallelTransition(fadeOut, shrink);
        SequentialTransition sequence = new SequentialTransition
        (
            intro,
            fireworks,
            new PauseTransition(Duration.millis(500)),
            outro
        );
        sequence.setOnFinished(e -> {
            container.getChildren().remove(overlay);
            if (callback != null) callback.run();
        });
        sequence.play();
    }

    /**********************************************************************
     * Génère un effet de feu d'artifice composé de particules animées.
     *
     * @param parent conteneur dans lequel les particules seront ajoutées
     **********************************************************************/
    private static void spawnFirework(StackPane parent)
    {
        double centerX = random.nextDouble() * 400 - 200;
        double centerY = random.nextDouble() * 300 - 150;
        List<Circle> particles = new ArrayList<>();
        for (int i = 0; i < 20; i++) 
        {
            Circle p = new Circle(3, Color.hsb(random.nextDouble() * 360, 1, 1));
            p.setTranslateX(centerX);
            p.setTranslateY(centerY);
            parent.getChildren().add(p);
            particles.add(p);
            double angle = random.nextDouble() * 360;
            double distance = 100 + random.nextDouble() * 100;
            double dx = Math.cos(Math.toRadians(angle)) * distance;
            double dy = Math.sin(Math.toRadians(angle)) * distance;
            TranslateTransition move = new TranslateTransition(Duration.millis(800), p);
            move.setByX(dx);
            move.setByY(dy);
            FadeTransition fade = new FadeTransition(Duration.millis(800), p);
            fade.setToValue(0);
            ParallelTransition pt = new ParallelTransition(move, fade);
            pt.setOnFinished(e -> parent.getChildren().remove(p));
            pt.play();
        }
    }
}