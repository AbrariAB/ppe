package com.memory.animation;

import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/*┌────────────────────────────────────────┐
 │       FlipAnimation                    │
 │----------------------------------------│
 │  Fournit des méthodes statiques pour   │
 │  animer des éléments graphiques avec   │
 │  un effet de rotation sur l'axe Y.     │
└────────────────────────────────────────┘*/

/*******************************************************************************
 * Classe utilitaire permettant de gérer les animations de retournement (flip) 
 * en 2D et en pseudo 3D sur des nodes JavaFX.                              
 *******************************************************************************/
public class FlipAnimation 
{
    /*********************************************************************************************
     * Effectue une animation de retournement simple (flip) en deux phases sur un {@link Node}.
     *
     * @param node le {@link Node} à animer
     * @param midAction action exécutée à mi-parcours de l'animation (à 90°)
     *********************************************************************************************/
    public static void flip(Node node, Runnable midAction) 
    {
        RotateTransition firstHalf = new RotateTransition(Duration.millis(150), node);
        firstHalf.setAxis(Rotate.Y_AXIS);
        firstHalf.setFromAngle(0);
        firstHalf.setToAngle(90);
        RotateTransition secondHalf = new RotateTransition(Duration.millis(150), node);
        secondHalf.setAxis(Rotate.Y_AXIS);
        secondHalf.setFromAngle(90);
        secondHalf.setToAngle(180);
        firstHalf.setOnFinished(e -> {
            midAction.run(); 
            secondHalf.play();
        });
        firstHalf.play();
    }

    /*******************************************************************************
     * Effectue une animation de retournement en pseudo 3D sur un {@link Button}.
     *
     * @param btn le {@link Button} à animer
     * @param newGraphic le nouveau {@link Node} graphique à appliquer au bouton
     *                   au milieu de l'animation
     *******************************************************************************/
    public static void flip3D(Button btn, Node newGraphic) 
    {
        if (btn.getProperties().get("animating") == Boolean.TRUE) return;
        btn.getProperties().put("animating", true);
        RotateTransition firstHalf = new RotateTransition(Duration.millis(200), btn);
        firstHalf.setFromAngle(0);
        firstHalf.setToAngle(90);
        firstHalf.setAxis(Point3D.ZERO.add(0,1,0));
        RotateTransition secondHalf = new RotateTransition(Duration.millis(200), btn);
        secondHalf.setFromAngle(90);
        secondHalf.setToAngle(180);
        secondHalf.setAxis(Point3D.ZERO.add(0,1,0));
        btn.setStyle("-fx-background-color: linear-gradient(to right, #444, #222);");
        firstHalf.setOnFinished(e -> {
            btn.setGraphic(newGraphic);
            btn.setStyle("-fx-background-color: linear-gradient(to left, #444, #222);");
            secondHalf.play();
        });
        secondHalf.setOnFinished(e -> {
            btn.setRotate(0); 
            btn.setStyle("");
            btn.getProperties().put("animating", false);
        });
        firstHalf.play();
    }
}