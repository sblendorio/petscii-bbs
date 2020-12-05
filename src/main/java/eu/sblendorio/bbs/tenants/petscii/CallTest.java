package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiInputOutput;
import eu.sblendorio.bbs.core.PetsciiThread;

public class CallTest extends PetsciiThread {
    @Override
    public void doLoop() throws Exception {
        for (int i=0; i<25; ++i) println();
        print("scrivi: ");
        String msg = ((PetsciiInputOutput)io).readLine(3,true);
        println("hai scritto: '"+msg+"'");
        println("premi un tasto");
        readKey();
        resetInput();
        //throw new BbsIOException();
        launch(new MenuPetscii());
    }
}
