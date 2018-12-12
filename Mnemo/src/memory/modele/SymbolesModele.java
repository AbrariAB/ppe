
package memory.modele;



/**
 * @author admin
 *
 */
    public enum SymbolesModele {
	
      //==Cartes des Constellations et des Chevaliers du Zodiaque
    	
    	belier, taureau, gemeaux, cancer, lion, vierge, balance, scorpion, 
    	ophiuchus,sagittaire,capricorne,verseau,poissons,rat,buffle,tigre,
    	lievre,dragon,serpent,cheval,chevre,singe,phenix,chien,sanglier,
    	pegase,cygne,andromede,aigle,lezard,persee,lyre;
    	
    	private static final SymbolesModele[] PLATEAU = SymbolesModele.values(); 

    	public static SymbolesModele getSymbolesModele(int indice) {
    		return PLATEAU[indice];
    	}
    	
    	@Override
    	public String toString() {
    		String rep="";
    		switch (this) {
    		case belier:		rep="[ belier     ]";			break;
    		case taureau:		rep="[ taureau    ]";			break;
    		case gemeaux:		rep="[ gemeaux    ]";			break;
    		case cancer:		rep="[ cancer     ]";			break;
    		case lion:			rep="[ lion       ]";		    break;
    		case vierge:		rep="[ vierge     ]";			break;
    		case balance:		rep="[ balance    ]";			break;
    		case scorpion:		rep="[ scorpion   ]";           break;
    		case ophiuchus:		rep="[ ophiuchus  ]";	        break;
    		case sagittaire:	rep="[ sagittaire ]";		    break;
    		case capricorne:	rep="[ capricorne ]";		    break;
    		case verseau:		rep="[ verseau    ]";			break;
    		case poissons:		rep="[ poissons   ]";			break;
    		case rat:			rep="[ rat        ]";		    break;
    		case buffle:		rep="[ buffle     ]";			break;
    		case tigre:			rep="[ tigre      ]";			break;
    		case lievre:		rep="[ lievre     ]";			break;
    		case dragon:		rep="[ dragon     ]";			break;
    		case serpent:		rep="[ serpent    ]";			break;
    		case cheval:		rep="[ cheval     ]";			break;
    		case chevre:		rep="[ chevre     ]";			break;
    		case singe:			rep="[ singe      ]";			break;
    		case phenix:		rep="[ phenix     ]";			break;
    		case chien:			rep="[ chien      ]";			break;
    		case sanglier:		rep="[ sanglier   ]";           break;
    		case pegase:		rep="[ pegase     ]";			break;
    		case cygne:		   	rep="[ cygne      ]";			break;
    		case andromede:		rep="[ andromede  ]";		    break;
    		case aigle:		    rep="[ aigle      ]";			break;
    		case lezard:		rep="[ lezard     ]";			break;   				
    		case persee:		rep="[ persee     ]";			break;
    		case lyre:		    rep="[ lyre       ]";			break;
    		
    		default:
    			rep="[***********]";
    			break;
    		}
    		return rep;
    	}

  }	
