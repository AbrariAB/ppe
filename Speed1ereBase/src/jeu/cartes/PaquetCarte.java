package jeu.cartes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import jeu.Carte;

public class PaquetCarte {

	public static final int NBR_CARTES=72;

	private List<Carte> cartes = new ArrayList<Carte>();

	public PaquetCarte(PaquetCarte p1, int n) {
		if (p1.size()<n) {
			System.err.println("Paquet trop petit !!");
		}
		else {
			                                                                 // construire le nouveau "this"
			for (int i = 0; i < n; i++) {
				
				/* sans exploiter le remove
				Carte c1 = p1.get(0);
				p1.remove(0);
				this.add(c1);*/
				
				                                                                // équivalence sans raccourcis
				                                                                // this.cartes.add(p1.cartes.remove(0));
				this.add(p1.remove(0));
			}
		}
	}

	
	public PaquetCarte() {
		super();
		for (int val = 1; val <= Carte.NBR_VALEURS; val++) {
			for (int coul = 1; coul <= Carte.NBR_COULEURS; coul++) {
				for (int symb = 0; symb < Carte.NBR_MOTIFS; symb++) {
					                                                                 //if (this.size()<NBR_CARTES) { 
						                                                            //Symbole symbol = Symbole.get(symb);
						Carte c = new Carte(val,coul,symb);
						                                                           //this.cartes.add(c);
						this.add(c);
					//}
				}
			}
		}
		Collections.shuffle(cartes);
		                                                    // on supprime avec un while mal à propos
		/*
		int taille = this.size();
		while (taille>NBR_CARTES) {
			this.remove(0);
			taille--;
		}*/
		                                                    // mais en fait on pourrait compter avec un for
		for (int i = this.size(); i >NBR_CARTES; i--) {
			this.remove(0);
		}
	}

	public boolean isVide() {
		return this.get(0).isVide();
	}
	
	public Carte get(int i) {
		return this.cartes.get(i);
	}
	
	public Carte remove(int i) {
		return this.cartes.remove(i);
	}

	                                                                 // PaquetCartes paquet .... paquet.size();
	                                                                // évite d'écrire paquet.cartes.size(); ou paquet.getCartes().size();
	public int size() {
		return this.cartes.size();
	}

	                                                                // PaquetCartes paquet .... paquet.add(carte); ==> append de Python
	                                                               // évite d'écrire paquet.cartes.add(carte); ou paquet.getCartes().add(carte);
	public boolean add(Carte c) {
		return this.cartes.add(c);
	}

	@Override
	public String toString() {
		return "PaquetCarte [size()=" + size() + ", cartes=" + cartes + "]";
	}

	

}
