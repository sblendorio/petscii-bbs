package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.tenants.ascii.WordpressProxyAscii;
import org.apache.commons.lang3.StringUtils;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class IlFattoQuotidianoMinitel extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public IlFattoQuotidianoMinitel(BbsInputOutput x) {
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

    public IlFattoQuotidianoMinitel() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.ilfattoquotidiano.it";
        this.pageSize = 5;
        this.mainLogoSize = 2;
    }

    private static final byte[] LOGO_BYTES =  bytes(readBinaryFile("minitel/fq.vdt"),30,10, 0x1b, 0x3a, 0x69, 0x43, 17);

}
