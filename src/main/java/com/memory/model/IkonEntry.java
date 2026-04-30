package com.memory.model;

import org.kordamp.ikonli.Ikon;
import java.util.Set;

/*┌──────────────────────────────────────┐
 │           IkonEntry                   │
 │---------------------------------------│
 │  Représente une entrée Ikon pour le   │
 │  jeu, associant un objet Ikon         │ 
 │  à un ensemble de mots-clés permettant│
 │  de catégoriser ou filtrer les icônes.│
 └────────────────────────────────────────┘*/

/*******************************************************************
 * Représente une entrée d’icône Ikon avec ses mots-clés associés.
 *
 * @param ikon     L’icône Ikon
 * @param keywords Ensemble de mots-clés liés à cette icône
 ******************************************************************/
public record IkonEntry(Ikon ikon, Set<String> keywords) {}