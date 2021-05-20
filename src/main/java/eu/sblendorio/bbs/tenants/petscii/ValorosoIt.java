package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class ValorosoIt extends WordpressProxy {

    public ValorosoIt() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.valoroso.it";
        this.pageSize = 8;
        this.categoriesId = "148";
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/valoroso.seq");

}
