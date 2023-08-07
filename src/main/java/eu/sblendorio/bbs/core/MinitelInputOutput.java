package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.MinitelControls.CURSOR_LEFT;

public class MinitelInputOutput extends BbsInputOutput {

    private final static byte SS2 = 0x19;
    private final static byte SI = 0x0F;
    private final static byte ACCENT_GRAVE = 0x41;
    private final static byte ACCENT_AIGU = 0x42;
    private final static byte ACCENT_CIRCONFLEXE = 0x43;
    private final static byte TREMA = 0x48;
    private final static byte CEDILLE = 0x4B;


    public MinitelInputOutput(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public byte[] newlineBytes() {
        return new byte[] { 13, 10 };
    }

    @Override
    public byte[] backspace() {
        return new byte[] {CURSOR_LEFT, ' ', CURSOR_LEFT};
    }

    @Override
    public int backspaceKey() {
        return MinitelControls.BACKSPACE_KEY;
    }

    @Override
    public boolean isNewline(int ch) {
        return ch == AsciiKeys.CR || ch == AsciiKeys.LF;
    }

    @Override
    public boolean isBackspace(int ch) {
        return ch == AsciiKeys.BACKSPACE || ch == AsciiKeys.DELETE;
    }

    @Override
    public void writeDoublequotes() {
        write(34);
    }

    @Override
    public int convertToAscii(int ch) {
        return ch;
    }

    @Override
    public boolean quoteMode() {
        return false;
    }

    @Override
    public void writeBackspace() {
        write(AsciiKeys.BACKSPACE, ' ', AsciiKeys.BACKSPACE);
    }

    @Override
    public void print(String msg) {
        if (msg == null) return;
        for (char c: msg.toCharArray()) {
            try {
                if (isDiacritic(c)) {
                    printDiacritic(c);
                } else if (Character.isLetter(c)) {
                    out.write(Character.toString(c).getBytes(StandardCharsets.UTF_8));
                } else {
                    out.write(c);
                }
            } catch (IOException e) {}
        }
    }

    private void printDiacritic(char ch) {
        char car;
        switch (ch) {
/*
            case 'à': car = 'a'; write(SS2, ACCENT_GRAVE); break;
            case 'á': car = 'a'; write(SS2, ACCENT_AIGU); break;
            case 'â': car = 'a'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ä': car = 'a'; write(SS2, TREMA); break;
            case 'è': car = 'e'; write(SS2, ACCENT_GRAVE); break;
            case 'é': car = 'e'; write(SS2, ACCENT_AIGU); break;
            case 'ê': car = 'e'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ë': car = 'e'; write(SS2, TREMA); break;
            case 'ì': car = 'i'; write(SS2, ACCENT_GRAVE); break;
            case 'í': car = 'i'; write(SS2, ACCENT_AIGU); break;
            case 'î': car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ï': car = 'i'; write(SS2, TREMA); break;
            case 'ò': car = 'o'; write(SS2, ACCENT_GRAVE); break;
            case 'ó': car = 'o'; write(SS2, ACCENT_AIGU); break;
            case 'ô': car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ö': car = 'o'; write(SS2, TREMA); break;
            case 'ù': car = 'u'; write(SS2, ACCENT_GRAVE); break;
            case 'ú': car = 'u'; write(SS2, ACCENT_AIGU); break;
            case 'û': car = 'u'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ü': car = 'u'; write(SS2, TREMA); break;
            case 'ç': car = 'c'; write(SS2, CEDILLE); break;
*/
            case 'à': car = 'a'; write(SS2, ACCENT_GRAVE); break;
            case 'á': car = 'a'; write(SS2, ACCENT_GRAVE); break;
            case 'â': car = 'a'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ä': car = 'a'; write(SS2, TREMA); break;
            case 'è': car = 'e'; write(SS2, ACCENT_GRAVE); break;
            case 'é': car = 'e'; write(SS2, ACCENT_AIGU); break;
            case 'ê': car = 'e'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ë': car = 'e'; write(SS2, TREMA); break;
            case 'ì': car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'í': car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'î': car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ï': car = 'i'; write(SS2, TREMA); break;
            case 'ò': car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ó': car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ô': car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ö': car = 'o'; write(SS2, TREMA); break;
            case 'ù': car = 'u'; write(SS2, ACCENT_GRAVE); break;
            case 'ú': car = 'u'; write(SS2, ACCENT_GRAVE); break;
            case 'û': car = 'u'; write(SS2, ACCENT_CIRCONFLEXE); break;
            case 'ü': car = 'u'; write(SS2, TREMA); break;
            case 'ç': car = 'c'; write(SS2, CEDILLE); break;

            // Pour les cas où on essaye d'afficher un caractère diacritique majuscule,
            // ce que ne peut pas faire le Minitel.

            case 'À': car = 'A'; break;
            case 'Á': car = 'A'; break;
            case 'Â': car = 'A'; break;
            case 'Ä': car = 'A'; break;
            case 'È': car = 'E'; break;
            case 'É': car = 'E'; break;
            case 'Ê': car = 'E'; break;
            case 'Ë': car = 'E'; break;
            case 'Ì': car = 'I'; break;
            case 'Í': car = 'I'; break;
            case 'Î': car = 'I'; break;
            case 'Ï': car = 'I'; break;
            case 'Ò': car = 'O'; break;
            case 'Ó': car = 'O'; break;
            case 'Ô': car = 'O'; break;
            case 'Ö': car = 'O'; break;
            case 'Ù': car = 'U'; break;
            case 'Ú': car = 'U'; break;
            case 'Û': car = 'U'; break;
            case 'Ü': car = 'U'; break;
            case 'Ç': car = 'C'; break;

            default: car = ' ';
        }
        write(car);

    }

    private boolean isDiacritic(char ch) {
        return "àáâäèéêëìíîïòóôöùúûüçÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ".indexOf(ch) != -1;
    }
}
