package memory.modele;

import java.util.ArrayList;


import java.util.List;
import java.util.Scanner;

/**
 * @author admin
 *
 */



public class PartieModele {
	
	public static final int NB_CARTES = 32;
	public static final int NB_JOUEURS = 3;

	private final List<JoueurModele> lesJoueurs = new ArrayList<JoueurModele>(NB_JOUEURS);

	private Plateaumodele lePlateau = new Plateaumodele();

	public PartieModele() {
		super();
		this.lesJoueurs.add(new JoueurModele("Luffy"));
		this.lesJoueurs.add(new JoueurModele("Zorro"));
		this.lesJoueurs.add(new JoueurModele("Sanji"));
	}

	public int lirePosition(Scanner sc) {
		//System.out.println("Choisissez une carte");
		int position=sc.nextInt();
		if (lePlateau.get(position).estVide()) {
			System.out.println("Mauvaise Saisie !!");
			
			position=lirePosition(sc);
			
		}
		return position;
	}
	
	public void jouer() {
		boolean fini=false;
		
		Scanner sc = new Scanner(System.in);
		int indiceJoueurCourant=0;
		
		while (!fini) {
			System.out.println("Choisissez Deux cartes");
			// Le joueur courant joue deux fois
			for (int i = 0; i < 2; i++) {				
				int position = lirePosition(sc);
				lePlateau.retourner(position);
				System.out.println(lePlateau);
			}
			// On regarde s'il a retourné les deux mêmes cartes
			if (lePlateau.deuxCartesIdentiques()) {
				// OUI : modifier cartes gagnées
				lesJoueurs.get(indiceJoueurCourant).add(lePlateau.nextCarteGagnees());
				// vérifier fin de jeu
				fini = lePlateau.isEmpty();
			}
			else {
				// NON : cacher les cartes
				lePlateau.toutCacher();
				// passer au joueur suivant
				indiceJoueurCourant++;
				if (indiceJoueurCourant==NB_JOUEURS) {
					indiceJoueurCourant=0;
				}
				//System.out.println("prochain joueur :"+lesJoueurs.get(indiceJoueurCourant).getNom());
			}
		}
		System.out.println(this);
		System.out.println("gagnant = "+this.getGagnant());
		sc.close();
	}

	private String getGagnant() {
		int nbrGagnant=0;
		for (JoueurModele joueur : lesJoueurs) {
			if (joueur.nbCartes()>nbrGagnant) {
				nbrGagnant=joueur.nbCartes();
			}
		}
		String rep = "";
		for (JoueurModele joueur : lesJoueurs) {
			if (joueur.nbCartes()==nbrGagnant) {
				rep+=joueur.getNom()+" ";
			}
		}
		return rep;
	}
	
	@Override
	public String toString() {
		return  "\n" + lePlateau;
	}
	

	
	
}
