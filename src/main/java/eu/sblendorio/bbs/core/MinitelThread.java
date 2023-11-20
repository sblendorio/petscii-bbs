package eu.sblendorio.bbs.core;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.net.Socket;

import static eu.sblendorio.bbs.core.MinitelConstants.*;
import static eu.sblendorio.bbs.core.MinitelControls.*;
import static eu.sblendorio.bbs.core.Utils.bytes;
import static eu.sblendorio.bbs.core.Utils.hex;

public abstract class MinitelThread extends BbsThread {

    private byte currentSize = TEXTSIZE_NORMAL;

    public MinitelThread() {
        setLocalEcho(true);
    }

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException {
        return new MinitelInputOutput(socket);
    }

    @Override
    public String getTerminalType() {
        return "minitel";
    }

    @Override
    public void cls() {
        write(12);
    }

    @Override

    public int getScreenColumns() {
        return 40;
    }

    @Override
    public int getScreenRows() {
        return 24;
    }


    public void writeBytesP(int n) {
        // Pn, Pr, Pc : Voir remarques p.95 et 96
        if (n <= 9) {
            write(0x30 + n);
        } else {
            write(0x30 + n / 10);
            write(0x30 + n % 10);
        }
    }

    public void writeBytesPRO(int n) {  // Voir p.134
        write(ESC);  // 0x1B
        switch (n) {
            case 1:
                write(0x39);
                break;
            case 2:
                write(0x3A);
                break;
            case 3:
                write(0x3B);
                break;
        }
    }

    public void moveCursorDown(int n) {  // Voir p.94
        if (n == 1)
            write(LF);
        else if (n > 1) {
            // Curseur vers le bas de n rangées. Arrêt en bas de l'écran.
            write(bytes(MinitelControls.CSI));   // 0x1B 0x5B
            writeBytesP(n);   // Pn : Voir section Private ci-dessous
            write(0x42);
        }
    }

    public void attributes(byte... attributes) {
        for (int i = 0; i < attributes.length; i++) {
            byte attribute = attributes[i];

            write(ESC);  // Accès à la grille C1 (voir p.92)
            write(attribute);
            if (attribute == TEXTSIZE_DOUBLE_HEIGHT || attribute == TEXTSIZE_DOUBLE_ALL) {
                moveCursorDown(1);
                currentSize = attribute;
            } else if (attribute == TEXTSIZE_NORMAL || attribute == TEXTSIZE_DOUBLE_WIDTH) {
                currentSize = attribute;
            }
        }
    }

    public void gotoXY(int x, int y) {
        // origin (0,0)
        write(MOVEXY, 0x41 + y, 0x41 + x);
    }

    private Integer minitelType = null;
    private static final String MINITEL_TYPE_DETECTION = "MINITEL_TYPE_DETECTION";

    public int getMinitelType() throws IOException, InterruptedException {
        if (minitelType != null)
            return minitelType;

        try {
            minitelType = NumberUtils.toInt((String) getRoot().getCustomObject(MINITEL_TYPE_DETECTION));
        } catch (NullPointerException | ClassCastException e) {
            minitelType = detectMinitelType();
            getRoot().setCustomObject(MINITEL_TYPE_DETECTION, minitelType);
            return minitelType;
        }
        if (minitelType != null && minitelType != 0)
            return minitelType;

        minitelType = detectMinitelType();
        getRoot().setCustomObject(MINITEL_TYPE_DETECTION, minitelType);
        return minitelType;
    }

    private int detectMinitelType() throws InterruptedException, IOException {
        write(0x1b, 0x39, 0x7b);
        Thread.sleep(1000L);
        byte[] out = resetInput();
        String outString = hex(out);
        if (outString.contains(STRING_1B))
            return TYPE_1B;
        else if (outString.contains(STRING_1B_OLD))
            return TYPE_1B_OLD;
        else if (outString.contains(STRING_2))
            return TYPE_2;
        else if (outString.contains(STRING_12))
            return TYPE_12;
        else
            return TYPE_UNKNOWN;
    }
}