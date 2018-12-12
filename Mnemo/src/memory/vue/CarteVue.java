package memory.vue;

import memory.modele.CarteModele;

/**
 * @author admin
 *
 */
public class CarteVue {
		
	private CarteModele carte;

	

	public CarteVue(CarteModele carte) {
		super();
		this.carte = carte;
		
	}


	public CarteModele getCarte() {
		return carte;
	}
	
	public CarteModele vider() {
		CarteModele cm = carte;
		carte=null;
		return cm;
	}
	
	public boolean estVide() {
		return carte==null;
	}
	
	@Override
	public String toString() {
		String rep="[           ]";
		if (!estVide()) {
			rep=carte.toString();
		}
		return rep;
	}
	
	
	
}
