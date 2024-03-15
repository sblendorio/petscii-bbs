package eu.sblendorio.bbs.games;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.ascii.IlFattoQuotidianoAscii;

import javax.script.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static eu.sblendorio.bbs.core.BbsThread.readBinaryFile;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AvventuraNelCastelloBridge {

    protected BbsThread bbs;
    protected ScriptEngine engine;
    protected Bindings bindings;

    public AvventuraNelCastelloBridge(BbsThread bbs) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        engine = (ScriptEngine) new ScriptEngineManager().getEngineByName("Graal.js");
        this.bbs = bbs;
    }

    public boolean fileExists(String filename, String lang) {
        return new File(
                new File(System.getProperty("user.dir")).getAbsolutePath()
                        + File.separator
                        + (filePrefix() == null || filePrefix().isBlank() ? "" : filePrefix().trim() + "-")
                        + lang + "-" + filename.toLowerCase()
                        + ".anc"
        ).exists();
    }

    public boolean save(String filename, String state, String lang) {
        try {
            if (filename == null || filename.isBlank()) return false;
            String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
            File saveFile = new File(
                    currentdir
                            + File.separator
                            + (filePrefix() == null || filePrefix().isBlank() ? "" : filePrefix().trim() + "-")
                            + lang + "-" + filename.toLowerCase() + ".anc");
            try (RandomAccessFile raf = new RandomAccessFile(saveFile, "rw")) {
                raf.write(state.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String restore(String filename, String lang) {
        String result;
        try {
            if (filename == null || filename.isBlank()) return "";
            String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
            File loadFile = new File(
                    currentdir
                            + File.separator
                            + (filePrefix() == null || filePrefix().isBlank() ? "" : filePrefix().trim() + "-")
                            + lang + "-" + filename.toLowerCase()
                            + ".anc");
            try (RandomAccessFile raf = new RandomAccessFile(loadFile, "r")) {
                byte[] data = new byte[(int) raf.length()];
                raf.readFully(data);
                result = new String(data, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return result;
    }

    public boolean showOriginalBanner() {
        return true;
    }

    public void print(String s) throws Exception {
        bbs.print(s);
    }

    public void println() throws Exception {
        bbs.newline();
    }

    public void println(String s) throws Exception {
        print(s);
        println();
    }

    public String readLine() throws Exception {
        flush();
        bbs.resetInput();
        String result = bbs.readLineUppercase();
        if (result.trim().equalsIgnoreCase(".")) return "stop";
        if (result.trim().equalsIgnoreCase("q")) return "stop";
        if (result.trim().equalsIgnoreCase("restore")) return "load";
        bbs.optionalCls();
        return result.trim().toLowerCase();
    }

    public void pressAnyKey() throws Exception {
        flush();
        bbs.resetInput();
        bbs.readKey();
        bbs.write(bbs.backspace());
        bbs.write(bbs.backspace());
        bbs.write(32, 32);
        bbs.write(bbs.backspace());
        bbs.write(bbs.backspace());
        bbs.newline();
        bbs.optionalCls();
    }

    public String filePrefix() {
        return "anc";
    }

    public void pause() throws Exception {
        Thread.sleep(3000L);
        bbs.resetInput();
    }

    public void clear() throws Exception {
        bbs.cls();
    }

    public int width() {
        return bbs.getScreenColumns() - 1;
    }

    public String transformDiacritics(String s) {
        return s;
    }

    public void flush() {
        bbs.flush();
    }

    public void sleep(long ms) throws Exception {}

    public void revOn() {}

    public void revOff() {}

    public void flashOn() {}

    public void flashOff() {}

    public void underlineOn() {}

    public void underlineOff() {}

    public void beep() throws Exception { bbs.write(7); }

    public void joke() throws Exception {
        for (int i = 0; i < bbs.getScreenRows() * 2; i++) {
            for (int j = 0; j < width(); j++) {
                int ch = ThreadLocalRandom.current().nextInt(32, 126);
                int probability = ThreadLocalRandom.current().nextInt(0, 100);
                bbs.write(ch);
                if (probability < 5) beep();
            }
            bbs.newline();
        }
        Thread.sleep(2000L);
    }

    public void init(String lang) throws Exception {
        bindings = new SimpleBindings();
        bindings.put("polyglot.js.allowAllAccess", true);

        bindings.put("bridge", this);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/" + lang + ".i18n.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/Sound.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/CRT.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/Thesaurus.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/Parser.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/IFEngine.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/game/AvventuraNelCastelloJSEngine.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/game/AvventuraNelCastelloJS.js" ), UTF_8), bindings);
    }

    public void start() throws Exception {
        engine.eval("new Avventura().start();", bindings);
    }

}
