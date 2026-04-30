package com.memory.ui;

import com.memory.animation.AnimationManager;
import com.memory.animation.NiceFX;
import com.memory.fsm.PlayState;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/*┌────────────────────────────────────────────────────────────┐
│                          HudManager                          │
│--------------------------------------------------------------│
│ - Affichage score, timer, niveau et progression              │
│ - Gestion des paramètres de jeu (thème, difficulté, provider)│
│ - Contrôle de visibilité de l’UI                             │
│ - Animation des éléments HUD                                 │
└──────────────────────────────────────────────────────────────┘*/

/****************************************************************************
 * Gestion du HUD (Head-Up Display)  (score, timer, progression, options UI).
 ****************************************************************************/
public class HudManager 
{
    private final Label score;
    private final Label timer;
    private final Label hintLabel;
    private Label playerName;
    private FontAwesomeIconView playerIcon;

    private final ComboBox<String> difficultyComboBox;
    private final ComboBox<String> providerComboBox;
    private final ComboBox<String> themeComboBox;

    private final HBox topUIContainer;
    private final HBox bottomUIContainer;

    private final AnimationManager fx;
    private final MenuBar menuBar;

    private boolean uiHidden = false;
    private ProgressBar levelProgressBar;
    private Label levelLabel;

    private Timeline progressTimeline;

    /*****************************************************************
     * Constructeur du HUD Manager.
     *
     * @param score label score
     * @param timer label timer
     * @param hintLabel label d’aide
     * @param themeComboBox sélection thème
     * @param providerComboBox sélection provider d’icônes
     * @param difficultyComboBox sélection difficulté
     * @param topUIContainer conteneur UI supérieur
     * @param bottomUIContainer conteneur UI inférieur
     * @param playerName nom joueur
     * @param playerIcon icône joueur
     * @param menuBar barre de menu
     *******************************************************************/
    public HudManager(
            Label score,
            Label timer,
            Label hintLabel,
            ComboBox<String> themeComboBox,
            ComboBox<String> providerComboBox,
            ComboBox<String> difficultyComboBox,
            HBox topUIContainer,
            HBox bottomUIContainer,
            Label playerName,
            FontAwesomeIconView playerIcon,
            MenuBar menuBar) 
    {
        this.score = score;
        this.timer = timer;
        this.hintLabel = hintLabel;
        this.themeComboBox = themeComboBox;
        this.providerComboBox = providerComboBox;
        this.difficultyComboBox = difficultyComboBox;
        this.topUIContainer = topUIContainer;
        this.bottomUIContainer = bottomUIContainer;
        this.playerName = playerName;
        this.playerIcon = playerIcon;
        this.menuBar = menuBar;

        this.fx = new AnimationManager(new NiceFX());
    }

    /******************************************************
     * Initialise les composants HUD et leurs interactions.
     *
     * @param play état principal du jeu
     ******************************************************/
    public void initialize(PlayState play) 
    {
        if (levelProgressBar != null) {levelProgressBar.setProgress(0);}
        if (themeComboBox != null) 
        {
            themeComboBox.getItems().addAll("Classique", "Emojis", "Symbols", "Animaux");
            themeComboBox.setValue("Classique");
            themeComboBox.setOnAction(e -> play.changeTheme(themeComboBox.getValue()));
        }
        if (providerComboBox != null) 
        {
            providerComboBox.getItems().addAll("FontAwesome", "Ikon", "OpenMoji");
            providerComboBox.setValue("FontAwesome");
            providerComboBox.setOnAction(e -> play.changeProviderTheme(providerComboBox.getValue()));
        }
        if (difficultyComboBox != null) 
        {
            difficultyComboBox.getItems().addAll("Normal", "Difficile");
            difficultyComboBox.setValue("Normal");
            difficultyComboBox.setOnAction(e -> {
                String value = difficultyComboBox.getValue();
                if ("Difficile".equals(value)) {play.changeDifficulty("HARD");} 
                else {play.changeDifficulty("NORMAL");}
            });
        }
        Platform.runLater(() -> {if (hintLabel != null) fx.showHint(hintLabel);});
    }

    /*******************************
     * Met à jour le score affiché.
     *
     * @param value score courant
     *****************************/
    public void updateScore(int value) 
    {if (score != null) {score.setText(String.valueOf(value));}}

    /***************************************
     * Met à jour le timer affiché.
     *
     * @param t temps en secondes
     * @param hardcore mode hardcore actif
     ****************************************/
    public void updateTimer(int t, boolean hardcore) 
    {
        if (timer != null) 
        {timer.setText(hardcore ? "--:--": String.format("%02d:%02d", t / 60, t % 60));}
    }

    /************************************
     * Affiche ou masque l’interface HUD.
     ************************************/
    public void toggleUI() 
    {
        uiHidden = !uiHidden;
        fx.setUIVisible(!uiHidden, topUIContainer, bottomUIContainer);
        if (menuBar != null) 
        {
            menuBar.setVisible(!uiHidden);
            menuBar.setManaged(!uiHidden);
        }
    }

    /**********************************
     * Indique si le HUD est masqué.
     *
     * @return true si UI cachée
     *********************************/
    public boolean isHidden() {return uiHidden;}

    /*******************************************************
     * Met à jour la progression du niveau avec animation.
     *
     * @param progress valeur entre 0 et 1
     *******************************************************/
    public void updateProgress(double progress) 
    {
        if (levelProgressBar == null) return;
        progress = Math.max(0, Math.min(1, progress));
        if (progressTimeline != null) {progressTimeline.stop();}
        if (progress >= 1.0) 
        {
            levelProgressBar.setProgress(1.0);
            changeprogressStyle(progress);
            return;
         }
        progressTimeline = new Timeline
        (
            new KeyFrame(Duration.millis(200),
                new KeyValue(levelProgressBar.progressProperty(), progress))
        );
        progressTimeline.play();
        changeprogressStyle(progress);
    }

    /******************************************
     * Réinitialise la progression du niveau.
     ******************************************/
    public void reset() 
    {
        if (progressTimeline != null) {progressTimeline.stop();}
        if (levelProgressBar != null) 
        {
            levelProgressBar.setProgress(0);
            levelProgressBar.setStyle("");
        }
    }

    /*************************************************
     * Met à jour le style de la barre de progression.
     *
     * @param progress valeur normalisée (0-1)
     *************************************************/
    private void changeprogressStyle(double progress)
    {
        if (progress >= 1.0) {levelProgressBar.setStyle("-fx-accent: gold;");} 
        else if (progress < 0.3) {levelProgressBar.setStyle("-fx-accent: red;");} 
        else if (progress < 0.7) {levelProgressBar.setStyle("-fx-accent: orange;");} 
        else {levelProgressBar.setStyle("-fx-accent: limegreen;");}
    }

    /**************************************************
     * Définit les éléments de progression de niveau.
     *
     * @param bar barre de progression
     * @param label label de niveau
     **************************************************/
    public void setLevelUI(ProgressBar bar, Label label) 
    {
        this.levelProgressBar = bar;
        this.levelLabel = label;
    }

    public Label getTimer() {return timer;}

    public Label getScore() {return score;}

    public void setLevelLabel(Label levelLabel) {this.levelLabel = levelLabel;}

    public ProgressBar getLevelProgressBar() {return levelProgressBar;}

    public Label getLevelLabel() {return levelLabel;}

    public ComboBox<String> getDifficultyComboBox() {return difficultyComboBox;}

    public void setUiHidden(boolean uiHidden) {this.uiHidden = uiHidden;}

    public FontAwesomeIconView getPlayerIcon() {return playerIcon;}

    public Label getPlayerName() {return playerName;}

    public Label getHintLabel() {return hintLabel;}

    public ComboBox<String> getThemeComboBox() {return themeComboBox;}

    public ComboBox<String> getProviderComboBox() {return providerComboBox;}

    public HBox getTopUIContainer() {return topUIContainer;}

    public HBox getBottomUIContainer() {return bottomUIContainer;}

    public AnimationManager getFx() {return fx;}

    public boolean isUiHidden() {return uiHidden;}
}