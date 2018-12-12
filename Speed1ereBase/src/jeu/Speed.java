package jeu;

import controleur.Controleur;
import jeu.cartes.PaquetCarte;

public class Speed {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
				
		new Controleur();
		
		PaquetCarte p1 = new PaquetCarte();
		
		Carte c = new Carte();
		Carte c2 = new Carte(2,2,0);
		Carte c3= new Carte(8,2,0);
		
		System.out.println(c+"\n"+c2+"\n"+c3);
		
		c3= new Carte(3,99,0);
		
		System.out.println(c+"\n"+c2+"\n"+c3);

		
		
		System.out.println(p1);
		PaquetCarte p2 = new PaquetCarte(p1,20);
		System.out.println(p1);
		System.out.println(p2);
		
		 
		
	}			

				
	}		
	

