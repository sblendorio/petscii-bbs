package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import eu.sblendorio.bbs.tenants.mixed.LiteFanpageCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuVic20 extends MenuApple1 {

    public MenuVic20() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 22;
        screenRows = 23;
    }

    @Override
    public TriConsumer<BbsThread, Integer, Integer> locate() {
        return (bbs, y, x) -> {
            if (x>0 && y>0) {
                bbs.write(19);
                for (int i=1; i<y; i++) bbs.write(17);
                for (int i=1; i<x; i++) bbs.write(29);
            } else if (y>0) {
                for (int i=0; i<25; i++) bbs.write(145);
                for (int i=1; i<y; i++) bbs.write(17);
            } else if (x>0) {
                bbs.write(13, 145);
                for (int i=1; i<x; i++) bbs.write(29);
            }
        };
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(300);
        resetInput();
    }

    @Override
    public void logo() throws Exception {}

    public String rssPropertyTimeout() { return "rss.vic20.timeout"; }

    public String rssPropertyTimeoutDefault() { return "60000"; }

    public void showMainMenu() {
        cls();
        println("RetrocampusBBS-VIC20");
        println();
        println("NEWS & MISC");
        println("----------------------");
        println("1 - International News");
        println("2 - Italian News");
        println("3 - Games");
        println("4 - Patreon - list members");
        println("5 - Patreon - Publishers");
        println();
        println("SPECIALS");
        println("-----------------");
        println("A - Chat");
        println("B - Private Msg");
        println("C - Eliza");
        println("D - Chat GPT");
        println("E - WiFi Modem");
        println("F - Apple-1 Demo");
        println("G - Wikipedia");
        println("H - BASIC");
        println("I - Enigma");
        println(". - Logout");
    }

    public void showInternationalNews() {
        cls();
        println("International News");
        println("---------------------");
        println("1 - CNN News");
        println("2 - BBC News");
        println("3 - Politico.com");
        println("4 - Al Jazeera");
        println("5 - Fox News");
        println("6 - Wired");
        println("7 - VCF News");
        println("8 - Indie Retro News");
        println("9 - The 8-Bit Guy");
        println("0 - Vitno");
        println("A - 2600 News");
        println("B - Hackaday Blog");
        println("C - Amedeo Valoroso");
        println("D - NPR");
        println(". - Go back");
    }

    public void showItalianNews() {
        cls();
        println("Italian News");
        println("---------------------");
        println("1 - Televideo");
        println("2 - Lercio");
        println("3 - Disinformatico");
        println("4 - Mupin.it");
        println("5 - Fatto Quotidiano");
        println("6 - Amedeo Valoroso");
        println("7 - Butac.it");
        println("8 - Alessandro Albano");
        println("9 - Ready 64");
        println("0 - CommessoPerplesso");
        println("A - Fanpage");
        println(". - Go back");
    }

    public void showGames() {
        cls();
        println("Games");
        println("------------");
        println("1 - Tic-Tac-Toe");
        println("2 - Connect 4");
        println("3 - Zork I");
        println("4 - Zork II");
        println("5 - Zork III");
        println("6 - Hitchhiker's");
        println("7 - Planetfall");
        println("8 - Stationfall");
        println("9 - Castle Adventure");
        println("0 - Zork I (ITA)");
        println("A - Avv. nel Castello");
        println("B - Cave Adventure");
        println("C - Avventura (ITA)");
        println(". - Go back");
    }

    public String readChoice() throws Exception {
        print(">");
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

    @Override
    public void wifiModem() throws Exception {
        cls();
        println("Once upon a a time,");
        println("there were dial up");
        println("BBSes. Nowadays we");
        println("have Internet but we");
        println("recreate such an");
        println("experience.");
        println();
        println("www.museo-computer.it");
        println("      /en/wifi-modem/");
        println();
        println("Get here your brand");
        println("new WiFi modem, it");
        println("uses your Internet");
        println("connection to allow");
        println("you to telnet BBSes");
        println("around the world");
        println();
        println("Press any key");
        println("-------------");
        flush(); resetInput(); readKey();
    }

    public void execute(BbsThread subThread) throws Exception {
        if (subThread == null) return;

        if (subThread instanceof AsciiThread t) {
            t.clsBytes = this.clsBytes;
            t.screenColumns = this.screenColumns;
            t.screenRows = this.screenRows;
        }
        if (subThread instanceof WordpressProxyAscii t) {
            if (t.resizeable()) t.pageSize /= 2;
        } else if (subThread instanceof GoogleBloggerProxyAscii t) {
            if (t.resizeable()) t.pageSize /= 2;
        } else if (subThread instanceof OneRssAscii t) {
            if (t.resizeable()) t.pageSize /= 2;
        } else if (subThread instanceof LiteAscii t) {
            t.liteCommons.pageSize /= 2;
        }
        launch(subThread);
    }

    public BbsThread createAvventuraNelCastello() {
        return new AvventuraNelCastelloVic20("it-it");
    }

    public BbsThread createCastleAdventure() {
        return new AvventuraNelCastelloVic20("en-gb");
    }

}
