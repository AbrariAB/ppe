package memory.modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import memory.vue.CarteVue;

/**
 * @author admin
 *
 */
public class Plateaumodele {
	
	private static final int LG=3;
	private static final int H=4;
	private static final int NB_POSITIONS=PartieModele.NB_CARTES*2;
	
	private List<CarteVue> lesCartes = new ArrayList<CarteVue>(NB_POSITIONS);
	

	public Plateaumodele() {
		super();
		//on cree un paquet de cartes a partir des modeles de cartes enumerées
		List<CarteModele> cartesMelangees = new ArrayList<CarteModele>(NB_POSITIONS);
		
		for (int i = 0; i < NB_POSITIONS; i++) {
			CarteModele c = new CarteModele(SymbolesModele.getSymbolesModele(i/2));
			cartesMelangees.add(c);
		}
		
		Collections.shuffle(cartesMelangees);

		for (int i = 0; i < NB_POSITIONS; i++) {
			CarteVue position = new CarteVue(cartesMelangees.get(i));
			lesCartes.add(position);			
		}
	}

	public void retourner(int position) {
		get(position).getCarte().retourner();
	}

	public void toutCacher() {
		for (CarteVue position : lesCartes) {
			if (!position.estVide()) {
				CarteModele c = position.getCarte();
				if (c.isVisible()) {
					c.retourner();
				}
			}
		}

	}

	public boolean deuxCartesIdentiques() {
		boolean identique=false;
		CarteModele carteVue=null;
		for (CarteVue position : lesCartes) {
			if (!position.estVide()) {
				CarteModele c = position.getCarte();
				if (c.isVisible()) {
					if (carteVue==null){
						carteVue=c;
					}
					else {
						identique = carteVue.equals(c);
					}
				}
			}
		}
		return identique;
	}

	public boolean isEmpty() {
		int indice = 0;
		boolean vide=true;
		while(vide && indice<NB_POSITIONS) {
			vide=get(indice++).estVide();			
		}
		return vide;
	}

	public CarteModele nextCarteGagnees() {
		CarteModele c = null;
		
		// On supprimer les deux cartes mais on n'en retourne qu'une seule
		
		for (int i = 0; i < 2; i++) {
			
			int indice=0;
			while (get(indice).estVide() || !get(indice).getCarte().isVisible()) {
				indice++;
			}
			c= get(indice).vider();
			System.out.println("Carte gagnée ="+indice);
		}
		return c;
	}

	public CarteVue get(int position) {
		return lesCartes.get(position);
	}

	@Override
	public String toString() {
		int numero=0;
		String rep="";
		
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < LG; j++) {
				if (numero<10) {
					rep+=" ";
				}
				
				rep+=numero+":"+get(numero++).toString()+" ";
			}
			rep+="\n";
		}
		return rep;
	}



	
}

	




