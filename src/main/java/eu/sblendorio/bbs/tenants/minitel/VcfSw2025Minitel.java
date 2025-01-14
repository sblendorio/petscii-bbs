package eu.sblendorio.bbs.tenants.minitel;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import eu.sblendorio.bbs.core.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static eu.sblendorio.bbs.core.MinitelControls.*;

public class VcfSw2025Minitel extends MinitelThread {

    public VcfSw2025Minitel() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        cls();

        write(readBinaryFile("minitel/vcfsw2025.vdt"));
        String[] matrix = BlockGraphicsMinitel.stringToQr("vcfsw.org", ErrorCorrectionLevel.L);
        int len = matrix.length+1;
        List<String> listMatrix = new ArrayList<>();
        listMatrix.add(StringUtils.repeat('.', matrix[0].length()+1));
        for (String line: matrix) {
            listMatrix.add("." + line);
        }
        String[] strMatrix = listMatrix.toArray(new String[len]);
        gotoXY(0,16);
        write(GRAPHICS_MODE);
        write(BlockGraphicsMinitel.getRenderedMidres(28 , strMatrix, false, true));
        write(TEXT_MODE);

        gotoXY(29,14);
        attributes(TEXTSIZE_DOUBLE_HEIGHT);
        print("vcfsw.org");
        attributes(TEXTSIZE_NORMAL);

        write(CURSOR_OFF);
        flush(); resetInput();
        readKey();

        write(CURSOR_ON);

    }
}
