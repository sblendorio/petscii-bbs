package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;

import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.Utils.bytes;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class MenuTelnetAnsi extends MenuTelnetPureAscii {

    @Override
    public String getTerminalType() {
        return "ansi";
    }

    public MenuTelnetAnsi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

    @Override
    public void initTerminal() throws Exception {
        write("\033[r".getBytes(ISO_8859_1));
    }

    @Override
    public void boldOn() {
        write(bytes("\033[1m"));
    }

    @Override
    public void boldOff() {
        write(bytes("\033[0m"));
    }

    @Override
    public String readChoice() throws Exception {
        int ch = readKey();
        return "" + (char) ch;
    }

    public void showMainMenu() {
        cls();
        printText(readBinaryFile("ansi/RetrocampusBbsMainMenu.ans"));
    }

    public void showInternationalNews() {
        cls();
        printText(readBinaryFile("ansi/MenuInternationalNews.ans"));
    }

    public void showItalianNews() {
        cls();
        printText(readBinaryFile("ansi/MenuItalianNews.ans"));
    }

    public void showGames() {
        cls();
        printText(readBinaryFile("ansi/MenuGames.ans"));
    }

    public BbsThread createAvventuraNelCastello() {
        return new AvventuraNelCastelloAnsi(readBinaryFile("ansi/castello.ans"), "it-it", false);
    }

    public BbsThread createCastleAdventure() {
        return new AvventuraNelCastelloAnsi(readBinaryFile("ansi/castle.ans"), "en-gb", false);
    }

}
