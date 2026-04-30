package com.memory.service;

import com.memory.model.*;
import com.memory.service.GameFlowService.Difficulty;
import com.memory.dao.*;

/*┌────────────────────────────────────────────┐
 │           GameFacadeService                 │
 │---------------------------------------------│
 │Service simple pour démarrer une partie,     │
 │gérer les cartes, vérifier les paires, gérer │
 │ les niveaux, les bonus et l'historique      │
 └─────────────────────────────────────────────┘*/

/********************************************************************
 * Service façade pour le jeu .
 * Simplifie l'accès aux fonctionnalités du {@link GameFlowService}.
 ********************************************************************/
public class GameFacadeService 
{
    private final GameFlowService service = GameFlowService.getInstance();

    /*******************************************************
     * Démarre une nouvelle partie.
     *
     * @param hardcore true si le mode hardcore est activé
     ********************************************************/
    public void startNewGame(boolean hardcore) 
    {service.startNewGame(hardcore);}

    /*************************************
     * Retourne le modèle de jeu actuel.
     *
     * @return {@link GameModel} courant
     *************************************/
    public GameModel getModel() {return service.getModel();}

    /*******************************************************
     * Sélectionne une carte pour le tour courant.
     *
     * @param card carte à sélectionner
     * @return true si la carte a été révélée, false sinon
     *******************************************************/
    public boolean selectCard(Card card) 
    {
        if (card.isRevealed() || card.isMatched()) return false;
        boolean result = service.getModel().selectCard(card);
        if (result) {service.incrementMoves(); }
        return result;
    }

    /**************************************************************
     * Vérifie si les deux cartes sélectionnées forment une paire.
     *
     * @return true si c'est une paire, false sinon
     **************************************************************/
    public boolean checkMatch() {return service.getModel().checkMatch();}

    /*****************************************
     * Enregistre un succès de paire trouvée.
     *****************************************/
    public void onMatch() {service.registerMatch();}

    /**********************************************************
     * Enregistre une erreur de sélection et cache les cartes.
     **********************************************************/
    public void onError() 
    {
        service.registerError();
        service.getModel().hideCards();
    }

    /******************************************************
     * Vérifie si le jeu est terminé.
     *
     * @return true si toutes les paires ont été trouvées
     ******************************************************/
    public boolean isGameOver() {return service.getModel().isGameOver();}

    /***************************************
     * Applique les bonus de fin de niveau.
     ***************************************/
    public void applyEndLevelBonus() 
    {
        service.addTimeBonus();
        service.addProgressionBonus();
    }

    /*****************************************************************
     * Passe au niveau suivant si possible.
     *
     * @return true si le niveau suivant est disponible, false sinon
     *****************************************************************/
    public boolean nextLevel() {return service.nextLevel();}

    /***********************************
     * Retourne le dernier gain obtenu.
     *
     * @return dernier gain en points
     ***********************************/
    public int getLastGain() {return service.getLastGain();}

    /***************************************
     * Décrémente le timer du jeu.
     *
     * @return temps restant après le tick
     ***************************************/
    public int tickTimer() 
    {
        service.tickTimer();
        return service.getTimeLeft();
    }

    /***************************************
     * Vérifie si le temps est écoulé.
     *
     * @return true si le temps est écoulé
     ***************************************/
    public boolean isTimeUp() 
    {
        if (service.isEndGame()) {return true;}
        return service.getTimeLeft() <= 0;
    }

    /******************************************
     * Indique si le mode hardcore est actif.
     *
     * @return true si mode hardcore
     ******************************************/
    public boolean isHardcore() {return service.isHardcore();}

    /*************************************
     * Retourne le niveau courant.
     *
     * @return numéro du niveau courant
     *************************************/
    public int getLevel() {return service.getCurrentLevel();}

    /************************************
     * Calcule le score final du joueur.
     *
     * @return score final
     ************************************/
    public int getFinalScore() {return service.computeScore();}

    /*********************************************************
     * Construit un objet {@link GameLog} pour l'historique.
     *
     * @param p joueur concerné
     * @return {@link GameLog} correspondant
     ********************************************************/
    public GameLog buildLog(Player p) {return service.buildGameLog(p);}

    /*****************************************
     * Définit le thème des icônes.
     *
     * @param theme thème {@link IconTheme}
     *****************************************/
    public void setTheme(IconTheme theme) {service.setTheme(theme);}

    /*************************************
     * Retourne le type de carte courant.
     *
     * @return type {@link CardType}
     *************************************/
    public CardType getType() {return service.getType();}

    /******************************************************************************
     * Définit le type de cartes utilisé pour la partie.
     *
     * @param type le type de cartes à utiliser ({@link CardType})
     ******************************************************************************/
    public void setType(CardType type) {service.setType(type);}

    /*******************************************************************************
     * Indique si le niveau de difficulté (tier) a changé depuis le dernier appel.
     *
     * @return {@code true} si le tier a changé, {@code false} sinon
     ******************************************************************************/
    public boolean isTierChanged() {return service.isTierChanged();}

    /*******************************************************************************
     * Retourne le niveau de difficulté actuel (tier).
     *
     * @return le tier courant ({@link GameFlowService.Tier})
     ******************************************************************************/
    public GameFlowService.Tier getTier() {return service.getCurrentTier();}

    /*********************************************************************************
     * Vérifie si toutes les cartes du plateau sont actuellement révélées.
     *
     * @return {@code true} si toutes les cartes sont révélées, sinon {@code false}
     **********************************************************************************/
    public boolean allCardsRevealed() 
    {
        if (getModel() == null) return false;
        return service.getModel()
                      .getCards()
                      .stream()
                      .allMatch(Card::isRevealed);
    }

    /**************************************************************************
     * Retourne le niveau maximum atteignable dans le jeu.
     *
     * @return le niveau maximum du jeu
     **************************************************************************/
    public int getMaxLevel() {return GameFlowService.getMaxLevel();}

    /*******************************************************************************
     * Calcule et retourne le rang obtenu en mode hardcore.
     *
     * @return une chaîne représentant le rang du joueur (ex : "S", "A", "B", "C")
     *******************************************************************************/
    public String getHardcoreRank() {return service.computeHardcoreRank();}

    /************************************************************
     * Indique si la partie est terminée.
     *
     * @return {@code true} si la partie est finie,
     *         {@code false} sinon
     ************************************************************/
    public boolean isEndGame() {return isVictory() || isTimeUp();}

    /*************************************************************
     * Vérifie si le joueur a remporté la partie complète.
     * 
     * @return {@code true} si la victoire totale est atteinte,
     *         {@code false} sinon
     *************************************************************/
    public boolean isVictory() 
    {
        if (isHardcore()) {return allCardsRevealed(); }
        return getLevel() >= getMaxLevel()
            && allCardsRevealed();
    }   

    /************************************************************
     * Indique si le joueur a commencé à jouer.
     * 
     * @return {@code true} si le joueur a commencé la partie,
     *         {@code false} sinon
     ***********************************************************/
    public boolean hasPlayerStarted(){return service.hasPlayerStarted();}

    /*************************************************
     * Calcule le score total courant du joueur.
     *
     * @return score cumulé
     ************************************************/
    public int computeScore() {return service.getTotalScore();}

    /**********************************************
     * Retourne le multiplicateur de combo actuel.
     *
     * @return valeur du combo
     **********************************************/
    public int getCombo(){return service.getScoreCombo();}

    /**************************************************************
     * Sélectionne une carte via son index dans la liste du modèle.
     * Valide les bornes et l’état du modèle avant délégation.
     *
     * @param index position de la carte
     * @return true si la sélection est valide et traitée
     *************************************************************/
    public boolean selectCard(int index) 
    {
        GameModel model = service.getModel();
        if (model == null || model.getCards() == null) {return false;}
        if (index < 0 || index >= model.getCards().size()) {return false;}
        Card card = model.getCards().get(index);
        return selectCard(card);
    }

    /******************************************************************
     * Indique si deux cartes non appariées sont actuellement révélées.
     *
     * @return true si la sélection de paire est complète
     ****************************************************************/
    public boolean isSelectionComplete() 
    {
        return service.getModel()
                      .getCards()
                      .stream()
                      .filter(Card::isRevealed)
                      .filter(c -> !c.isMatched())
                      .count() == 2;
    }

    /**************************************************
     * Indique si la partie est perdue (temps écoulé).
     *
     * @return true si défaite
     **************************************************/
    public boolean isDefeat() {return service.getTimeLeft() <= 0;}

    /*************************************************
     * Retourne la progression dans le niveau courant.
     *
     * @return progression [0.0 – 1.0]
     ************************************************/
    public double getLevelProgress(){return service.getLevelProgress();}

    /*****************************************
     * Retourne le niveau actuel du joueur.
     *
     * @return niveau courant
     *******************************************/
    public int getCurrentLevel(){return service.getCurrentLevel();}

    /******************************************************
     * Enregistre une correspondance de cartes trouvée.
     *****************************************************/
    public void registerMatch() {service.registerMatch();}

    /********************************************
     * Retourne la progression globale du joueur.
     *
     * @return progression normalisée
     ********************************************/
    public double getProgress(){if (isEndGame()) return 1.0; return service.getProgress();}

    /******************************************
     * Retourne la progression en mode hardcore.
     *
     * @return progression hardcore
     *******************************************/
    public double getHardcoreProgress(){return service.getHardcoreProgress();}

    /*******************************************************************
     * Indique si un changement de palier (tier) vient d’être consommé.
     *
     * @return true si changement détecté
     ******************************************************************/
    public boolean consumeTierChanged() {return service.consumeTierChanged();}

    /***************************************
     * Définit la difficulté de la partie.
     *
     * @param hard niveau de difficulté
     **************************************/
    public void setDifficulty(Difficulty hard) {service.setDifficulty(hard);}

    /****************************************************************************
     * Retourne le résultat final de la partie.
     *
     * @return {@link GameResult#WIN} si victoire, sinon {@link GameResult#LOSE}
     ****************************************************************************/
    public GameResult getResult() 
    {return  isVictory() ? GameResult.WIN: GameResult.LOSE;}

    public int getMatchedPairs(){return service.getMatchedPairs();}

    public int getScoreTemps(){return service.getScoreTemps();}

    public int getScoreProgression(){return service.getScoreProgression();}
}