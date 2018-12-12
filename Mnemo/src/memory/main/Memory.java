/**
 * 
 */
package memory.main;


import memory.modele.PartieModele;

/**
 * @author admin
 *
 */
public class Memory {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		 System.out.println("let's play MEMORY ");
		 System.out.println();
		 PartieModele partie = new PartieModele();
			System.out.println(partie);
			partie.jouer();
		}
	
	}


