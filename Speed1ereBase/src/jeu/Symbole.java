package jeu;

public enum Symbole {
	
	arbre("arbres"), maison("maison"), fanion("fanion"), etoile("étoile"), ballon("ballon"),
	diamant("diamnt"), chat(" chat "), VIDE("***");
	
	
	private static final Symbole[] TABLEAU = Symbole.values();
	public static final int NBR_SYMBOLES =  TABLEAU.length;

	private final String equivalentChaineDeCaracteres;

	private Symbole(String chaine) {
		this.equivalentChaineDeCaracteres = chaine;
	}

	public static Symbole get(int i) {
		return TABLEAU[i]; 
	}
	
	@Override
	public String toString() {
		return equivalentChaineDeCaracteres;
	}

	


	
}

