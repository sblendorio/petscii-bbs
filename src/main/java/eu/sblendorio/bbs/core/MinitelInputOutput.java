package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

import static eu.sblendorio.bbs.core.HtmlUtils.utilHtmlClean;
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
        msg = msg.replace("\r\n", "\n");
        for (char c: msg.toCharArray()) {
            try {
                if (isDiacritic(c)) {
                    printDiacritic(c);
                // } else if (Character.isLetter(c) && Character.is) {
                //     out.write(Character.toString(c).getBytes(StandardCharsets.UTF_8));
                } else if (c >= 32 && c <= 127) {
                    out.write(c);
                } else if (c == 10) {
                    newline();
                } else {
                    out.write('*');
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String htmlClean(String s) {
        return utilHtmlClean(s);
    }

    private void printDiacritic(char ch) {
        char car;
        boolean quote = false;
        switch (ch) {
/*
            case 'à' -> { car = 'a'; write(SS2, ACCENT_GRAVE); }
            case 'á' -> { car = 'a'; write(SS2, ACCENT_AIGU); }
            case 'â' -> { car = 'a'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ä' -> { car = 'a'; write(SS2, TREMA); }
            case 'è' -> { car = 'e'; write(SS2, ACCENT_GRAVE); }
            case 'é' -> { car = 'e'; write(SS2, ACCENT_AIGU); }
            case 'ê' -> { car = 'e'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ë' -> { car = 'e'; write(SS2, TREMA); }
            case 'ì' -> { car = 'i'; write(SS2, ACCENT_GRAVE); }
            case 'í' -> { car = 'i'; write(SS2, ACCENT_AIGU); }
            case 'î' -> { car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ï' -> { car = 'i'; write(SS2, TREMA); }
            case 'ò' -> { car = 'o'; write(SS2, ACCENT_GRAVE); }
            case 'ó' -> { car = 'o'; write(SS2, ACCENT_AIGU); }
            case 'ô' -> { car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ö' -> { car = 'o'; write(SS2, TREMA); }
            case 'ù' -> { car = 'u'; write(SS2, ACCENT_GRAVE); }
            case 'ú' -> { car = 'u'; write(SS2, ACCENT_AIGU); }
            case 'û' -> { car = 'u'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ü' -> { car = 'u'; write(SS2, TREMA); }
            case 'ç' -> { car = 'c'; write(SS2, CEDILLE); }
*/
            case 'à' -> { car = 'a'; write(SS2, ACCENT_GRAVE); }
            case 'á' -> { car = 'a'; write(SS2, ACCENT_GRAVE); }
            case 'â' -> { car = 'a'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ä' -> { car = 'a'; write(SS2, TREMA); }
            case 'è' -> { car = 'e'; write(SS2, ACCENT_GRAVE); }
            case 'é' -> { car = 'e'; write(SS2, ACCENT_AIGU); }
            case 'ê' -> { car = 'e'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ë' -> { car = 'e'; write(SS2, TREMA); }
            case 'ì' -> { car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'í' -> { car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'î' -> { car = 'i'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ï' -> { car = 'i'; write(SS2, TREMA); }
            case 'ò' -> { car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ó' -> { car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ô' -> { car = 'o'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ö' -> { car = 'o'; write(SS2, TREMA); }
            case 'ù' -> { car = 'u'; write(SS2, ACCENT_GRAVE); }
            case 'ú' -> { car = 'u'; write(SS2, ACCENT_GRAVE); }
            case 'û' -> { car = 'u'; write(SS2, ACCENT_CIRCONFLEXE); }
            case 'ü' -> { car = 'u'; write(SS2, TREMA); }
            case 'ç' -> { car = 'c'; write(SS2, CEDILLE); }

            // Pour les cas où on essaye d'afficher un caractère diacritique majuscule,
            // ce que ne peut pas faire le Minitel.

            case 'À', 'Á' -> { quote = true; car = 'A'; }
            case 'È', 'É' -> { quote = true; car = 'E'; }
            case 'Ì', 'Í' -> { quote = true; car = 'I'; }
            case 'Ò', 'Ó' -> { quote = true; car = 'O'; }
            case 'Ù', 'Ú' -> { quote = true; car = 'U'; }

            case 'Â', 'Ä' -> car = 'A';
            case 'Ê', 'Ë' -> car = 'E';
            case 'Î', 'Ï' -> car = 'I';
            case 'Ô', 'Ö' -> car = 'O';
            case 'Û', 'Ü' -> car = 'U';
            case 'Ç' -> car = 'C';

            default -> car = ' ';
        }
        write(car);
        if (quote) write('\'');

    }

    private boolean isDiacritic(char ch) {
        return "àáâäèéêëìíîïòóôöùúûüçÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ".indexOf(ch) != -1;
    }

    @Override
    public int readKey() throws IOException {
        int ch;
        while ((ch = super.readKey()) == 19);
        return ch;
    }
}
