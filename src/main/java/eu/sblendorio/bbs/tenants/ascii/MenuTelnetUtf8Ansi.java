package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import static eu.sblendorio.bbs.core.Utils.bytes;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class MenuTelnetUtf8Ansi extends MenuTelnetPureAscii {


    @Override
    public String getTerminalType() {
        return "utf8";
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

    public MenuTelnetUtf8Ansi() {
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
        printText(readBinaryFile("ansi/RetrocampusBbsMainMenu.utf8ans"));
    }

    public void showInternationalNews() {
        cls();
        printText(readBinaryFile("ansi/MenuInternationalNews.utf8ans"));
    }

    public void showItalianNews() {
        cls();
        printText(readBinaryFile("ansi/MenuItalianNews.utf8ans"));
    }

    public void showGames() {
        cls();
        printText(readBinaryFile("ansi/MenuGames.utf8ans"));
    }

    public void showBasicPrograms() {
        cls();
        printText(readBinaryFile("ansi/MenuBasicPrograms.utf8ans"));
    }

    public BbsThread createAvventuraNelCastello() {
        return new AvventuraNelCastelloAnsi(
                readBinaryFile("ansi/castello.utf8ans"),
                readBinaryFile("ansi/castello-copyright.utf8ans"),
                "it-it",
                true
        );
    }

    public BbsThread createCastleAdventure() {
        return new AvventuraNelCastelloAnsi(
                readBinaryFile("ansi/castle.utf8ans"),
                readBinaryFile("ansi/castle-copyright.utf8ans"),
                "en-gb",
                true
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
                else if ("1".equals(choice)) subThread = new LiteCnnAscii80AnsiUtf();
                        /*new CnnAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );*/
                else if ("2".equals(choice)) subThread = new BbcAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("3".equals(choice)) subThread = new OneRssPoliticoAscii();
                else if ("4".equals(choice)) subThread = new OneRssAJPlusAscii();
                else if ("5".equals(choice)) subThread = new OneRssFoxNewsAscii();
                else if ("6".equals(choice)) subThread = new WiredComAscii();
                else if ("7".equals(choice)) subThread = new VcfedAscii();
                else if ("8".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("9".equals(choice)) subThread = new The8BitGuyAscii();
                else if ("0".equals(choice)) subThread = new VitnoAscii();
                else if ("a".equals(choice)) subThread = new OneRss2600Ascii();
                else if ("b".equals(choice)) subThread = new HackadayAscii();
                else if ("c".equals(choice)) subThread = new OneRssAmedeoValorosoEngAscii();
                else {
                    validKey = false;
                    subThread = null;
                }
                execute(subThread);
            } while (!validKey);
        }
    }

}
