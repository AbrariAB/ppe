package com.memory.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*┌────────────────────────────────────────┐
│            SvgColorService              │
│----------------------------------------│
│ - Colorisation DOM-safe des SVG         │
│ - Conversion en Image JavaFX (PNG)      │
│ - Résilience aux SVG invalides          │
│ - Cache mémoire optimisé                │
└────────────────────────────────────────┘*/

/*********************************************************************************
 * Service utilitaire pour coloriser des SVG et les convertir en {@link Image}.
 **********************************************************************************/
public final class SvgColorService 
{
    private static final float SIZE = 80f;
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private SvgColorService() {}

    /*************************************************
     * Colorise un SVG et retourne une image JavaFX.
     *
     * @param svgContent contenu SVG brut
     * @param colorHex   couleur (#RRGGBB)
     * @return image colorisée ou fallback si erreur
     *************************************************/
    public static Image colorize(String svgContent, String colorHex) 
    {
        if (svgContent == null || svgContent.isBlank()) {return null;}
        final String color = sanitizeColor(colorHex);
        final String key = svgContent.hashCode() + "_" + color;
        return CACHE.computeIfAbsent(key, k -> renderSafe(svgContent, color));
    }

    /**********************************
     * Pipeline sécurisé avec fallback.
     **********************************/
    private static Image renderSafe(String svgContent, String color) 
    {
        try 
        {
            String svg = applyColor(svgContent, color);
            return transcode(svg);
        } 
        catch (Exception e) 
        {
            System.err.println("SVG invalid → fallback");
            return fallback();
        }
    }

    /***********************************
     * Applique la colorisation via DOM.
     **********************************/
    private static String applyColor(String svgContent, String color) throws Exception 
    {
        Document doc = parse(svgContent);
        sanitize(doc);
        String[] tags = {"path", "line", "polyline", "polygon", "circle", "rect", "ellipse"};
        float strokeWidth = Math.max(1.5f, SIZE / 40f);
        for (String tag : tags) 
        {
            NodeList nodes = doc.getElementsByTagName(tag);
            for (int i = 0; i < nodes.getLength(); i++) 
            {
                Element el = (Element) nodes.item(i);
                String fill = el.getAttribute("fill");
                String stroke = el.getAttribute("stroke");
                boolean isLine = tag.equals("line") || tag.equals("polyline") || tag.equals("path");
                if ("none".equalsIgnoreCase(fill) || (fill.isEmpty() && isLine)) 
                {
                    el.setAttribute("fill", "none");
                    el.setAttribute("stroke", color);
                    el.setAttribute("stroke-width", String.valueOf(strokeWidth));
                } 
                else 
                {
                    el.setAttribute("fill", color);
                    if (stroke.isEmpty() || "none".equalsIgnoreCase(stroke)) 
                    {
                        el.setAttribute("stroke", color);
                        el.setAttribute("stroke-width", String.valueOf(strokeWidth / 2f));
                    }
                }
                el.setAttribute("stroke-linecap", "round");
                el.setAttribute("stroke-linejoin", "round");
            }
        }
        return serialize(doc);
    }

    /**********************
     * Parse le SVG en DOM.
     **********************/
    private static Document parse(String svgContent) throws IOException 
    {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        return factory.createDocument(null, new StringReader(svgContent));
    }

    /*****************************************
     * Corrige les incohérences DOM critiques.
     *****************************************/
    private static void sanitize(Document doc) 
    {
        NodeList rects = doc.getElementsByTagName("rect");
        for (int i = 0; i < rects.getLength(); i++) 
        {
            Element el = (Element) rects.item(i);
            if (!el.hasAttribute("width") || el.getAttribute("width").isBlank()) 
            {el.setAttribute("width", "1");}
            if (!el.hasAttribute("height") || el.getAttribute("height").isBlank()) 
            {el.setAttribute("height", "1");}
        }
        Element svg = doc.getDocumentElement();
        if (!svg.hasAttribute("viewBox")) {svg.setAttribute("viewBox", "0 0 100 100");}
    }

    /***************************************
     * Transcode SVG → PNG → Image JavaFX.
     ***************************************/
    private static Image transcode(String svg) throws Exception 
    {
        try (InputStream is = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8))) 
        {
            TranscoderInput input = new TranscoderInput(is);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PNGTranscoder t = new PNGTranscoder();
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, SIZE);
            t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, SIZE);
            t.transcode(input, new TranscoderOutput(out));
            try (ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray())) 
            {
                BufferedImage img = ImageIO.read(bis);
                return img != null ? SwingFXUtils.toFXImage(img, null) : null;
            }
        }
    }

    /******************************
     * Sérialise le DOM en String.
     *****************************/
    private static String serialize(Document doc) throws Exception 
    {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    /*********************************
     * Fallback visuel en cas d’échec.
     *********************************/
    private static Image fallback() 
    {
        BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        return SwingFXUtils.toFXImage(img, null);
    }

    /***********************
     * Normalise la couleur.
     ***********************/
    private static String sanitizeColor(String color) 
    {return (color == null || color.isBlank()) ? "#000000" : color;}

    /**************************************
     * Convertit une couleur JavaFX en hex.
     **************************************/
    public static String toHex(javafx.scene.paint.Color color) 
    {
        return String.format
        (
            "#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
}