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

    public void showVcfSw2025() throws Exception {
        cls();
        printText(readBinaryFile("ascii/vcfsw2025-m10.txt"));
        flush(); resetInput();
        readKey();
    }

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
        println("5 - Patreon Publishers  E - Mistral AI");
        println("                        F - WiFi Modem");
        println("                        G - Apple1 Demo");
        println("                        H - Wikipedia");
        println("                        I - BASIC");
        if (HolidayCommons.isVcf())
            println("V - VCF SW 2025         J - Enigma");
        else
            println("V - VCF SW 2025         J - Enigma");
        println(". - Logout              K - Browser");
    }

    public void showInternationalNews() {
        cls();
        println("Retrocampus BBS - International News");
        println("---------------------------------------");
        println("1 - CNN News       8 - Indie Retro News");
        println("2 - BBC News       9 - The 8-Bit Guy");
        println("3 - Politico.com   0 - Vitno");
        println("4 - Al Jazeera     A - 2600 News");
        println("5 - Fox News       B - Hackaday Blog");
        println("6 - Wired          C - A.Valoroso (ENG)");
        println("7 - VCF News       D - NPR.org");
        println("                   . - Go back");
    }

    public void showItalianNews() {
        cls();
        println("Retrocampus BBS - Italian News");
        println("---------------------------------------");
        println("1- Televideo       7- A. Albano");
        println("2- Lercio          8- Ready 64");
        println("3- Disinformatico  0- CommessoPerplesso");
        println("4- Mupin.it        A- Fanpage");
        println("5- F.Quot          B- DigiTANTO");
        println("6- Amedeo Valoroso C- Bufale.NET");
        println("7- Butac.it        . - EXIT");
    }

    public void showGames() {
        cls();
        println("Games");
        println("1 - Tic-Tac-Toe  0 - Zork I (ITA)");
        println("2 - Connect 4    A - Avv.Castello (ITA)");
        println("3 - Zork I       B - Cave Adventure");
        println("4 - Zork II      C - Avventura (ITA)");
        println("5 - Zork III     D - Wishbringer");
        println("6 - Hitchhiker's E - Wild West");
        println("7 - Planetfall   F - Tin Star");
        println("8 - Stationfall  G - Desperados");
        println("9 - Castle Adv.  H - .........");
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
