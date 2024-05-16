package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.EnigmaCommons;
import eu.sblendorio.bbs.tenants.mixed.EnigmaCommons.EnigmaStatus;

public class EnigmaAscii extends AsciiThread {
    private EnigmaStatus machine = new EnigmaStatus();
    private BbsInputOutput interfaceType = null;

    public EnigmaAscii() {
    }

    public EnigmaAscii(BbsInputOutput interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public void doLoop() throws Exception {
        if (interfaceType != null) {
            this.setBbsInputOutput(interfaceType);
        }
        EnigmaCommons.menu(this, machine);
    }

}
