package com.memory.service;

import com.memory.model.IconTheme;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.Collectors;

/*┌──────────────────────────────────────────────────────────────┐
 │                    ImageIconService                           │
 │--------------------------------------------------------------│
 │ - Scanner les ressources disponibles (filesystem / JAR)      │
 │ - Charger les images JavaFX                                  │
 │ - Filtrer les icônes par thème                               │
 │ - Mettre en cache pour optimiser les performances            │
 └──────────────────────────────────────────────────────────────┘*/

/*****************************************************************
 * Service utilitaire permettant de charger et mettre en cache
 * des icônes PNG sous forme d’objets {@link Image}.
 *****************************************************************/
public class ImageIconService 
{
    private static final String BASE_PATH = "images/";
    private static final Map<String, Image> CACHE = new HashMap<>();

    /******************************************************
     * Retourne les icônes correspondant au thème donné.
     *
     * @param theme thème d’icônes (ou ALL/null pour tout)
     * @return liste d’images filtrées
     ******************************************************/
    public static List<Image> getIcons(IconTheme theme) 
    {
        List<String> allFiles = listResourceFiles();
        if (theme == null || theme == IconTheme.ALL) {return loadImages(allFiles);}
        List<String> keywords = IconService.getKeywords(theme);
        return loadImages
        (
            allFiles.stream()
                    .filter(name -> match(name, keywords))
                    .collect(Collectors.toList())
        );
    }

    /***************************************************
     * Charge une liste d’images avec mise en cache.
     *
     * @param files noms de fichiers
     * @return liste d’images valides
     **************************************************/
    private static List<Image> loadImages(List<String> files) 
    {
        return files.stream()
                    .map(ImageIconService::loadImage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    /*************************************************
     * Charge une image unique en utilisant le cache.
     *
     * @param fileName nom du fichier image
     * @return image chargée ou null en cas d’erreur
     *************************************************/
    private static Image loadImage(String fileName) 
    {
        return CACHE.computeIfAbsent(fileName, name -> {
            String path = "/" + BASE_PATH + name;
            try (InputStream is = ImageIconService.class.getResourceAsStream(path)) 
            {
                if (is == null) 
                {
                    System.err.println("Missing: " + path);
                    return null;
                }
                return new Image(is);
            } 
            catch (Exception e) 
            {
                System.err.println("Error loading: " + path);
                return null;
            }
        });
    }

    /***********************************************************
     * Liste tous les fichiers PNG présents dans les ressources.
     *
     * @return liste de noms de fichiers PNG
     ***********************************************************/
    private static List<String> listResourceFiles() 
    {
        try 
        {
            URL url = ImageIconService.class.getClassLoader().getResource(BASE_PATH);
            if (url == null) return List.of();
            if ("file".equals(url.getProtocol())) 
            {
                return Files.list(Paths.get(url.toURI()))
                            .map(path -> path.getFileName().toString())
                            .filter(name -> name.endsWith(".png"))
                            .collect(Collectors.toList());
            }

            if ("jar".equals(url.getProtocol())) {return scanJar(url);}
        } 
        catch (Exception e) {e.printStackTrace();}
        return List.of();
    }

    /***********************************************************
     * Parcourt un fichier JAR pour extraire les ressources PNG.
     *
     * @param url URL pointant vers le dossier dans le JAR
     * @return liste des noms de fichiers PNG
     ***********************************************************/
    private static List<String> scanJar(URL url) 
    {
        List<String> result = new ArrayList<>();
        try 
        {
            String urlPath = url.getPath();
            String jarPath = urlPath.substring(0, urlPath.indexOf("!"));
            try (JarFile jar = new JarFile(new File(new URI(jarPath)))) 
            {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) 
                {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(BASE_PATH) && name.endsWith(".png")) 
                    {result.add(name.substring(BASE_PATH.length()));}
                }
            }
        } 
        catch (Exception e) {e.printStackTrace();}
        Collections.sort(result);
        return result;
    }

    /*************************************************************
     * Vérifie si un fichier correspond à au moins un mot-clé.
     *
     * @param fileName nom du fichier
     * @param keywords mots-clés de filtrage
     * @return true si correspondance
     ***********************************************************/
    private static boolean match(String fileName, List<String> keywords) 
    {
        String upper = fileName.toUpperCase();
        return keywords.stream().anyMatch(upper::contains);
    }

    /*****************************************************************
     * Méthode utilitaire de debug pour afficher les fichiers trouvés.
     *
     * @param files liste de fichiers
     ****************************************************************/
    @SuppressWarnings("unused")
    private static void debugFiles(List<String> files)
    {
        System.out.println("PNG trouvés : " + files.size());
        files.forEach(f -> System.out.println("  " + f));
    }
}