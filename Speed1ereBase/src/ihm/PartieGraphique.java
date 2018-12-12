package ihm;

import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PartieGraphique extends JPanel {

	// Supprime le warning lié aux objets sérialisables
	private static final long serialVersionUID = 1L;
	
	// constantes de prise en compte du bord de la fenêtre
	private static final int X_DECALAGE = 10;
	private static final int X_DUO_DE_DECALAGE = 15;
	private static final int Y_DECALAGE = 45;
	private static final int Y_SAUTE_LIGNE = 25;

	// constantes de positionnement des cartes sommet 1 et 2
	private static final int X_DUO1 = 132 + X_DECALAGE + X_DUO_DE_DECALAGE; 
	private static final int Y_DUO1 = 56 + Y_DECALAGE; 
	private static final int X_DUO2 = 295 + X_DECALAGE + X_DUO_DE_DECALAGE; 
	private static final int Y_DUO2 = 57 + Y_DECALAGE;

	// constantes de positionnement des cartes du joueur 1
	private static final int X_J1_C1 = 17 + X_DECALAGE; 
	private static final int X_J1_C2 = 113 + X_DECALAGE; 
	private static final int X_J1_C3 = 206 + X_DECALAGE; 
	private static final int Y_J1 = 356 + Y_DECALAGE;

	// constantes de positionnement des cartes du joueur 2
	private static final int X_J2_C1 = 310 + X_DECALAGE; 
	private static final int X_J2_C2 = 409 + X_DECALAGE; 
	private static final int X_J2_C3 = 507 + X_DECALAGE; 
	private static final int Y_J2 = 280 + Y_DECALAGE;

	// variables décrivant le jeu : noms des joueurs, nom de l'éventuel gagnant et contenus des cartes 
	private String nomJoueur1="";
	private String nomJoueur2="";
	// tant que gagnant est null, il n'y a pas de gagnant à afficher.
	private String gagnant=null;
	private String sommet1=""; 	private Color cSommet1; 	private int vSommet1;
	private String sommet2=""; 	private Color cSommet2; 	private int vSommet2;
	private String joueur10=""; 	private Color cjoueur10; 	private int vjoueur10;
	private String joueur11=""; 	private Color cjoueur11; 	private int vjoueur11;
	private String joueur12=""; 	private Color cjoueur12; 	private int vjoueur12;
	private String joueur20=""; 	private Color cjoueur20; 	private int vjoueur20;
	private String joueur21=""; 	private Color cjoueur21; 	private int vjoueur21;
	private String joueur22=""; 	private Color cjoueur22; 	private int vjoueur22;

	// chemin vers le fichier utilisé en fond de fenêtre
	private static final String CHEMIN_IMAGE = "/donnees/speed.jpg";

	public void setNomJoueur1(String nomJoueur1) {
		this.nomJoueur1 = nomJoueur1;
	}

	public void setNomJoueur2(String nomJoueur2) {
		this.nomJoueur2 = nomJoueur2;
	}

	public void setSommet1(String motif, Color couleur, int valeur) {
		this.sommet1=motif;
		this.cSommet1=couleur;
		this.vSommet1=valeur;
	}

	public void setSommet2(String motif, Color couleur, int valeur) {
		this.sommet2=motif;
		this.cSommet2=couleur;
		this.vSommet2=valeur;
	}	

	public void setGagnant(String nomGagnant) {
		this.gagnant = nomGagnant;
	}
	
	public void setCartesJoueur1(String[] motifs, int[] valeurs, Color[] couleurs){
		vjoueur10=valeurs[0]; cjoueur10=couleurs[0]; joueur10=motifs[0];
		vjoueur11=valeurs[1]; cjoueur11=couleurs[1]; joueur11=motifs[1];
		vjoueur12=valeurs[2]; cjoueur12=couleurs[2]; joueur12=motifs[2];
		System.out.println("joueur1 =");
		System.out.println(vjoueur10+" "+cjoueur10+" "+joueur10+" ");
		System.out.println(vjoueur11+" "+cjoueur11+" "+joueur11+" ");
		System.out.println(vjoueur12+" "+cjoueur12+" "+joueur12+" ");
	}

	public void setCartesJoueur2(String[] motifs, int[] valeurs, Color[] couleurs){
		vjoueur20=valeurs[0]; cjoueur20=couleurs[0]; joueur20=motifs[0];
		vjoueur21=valeurs[1]; cjoueur21=couleurs[1]; joueur21=motifs[1];
		vjoueur22=valeurs[2]; cjoueur22=couleurs[2]; joueur22=motifs[2];
		System.out.println("joueur2 =");
		System.out.println(vjoueur20+" "+cjoueur20+" "+joueur20+" ");
		System.out.println(vjoueur21+" "+cjoueur21+" "+joueur21+" ");
		System.out.println(vjoueur22+" "+cjoueur22+" "+joueur22+" ");
	}
	
	
	/**
	 * Méthode utilisée pour mettre à jour la partie graphique. Elle positionne chaque information
	 * à sa place dans la partie graphique.
	 */
	public void paint(final Graphics g) {
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream(CHEMIN_IMAGE));

			g.drawImage(image, 0, 0, null);

			Font f = new Font("DialogInput", Font.BOLD+Font.ITALIC, 32);
			g.setFont(f);
			g.setColor(Color.lightGray);
			g.drawString("Speed", 450,150);

			f = new Font("DialogInput", Font.BOLD, 24);
			g.setFont(f);
			g.setColor(Color.darkGray);		

			g.drawString(nomJoueur1, 100, 300);
			g.drawString(nomJoueur2, 373, 260);


			// Les touches du joueur 1 (NB : ces données devraient être transmises par le contrôleur)
			g.setColor(Color.green);					
			g.drawString(Touches.CAR_TOUCHE1_J1+"      "+Touches.CAR_TOUCHE2_J1+"     "+Touches.CAR_TOUCHE3_J1, X_J1_C1+30, Y_J1-50);

			// Les touches du joueur 2  (NB : ces données devraient être transmises par le contrôleur)
			g.setColor(Color.red);					
			g.drawString(Touches.CAR_TOUCHE1_J2+"      "+Touches.CAR_TOUCHE2_J2+"     "+Touches.CAR_TOUCHE3_J2, X_J2_C1+30, Y_J2+90);

			afficherCartes(g);

			// si le gagnant est connu, on l'affiche.
			if (gagnant!=null) {
				f = new Font("DialogInput", Font.BOLD, 46);
				g.setFont(f);
				g.setColor(Color.magenta);					
				g.drawString("Victoire : "+gagnant, 20, 50);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Affiche les 3 x 8 informations relatives aux 8 cartes visibles du jeu
	 * @param g le composant graphique modifié
	 */
	public void afficherCartes(final Graphics g){
		for (int i = 1; i <= 8; i++) {
			afficherCarte(g, i);
		}
	}

	/**
	 * Affiche la valeur et le motif de la numCarte-ème carte du jeu dans la couleur associée, sauf si la valeur est de -1, alors
	 * seul le motif est affiché.
	 * @param g le composant graphique modifié
	 * @param numCarte la place associée à la carte à afficher : 1-2 pour les sommets, 3-5 pour les cartes du joueur 1
	 * 			6-8 pour les cartes du joueur 2
	 */
	private void afficherCarte(final Graphics g, final int numCarte){
		int x=0;
		int y=0;
		Color couleur; String motif; int valeur;
		switch (numCarte) {
		case 1 : x=X_DUO1; y=Y_DUO1; valeur = vSommet1; couleur=cSommet1; motif=sommet1;	break;
		case 2 : x=X_DUO2; y=Y_DUO2; valeur = vSommet2; couleur=cSommet2; motif=sommet2;	break;
		case 3 : x=X_J1_C1; y=Y_J1; valeur = vjoueur10; couleur=cjoueur10; motif=joueur10;			break;
		case 4 : x=X_J1_C2; y=Y_J1; valeur = vjoueur11; couleur=cjoueur11; motif=joueur11;			break;
		case 5 : x=X_J1_C3; y=Y_J1; valeur = vjoueur12; couleur=cjoueur12; motif=joueur12;			break;
		case 6 : x=X_J2_C1; y=Y_J2; valeur = vjoueur20; couleur=cjoueur20; motif=joueur20;			break;
		case 7 : x=X_J2_C2; y=Y_J2; valeur = vjoueur21; couleur=cjoueur21; motif=joueur21;			break;
		case 8 : x=X_J2_C3; y=Y_J2; valeur = vjoueur22; couleur=cjoueur22; motif=joueur22;			break;
		default:	x=0; y=0; valeur = 0; couleur=Color.white; motif="Cas impossible";					break;
		}

		// la valeur n'est indiquée que si elle est différente de -1
		if (valeur!=-1){ 
		afficherTexte(g, ""+valeur, couleur, x, y);
		}
		// le motif est tout le temps indiqué
		afficherTexte(g, motif, couleur, x, y+Y_SAUTE_LIGNE);
	}

	/**
	 * Affiche un texte à une position donnée  
	 * @param texte
	 * @param x, y : coordonnées du texte
	 */
	private void afficherTexte(final Graphics g, final String texte, final Color couleur, final int x, final int y) {
		g.setColor(couleur);
		Font f = new Font("DialogInput", Font.BOLD, 20);
		g.setFont(f);
		g.drawString(texte, x, y);


	}

}
