package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;

public class AlessandroAlbanoTelnet extends WordpressProxyAscii {

    public AlessandroAlbanoTelnet() {
        super();
        this.logo = LOGO_BYTES;
        this.secondaryLogo = SECONDARY_LOGO_BYTES;
        this.mainLogoSize = 4;
        this.secondaryLogoSize = 1;
        this.domain = "https://www.alessandroalbano.it";
        this.showAuthor = true;
        this.pageSize = 12;
        this.screenLines = 24;
        this.screenColumns = 80;
    }

    private static final byte[] LOGO_BYTES = (
        "Alessandro Albano - Formazione e Lavoro\r\n" +
            "per una vita \"inclusiva e autonoma\":\r\n" +
            "la disabilita' vista dagli occhi di un\r\n" +
            "\"non vedente\"!"
    ).getBytes(StandardCharsets.ISO_8859_1);

    private static final byte[] SECONDARY_LOGO_BYTES = (
        "Alessandro Albano"
    ).getBytes(StandardCharsets.ISO_8859_1);

}
