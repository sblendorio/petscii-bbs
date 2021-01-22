package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.PetsciiColors.BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY1;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY2;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_RED;
import static eu.sblendorio.bbs.core.PetsciiColors.PURPLE;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiColors.YELLOW;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class TelevideoRaiPetscii extends RssPetscii {

    protected Map<String, NewsSection> sections;

    public TelevideoRaiPetscii() {
        this("rss.petscii.timeout", "50000");
    }

    public TelevideoRaiPetscii(String property, String defaultValue) {
        super(property, defaultValue);
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightNews = 4;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }


    @Override
    public String prefix() {
        return "http://www.servizitelevideo.rai.it/televideo/pub/";
    }

    @Override
    public byte[] getLogo() {
        return LOGO_TELEVIDEO;
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection(WHITE, "Ultim'Ora", prefix() + "rss101.xml", Logos.LOGO_ULTIMORA));
        result.put("102", new NewsSection(CYAN, "24h No Stop", prefix() + "rss102.xml", Logos.LOGO_NOSTOP24H));
        result.put("110", new NewsSection(RED, "Primo Piano", prefix() + "rss110.xml", Logos.LOGO_PRIMOPIANO));
        result.put("120", new NewsSection(GREEN, "Politica", prefix() + "rss120.xml", Logos.LOGO_POLITICA));
        result.put("130", new NewsSection(BLUE, "Economia", prefix() + "rss130.xml", Logos.LOGO_ECONOMIA));
        result.put("140", new NewsSection(GREY2, "Dall'Italia", prefix() + "rss140.xml", Logos.LOGO_DALLITALIA));
        result.put("150", new NewsSection(LIGHT_BLUE, "Dal Mondo", prefix() + "rss150.xml", Logos.LOGO_DALMONDO));
        result.put("160", new NewsSection(LIGHT_RED, "Culture", prefix() + "rss160.xml", Logos.LOGO_CULTURE));
        result.put("170", new NewsSection(PURPLE, "Cittadini", prefix() + "rss170.xml", Logos.LOGO_CITTADINI));
        result.put("180", new NewsSection(GREY3, "Speciale", prefix() + "rss180.xml", Logos.LOGO_SPECIALE));
        result.put("190", new NewsSection(LIGHT_RED, "Atlante Crisi", prefix() + "rss190.xml", Logos.LOGO_ATLANTECRISI));
        result.put("229", new NewsSection(LIGHT_GREEN, "Brevi Calcio", prefix() + "rss229.xml", Logos.LOGO_BREVICALCIO));
        result.put("230", new NewsSection(YELLOW, "CalcioSquadre", prefix() + "rss230.xml", Logos.LOGO_CALCIOSQUADRE));
        result.put("260", new NewsSection(GREEN, "Altri Sport", prefix() + "rss260.xml", Logos.LOGO_ALTRISPORT));
        result.put("299", new NewsSection(GREY1, "Brevissime", prefix() + "rss299.xml", Logos.LOGO_SPORTBREVISSIME));
        result.put("810", new NewsSection(GREY2, "Motori", prefix() + "rss810.xml", Logos.LOGO_MOTORI));
        return result;
    }

    private static final byte[] LOGO_TELEVIDEO = new byte[] {
        32, 32, 18, -98, 32, 32, 32, 32, -110, -95, 18, 32, 32, 32, -110, -95,
        18, 32, -110, -95, 32, 32, 18, 32, 32, 32, -110, -95, 18, 32, -110, -95,
        32, 18, 32, -95, 32, -95, 32, 32, -68, -110, 32, 18, 32, 32, 32, -110,
        -95, 18, -66, 32, 32, -68, -110, 13, 32, 32, 18, -94, -69, 32, -94, -110,
        -66, 18, 32, -68, -110, -94, 32, 18, 32, -110, -95, 32, 32, 18, 32, -68,
        -110, -94, 32, 18, -69, -110, -95, 32, 18, -84, -95, 32, -95, 32, -94, -69,
        -110, -95, 18, 32, -68, -110, -94, 32, 18, 32, -84, -94, 32, -110, 13, 32,
        32, 32, 18, -95, 32, -110, 32, 32, 18, 32, -68, -110, -94, -69, 18, 32,
        32, 32, -110, -95, 18, 32, -68, -110, -94, -69, -68, 18, 32, 32, -110, -66,
        18, -95, 32, -95, 32, 32, 32, -110, -95, 18, 32, -68, -110, -94, -69, 18,
        32, 32, 32, 32, -110, 13, 32, 32, 32, -68, 18, -94, -110, 32, 32, 18,
        -94, -94, -94, -110, -66, 18, -94, -94, -94, -110, -66, 18, -94, -94, -94, -110,
        -66, 32, 18, -94, -94, -110, 32, -68, 18, -94, -110, -68, 18, -94, -94, -94,
        -110, 32, 18, -94, -94, -94, -110, -66, -68, 18, -94, -94, -110, -66, 13
    };

    static class Logos {
        static final byte[] LOGO_ULTIMORA = new byte[] {
            32, 32, 32, 18, -98, 32, -110, -95, 32, 18, 32, -95, 32, -110, 32, 18,
            32, 32, 32, 32, -110, -95, 18, 32, -110, -95, 18, 32, -68, -110, -84, 18,
            32, -95, -110, -84, 18, 32, 32, 32, -110, -69, 18, 32, 32, 32, 32, -110,
            -69, 18, -66, 32, 32, -68, -110, 13, 32, 32, 32, 18, 32, -110, -95, 32,
            18, 32, -95, 32, -110, 32, 18, -94, -69, 32, -94, -110, -66, 18, 32, -110,
            -95, 18, 32, 32, 32, 32, -110, 32, 18, -95, 32, -94, -69, -110, -95, 18,
            32, -68, -110, -94, 18, -66, -110, -66, 18, 32, -84, -94, 32, -110, 13, 32,
            32, 32, 18, 32, 32, 32, 32, -95, 32, 32, 32, -95, 32, -110, 32, 32,
            18, 32, -110, -95, 18, 32, -110, -95, -95, 18, 32, -110, 32, 18, -95, 32,
            32, 32, -110, -95, 18, 32, -110, -95, 18, -95, -110, -94, -69, 18, 32, -84,
            -94, 32, -110, 13, 32, 32, 32, -68, 18, -94, -94, -110, -66, -68, 18, -94,
            -94, -94, -110, -68, 18, -94, -110, 32, 32, 18, -94, -110, -66, 18, -94, -110,
            -66, 32, 18, -94, -110, 32, 32, 18, -94, -94, -94, -110, 32, 18, -94, -110,
            -66, -68, 18, -94, -110, -66, 18, -94, -110, -66, 32, 18, -94, -110, 13
        };

        static final byte[] LOGO_POLITICA = new byte[] {
            32, 32, 32, 32, 32, 18, -98, 32, 32, 32, -68, -110, -84, 18, 32, 32,
            32, -110, -69, 18, 32, -110, -95, 32, 32, 18, 32, -110, -95, 18, 32, 32,
            32, 32, -110, -95, 18, 32, -110, -95, 18, -66, 32, 32, 32, -110, -84, 18,
            32, 32, 32, -110, -69, 13, 32, 32, 32, 32, 32, 18, 32, -84, -94, 32,
            -95, 32, -94, -69, -110, -95, 18, 32, -110, -95, 32, 32, 18, 32, -110, -95,
            18, -94, -69, 32, -94, -110, -66, 18, 32, -110, -95, 18, 32, -84, -94, -94,
            -95, 32, -94, -69, -110, -95, 13, 32, 32, 32, 32, 32, 18, 32, -84, -94,
            -110, -66, 18, -95, 32, 32, 32, -110, -95, 18, 32, -68, -110, -94, -69, 18,
            32, -110, -95, 32, 18, -95, 32, -110, 32, 32, 18, 32, -110, -95, 18, 32,
            32, 32, 32, -95, 32, -94, -69, -110, -95, 13, 32, 32, 32, 32, 32, 18,
            -94, -110, -66, 32, 32, 32, 18, -94, -94, -94, -110, 32, 18, -94, -94, -94,
            -110, -66, 18, -94, -110, -66, 32, -68, 18, -94, -110, 32, 32, 18, -94, -110,
            -66, -68, 18, -94, -94, -94, -110, -68, 18, -94, -110, 32, -68, -66, 13
        };

        static final byte[] LOGO_ECONOMIA = new byte[] {
            32, 32, 32, 32, 18, -98, 32, 32, 32, -110, -95, 18, -66, 32, 32, -110,
            -95, 18, -66, 32, 32, -68, -95, 32, -110, -69, 18, 32, -110, -95, 18, -66,
            32, 32, -68, -95, 32, -110, -69, 18, -66, -110, -95, 18, 32, -110, -95, 18,
            -66, 32, 32, -68, -110, 13, 32, 32, 32, 32, 18, 32, -68, -110, -94, 32,
            18, 32, -84, -94, -110, -66, 18, 32, -84, -94, 32, -95, 32, 32, 32, -110,
            -95, 18, 32, -84, -94, 32, -95, 32, 32, 32, -110, -95, 18, 32, -110, -95,
            18, 32, -84, -94, 32, -110, 13, 32, 32, 32, 32, 18, 32, -68, -110, -94,
            -69, 18, 32, 32, 32, -110, -95, 18, 32, 32, 32, 32, -95, 32, -69, 32,
            -110, -95, 18, 32, 32, 32, 32, -95, 32, -95, -95, -110, -95, 18, 32, -110,
            -95, 18, 32, -84, -94, 32, -110, 13, 32, 32, 32, 32, 18, -94, -94, -94,
            -110, -66, -68, 18, -94, -94, -110, -66, -68, 18, -94, -94, -110, -66, -68, 18,
            -94, -110, 32, 18, -94, -110, -66, -68, 18, -94, -94, -110, -66, -68, 18, -94,
            -110, 32, -68, -66, 18, -94, -110, -66, 18, -94, -110, -66, 32, 18, -94, -110,
            13
        };

        static final byte[] LOGO_DALLITALIA = new byte[] {
            18, -98, 32, 32, 32, -110, -69, -84, 18, 32, 32, 32, -110, -69, 18, 32,
            -110, -95, 32, 32, 18, 32, -110, -95, 32, 18, -95, -110, -95, 18, 32, -110,
            -95, 18, 32, 32, 32, 32, -110, -95, 18, -66, 32, 32, -68, -95, 32, -110,
            32, 32, 18, -95, 32, -110, -84, 18, 32, 32, 32, -110, -69, 18, 32,
            -84, -94, 32, -95, 32, -94, -69, -110, -95, 18, 32, -110, -95, 32, 32, 18,
            32, -110, -95, 32, 32, 32, 18, 32, -110, -95, 18, -94, -69, 32, -94, -110,
            -66, 18, 32, -84, -94, 32, -95, 32, -110, 32, 32, 18, -95, 32, -95, 32,
            -94, -69, -110, -95, 18, 32, 32, 32, 32, -95, 32, -94, -69, -110, -95,
            18, 32, 32, 32, -110, -95, 18, 32, 32, 32, -110, -95, 32, 18, 32, -110,
            -95, 32, 18, -95, 32, -110, 32, 32, 18, 32, -84, -94, 32, -95, 32, 32,
            32, -95, 32, -95, 32, -94, -69, -110, -95, 18, -94, -94, -94, -110, -66,
            -68, 18, -94, -110, 32, -68, -66, 18, -94, -94, -94, -110, -66, 18, -94, -94,
            -94, -110, -66, 32, 18, -94, -110, -66, 32, -68, 18, -94, -110, 32, 32, 18,
            -94, -110, -66, 32, 18, -94, -110, -68, 18, -94, -94, -94, -110, -68, 18, -94,
            -110, -68, 18, -94, -110, 32, -68, -66
        };

        static final byte[] LOGO_DALMONDO = new byte[] {
            32, 32, 18, -98, 32, 32, 32, -110, -69, -84, 18, 32, 32, 32, -110, -69,
            18, 32, -110, -95, 32, 32, 32, 18, -95, 32, -110, -69, 18, -66, -110, -95,
            18, -66, 32, 32, -68, -95, 32, -110, -69, 18, 32, -95, 32, 32, -68, -110,
            32, 18, -66, 32, 32, -68, -110, 13, 32, 32, 18, 32, -84, -94, 32, -95,
            32, -94, -69, -110, -95, 18, 32, -110, -95, 32, 32, 32, 18, -95, 32, 32,
            32, -110, -95, 18, 32, -84, -94, 32, -95, 32, 32, 32, -95, 32, -94, -69,
            -110, -95, 18, 32, -84, -94, 32, -110, 13, 32, 32, 18, 32, 32, 32, 32,
            -95, 32, -94, -69, -110, -95, 18, 32, 32, 32, -110, -95, 32, 18, -95, 32,
            -95, -95, -110, -95, 18, 32, 32, 32, 32, -95, 32, -69, 32, -95, 32, 32,
            32, -110, -95, 18, 32, 32, 32, 32, -110, 13, 32, 32, 18, -94, -94, -94,
            -110, -66, -68, 18, -94, -110, 32, -68, -66, 18, -94, -94, -94, -110, -66, 32,
            -68, 18, -94, -110, 32, -68, -66, -68, 18, -94, -94, -110, -66, -68, 18, -94,
            -110, 32, 18, -94, -110, -68, 18, -94, -94, -94, -110, 32, -68, 18, -94, -94,
            -110, -66, 13
        };

        static final byte[] LOGO_CULTURE = new byte[] {
            32, 32, 32, 32, 32, 32, 18, -98, -66, 32, 32, -110, -95, 18, 32, -110,
            -95, 32, 18, 32, -95, 32, -110, 32, 18, 32, 32, 32, 32, -110, -95, 18,
            32, -110, -95, 32, 18, 32, -95, 32, 32, -68, -110, -69, 18, 32, 32, 32,
            -110, -95, 13, 32, 32, 32, 32, 32, 32, 18, 32, -84, -94, -110, -66, 18,
            32, -110, -95, 32, 18, 32, -95, 32, -110, 32, 18, -94, -69, 32, -94, -110,
            -66, 18, 32, -110, -95, 32, 18, 32, -95, 32, -110, -94, 18, -66, -110, -66,
            18, 32, -68, -110, -94, 13, 32, 32, 32, 32, 32, 32, 18, 32, 32, 32,
            -110, -95, 18, 32, 32, 32, 32, -95, 32, 32, 32, -95, 32, -110, 32, 32,
            18, 32, 32, 32, 32, -95, 32, -95, -110, -94, -69, 18, 32, -68, -110, -94,
            -69, 13, 32, 32, 32, 32, 32, 32, -68, 18, -94, -94, -110, -66, -68, 18,
            -94, -94, -110, -66, -68, 18, -94, -94, -94, -110, -68, 18, -94, -110, 32, 32,
            -68, 18, -94, -94, -110, -66, -68, 18, -94, -110, -68, 18, -94, -110, -66, 18,
            -94, -94, -94, -110, -66, 13
        };

        static final byte[] LOGO_BREVICALCIO = new byte[] {
            32, 32, 32, 32, 32, 18, -98, -66, 32, 32, -110, -95, 18, -66, 32, 32,
            -68, -95, 32, -110, 32, 32, -84, 18, 32, 32, 32, -95, -110, -95, 18, -66,
            32, 32, -68, -110, 13, 32, 32, 32, 32, 32, 18, 32, -84, -94, -110, -66,
            18, 32, -84, -94, 32, -95, 32, -110, 32, 32, 18, -95, 32, -94, -94, -95,
            -110, -95, 18, 32, -84, -94, 32, -110, 32, 32, 32, 5, -62, -46, -59, -42,
            -55, 13, 32, 32, 32, 32, 32, 18, -98, 32, 32, 32, -110, -95, 18, 32,
            -84, -94, 32, -95, 32, 32, 32, -95, 32, 32, 32, -95, -110, -95, 18, 32,
            32, 32, 32, -110, 32, 32, 32, 5, -94, -94, -94, -94, -94, 13, 32, 32,
            32, 32, 32, -98, -68, 18, -94, -94, -110, -66, 18, -94, -110, -66, 32, 18,
            -94, -110, -68, 18, -94, -94, -94, -110, 32, 18, -94, -94, -94, -110, -68, -66,
            -68, 18, -94, -94, -110, -66, 13
        };

        static final byte[] LOGO_CALCIOSQUADRE = new byte[] {
            32, 32, 32, 32, 18, -98, -66, 32, 32, -110, -95, 18, -66, 32, 32, -68,
            -95, 32, -110, 32, 32, -84, 18, 32, 32, 32, -95, -110, -95, 18, -66, 32,
            32, -68, -110, 13, 32, 32, 32, 32, 18, 32, -84, -94, -110, -66, 18, 32,
            -84, -94, 32, -95, 32, -110, 32, 32, 18, -95, 32, -94, -94, -95, -110, -95,
            18, 32, -84, -94, 32, -110, 32, 32, 32, 5, -45, -47, -43, -63, -60, -46,
            -59, 13, 32, 32, 32, 32, 18, -98, 32, 32, 32, -110, -95, 18, 32, -84,
            -94, 32, -95, 32, 32, 32, -95, 32, 32, 32, -95, -110, -95, 18, 32, 32,
            32, 32, -110, 32, 32, 32, 5, -94, -94, -94, -94, -94, -94, -94, 13, 32,
            32, 32, 32, -98, -68, 18, -94, -94, -110, -66, 18, -94, -110, -66, 32, 18,
            -94, -110, -68, 18, -94, -94, -94, -110, 32, 18, -94, -94, -94, -110, -68, -66,
            -68, 18, -94, -94, -110, -66, 13
        };

        static final byte[] LOGO_ALTRISPORT = new byte[] {
            32, 32, 32, 32, 32, 32, 32, 32, 32, 18, -98, -66, 32, 32, -110, 32,
            18, 32, 32, 32, -68, -110, 5, -63, -52, -44, -46, -55, 18, -98, -95, 32,
            32, -68, -95, 32, 32, 32, 32, -110, 13, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 18, -69, -68, -110, -94, 32, 18, 32, -94, -69, 32, -110, -84, 18,
            32, 32, 32, -68, -95, -68, -110, -94, 18, -84, -110, 32, 32, 18, 32, -110,
            -95, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, -94, -94, 18, -66, -110,
            -95, 18, 32, -94, -94, -110, -66, 18, -95, 32, -110, -94, 18, -66, 32, -95,
            -110, -95, 18, -95, -110, -94, 32, 32, 18, 32, -110, -95, 13, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 18, -94, -94, -94, -110, 32, 18, -94, -110, 32,
            32, 32, 32, 18, -94, -94, -94, -110, -66, -68, -66, -68, 18, -94, -110, 32,
            32, 18, -94, -110, -66, 13
        };

        static final byte[] LOGO_MOTORI = new byte[] {
            32, 32, 32, 32, 32, 32, 18, -98, -95, 32, -110, -69, 18, -66, 32, -110,
            -84, 18, 32, 32, 32, -110, -69, 18, 32, 32, 32, 32, -110, -95, 18, -66,
            32, 32, -68, -95, 32, 32, 32, -68, -95, 32, -110, 13, 32, 32, 32, 32,
            32, 32, 18, -95, 32, 32, 32, 32, -95, 32, -94, -69, -110, -95, 18, -94,
            -69, 32, -94, -110, -66, 18, 32, -84, -94, 32, -95, 32, -110, -94, 18, -66,
            -84, -95, 32, -110, 13, 32, 32, 32, 32, 32, 32, 18, -95, 32, -95, -95,
            32, -95, 32, 32, 32, -110, -95, 32, 18, -95, 32, -110, 32, 32, 18, 32,
            32, 32, 32, -95, 32, -110, 32, 18, -68, -110, -94, 18, -95, 32, -110, 13,
            32, 32, 32, 32, 32, 32, -68, 18, -94, -110, 32, -68, 18, -94, -110, 32,
            18, -94, -94, -94, -110, 32, 32, -68, 18, -94, -110, 32, 32, -68, 18, -94,
            -94, -110, -66, -68, 18, -94, -110, 32, 18, -94, -94, -110, -68, 18, -94, -110,
            13
        };

        static final byte[] LOGO_SPORTBREVISSIME = new byte[] {
            32, 32, 32, 18, -98, -66, 32, 32, -110, 32, 18, 32, 32, 32, -68, -110,
            -84, 18, 32, 32, 32, -68, -95, 32, 32, -68, -95, 32, 32, 32, 32, -110,
            13, 32, 32, 32, 18, -69, -68, -110, -94, 32, 18, 32, -94, -69, 32, -95,
            32, -94, -94, 32, -95, -68, -110, -94, 18, -84, -110, 32, 32, 18, 32, -110,
            -95, 32, 32, 5, -62, -46, -59, -42, -55, -45, -45, -55, -51, -59, 13, 32,
            32, 32, -98, -94, -94, 18, -66, -110, -95, 18, 32, -94, -94, -110, -66, 18,
            -95, 32, 32, 32, 32, -95, -110, -95, 18, -95, -110, -94, 32, 32, 18, 32,
            -110, -95, 32, 32, 5, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, 13,
            32, 32, 32, 18, -98, -94, -94, -94, -110, 32, 18, -94, -110, 32, 32, 32,
            32, 18, -94, -94, -94, -110, -66, -68, -66, -68, 18, -94, -110, 32, 32, 18,
            -94, -110, -66, 13
        };

        static final byte[] LOGO_PRIMOPIANO = new byte[] {
            18, -98, 32, 32, 32, -68, -95, 32, 32, 32, -110, -69, 18, 32, -110, -95,
            18, 32, -68, -110, -84, 18, 32, -110, -84, 18, 32, 32, 32, -110, -69, 32,
            18, 32, 32, 32, -68, -95, 32, -110, -84, 18, 32, 32, -68, -95, -68, -95,
            -110, -95, 18, -66, 32, 32, -68, -110, 13, 18, 32, -84, -94, 32, -95, 32,
            -94, -69, -110, -95, 18, 32, -110, -95, 18, 32, 32, 32, 32, -95, 32, -94,
            -69, -110, -95, 32, 18, 32, -84, -94, 32, -95, 32, -95, -84, -94, 32, -95,
            32, 32, -110, -95, 18, 32, -94, -94, 32, -110, 13, 18, 32, -84, -94, -110,
            -66, 18, -95, 32, -94, 32, -110, 32, 18, 32, -110, -95, 18, 32, -110, -95,
            -95, 18, 32, -95, 32, 32, 32, -110, -95, 32, 18, 32, -84, -94, -110, -66,
            18, -95, 32, -95, -84, -94, 32, -95, -110, -95, 18, -69, -110, -95, 18, 32,
            32, 32, 32, -110, 13, 18, -94, -110, -66, 32, 32, -68, 18, -94, -110, 32,
            18, -94, -110, -66, 18, -94, -110, -66, 18, -94, -110, -66, 32, 18, -94, -110,
            32, 18, -94, -94, -94, -110, 32, 32, 18, -94, -110, -66, 32, 32, -68, 18,
            -94, -110, -68, -66, 32, 18, -94, -110, -68, -66, -68, -66, -68, 18, -94, -94,
            -110, -66, 13
        };

        static final byte[] LOGO_NOSTOP24H = new byte[] {
            32, 18, -98, -66, 32, 32, -110, -69, 18, -95, -110, -95, 32, 32, 18, -95,
            -110, -95, 32, 32, 32, 32, 18, 5, -95, -68, -95, -110, -95, 18, -66, -94,
            -68, -110, 32, 32, -84, 18, -84, -69, -110, -69, 18, -94, 32, -94, -110, -84,
            18, -84, -69, -110, -69, 18, 32, -94, -68, -110, 13, 32, 18, -98, -94, -110,
            -84, 18, 32, -110, -66, 18, -95, -68, 32, -110, -69, 18, -95, 32, 32, -110,
            -69, 32, 32, 18, 5, -95, -84, 32, -110, -95, 18, 32, -110, 32, 18, 32,
            -110, 32, 32, -68, 18, -68, -110, -94, 32, 32, 18, 32, -110, 32, 18, -95,
            -110, -95, 18, -95, -110, -95, 18, 32, -110, -94, 18, -84, -110, 13, 32, -98,
            -84, 18, 32, -68, -110, -69, -68, 18, -94, 32, -110, -66, 18, -95, -110, -95,
            18, -95, -110, -95, 32, 32, 18, 5, -95, -110, -95, 18, -95, -110, -95, 18,
            32, -110, 32, 18, 32, -110, 32, 32, -84, -69, 18, -95, -110, -95, 32, 18,
            32, -110, 32, 18, -95, -110, -95, 18, -95, -110, -95, 18, 32, -110, 13, 32,
            18, -98, -94, -94, -94, -110, -66, 32, 32, 18, -94, -110, 32, -68, -66, -68,
            -66, 32, 32, 5, -68, -66, -68, -66, -68, 18, -94, -110, -66, 32, 32, 32,
            18, -94, -94, -110, 32, 32, 18, -94, -110, 32, 32, 18, -94, -94, -110, 32,
            18, -94, -110, 13
        };

        static final byte[] LOGO_SPECIALE = new byte[] {
            32, 32, 32, 32, 18, -98, -66, 32, -110, -95, 18, -95, 32, 32, -110, -69,
            18, -95, 32, 32, -110, -95, -84, 18, 32, 32, -68, -110, 32, 18, 32, 32,
            -110, 32, 18, -66, 32, 32, -110, -69, 18, -95, -110, -95, 32, 32, 18, -95,
            32, 32, -110, -95, 13, 32, 32, 32, 32, 18, -69, -110, -94, 32, 18, -95,
            -68, -66, -110, -95, 18, -95, -68, -110, -69, 32, 18, -95, -110, -95, 32, 18,
            -94, -110, 32, 18, -95, -110, -95, 32, 18, 32, -110, 32, 18, -95, -110, -95,
            18, -95, -110, -95, 32, 32, 18, -95, -68, -110, -69, 13, 32, 32, 32, 32,
            -94, 18, -66, -110, -95, 18, -95, -84, -94, -110, 32, 18, -95, -68, -110, -94,
            -69, 18, -95, -68, -110, -94, 18, 32, -110, 32, 18, -66, -68, -110, 32, 18,
            32, -94, -69, -110, -95, 18, -95, -68, -110, -94, -69, 18, -95, -68, -110, -94,
            -69, 13, 32, 32, 32, 32, 18, -94, -94, -110, 32, -68, -66, 32, 32, -68,
            18, -94, -94, -110, -66, 32, 18, -94, -94, -110, -66, 32, 18, -94, -94, -110,
            32, 18, -94, -110, 32, -68, -66, -68, 18, -94, -94, -110, -66, -68, 18, -94,
            -94, -110, -66, 13
        };

        static final byte[] LOGO_ATLANTECRISI = new byte[] {
            18, -98, -66, 32, -68, -95, 32, 32, 32, -95, -110, -95, 32, 32, 18, -66,
            32, -68, -95, -68, -95, -110, -95, 18, 32, 32, 32, -110, -95, 18, 32, 32,
            -110, -95, 32, 18, -66, 32, -68, -95, 32, 32, -110, -69, 18, 32, -110, -84,
            18, 32, 32, -110, -95, 18, 32, -110, 13, 18, 32, -110, 32, 18, 32, -110,
            32, 18, -95, 32, -110, 32, 18, -95, -110, -95, 32, 32, 18, 32, -110, 32,
            18, 32, -95, 32, 32, -110, -95, 32, 18, 32, -110, -95, 32, 18, 32, -110,
            -94, 32, 32, 18, 32, -110, 32, 18, -94, -95, -110, -95, 18, -95, -110, -95,
            18, 32, -110, -68, 18, -68, -110, -94, 32, 18, 32, -110, 13, 18, 32, -94,
            32, -110, 32, 18, -95, 32, -110, 32, 18, -95, -68, -110, -94, -69, 18, 32,
            -94, 32, -95, -110, -95, 18, -69, -110, -95, 32, 18, 32, -110, -95, 32, 18,
            32, -110, -94, -69, 32, 18, 32, -110, -94, 18, 32, -95, -84, -69, -110, -69,
            18, 32, -110, -84, -94, 18, -66, -110, -95, 18, 32, -110, 13, 18, -94, -110,
            32, 18, -94, -110, 32, -68, 18, -94, -110, 32, -68, 18, -94, -94, -110, -66,
            18, -94, -110, 32, 18, -94, -110, -68, -66, -68, -66, 32, 18, -94, -110, -66,
            32, 18, -94, -94, -110, -66, 32, -68, 18, -94, -110, -66, -68, -66, -68, -66,
            18, -94, -110, -68, 18, -94, -94, -110, 32, 18, -94, -110, 13
        };

        static final byte[] LOGO_CITTADINI = new byte[] {
            32, 32, 32, 18, -98, -66, 32, 32, -110, -95, 18, 32, -110, -95, 18, 32,
            32, 32, 32, 32, -95, 32, 32, 32, 32, -110, -84, 18, 32, 32, 32, -110,
            -69, 18, 32, 32, 32, -110, -69, 18, -95, 32, -95, 32, -110, -69, 18, 32,
            -95, 32, -110, 13, 32, 32, 32, 18, 32, -84, -94, -110, -66, 18, 32, -110,
            -95, 18, -94, -69, 32, -84, -94, -110, -68, 18, -94, 32, -84, -94, -95, 32,
            -94, -69, -110, -95, 18, 32, -84, -94, 32, -95, 32, -95, 32, 32, 32, -95,
            32, -110, 13, 32, 32, 32, 18, 32, 32, 32, -110, -95, 18, 32, -110, -95,
            32, 18, -95, 32, -110, -95, 32, 32, 32, 18, 32, -110, -95, 32, 18, -95,
            32, -94, -69, -110, -95, 18, 32, 32, 32, 32, -95, 32, -95, 32, -110, -68,
            18, 32, -95, 32, -110, 13, 32, 32, 32, -68, 18, -94, -94, -110, -66, 18,
            -94, -110, -66, 32, -68, 18, -94, -110, -66, 32, 32, 32, 18, -94, -110, -66,
            32, -68, 18, -94, -110, 32, -68, -66, 18, -94, -94, -94, -110, -66, -68, 18,
            -94, -110, -68, 18, -94, -110, 32, 18, -94, -110, -68, 18, -94, -110, 13
        };
    }
}
