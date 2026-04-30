package com.memory.service;

import com.memory.model.IconTheme;
import com.memory.model.IkonEntry;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*┌────────────────────────────────────────┐
 │           IconService                  │
 │----------------------------------------│
 │  Service centralisé pour la gestion    │
 │  des icônes Ikonli. Indexe toutes les  │
 │  icônes disponibles (FontAwesome,      │
 │  MaterialDesign), gère les mots-clés,  │
 │  le cache par thème et l'accès rapide.│
 └────────────────────────────────────────┘*/

/***************************************************************************************
 * Service permettant de récupérer les icônes {@link Ikon} selon un {@link IconTheme}.
 * Indexe et met en cache les icônes pour un accès rapide.
 ***************************************************************************************/
public final class IconService 
{
    private static final List<IkonEntry> ALL_ENTRIES;
    private static final Map<IconTheme, List<Ikon>> CACHE = new ConcurrentHashMap<>();
    private static final Map<String, List<Ikon>> INDEX = new ConcurrentHashMap<>();
    private static final Set<String> ALL_KEYWORDS;

    static 
    {
        List<IkonEntry> tmp = new ArrayList<>();
        Arrays.stream(FontAwesomeSolid.values()).forEach(icon -> tmp.add(createEntry(icon)));
        Arrays.stream(MaterialDesignC.values()).forEach(icon -> tmp.add(createEntry(icon)));
        ALL_ENTRIES = List.copyOf(tmp);
        ALL_KEYWORDS = ALL_ENTRIES.stream()
                                  .flatMap(e -> e.keywords().stream())
                                  .collect(Collectors.toUnmodifiableSet());
        buildIndex();
    }

    private IconService() {}

    /***********************************************************
     * Crée une entrée {@link IkonEntry} à partir d'une icône.
     * Sépare la description de l'icône en mots-clés.
     * 
     * @param icon icône {@link Ikon} à traiter
     * @return {@link IkonEntry} avec icône et mots-clés
     **********************************************************/
    private static IkonEntry createEntry(Ikon icon) 
    {
        String desc = icon.getDescription();
        if (desc == null || desc.isEmpty()) return new IkonEntry(icon, Set.of());
        Set<String> keywords = Arrays.stream(desc.toUpperCase(Locale.ROOT).split("[ _]"))
                                     .collect(Collectors.toSet());
        return new IkonEntry(icon, keywords);
    }

    /*****************************************************************
     * Construit l'index des icônes par mot-clé pour un accès rapide.
     *****************************************************************/
    private static void buildIndex() 
    {
        for (IkonEntry entry : ALL_ENTRIES) 
        {
            for (String keyword : entry.keywords()) 
            {INDEX.computeIfAbsent(keyword, k -> new ArrayList<>(4)).add(entry.ikon());}
        }
    }

    /***************************************************************
     * Calcule la liste d'icônes correspondant à un thème.
     * 
     * @param theme thème {@link IconTheme} à filtrer
     * @return liste d'icônes {@link Ikon} correspondant au thème
     ****************************************************************/
    private static List<Ikon> computeIconsForTheme(IconTheme theme) 
    {
        Set<Ikon> result = new HashSet<>();
        for (String keyword : theme.getKeywords()) 
        {
            List<Ikon> icons = INDEX.get(keyword);
            if (icons != null) result.addAll(icons);
        }
        return List.copyOf(result);
    }

    /***********************************************************
     * Retourne la liste des icônes pour un thème donné.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste d'icônes {@link Ikon}, toutes si theme==ALL
     ***********************************************************/
    public static List<Ikon> getIkons(IconTheme theme) 
    {
        if (theme == null || theme == IconTheme.ALL) return ALL_ENTRIES.stream().map(IkonEntry::ikon).toList();
        return CACHE.computeIfAbsent(theme, IconService::computeIconsForTheme);
    }

    /*****************************************************
     * Retourne les mots-clés associés à un thème.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste de mots-clés, toutes si theme==ALL
     ****************************************************/
    public static List<String> getKeywords(IconTheme theme) 
    {
        if (theme == null || theme == IconTheme.ALL) return new ArrayList<>(ALL_KEYWORDS);
        return theme.getKeywords();
    }
}