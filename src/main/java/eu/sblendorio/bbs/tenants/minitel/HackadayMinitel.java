package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.MinitelInputOutput;
import eu.sblendorio.bbs.tenants.ascii.WordpressProxyAscii;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class HackadayMinitel extends WordpressProxyAscii {
    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    public String by() { return "by"; }

    @Override
    public void initBbs() throws Exception { HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1); }

    public HackadayMinitel() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.hackaday.com";
        this.showAuthor = true;
        this.pageSize = 9;
        this.mainLogoSize = 2;
    }

    private static final byte[] LOGO_BYTES =  bytes(readBinaryFile("minitel/hackaday.vdt"),30,10, 0x1b, 0x3a, 0x69, 0x43, 17);

}
