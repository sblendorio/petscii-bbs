package eu.sblendorio.bbs.tenants.petscii;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import eu.sblendorio.bbs.core.BlockGraphicsMinitel;
import eu.sblendorio.bbs.core.BlockGraphicsPetscii;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;

import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;

public class VcfSw2025Petscii extends PetsciiThread {

    public VcfSw2025Petscii() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        cls();

        String[] matrix = BlockGraphicsMinitel.stringToQr("vcfsw.org", ErrorCorrectionLevel.M);
        String[] matrixStr = new String[matrix.length+1];
        matrixStr[0] = StringUtils.repeat('.', matrix[0].length());
        for (int i=0; i<matrix.length; i++) matrixStr[i+1] = "." + matrix[i];
        gotoXY(0,12);
        write(WHITE);
        write(BlockGraphicsPetscii.getRenderedMidres(28, matrixStr, true, true));

        write(HOME);
        write(readBinaryFile("petscii/vcfsw2025.seq"));

        flush(); resetInput();
        readKey();

    }
}
