package eu.sblendorio.bbs.tenants.petscii;

public class Retrocommodore extends GoogleBloggerProxy {

    public Retrocommodore() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://faberpixel.blogspot.com";
        this.pageSize = 10;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/retrocommodore.seq");

}
