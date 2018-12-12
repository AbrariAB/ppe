package memory.modele;

import java.util.ArrayList;


import java.util.List;

/**
 * @author admin
 *
 */

public class JoueurModele {
	
	private final String nom;
	private List<CarteModele> cartesGagnees = new ArrayList<CarteModele>(PartieModele.NB_CARTES*2);
	
	public JoueurModele(String nom) {
		super();
		this.nom = nom;
	}
	
	public String getNom() {
		return nom;
	}



	@Override
	public String toString() {
		return nom + " " + cartesGagnees;
	}

	public void add(CarteModele carte) {
		cartesGagnees.add(carte);		
	}

	public int nbCartes() {
		return cartesGagnees.size();
	}
	
	

}
