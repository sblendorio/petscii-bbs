package eu.sblendorio.bbs.games;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;

import static eu.sblendorio.bbs.core.Utils.*;

public class SwBasicBridge {

    protected BbsThread bbs;
    protected ScriptEngine engine;
    protected Bindings bindings;

    public SwBasicBridge(BbsThread bbs) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        engine = (ScriptEngine) new ScriptEngineManager().getEngineByName("Graal.js");
        this.bbs = bbs;
    }

    private String readFile(String name) throws Exception {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            return new String(is.readAllBytes());
        }
    }

    public void cls() {
        bbs.cls();
    }

    public void end() throws Exception {
    }

    public void print(String s, boolean eol) throws Exception {
        bbs.print(s);
        if (eol) bbs.newline();
    }

    public String stringInput(String prompt, int count) throws Exception {
        bbs.flush(); bbs.resetInput();
        String line = bbs.readLine(setOfChars(ASCII_PRINTABLE));
        return line;
    }

    public double numberInput(String prompt, int count) throws Exception {
        bbs.flush(); bbs.resetInput();
        String line = bbs.readLine(setOfChars(STR_NUMBERS, "+-."));
        try {
            return Double.valueOf(line);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
