package com.memory.service;

import com.memory.model.Card;
import com.memory.model.CardType;
import com.memory.model.GameModel;
import com.memory.model.IconTheme;
import com.memory.model.Player;
import com.memory.dao.GameLog;

/*┌────────────────────────────────────────┐
 │  class : GameFlowService               │
 │---------------------------------------│
 │  Service principal de la logique du    │
 │  jeu Gère les niveaux, le              │
 │  score, les bonus, le timer, le mode   │
 │  hardcore et la création du modèle de  │
 │  jeu. Fournit aussi la construction    │
 │  d'un {@link GameLog} pour l'historique│
 └────────────────────────────────────────┘*/

/******************************************************************************
 * Service central du flux de jeu.
 * Gère le démarrage de nouvelles parties, les niveaux, les scores, le timer,
 * les combos et les bonus, ainsi que la construction des logs de jeu.
 ******************************************************************************/
public class GameFlowService 
{
    private static final GameFlowService INSTANCE = new GameFlowService();
    public enum Tier {EASY, MEDIUM, HARD}
    public enum Difficulty {NORMAL,HARD}
    private int currentLevel = 1;
    private boolean hardcoreMode = false;
    private int totalScore = 0;
    private int timeLeft = 0;
    private int lastTotalCards = 0;
    private int combo = 0;
    private int errors = 0;
    private int scorePaires = 0;
    private int scoreCombo = 0;
    private int scoreTemps = 0;
    private int scoreProgression = 0;
    private int lastGain = 0;
    private int moves = 0;
    private int matchedPairs = 0;
    private GameModel model;
    private IconTheme theme = IconTheme.ALL;
    private CardType type = CardType.FONT_AWESOME;
    private static final int INITIAL_CARDS = 6;
    private static final int STEP = 2;
    private static final int MAX_LVL = 10;
    private static final int HARDCORE_CARDS = 100;
    private Tier lastTier = Tier.EASY;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean tierChanged = false;

    private GameFlowService() {}

    /**********************************************************
     * Retourne l'instance unique de {@link GameFlowService}.
     * 
     * @return instance singleton
     **********************************************************/
    public static GameFlowService getInstance() { return INSTANCE; }

    /*******************************************************
     * Démarre une nouvelle partie.
     * 
     * @param hardcore true si le mode hardcore est activé
     *******************************************************/
    public void startNewGame(boolean hardcore) 
    {
        this.hardcoreMode = hardcore;
        this.currentLevel = 1;
        lastTotalCards = 0;
        moves = 0;
        resetScore();
        startLevel();
    }

    /********************************
     * Initialise le niveau courant.
     *******************************/
    public void startLevel() 
    {
        matchedPairs = 0;
        resetTurnStats();
        Tier oldTier = lastTier;
        if (hardcoreMode) 
        {
            startHardcoreLevel();
            return;
        }
        else 
        {
            int startCards = (difficulty == Difficulty.HARD) ? 10 : INITIAL_CARDS;
            int step = (difficulty == Difficulty.HARD)
                    ? 5 + (currentLevel / 2) 
                    : STEP;
            int baseCards = startCards + step * (currentLevel - 1);
            int totalCards = computePerfectGrid(baseCards);
            model = new GameModel(totalCards, theme, type);
            initTimerForLevel(currentLevel);
        }
        Tier newTier = getCurrentTier();
        if (newTier != oldTier) {tierChanged = true;}
        lastTier = newTier;
    }

    /**********************************************************
     * Initialise une partie en mode hardcore.
     ************************************************************/
    private void startHardcoreLevel() 
    {
        matchedPairs = 0;
        int totalCards = computeHardcoreGrid(HARDCORE_CARDS);
        model = new GameModel(totalCards, theme, type);
        timeLeft = Integer.MAX_VALUE;
    }

    /*****************************************************************
     * Passe au niveau suivant si possible.
     * 
     * @return true si le niveau suivant est disponible, false sinon
     *****************************************************************/
    public boolean nextLevel() 
    {
        if (currentLevel >= MAX_LVL || hardcoreMode) return false;
        currentLevel++;
        startLevel();
        return true;
    }

    /***********************************************************
     * Calcule un nombre de cartes formant une grille parfaite.
     * 
     * @param base nombre de cartes de base
     * @return nombre total de cartes ajusté
     ***********************************************************/
    private int computePerfectGrid(int base) 
    {
        int total = Math.max(base, lastTotalCards + (base - lastTotalCards));
        int guard = 0;
        while (guard++ < 500) 
        {
            if (total % 2 != 0) 
            {
                total++;
                continue;
            }
            int cols = (int) Math.ceil(Math.sqrt(total));
            int rows = (int) Math.ceil((double) total / cols);
            if (rows * cols == total && total > lastTotalCards) 
            {
                lastTotalCards = total;
                return total;
            }
            total++;
        }
        lastTotalCards += 2;
        return lastTotalCards;
    }

    /***************************************************
     * Calcule une grille valide pour le mode hardcore.
     *
     * @param base nombre de cartes souhaité
     * @return nombre ajusté pour une grille valide
     **************************************************/
    private int computeHardcoreGrid(int base) 
    {
        int total = base;
        while (true) 
        {
            if (total % 2 != 0) 
            {
                total++;
                continue;
            }
            int cols = (int) Math.ceil(Math.sqrt(total));
            int rows = (int) Math.ceil((double) total / cols);
            if (rows * cols == total) {return total;}
            total++;
        }
    }

    /*********************************************
     * Initialise le timer pour un niveau donné.
     * 
     * @param lvl niveau courant
     *********************************************/
    private void initTimerForLevel(int lvl) 
    {
        int base = lvl <= 5 ? 150 : (lvl <= 10 ? 250 : 400);
        int reduction = lvl <= 5 ? 10 : (lvl <= 10 ? 50 : 80);
        int time = Math.max(10, base - reduction);
        if (difficulty == Difficulty.HARD) {time *= 0.6; }
        timeLeft = time;
    }

    /***************************************
     * Décrémente le timer.
     * 
     * @return true si le temps est écoulé
     ***************************************/
    public boolean tickTimer() 
    {
        if (hardcoreMode) return false;
        if (timeLeft <= 0) return true;
        timeLeft--;
        return timeLeft <= 0;
    }

    /*******************************************************************
     * Enregistre une paire correcte et met à jour les scores et bonus.
     *******************************************************************/
    public void registerMatch() 
    {
        matchedPairs++;
        combo++;
        int basePoints = 100;
        int gained = basePoints;
        if (combo >= 2) 
        {
            double multiplier =
                    combo == 2 ? 1.2 :
                    combo == 3 ? 1.5 : 2.0;
            gained = (int)(basePoints * multiplier);
            scoreCombo += (gained - basePoints);
        }
        int timeBonus = hardcoreMode ? 0 : timeLeft * 2;
        int levelBonus = hardcoreMode ? currentLevel * 50 : currentLevel * 20;
        scoreTemps += timeBonus;
        scoreProgression += levelBonus;
        lastGain = gained + timeBonus + levelBonus;
        scorePaires += basePoints;
        totalScore += lastGain;
    }

    /*********************************************************************
     * Enregistre une erreur et réinitialise le combo et le dernier gain.
     *********************************************************************/
    public void registerError() 
    {
        combo = 0;
        errors++;
        lastGain = 0;
    }

    /****************************************
     * Ajoute le bonus lié au temps restant.
     ****************************************/
    public void addTimeBonus() {if (!hardcoreMode) scoreTemps += timeLeft * 5;}

    /********************************************
     * Ajoute le bonus de progression du niveau.
     ********************************************/
    public void addProgressionBonus() {scoreProgression += 250;}

    /************************************************************************************
     * Calcule le score final du joueur en combinant paires, combos, bonus et pénalités.
     * 
     * @return score final
     ***********************************************************************************/
    public int computeFinalScore() 
    {
        int rawSkill = scorePaires + scoreCombo;
        int rawBonus = scoreTemps + scoreProgression;
        int penalties = errors * 20;
        int rawTotal = rawSkill + rawBonus;
        if (rawTotal <= 0) return 0;
        double coefSkill = 0.7 / ((double) rawSkill / rawTotal);
        double coefBonus = 0.3 / ((double) rawBonus / rawTotal);
        double balanced = rawSkill * coefSkill + rawBonus * coefBonus - penalties;
        return Math.max((int) balanced, 0);
    }

    /*******************************************************
     * Construit le log de la partie pour un joueur donné.
     * 
     * @param p joueur concerné
     * @return {@link GameLog} de la partie
     *******************************************************/
    public GameLog buildGameLog(Player p) 
    {
        return new GameLog
        (
            p != null ? p.getName() : "Unknown",
            computeScore(),
            currentLevel,
            timeLeft,
            isHardcore() ? scoreProgression : scoreTemps,
            isHardcore() ? computeHardcoreRank() : "Not Ranked"
        );
    }

    /****************************************
     * Définit le thème des icônes.
     * @param theme thème {@link IconTheme}
     ****************************************/
    public void setTheme(IconTheme theme) { this.theme = theme; }

    /*******************************
     * Définit le type de cartes.
     * @param type {@link CardType}
     *******************************/
    public void setType(CardType type) { this.type = type; }

    /**************************************
     * Retourne le type de cartes courant.
     * @return type {@link CardType}
     **************************************/
    public CardType getType() { return this.type; }

    /*************************************************
     * Réinitialise les statistiques du tour courant.
     *************************************************/
    private void resetTurnStats() 
    {
        combo = 0;
        errors = 0;
        lastGain = 0;
    }

    /*********************************************
     * Réinitialise tous les scores de la partie.
     *********************************************/
    private void resetScore() 
    {
        scorePaires = 0;
        scoreCombo = 0;
        scoreTemps = 0;
        scoreProgression = 0;
        errors = 0;
        combo = 0;
        lastGain = 0;
    }

    /********************************************************************************
     * Détermine le niveau de difficulté actuel (tier) en fonction
     * du nombre de cartes présentes dans la grille.
     *
     * @return le tier actuel ({@link Tier})
     *********************************************************************************/
    public Tier getCurrentTier() 
    {
        int cards = model.getCards().size();
        if (cards <= 20) return Tier.EASY;
        if (cards <= 36) return Tier.MEDIUM;
        return Tier.HARD;
    }

    /*************************************************************************************
     * Indique si le niveau de difficulté (tier) a changé depuis la dernière vérification.
     *
     * @return {@code true} si le tier a changé depuis le dernier appel,
     *         {@code false} sinon
     ************************************************************************************/
    public boolean isTierChanged() 
    {
        Tier current = getCurrentTier();
        boolean changed = current != lastTier;
        lastTier = current;
        return changed;
    }

    /**************************************************************
     * Calcule le score en mode hardcore.
     * Logique plus punitive et skill-based (pas de bonus temps).
     *
     * @return score hardcore
     *************************************************************/
    public int computeHardcoreScore() 
    {
        int pairs = scorePaires;
        int comboScore = scoreCombo;
        int difficulty = model != null ? model.getCards().size() : 1;
        int skillScore = pairs + comboScore;
        int streakBonus = combo * 20;
        double difficultyMultiplier = 1.0 + (difficulty / 60.0);
        int penalties = errors * 10;
        int levelBonus = currentLevel * 80;
        int total = (int)((skillScore + streakBonus) * difficultyMultiplier
                + levelBonus
                - penalties);
        return Math.max(total, 0);
    }

    /***********************************************
     * Retourne le score final selon le mode de jeu.
     ***********************************************/
    public int computeScore() 
    {
        return hardcoreMode
                ? computeHardcoreScore()
                : computeFinalScore();
    }

    /************************************
     * Retourne le rang selon le score .
     ************************************/
    public String computeHardcoreRank()
    {
        int score = computeHardcoreScore();
        if (errors <=50 && combo >= 8) return "S";
        if (score >= 50000) return "A";
        if (score >= 10000) return "B";
        return "C";
    }

    /***************************************
     * Indique si la partie est terminée.
     *
     * @return true si la partie est finie
     **************************************/
    public boolean isEndGame() 
    {return isBoardCompleted() || isDefeat() || isMaxLevelCompleted();}

    /**********************************************
     * Vérifie si toutes les cartes sont trouvées.
     **********************************************/
    private boolean isBoardCompleted() 
    {return model != null && model.isGameOver();}

    /**********************************
     * Vérifie si le temps est écoulé.
     *********************************/
    @SuppressWarnings("unused")
    private boolean isTimeUp() 
    {return !hardcoreMode && timeLeft <= 0;}

    /*******************************************
     * Vérifie si le dernier niveau est terminé.
     *******************************************/
    private boolean isMaxLevelCompleted() 
    {
        return !hardcoreMode
                && currentLevel >= MAX_LVL
                && model != null
                && model.isGameOver();
    }

    /*********************************************************
     * Incrémente le nombre de coups effectués par le joueur.
     *********************************************************/
    public void incrementMoves() {moves++;}

    /*********************************************************
     * Indique si le joueur a réellement commencé à jouer.
     *
     * @return {@code true} si le joueur a effectué au moins
     *         deux actions, sinon {@code false}
     *********************************************************/
    public boolean hasPlayerStarted(){return moves >= 2;}

     /***************************************
     * Retourne le score total accumulé.
     *
     * @return score courant
     ****************************************/
    public int getTotalScore(){return totalScore;}

    /******************************************************
     * Calcule la progression basée sur le niveau atteint.
     *
     * @return progression normalisée [0.0 – 1.0]
     ******************************************************/
    public double getLevelProgress() 
    {return (double)(currentLevel - 1) / MAX_LVL;}

    /*************************************************************************
     * Calcule la progression en mode hardcore basée sur les paires révélées.
     *
     * @return progression des paires trouvées [0.0 – 1.0]
     ************************************************************************/
    public double getHardcoreProgress() 
    {
        if (model == null || model.getCards().isEmpty()) return 0;
        long matchedPairs = model.getCards().stream()
                                 .filter(Card::isMatched)
                                 .count()/2;
        int totalPairs = model.getCards().size() / 2;
        return matchedPairs / (double) totalPairs;
    }

    /********************************************************
     * Retourne la progression active selon le mode de jeu.
     *
     * @return progression normalisée
     *********************************************************/
    public double getProgress() 
    {
        if (hardcoreMode) return getHardcoreProgress();
        double levelPart = getLevelProgress();
        if (model == null) return levelPart;
        double boardProgress = getHardcoreProgress();
        return Math.min(1.0, levelPart + boardProgress / MAX_LVL);
    }

    /****************************************
     * Retourne le nombre de paires validées.
     *
     * @return nombre de paires trouvées
     *****************************************/
    public int getMatchedPairs() {return matchedPairs;}

    /********************************************************
     * Consomme l’indicateur de changement de palier (tier).
     * Réinitialise l’état après lecture.
     *
     * @return true si un changement était présent
     ********************************************************/
    public boolean consumeTierChanged() 
    {
        boolean value = tierChanged;
        tierChanged = false;
        return value;
    }

    /******************************************
     * Définit la difficulté du jeu.
     *
     * @param difficulty niveau de difficulté
     ******************************************/
    public void setDifficulty(Difficulty difficulty) {this.difficulty = difficulty;}

    /************************************
     * Retourne la difficulté courante.
     *
     * @return difficulté active
     ************************************/
    public Difficulty getDifficulty() {return difficulty;}

    /***************************************************
     * Indique si la partie est perdue (temps écoulé).
     *
     * @return true si défaite
     ***************************************************/
    public boolean isGameLost() {return getTimeLeft() <= 0;}

    /***********************************************
     * Retourne le multiplicateur de combo courant.
     *
     * @return valeur du combo
     ************************************************/
    public int getCombo() {return combo;}

    /****************************************
     * Retourne le nombre d’erreurs commises.
     *
     * @return nombre d’erreurs
     ****************************************/
    public int getErrors() {return errors;}

    /****************************************
     * Indique si la partie est perdue 
     *
     * @return true si défaite
     ****************************************/
    public boolean isDefeat() {return isGameLost();}

    /*************************************
     * Retourne le modèle de jeu courant.
     * 
     * @return {@link GameModel} courant
     *************************************/
    public GameModel getModel() { return model; }

    /****************************************************
     * Retourne le temps restant pour le niveau courant.
     * 
     * @return temps restant
     ****************************************************/
    public int getTimeLeft() { return timeLeft; }

    /******************************
     * Retourne le niveau courant.
     * 
     * @return numéro du niveau
     *****************************/
    public int getCurrentLevel() { return currentLevel; }

    /*****************************************
     * Indique si le mode hardcore est actif.
     * 
     * @return true si hardcore
     *****************************************/
    public boolean isHardcore() { return hardcoreMode; }

    /*********************************************
     * Retourne le score de progression accumulé.
     * 
     * @return score de progression
     ********************************************/
    public int getScoreProgression() { return scoreProgression; }

    /*******************************************************
     * Retourne le dernier gain obtenu pour l'effet visuel.
     * 
     * @return dernier gain
     *******************************************************/
    public int getLastGain() { return lastGain; }

    /*****************************************
     * Retourne le score des paires trouvées.
     * 
     * @return score paires
     *****************************************/
    public int getScorePaires() { return scorePaires; }

    /***************************************
     * Retourne le score de combo accumulé.
     * 
     * @return score combo
     ***************************************/
    public int getScoreCombo() { return scoreCombo; }

    /***************************************************************
     * Retourne le niveau maximum atteignable dans le jeu.
     *
     * @return le niveau maximum du jeu
     **************************************************************/
    public static int getMaxLevel() {return MAX_LVL;}

    /*********************************************
     * Retourne le score selon le temps écoulé.
     * 
     * @return score de progression
     ********************************************/
    public int getScoreTemps() {return scoreTemps;}

    

}