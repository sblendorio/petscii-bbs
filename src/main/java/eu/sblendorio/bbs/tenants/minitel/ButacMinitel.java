package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.tenants.ascii.WordpressProxyAscii;
import org.apache.commons.lang3.StringUtils;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class ButacMinitel extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public ButacMinitel(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

    public ButacMinitel() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.butac.it";
        this.showAuthor = true;
        this.pageSize = 9;
        this.mainLogoSize = 3;
    }

    private static final byte[] LOGO_BYTES =  bytes(readBinaryFile("minitel/butac.vdt"),30,10,10, 0x1b, 0x3a, 0x69, 0x43, 17);

}
