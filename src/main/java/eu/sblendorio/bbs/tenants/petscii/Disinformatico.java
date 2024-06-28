package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class Disinformatico extends GoogleBloggerProxy {

    public Disinformatico() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://attivissimo.blogspot.com";
        this.pageSize = 6;
        this.screenLines = 19;
    }

    protected String downstreamTransform(String s) {
        return s
                .replaceAll("^.*?<!--INSERT STORY/NEWS HTML BELOW-->([\\n\\s\\r]*<p>)?\\s*", "")
                .replaceAll("(?is)<i>\\[[^\\]<>\n\r]*\\]</i></p>", "")
                .replaceAll("^\\s*(<p>\\s*)?", "")
                ;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, -127, 32, -94, -94, 32, 32, -110, 32, 5, -84, -84, 32, -84, -94, 32,
        -69, 32, 32, -69, 32, 32, -84, -69, 32, 32, 32, 32, 32, 32, 32, 32,
        32, -84, 32, -69, 13, 18, -127, 32, -110, 32, 18, -68, -110, -68, 18, 32,
        -110, 32, 18, 5, -95, -95, -110, 32, 18, -95, -110, 32, -95, -69, 18, -66,
        -110, -66, -69, 18, -84, -110, -69, 18, -68, -110, -84, 18, -94, -110, -69, 18,
        -68, -110, -66, 18, -84, -84, -110, -69, 18, -65, -69, -95, -110, -66, -69, 18,
        -65, -110, -66, 18, -65, -110, -65, 13, 18, -127, 32, -110, 32, 18, -94, -110,
        -84, 18, 32, -110, 32, 18, 5, -95, -95, -110, 32, 18, -95, -110, -94, -66,
        -95, -94, -66, -95, -95, -95, -95, -68, -94, -66, -95, 32, -95, -95, -95, -65,
        18, -66, -110, -68, -69, -95, -65, -69, -65, 18, -65, -110, 13, 18, -127, -94,
        -94, -94, -94, -94, -110, 13
    };

}
