package com.memory.animation;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

/*┌────────────────────────────────────────┐
 │           JuiceFX                      │
 │----------------------------------------│
 │  Fournit des animations simples comme  │
 │  pop, bounce, glow, shake ainsi que    │
 │  des effets combinés ou continus.      │
 │                                        │
└────────────────────────────────────────┘*/

/*********************************************************
 * Utilitaire regroupant divers effets d’animation JavaFX  
 *********************************************************/
public final class JuiceFX 
{
    private JuiceFX() {}

    /********************************************************************************
     * Applique un effet d’agrandissement rapide ("pop") sur un {@link Node}.
     *
     * @param node le {@link Node} cible de l’animation
     ********************************************************************************/
    public static void pop(Node node) 
    {
        if (node == null) return;
        ScaleTransition st = new ScaleTransition(Duration.millis(120), node);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.15);
        st.setToY(1.15);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    /************************************************************************************
     * Applique un effet de rebond vertical ("bounce") sur un {@link Node}.
     *
     * @param node le {@link Node} cible de l’animation
     ************************************************************************************/
    public static void bounce(Node node) 
    {
        if (node == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), node);
        tt.setByY(-10);
        tt.setAutoReverse(true);
        tt.setCycleCount(2);
        tt.play();
    }

    /***************************************************************************
     * Applique un effet de variation d’opacité ("glow") sur un {@link Node}.
     *
     * @param node le {@link Node} cible de l’animation
     ***************************************************************************/
    public static void glow(Node node) 
    {
        if (node == null) return;
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.6);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

    /*******************************************************************************
     * Applique un effet combiné (scale + rotation) sur un {@link Node}.
     *
     * @param node le {@link Node} cible de l’animation
     ******************************************************************************/
    public static void combo(Node node) 
    {
        if (node == null) return;
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        RotateTransition rotate = new RotateTransition(Duration.millis(300), node);
        rotate.setByAngle(10);
        rotate.setAutoReverse(true);
        rotate.setCycleCount(2);
        ParallelTransition pt = new ParallelTransition(scale, rotate);
        pt.play();
    }

    /***************************************************************************
     * Applique un effet de secousse horizontale ("shake") sur un {@link Node}.
     *
     * @param node le {@link Node} cible de l’animation
     ***************************************************************************/
    public static void shake(Node node) 
    {
        if (node == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setByX(6);
        tt.setAutoReverse(true);
        tt.setCycleCount(6);
        tt.play();
    }

    /***************************************************************************
     * Applique un effet de pulsation continue ("pulse") sur un {@link Node}.
     *
     * @param node le {@link Node} cible de l’animation
     * @return l’{@link Animation} créée, ou {@code null} si le node est null
     ***************************************************************************/
    public static Animation pulse(Node node) 
    {
        if (node == null) return null;
        ScaleTransition st = new ScaleTransition(Duration.millis(500), node);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.08);
        st.setToY(1.08);
        st.setAutoReverse(true);
        st.setCycleCount(Animation.INDEFINITE);
        st.play();
        return st;
    }

    /**********************************************************************************
     * Arrête proprement une {@link Animation}.
     *
     * @param animation l’{@link Animation} à arrêter
     ***********************************************************************************/
    public static void stop(Animation animation) {if (animation != null) {animation.stop();}}
}