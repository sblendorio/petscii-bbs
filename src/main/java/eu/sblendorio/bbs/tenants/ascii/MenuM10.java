package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuM10 extends MenuApple1 {

    public MenuM10() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 40;
        screenRows = 15;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2000);
        resetInput();
    }

    public void logo() throws Exception {}

    public String rssPropertyTimeout() { return "rss.m10.timeout"; }

    public String rssPropertyTimeoutDefault() { return "60000"; }

    public void showMainMenu() {
        cls();
        println("Retrocampus BBS for KC85 - M10");
        println("NEWS & MISC             SPECIALS");
        println("----------------------  --------");
        println("1 - International News  A - Chat");
        println("2 - Italian News        B - Private Msg");
        println("3 - Games               C - Eliza");
        println("4 - Patreon List        D - Chat GPT");
        println("5 - Patreon Publishers  E - WiFi Modem");
        println("                        F - Apple1 Demo");
        println("                        G - Wikipedia");
        println("                        H - BASIC");
        println(". - Logout              I - Enigma");
    }

    public void showInternationalNews() {
        cls();
        println("Retrocampus BBS - International News");
        println("---------------------------------------");
        println("1 - CNN News Full  9 - Indie Retro News");
        println("2 - CNN News Head  0 - The 8-Bit Guy");
        println("3 - BBC News       A - Vitno");
        println("4 - Politico.com   B - 2600 News");
        println("5 - Al Jazeera     C - Hackaday Blog");
        println("6 - Fox News       D - A.Valoroso (ENG)");
        println("7 - Wired          E - NPR.org");
        println("8 - VCF News       . - Go back");
    }

    public void showItalianNews() {
        cls();
        println("Retrocampus BBS - Italian News");
        println("---------------------------------------");
        println("1 - Televideo       6 - Amedeo Valoroso");
        println("2 - Lercio          7 - Butac.it");
        println("3 - Disinformatico  8 - A. Albano");
        println("4 - Mupin.it        9 - Ready 64");
        println("5 - F.Quot.      0 - Commesso Perplesso");
        println("                 A - Fanpage");
    }

    public void showGames() {
        cls();
        println("Games");
        println("---------------------------------------");
        println("1 - Tic-Tac-Toe  7 - Planetfall");
        println("2 - Connect 4    8 - Stationfall");
        println("3 - Zork I       9 - Castle Adventure");
        println("4 - Zork II      0 - Zork I (ITA)");
        println("5 - Zork III     A - Avv.Castello (ITA)");
        println("6 - Hitchhiker's B - Cave Adventure)");
        println(". - Go back      C - Avvenuta (ITA)");
        println("                 D - Wishbringer");
    }

    public String readChoice() throws Exception {
        print(">");
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

    @Override
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
}
