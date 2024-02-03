package eu.sblendorio.bbs.tenants.petscii;

public class SanremoPetscii extends GoogleBloggerProxy {

    public SanremoPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://sanremo-commenti.blogspot.com/";
        this.pageSize = 8;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/sanremo.seq");
}
