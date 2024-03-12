package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

public class AvventuraNelCastelloPetscii extends PetsciiThread {

    AvventuraNelCastelloBridge bridge;

    class Bridge extends AvventuraNelCastelloBridge {
        public Bridge(BbsThread bbs) {
            super(bbs);
        }

        @Override
        public void revOn() {
            write(PetsciiKeys.REVON);
        }

        @Override
        public void revOff() {
            write(PetsciiKeys.REVOFF);
        }
    }
    @Override
    public void doLoop() throws Exception {
        bridge = new Bridge(this);
        bridge.init("it");
        bridge.start();
    }

}
