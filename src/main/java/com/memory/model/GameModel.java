package com.memory.model;

import java.util.*;

/*┌────────────────────────────────────────┐
 │          GameModel                     │
 │----------------------------------------│
 │  Modèle de jeu                        │
 │  Contient les cartes, gère la logique │
 │  de sélection, de correspondance et   │
 │  de fin de partie.                     │
 └────────────────────────────────────────┘*/

/*****************************************************************
 * Représente le modèle de données et la logique d'un jeu Memory.
 *****************************************************************/
public class GameModel 
{
    private final List<Card> cards = new ArrayList<>();
    private Card first;
    private Card second;
    private final IconTheme theme;
    private final CardType type;

    /****************************************************************
     * Crée un nouveau modèle de jeu.
     *
     * @param size  Nombre total de cartes
     * @param theme Thème des icônes
     * @param type  Type de cartes (FontAwesome, Ikonli, PNG, SVG)
     ****************************************************************/
    public GameModel(int size, IconTheme theme, CardType type) 
    {
        this.theme = theme;
        this.type = type;
        initGame(size);
    }

    /***********************************************************
     * Initialise le jeu en générant et mélangeant les cartes.
     *
     * @param size Nombre total de cartes
     ***********************************************************/
    private void initGame(int size) 
    {
        cards.clear();
        int pairs = size / 2;
        cards.addAll(CardFactory.createPairs(pairs, theme, type));
        Collections.shuffle(cards);
    }

    /************************************************************************************
     * Sélectionne une carte. Si une carte est déjà révélée, gère la deuxième sélection.
     *
     * @param card Carte sélectionnée
     * @return true si la carte peut être révélée, false sinon
     *************************************************************************************/
    public boolean selectCard(Card card) 
    {
        if (card.isRevealed() || card.isMatched()) return false;
        card.reveal();
        if (first == null) first = card;
        else second = card;
        return true;
    }

    /****************************************************************************************
     * Vérifie si les deux cartes sélectionnées correspondent.
     *
     * @return true si les cartes correspondent et sont marquées comme matched, false sinon
     ****************************************************************************************/
    public boolean checkMatch() 
    {
        if (first == null || second == null) return false;
        if (first.getId() == second.getId()) 
        {
            first.match();
            second.match();
            resetTurn();
            return true;
        }
        return false;
    }

    /******************************************************
     * Cache les cartes non correspondantes après un tour.
     ******************************************************/
    public void hideCards() 
    {
        if (first != null && second != null) 
        {
            first.hide();
            second.hide();
        }
        resetTurn();
    }

    /*********************************************************************
     * Réinitialise le tour courant en effaçant les cartes sélectionnées.
     *********************************************************************/
    private void resetTurn() 
    {
        first = null;
        second = null;
    }

    /**************************************************************
     * Vérifie si toutes les cartes ont été trouvées.
     *
     * @return true si toutes les cartes sont matched, false sinon
     ***************************************************************/
    public boolean isGameOver() {return cards.stream().allMatch(Card::isMatched);}

    /*******************************************
     * Retourne la liste des cartes du jeu.
     *
     * @return Liste non modifiable des cartes
     *******************************************/
    public List<Card> getCards() {return Collections.unmodifiableList(cards);}
}