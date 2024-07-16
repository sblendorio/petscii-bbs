package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.Utils.bytes;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class MenuTelnetAnsi extends MenuTelnetPureAscii {

    @Override
    public String getTerminalType() {
        return "ansi";
    }

    @Override
    public TriConsumer<BbsThread, Integer, Integer> locate() {
        return (bbs, y, x) -> {
            if (y>0 && x>0) {
                bbs.write(0x1b);
                bbs.print("[" + (y - 1) + "," + (x - 1));
                bbs.write('H');
            } else if (y>0) {
                bbs.write(0x1b);
                bbs.print("[25");
                bbs.write('A');
                bbs.write(0x1b); bbs.print("[" + (y-1)); bbs.write('B');
            } else if (x>0) {
                bbs.write(0x0d, 0x0a, 0x1b);
                bbs.print("[1");
                bbs.write('A');
                bbs.write(0x1b); bbs.print("[" + (x-1)); bbs.write('C');
            }
        };
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

    public void showBasicPrograms() {
        cls();
        printText(readBinaryFile("ansi/MenuBasicPrograms.ans"));
    }

    public BbsThread createAvventuraNelCastello() {
        return new AvventuraNelCastelloAnsi(
                readBinaryFile("ansi/castello.ans"),
                readBinaryFile("ansi/castello-copyright.ans"),
                "it-it",
                false
        );
    }

    public BbsThread createCastleAdventure() {
        return new AvventuraNelCastelloAnsi(
                readBinaryFile("ansi/castle.ans"),
                readBinaryFile("ansi/castle-copyright.ans"),
                "en-gb",
                false
        );
    }

    public void menuInternationalNews() throws Exception {
        while (true) {
            showInternationalNews();
            flush();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = " + choice);
                BbsThread subThread;
                if (".".equals(choice)) return;
                subThread = switch (choice) {
                    case "1" -> new LiteCnnAscii80Ansi();
                                /*new CnnAscii(
                                rssPropertyTimeout(),
                                rssPropertyTimeoutDefault(),
                                getTerminalType(),
                                null,
                                null);*/
                    case "2" -> new BbcAscii(
                                    rssPropertyTimeout(),
                                    rssPropertyTimeoutDefault(),
                                    getTerminalType(),
                                    null,
                                    null
                                );
                    case "3" -> new OneRssPoliticoAscii();
                    case "4" -> new OneRssAJPlusAscii();
                    case "5" -> new OneRssFoxNewsAscii();
                    case "6" -> new WiredComAscii();
                    case "7" -> new VcfedAscii();
                    case "8" -> new IndieRetroNewsAscii();
                    case "9" -> new The8BitGuyAscii();
                    case "0" -> new VitnoAscii();
                    case "a" -> new OneRss2600Ascii();
                    case "b" -> new HackadayAscii();
                    case "c" -> new OneRssAmedeoValorosoEngAscii();
                    default -> {
                        validKey = false;
                        yield null;
                    }
                };
                execute(subThread);
            } while (!validKey);
        }
    }

}
