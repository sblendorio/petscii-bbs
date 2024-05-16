package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.EnigmaCommons;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import eu.sblendorio.bbs.tenants.mixed.EnigmaCommons.EnigmaStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import static eu.sblendorio.bbs.core.Utils.STR_LETTER;
import static eu.sblendorio.bbs.core.Utils.setOfChars;

public class EnigmaPetscii extends PetsciiThread {

    private EnigmaStatus machine = new EnigmaStatus();

    public EnigmaPetscii() {
    }

    @Override
    public void doLoop() throws Exception {
        write(PetsciiColors.GREY3, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK);
        EnigmaCommons.menu(this, machine);
    }

}
