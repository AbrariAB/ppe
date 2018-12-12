package jeu;

import jeu.cartes.PaquetCarte;

public class Joueur {
	
	private static final int PENALITE=3;
	
	private final String nom;
	private PaquetCarte paquet;
	private int penalite=0;

	public Joueur(String nom, PaquetCarte paquet) {
		super();
		this.nom = nom;
		this.paquet = paquet;		
	}

	public String getNom() {
		return nom;
	}

	public PaquetCarte getPaquet() {
		return paquet;
	}
 
	
	public boolean gagne() {		
		return paquet.isVide();
		
		                                        // ou directement si la fonction isVide n'existe pas dans PaquetCarte	
	                                                                   // this.paquet.get(0).isVide(); 
	}

	public Carte remove(int i) {
		return paquet.remove(i);
		
	}

	public Carte get(int i) {
		return paquet.get(i);
	}
	public int size() {
		return paquet.size();
	}

	public boolean add(Carte carte) {
		return paquet.add(carte);
	}

	public void annulerPenalite() {
	this.penalite=0;
	}
	
	public void ajoutePenalite() {
		
	this.penalite=PENALITE;
		
	}

	public boolean sansPenalite() {
		return this.penalite==0 ;
	}
	
    public void oeterEventuellementUnePenalite() {
    	
    	if(this.penalite>0) {
    	
    	this.penalite--;
    		
    	}
    }
	
    public void gererErreur(Joueur joueur2) {
    	
    	this.ajoutePenalite();
    	joueur2.annulerPenalite();
    	
    	
    }

	public Carte testerCarteSommet(int positionCarte, Carte sommet, Joueur joueur2) {
		
		Carte c = null;
		if (this.get(positionCarte).estCompatible(sommet)) {
			c=this.remove(positionCarte);
			joueur2.oeterEventuellementUnePenalite();
		}
		return c;

	}
  

	

}
