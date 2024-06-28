package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.MinitelInputOutput;
import eu.sblendorio.bbs.tenants.ascii.GoogleBloggerProxyAscii;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.Utils.bytes;

@Hidden
public class DisinformaticoMinitel extends GoogleBloggerProxyAscii {

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception { HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1); }

    public DisinformaticoMinitel() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://attivissimo.blogspot.com";
        this.pageSize = 7;
        this.logoSize = 2;
    }

    protected String downstreamTransform(String s) {
        return s
            .replaceAll("^.*?<!--INSERT STORY/NEWS HTML BELOW-->([\\n\\s\\r]*<p>)?\\s*", "")
                .replaceAll("(?is)<i>\\[[^\\]<>\n\r]*\\]</i></p>", "")
        ;
    }

    private static final byte[] LOGO_BYTES =  bytes(readBinaryFile("minitel/disinformatico.vdt"),30,10, 0x1b, 0x3a, 0x69, 0x43, 17);

}
