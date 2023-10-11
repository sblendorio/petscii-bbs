package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.tenants.petscii.WordpressProxy;

import java.nio.charset.StandardCharsets;

public class SyncroWebAscii extends WordpressProxyAscii {

    public SyncroWebAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.syncroweb.eu";
        this.pageSize = 8;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = "SyncroWeb".getBytes(StandardCharsets.ISO_8859_1);

}
