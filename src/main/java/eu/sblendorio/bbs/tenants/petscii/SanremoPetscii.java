package eu.sblendorio.bbs.tenants.petscii;

public class SanremoPetscii extends GoogleBloggerProxy {

    public SanremoPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://sanremo-commenti.blogspot.com/";
        this.pageSize = 6;
        this.screenLines = 19;
    }

    @Override
    public String disclaimer() {
        return "Retrocampus BBS Sanremo non è una testata giornalistica ma un progetto a termine che pubblica abstract di notizie senza alcuna cadenza continuativa nè lucro citando sempre la fonte";
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/sanremo-red.seq");
}
