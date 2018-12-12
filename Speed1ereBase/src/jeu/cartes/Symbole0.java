/**
 * 
 */
package jeu.cartes;



/**
 * @author admin
 *
 */
public enum Symbole0 {

arbre, maison, fanion, etoile, ballon, diamant, chat, VIDE;
	
	private static final Symbole0[] TABLEAU = Symbole0.values();
	public static final int NBR_SYMBOLES =  TABLEAU.length;		

	public static Symbole0 get(int i) {
		return TABLEAU[i]; 
	}
	
	@Override
	public String toString() {
		String texte="";
		switch (this) {
		case arbre:			texte="arbres";			break;
		case maison:		texte="maison";			break;
		case fanion:		texte="fanion";			break;
		case etoile:		texte="étoile";			break;
		case ballon:		texte="ballon";			break;
		case diamant:		texte="diamnt";			break;
		case chat:			texte=" chat ";			break;
		case VIDE:			texte=" VIDE ";			break;
		default:			texte=" NULL ";			break;
		}
		return texte;
	}
	
}