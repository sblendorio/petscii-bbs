package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import org.zmpp.vm.Input;

import java.nio.charset.StandardCharsets;

@Hidden
public class VcfedAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public VcfedAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.vcfed.org";
    }

    private static final byte[] LOGO_BYTES = "Vintage Computer Federation".getBytes(StandardCharsets.ISO_8859_1);

    public VcfedAscii(BbsInputOutput inputOutput) {
        this();
        this.inputOutput = inputOutput;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

}
