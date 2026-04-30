package com.memory.service;

import com.memory.model.IconTheme;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.*;
import java.util.stream.Collectors;

/*┌────────────────────────────────────────┐
 │           SvgIconService               │
 │---------------------------------------│
 │  Service de chargement et cache des    │
 │  icônes SVG.                           │
 └────────────────────────────────────────┘*/

/************************************************************************************
 * Fournit des méthodes pour récupérer les {@link Image} ou le contenu SVG
 * en appliquant un filtrage par {@link IconTheme} et en limitant le nombre d’icônes.
 ************************************************************************************/
public class SvgIconService 
{
    private static final String BASE_PATH = "emojis/openmoji/";
    private static final int MAX_ICONS = 100;
    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final Map<IconTheme, List<String>> THEME_CACHE = new HashMap<>();

    /********************************************************************************
     * Retourne la liste des icônes SVG sous forme {@link Image} pour un thème donné.
     *
     * @param theme thème d’icônes (null ou IconTheme.ALL = toutes)
     * @return liste d’images SVG
     *********************************************************************************/
    public static List<Image> getIcons(IconTheme theme) 
    {
        List<String> files = getSvgFileNames(theme);
        if (files.isEmpty()) return List.of();
        return files.stream()
                    .limit(MAX_ICONS)
                    .map(SvgIconService::loadSvg)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    /*****************************************************
     * Retourne le contenu brut d’un fichier SVG.
     *
     * @param fileName nom du fichier SVG
     * @return contenu SVG en texte, null si introuvable
     *****************************************************/
    public static String getSvgContent(String fileName) 
    {
        String path = "/" + BASE_PATH + fileName;
        try (InputStream is = SvgIconService.class.getResourceAsStream(path)) 
        {
            if (is == null) return null;
            return new String(is.readAllBytes());
        } 
        catch (IOException e) 
        {
            System.err.println("Erreur lecture SVG: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    /*********************************************************************
     * Retourne la liste des fichiers SVG disponibles pour un thème donné.
     *
     * @param theme thème d’icônes
     * @return liste de noms de fichiers SVG
     ********************************************************************/
    public static List<String> getSvgFileNames(IconTheme theme) 
    {
        if (theme != null && THEME_CACHE.containsKey(theme)) {return THEME_CACHE.get(theme);}
        List<String> allFiles = listSvgFiles();
        List<String> filtered;
        if (theme == null || theme == IconTheme.ALL) {filtered = new ArrayList<>(allFiles);} 
        else 
        {
            List<String> keywords = IconService.getKeywords(theme);
            filtered = allFiles.stream()
                               .filter(name -> match(name, keywords))
                               .sorted()
                               .collect(Collectors.toList());
        }
        if (theme != null) THEME_CACHE.put(theme, filtered);
        return filtered;
    }

    /*********************************************************
     * Charge un SVG et le transforme en {@link Image} JavaFX.
     *
     * @param fileName nom du fichier SVG
     * @return Image JavaFX, null si erreur
     *********************************************************/
    private static Image loadSvg(String fileName) 
    {
        return CACHE.computeIfAbsent(fileName, name -> {
            String path = "/" + BASE_PATH + name;
            try (InputStream is = SvgIconService.class.getResourceAsStream(path)) 
            {
                if (is == null) 
                {
                    System.err.println("SVG introuvable: " + path);
                    return null;
                }
                PNGTranscoder transcoder = new PNGTranscoder();
                transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 80f);
                transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 80f);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                transcoder.transcode(new TranscoderInput(is), new TranscoderOutput(outputStream));
                try (ByteArrayInputStream bis = new ByteArrayInputStream(outputStream.toByteArray())) 
                {
                    BufferedImage bufferedImage = javax.imageio.ImageIO.read(bis);
                    if (bufferedImage == null) 
                    {
                        System.err.println("Image invalide: " + fileName);
                        return null;
                    }
                    return SwingFXUtils.toFXImage(bufferedImage, null);
                }
            } 
            catch (TranscoderException | IOException e) 
            {
                System.err.println("Erreur chargement SVG: " + fileName);
                e.printStackTrace();
            }
            return null;
        });
    }

    /********************************************
     * Filtre les fichiers par mots-clés (thème)
     ********************************************/
    private static boolean match(String fileName, List<String> keywords) 
    {
        if (fileName == null || keywords == null || keywords.isEmpty()) return false;
        String upper = fileName.toUpperCase(Locale.ROOT);
        return keywords.stream().anyMatch(k -> k != null && upper.contains(k.toUpperCase(Locale.ROOT)));
    }

    /*****************************************
     * Liste tous les fichiers SVG disponibles
     *****************************************/
    private static List<String> listSvgFiles() 
    {
        try 
        {
            URL url = SvgIconService.class.getClassLoader().getResource(BASE_PATH);
            if (url == null) return List.of();
            if ("file".equals(url.getProtocol())) 
            {
                return Files.list(Paths.get(url.toURI()))
                            .map(p -> p.getFileName().toString())
                            .filter(n -> n.endsWith(".svg"))
                            .sorted()
                            .collect(Collectors.toList());
            } 
            else if ("jar".equals(url.getProtocol())) {return scanJar(url);}
        } 
        catch (Exception e) {e.printStackTrace();}
        return List.of();
    }

    /********************************************************
     * Scan d’un JAR pour lister tous les SVG dans BASE_PATH
     *******************************************************/
    private static List<String> scanJar(URL url) 
    {
        List<String> result = new ArrayList<>();
        try 
        {
            String path = url.getPath();
            int excl = path.indexOf("!");
            String jarFilePath = path.substring(0, excl);
            if (jarFilePath.startsWith("file:")) jarFilePath = jarFilePath.substring(5);
            jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
            try (JarFile jar = new JarFile(jarFilePath)) 
            {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) 
                {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(BASE_PATH) && name.endsWith(".svg")) 
                    {result.add(name.substring(BASE_PATH.length()));}
                }
            }

        }
        catch (Exception e) {e.printStackTrace();}
        Collections.sort(result);
        result.forEach(f -> System.out.println("  " + f));
        return result;
    }
}