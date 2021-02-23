package eu.sblendorio.bbs.tenants.petscii;

public class IndieCampus extends GoogleBloggerProxy {

    public IndieCampus() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://iononsoleggere.blogspot.com/";
        this.labels = "BBS";
        this.showTimestamp = false;
        this.pageSize = 7;
        this.screenLines = 18;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/indiecampus.seq");
}
