package memory.modele;

import memory.modele.CarteModele;

public class PositionModele {
	
	//option coordonnées
	private int x=-1;
	private int y=-1;
	
	//option numérotation
	
	private static int compteur=0;
	private int numero=-1;
	private CarteModele carte;
	/**
	 * @param x
	 * @param y
	 * @param numero
	 * @param carte
	 */
	
	public PositionModele(int x, int y, CarteModele carte) {
		super();
		this.x = x;
		this.y = y;
		this.carte = carte;
	}
	
	/**
	 * @param carte 
	 * 
	 */
	public PositionModele(CarteModele carte) {
		super();
		this.carte = carte;
		this.numero= compteur++;
	}
	
	

}
