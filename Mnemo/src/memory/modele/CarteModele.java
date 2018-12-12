package memory.modele;
import memory.modele.SymbolesModele;

/**
 * @author admin
 *
 */

public class CarteModele {
	
	private static final String FACE_CACHEE="[************]";
	private final SymbolesModele signe;
	private boolean visible=false;
	
	/**
	 * @Constructeur de carte avec les symboles
	 *
	 */
	
	public CarteModele(SymbolesModele signe) {
		super();
		this.signe = signe;
	}

	/**
	 * @Carte face visible
	 *
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * @retourner le signe de la Carte
	 *
	 */
	public SymbolesModele getSigne() {
		return signe;
	}
	/**
	 * @retourner la Carte
	 *
	 */
	public void retourner() {
		this.visible = ! this.visible;
	}
	
	@Override
	public String toString() {
		String rep=FACE_CACHEE;
		if (this.visible) {
			rep=this.signe.toString();
		}
		return rep;
	}
	
	@Override
	public boolean equals(Object carte) {
		return this.signe==((CarteModele)carte).signe;
	}
	
	
								
			
}
	
		







