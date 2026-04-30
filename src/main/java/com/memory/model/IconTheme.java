package com.memory.model;

import java.util.List;

/*┌────────────────────────────────────────┐
 │           IconTheme                    │
 │----------------------------------------│
 │  Enum représentant les thèmes          │
 │  d’icônes disponibles pour les cartes. │
 │  Chaque thème contient une             │
 │  liste de mots-clés ou codes d’icônes  │
 │  associés.                             │
 └────────────────────────────────────────┘*/

/*******************************************************
 * Enumération des thèmes d’icônes utilisés dans le jeu.
 *******************************************************/
public enum IconTheme 
{
    ALL(List.of()),
    ANIMALS(List.of("DOG","CAT","HORSE","FROG","DRAGON","CROW","HIPPO",
        "1F41","1F40","1F43","1F42","1F9A3","1F9AC","1F9A9","1F9A6","1F9AB")),
    OBJECTS(List.of("CAR","KEY","PHONE","1F94","1FA8","1FA9","1F5F","1F9B",
        "E1C","E1D","E2CB","E2CC","E2D1","E2CF","E04","E31","E32","1F4F","1F4","1F9")),
    SYMBOLS(List.of("STAR","HEART","CIRCLE","264C","264E","1F311","1F312",
        "E24B","1F4BF","1F4B2","E090","0030","2B21","2B1F","2B50","25D1",
        "25D0","2BEA","2BEB","2B22","2B23","1F7E0","1F7E5","00A9","1F549",
        "1F17","1F18","1F19","1F21","1F22","1F23","2648","2649","2650","2651","2652","2653")),
    EMOJIS(List.of("FACE","1F31A","1F31E","1F32C","1F31B","1F31C","1F97A",
        "1F383","1F60","1F61","1F631","1F632","1F635","1F636","1F637","1F644",
        "1F643","1F910","1F923","1F924","1F925","1F911","1F912","1F913","1F914",
        "1F915","1F916","1F917","1F970","1F971","1F972","1F973","1F974","1F975",
        "1F976","1F977","1F978","1F979","1FAE"));

    private final List<String> keywords;

    /***************************************************************
     * Constructeur d’un thème d’icônes.
     *
     * @param keywords Liste de mots-clés ou codes associés au thème
     ***************************************************************/
    IconTheme(List<String> keywords) {this.keywords = keywords;}

    /******************************************************************
     * Retourne la liste des mots-clés ou codes d’icônes pour ce thème.
     *
     * @return Liste des mots-clés du thème
     ******************************************************************/
    public List<String> getKeywords() {return keywords;}
}