package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;

public class CtrlAltAscii extends WordpressProxyAscii {

    public CtrlAltAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://ctrlalt.museum";
        this.pageSize = 7;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = "Ctrl Alt Museum".getBytes(StandardCharsets.ISO_8859_1);

}
