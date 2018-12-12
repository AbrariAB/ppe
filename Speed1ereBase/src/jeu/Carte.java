package jeu;

import java.awt.Color;

public class Carte {

	public static final int NBR_COULEURS=5;
	public static final int NBR_MOTIFS=5;
	public static final int NBR_VALEURS=5;

	private int valeur;
	private Color couleur;
	private Symbole motif;

	public Carte() {
		super();
		//carte vide est grise (Color.darkGray), a pour valeur -1
		//et un motif Symbole.VIDE.
		forcerCarteVide();
	}

	@SuppressWarnings("unused")
	private Carte(int valeur, int couleur, Symbole motif) {
		super();
		if (valeur>0 && valeur<=NBR_VALEURS && couleur>0 && couleur<=NBR_COULEURS
				&& NBR_MOTIFS<Symbole.NBR_SYMBOLES	) {
			this.valeur = valeur;
			this.couleur = getColor(couleur);
			this.motif = motif;
		}
		else {
			System.err.println("Problème de carte");
			forcerCarteVide();
		}
	}
	public Carte(int valeur, int couleur, int motif) {
		super();
		if (valeur>0 && valeur<=NBR_VALEURS && couleur>0 && couleur<=NBR_COULEURS
				&& NBR_MOTIFS<Symbole.NBR_SYMBOLES	) {
			this.valeur = valeur;
			this.couleur = getColor(couleur);
			this.motif = Symbole.get(motif);
		}
		else {
			System.err.println("Problème de carte");
			forcerCarteVide();
		}
	}

	private void forcerCarteVide() {
		this.valeur = -1;
		this.couleur = Color.DARK_GRAY;
		this.motif=Symbole.VIDE;
	}

	private static Color getColor(int indice) {
		Color coul = null;
		switch (indice) {
		case 1: coul = Color.blue;  break;
		case 2: coul = Color.orange;  break;
		case 3: coul = Color.cyan;  break;
		case 4: coul = Color.black;  break;
		case 5: coul = Color.lightGray;  break;
		default: coul = Color.darkGray;	break;
		}
		return coul;
	}

	public int getValeur() {
		return valeur;
	}
	public Color getCouleur() {
		return couleur;
	}
	public Symbole getMotif() {
		return motif;
	}

	public boolean isVide() {
		return this.motif.equals(Symbole.VIDE);
	}

	                                                           //c1, c2 -> deux cartes
	                                                           //c1.estCompatible(c2) -> c2 sera mon paramètre, c1 sera this.


	public boolean estCompatible(Carte c2) {		
		return !this.isVide()&&((this.valeur==c2.valeur ||this.couleur.equals(c2.couleur) || this.motif.equals(c2.motif)));		

		/*
		boolean rep=false;
		if (!this.isVide() && !c2.isVide()) { 
		// nous ne sommes pas obligés de tester c2
			if (this.valeur==c2.valeur || this.couleur.equals(c2.couleur) || this.motif.equals(c2.motif)) {
				rep=true;
			}
		}
		return rep;
		 */
	}



}
