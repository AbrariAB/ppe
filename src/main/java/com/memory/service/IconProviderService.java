package com.memory.service;

import com.memory.model.IconTheme;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.image.Image;
import org.kordamp.ikonli.Ikon;

import java.util.*;
import java.util.stream.Collectors;

/*┌────────────────────────────────────────┐
 │           IconProviderService          │
 │----------------------------------------│
 │  Service fournissant des icônes pour   │
 │  différents frameworks (Ikonli,        │
 │  FontAwesome, Images, SVG) selon le    │
 │  thème sélectionné. Permet aussi de    │
 │  récupérer des SVG colorisés.          │
 └────────────────────────────────────────┘*/

/************************************************************************************
 * Fournit des icônes de différents types selon un {@link IconTheme}.
 * Supporte Ikonli, FontAwesome, images classiques et SVG (avec coloration possible).
 ************************************************************************************/
public final class IconProviderService 
{
    private IconProviderService() {}

    /**************************************************************
     * Retourne la liste des icônes Ikonli pour le thème donné.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste d'icônes {@link Ikon}, vide si aucun résultat
     *************************************************************/
    public static List<Ikon> getIkonliIcons(IconTheme theme) 
    {
        if (theme == null) return Collections.emptyList();
        return Optional.ofNullable(IconService.getIkons(theme)).orElse(Collections.emptyList());
    }

    /*************************************************************************
     * Retourne la liste des icônes FontAwesome correspondant au thème.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste d'icônes {@link FontAwesomeIcon}, vide si aucun résultat
     *************************************************************************/
    public static List<FontAwesomeIcon> getFontAwesomeIcons(IconTheme theme) 
    {
        if (theme == null || theme == IconTheme.ALL) return Arrays.asList(FontAwesomeIcon.values());
        List<String> keywords = safeKeywords(theme);
        if (keywords.isEmpty()) return Collections.emptyList();
        return Arrays.stream(FontAwesomeIcon.values())
                     .filter(icon -> match(icon.name(), keywords))
                     .collect(Collectors.toList());
    }

    /*************************************************************
     * Vérifie si une valeur correspond à au moins un mot-clé.
     * 
     * @param value chaîne à tester
     * @param keywords liste de mots-clés
     * @return true si correspondance trouvée
     *************************************************************/
    private static boolean match(String value, List<String> keywords) 
    {
        if (value == null || keywords == null || keywords.isEmpty()) return false;
        String upper = value.toUpperCase(Locale.ROOT);
        return keywords.stream().anyMatch(upper::contains);
    }

    /***************************************************************
     * Retourne la liste des icônes Image pour le thème donné.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste d'icônes {@link Image}, vide si aucun résultat
     ****************************************************************/
    public static List<Image> getImageIcons(IconTheme theme) 
    {
        if (theme == null) return Collections.emptyList();
        return Optional.ofNullable(ImageIconService.getIcons(theme)).orElse(Collections.emptyList());
    }

    /**************************************************************
     * Retourne la liste des icônes SVG pour le thème donné.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste d'icônes {@link Image}, vide si aucun résultat
     **************************************************************/
    public static List<Image> getSvgIcons(IconTheme theme) 
    {
        if (theme == null) return Collections.emptyList();
        return Optional.ofNullable(SvgIconService.getIcons(theme)).orElse(Collections.emptyList());
    }

    /**********************************************************************************************
     * Retourne la liste des icônes SVG colorisées pour un thème donné et une liste de couleurs.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @param colors liste de couleurs hexadécimales (#RRGGBB), utilisera #FFFFFF si null ou vide
     * @return liste d'icônes {@link Image}, vide si aucun résultat
     ***********************************************************************************************/
    public static List<Image> getColoredSvgIcons(IconTheme theme, List<String> colors) 
    {
        if (theme == null) return Collections.emptyList();
        List<String> files = Optional.ofNullable(SvgIconService.getSvgFileNames(theme))
                                     .orElse(Collections.emptyList());
        if (files.isEmpty()) return Collections.emptyList();
        List<String> safeColors = (colors == null || colors.isEmpty()) ? List.of("#FFFFFF") : colors;
        List<Image> result = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) 
        {
            String file = files.get(i);
            String svg = SvgIconService.getSvgContent(file);
            if (svg == null || svg.isEmpty()) continue;
            String color = safeColors.get(i % safeColors.size());
            Image img = SvgColorService.colorize(svg, color);
            if (img != null) result.add(img);
        }
        return result;
    }

    /********************************************************************
     * Retourne les mots-clés associés à un thème de manière sécurisée.
     * 
     * @param theme thème {@link IconTheme} ciblé
     * @return liste de mots-clés, vide si null ou exception
     ********************************************************************/
    private static List<String> safeKeywords(IconTheme theme) 
    {
        if (theme == null) return Collections.emptyList();
        try {return Optional.ofNullable(theme.getKeywords()).orElse(Collections.emptyList());} 
        catch (UnsupportedOperationException | NullPointerException e) {return Collections.emptyList();}
    }
}