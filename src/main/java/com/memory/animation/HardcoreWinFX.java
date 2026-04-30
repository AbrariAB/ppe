package com.memory.animation;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
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
│          HardcoreWinFX                  │
│-----------------------------------------│
│ - Un texte animé en 3D                  │
│ - Des effets de glow                    │
│ - Des particules type feu d'artifice    │
│ - Un effet de secousse (shake)          │
└────────────────────────────────────────┘*/

/******************************************************************
 * Classe responsable de l'animation de victoire.
 *******************************************************************/
public class HardcoreWinFX
{
    private static final Random random = new Random();

    /********************************************************************************************
     * Lance l'animation de victoire hardcore.
     *
     * @param container le conteneur JavaFX dans lequel afficher l'animation
     * @param callback fonction optionnelle exécutée à la fin de l'animation (peut être null)
     **********************************************************************************************/
    public static void play(String text, StackPane container, Runnable callback)
    {
        StackPane overlay = new StackPane();
        overlay.setPickOnBounds(false);
        Label title = new Label(String.valueOf(text));
        title.setFont(Font.font(60));
        title.setTextFill(Color.CRIMSON);
        title.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: gold;");
        title.setOpacity(0);
        Glow glow = new Glow(0.8);
        title.setEffect(glow);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        title.getTransforms().add(rotateY);
        overlay.getChildren().add(title);
        container.getChildren().add(overlay);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), title);
        fadeIn.setToValue(1);
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(800), title);
        scaleUp.setToX(1.5);
        scaleUp.setToY(1.5);
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), title);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(180);
        rotate.setToAngle(0);
        ParallelTransition intro = new ParallelTransition(fadeIn, scaleUp, rotate);
        Timeline fireworks = new Timeline();
        fireworks.setCycleCount(10);
        fireworks.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> spawnHardcoreFirework(overlay)));
        TranslateTransition shake = new TranslateTransition(Duration.millis(400), overlay);
        shake.setByX(15);
        shake.setByY(15);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), title);
        fadeOut.setToValue(0);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(600), title);
        shrink.setToX(0.5);
        shrink.setToY(0.5);
        ParallelTransition outro = new ParallelTransition(fadeOut, shrink);
        SequentialTransition sequence = new SequentialTransition
        (
            intro,
            fireworks,
            shake,
            new PauseTransition(Duration.millis(500)),
            outro
        );
        sequence.setOnFinished(e -> {
            container.getChildren().remove(overlay);
            if (callback != null) callback.run();
        });

        sequence.play();
    }

    /**************************************************************************
     * Génère une explosion de particules colorées simulant un feu d'artifice.
     *
     * @param parent conteneur dans lequel les particules seront ajoutées
     **************************************************************************/
    private static void spawnHardcoreFirework(StackPane parent) 
    {
        double centerX = random.nextDouble() * 500 - 250;
        double centerY = random.nextDouble() * 400 - 200;
        List<Circle> particles = new ArrayList<>();
        for (int i = 0; i < 30; i++) 
        {
            Circle p = new Circle(4, Color.hsb(random.nextDouble() * 360, 1, 1));
            p.setTranslateX(centerX);
            p.setTranslateY(centerY);
            parent.getChildren().add(p);
            particles.add(p);
            double angle = random.nextDouble() * 360;
            double distance = 150 + random.nextDouble() * 150;
            double dx = Math.cos(Math.toRadians(angle)) * distance;
            double dy = Math.sin(Math.toRadians(angle)) * distance;
            TranslateTransition move = new TranslateTransition(Duration.millis(1000), p);
            move.setByX(dx);
            move.setByY(dy);
            FadeTransition fade = new FadeTransition(Duration.millis(1000), p);
            fade.setToValue(0);
            ParallelTransition pt = new ParallelTransition(move, fade);
            pt.setOnFinished(e -> parent.getChildren().remove(p));
            pt.play();
        }
    }
}