package ihm;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;

public class FenetreHote extends JFrame {

	// Supprime le warning lié aux objets sérialisables
	private static final long serialVersionUID = 1L;

	private PartieGraphique imagePanel;

	/**
	 * Constructeur pour la fenêtre créant une sous-partie graphique de dimension 600x500 et la positionne au centre
	 * de l'écran.
	 */
	public FenetreHote(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();

		/* Positionner l'Image de fond */

		imagePanel = new PartieGraphique();
		imagePanel.setPreferredSize(new Dimension(600, 500));
		c.add(imagePanel);

		Dimension d=getToolkit().getScreenSize();
		setLocation(d.width/3,d.height/6);

		pack();

		this.setVisible(true);
	}

	/**
	 * Transmet les chaines de caractères utilisées pour l'affichage des noms des joueurs 
	 * @param nom1 nom du premier joueur
	 * @param nom2 nom du deuxième joueur
	 */
	public void setNoms(String nom1, String nom2) {
		imagePanel.setNomJoueur1(nom1);
		imagePanel.setNomJoueur2(nom2);
	}

	/**
	 * Indique que le jeu possède un gagnant 
	 * @param nomGagnant le nom du joueur gagnant
	 */
	public void setGagnant(String nomGagnant) {
		imagePanel.setGagnant(nomGagnant);
	}
	
	/**
	 * Transmet les chaînes de caractères, couleurs et valeurs utilisées pour l'affichage des deux sommets
	 * pour les deux paquets posés au centre de la table.
	 * @param sommeti, cSommeti, vSommeti : motif (Symbole), couleur et valeur du sommet i
	 */
	public void setSommets(String sommet1, Color cSommet1, int vSommet1, String sommet2, Color cSommet2, int vSommet2){
		imagePanel.setSommet1(sommet1, cSommet1, vSommet1);		
		imagePanel.setSommet2(sommet2, cSommet2, vSommet2);		
	}
	
	/**
	 * Transmet les chaînes de caractères couleurs et valeurs utilisées pour l'affichage des trois cartes du joueur 1
	 * @param cartei, cCartei, vCartei : motif (Symbole), couleur et valeur de la carte i du joueur 1
	 */
	public void setCartesJoueur1(String carte1, Color cCarte1, int vCarte1, String carte2, Color cCarte2, int vCarte2, String carte3, Color cCarte3, int vCarte3){
		String[] motifs = new String[3];
		motifs[0] = carte1; motifs[1] = carte2;motifs[2] = carte3;
		int[] valeurs = new int[3];
		valeurs[0] = vCarte1; valeurs[1] = vCarte2;valeurs[2] = vCarte3;
		Color[] couleurs = new Color[3];
		couleurs[0] = cCarte1; couleurs[1] = cCarte2;couleurs[2] = cCarte3;
		imagePanel.setCartesJoueur1(motifs, valeurs, couleurs);
	}

	/**
	 * Transmet les chaînes de caractères couleurs et valeurs utilisées pour l'affichage des trois cartes du joueur 2
	 * @param cartei, cCartei, vCartei : motif (Symbole), couleur et valeur de la carte i du joueur 2
	 */
	public void setCartesJoueur2(String carte1, Color cCarte1, int vCarte1, String carte2, Color cCarte2, int vCarte2, String carte3, Color cCarte3, int vCarte3){
		String[] motifs = new String[3];
		motifs[0] = carte1; motifs[1] = carte2;motifs[2] = carte3;
		int[] valeurs = new int[3];
		valeurs[0] = vCarte1; valeurs[1] = vCarte2;valeurs[2] = vCarte3;
		Color[] couleurs = new Color[3];
		couleurs[0] = cCarte1; couleurs[1] = cCarte2;couleurs[2] = cCarte3;
		imagePanel.setCartesJoueur2(motifs, valeurs, couleurs);		
	}
	
}
