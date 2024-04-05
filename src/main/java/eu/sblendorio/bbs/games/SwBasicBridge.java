package eu.sblendorio.bbs.games;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.BbsThread.readBinaryFile;
import static eu.sblendorio.bbs.core.Utils.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SwBasicBridge {

    private static Logger logger = LogManager.getLogger(SwBasicBridge.class);

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
        String line = bbs.readLineNoCr(setOfChars(ASCII_PRINTABLE));
        return line;
    }

    public double numberInput(String prompt, int count) throws Exception {
        bbs.flush(); bbs.resetInput();
        String line = bbs.readLineNoCr(setOfChars(STR_NUMBERS, "E+-."));
        try {
            return Double.valueOf(line);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void init(String source) throws Exception {
        String sourceDecoded = java.net.URLDecoder.decode(source, StandardCharsets.UTF_8);
        if (sourceDecoded.contains("../") || sourceDecoded.contains("/..")) return;

        bindings = new SimpleBindings();
        bindings.put("polyglot.js.allowAllAccess", true);

        bindings.put("bridge", this);
        bindings.put("code", readFile(sourceDecoded));

        engine.eval(readFile("swbasic2/swbasic2.js"), bindings);
        engine.eval("interpreter = new Interpreter();", bindings);
        engine.eval("let parser = new Parser(code);", bindings);
        engine.eval("parser.parse()", bindings);
        engine.eval("interpreter.setParser(parser)", bindings);
        engine.eval("interpreter.printFunction = bridge.print", bindings);
        engine.eval("interpreter.stringInputFunction = bridge.stringInput", bindings);
        engine.eval("interpreter.numberInputFunction = bridge.numberInput", bindings);
        engine.eval("interpreter.clsFunction = bridge.cls", bindings);
        engine.eval("interpreter.endFunction = bridge.end", bindings);
    }

    public void start() throws Exception {
        engine.eval("interpreter.interpret();",bindings);
    }

    public static void run(String source, BbsThread bbsThread) throws Exception {
        logger.info("Executing BASIC Program: '{}', on '{}'", source, bbsThread.getClass().getSimpleName());
        bbsThread.cls();
        SwBasicBridge bridge = new SwBasicBridge(bbsThread);
        bridge.init(source);
        bridge.start();
    }

}
