package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static eu.sblendorio.bbs.core.Utils.*;

public class SwBasicBridge {

    private static Logger logger = LogManager.getLogger(SwBasicBridge.class);

    protected BbsThread bbs;
    protected ScriptEngine engine;
    protected Bindings bindings;
    protected TriConsumer<BbsThread, Integer, Integer> locateFunction = null;

    public SwBasicBridge(BbsThread bbs, TriConsumer<BbsThread, Integer, Integer> locateFunction) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        engine = (ScriptEngine) new ScriptEngineManager().getEngineByName("Graal.js");
        this.bbs = bbs;
        this.locateFunction = locateFunction;
    }

    private String readFile(String name) throws Exception {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            return new String(is.readAllBytes());
        }
    }

    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
            bbs.resetInput();
        } catch (InterruptedException | IOException e) {
            logger.debug(e);
        }
    }

    public void locate(int y, int x) {
        if (locateFunction == null) return;
        locateFunction.accept(bbs, y, x);
    }

    public void beep() {
        bbs.write(7);
    }

    public int inkey(long ms) throws Exception {
        bbs.resetInput();
        int result = bbs.readKey();
        bbs.resetInput();
        return result;
        /*
        int result = bbs.convertToAscii(bbs.keyPressed());
        if (ms > 0) Thread.sleep(ms);
        return result;
        */
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
        String line = bbs.readLineNoCrInterruptable(setOfChars(ASCII_PRINTABLE));
        if (line == null) {
            bbs.newline();
            throw new InterruptedException("BREAK");
        }
        return line;
    }

    public double numberInput(String prompt, int count) throws Exception {
        bbs.flush(); bbs.resetInput();
        String line = bbs.readLineNoCrInterruptable(setOfChars(STR_NUMBERS, "E+-."));
        if (line == null) {
            bbs.newline();
            throw new InterruptedException("BREAK");
        }
        try {
            return Double.valueOf(line);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void init(String filename) throws Exception {
        String sourceDecoded = java.net.URLDecoder.decode(filename, StandardCharsets.UTF_8);
        if (sourceDecoded.contains("../") || sourceDecoded.contains("/..")) return;
        String code = readFile(sourceDecoded);
        initWithProgramText(code);
    }

    public void initWithProgramText(String code) throws Exception {
        bindings = new SimpleBindings();
        bindings.put("polyglot.js.allowAllAccess", true);

        bindings.put("bridge", this);
        bindings.put("code", code);

        engine.eval(readFile("swbasic2/swbasic2.js"), bindings);
        engine.eval("let parser = new Parser(code);", bindings);
        engine.eval("parser.printFunction = bridge.print", bindings);
        engine.eval("parser.parse()", bindings);
        engine.eval("interpreter = new Interpreter();", bindings);
        engine.eval("interpreter.printFunction = bridge.print", bindings);
        engine.eval("interpreter.inkeyFunction = bridge.inkey", bindings);
        engine.eval("interpreter.beepFunction = bridge.beep", bindings);
        engine.eval("interpreter.locateFunction = bridge.locate", bindings);
        engine.eval("interpreter.stringInputFunction = bridge.stringInput", bindings);
        engine.eval("interpreter.numberInputFunction = bridge.numberInput", bindings);
        engine.eval("interpreter.clsFunction = bridge.cls", bindings);
        engine.eval("interpreter.endFunction = bridge.end", bindings);
        engine.eval("interpreter.breakFunction = function() { throw 'BREAK' }", bindings);
        engine.eval("interpreter.sleepFunction = bridge.sleep", bindings);
        engine.eval("interpreter.setParser(parser)", bindings);
    }

    public void start() throws Exception {
        engine.eval("interpreter.interpret();", bindings);
    }

    public static void run(String source, BbsThread bbsThread, TriConsumer<BbsThread, Integer, Integer> locate) throws Exception {
        run(null, source, bbsThread, locate, null);
    }

    public static void run(String source, BbsThread bbsThread) throws Exception {
        run(null, source, bbsThread, null, null);
    }

    public static void run(String caption, String source, BbsThread bbsThread, TriConsumer<BbsThread, Integer, Integer> locate) throws Exception {
        run(caption, source, bbsThread, locate, null);
    }

    public static void run(String caption, String source, BbsThread bbsThread, TriConsumer<BbsThread, Integer, Integer> locate, Runnable code) throws Exception {
        logger.info("Executing BASIC Program: '{}', on '{}'", source, bbsThread.getClass().getSimpleName());
        if (caption != null) {
            bbsThread.cls();
            bbsThread.println("*** RETROCAMPUS BBS BASIC V1.0 ***");
            bbsThread.println("DERIVED FROM SWBASIC2 BY KONYISOFT");
            bbsThread.println();
            bbsThread.println("READY.");
            bbsThread.flush();
            Thread.sleep(700);
            typeln(bbsThread, "LOAD\"" + caption.toUpperCase() + "\"");
            bbsThread.println();
            bbsThread.println("SEARCHING FOR " + caption.toUpperCase());
            bbsThread.flush();
            Thread.sleep(700);
            bbsThread.println("FOUND " + caption.toUpperCase());
            bbsThread.flush();
            Thread.sleep(700);
            bbsThread.print("LOADING...");
            bbsThread.flush();
            Thread.sleep(1800);
            bbsThread.println();
            bbsThread.println("READY.");
            bbsThread.flush();
            Thread.sleep(700);
            typeln(bbsThread, "RUN", DELAY * 8);
            bbsThread.flush();
            Thread.sleep(1400);
            bbsThread.flush();
            bbsThread.flush();
            Optional.ofNullable(code).ifPresent(Runnable::run);
        }
        bbsThread.cls();
        SwBasicBridge bridge = new SwBasicBridge(bbsThread, locate);
        bridge.init(source);
        bridge.start();
    }


    public static long DELAY = 50;
    public static void type(BbsThread bbsThread, String s) throws Exception {
        type(bbsThread, s, DELAY);
    }
    public static void type(BbsThread bbsThread, String s, long delay) throws Exception {
        for (int i=0; i<s.length(); i++) {
            bbsThread.print(s.substring(i, i+1));
            bbsThread.flush();
            Thread.sleep(delay);
        }
    }

    public static void typeln(BbsThread bbsThread, String s) throws Exception {
        typeln(bbsThread, s, DELAY);
    }
    public static void typeln(BbsThread bbsThread, String s, long delay) throws Exception {
        type(bbsThread, s, delay);
        bbsThread.println();
        bbsThread.flush();
        Thread.sleep(delay);
    }

}
