package eu.sblendorio.bbs.games;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.ascii.IlFattoQuotidianoAscii;

import javax.script.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    public static void main(String[] args) {
        BbsThread bbs = new IlFattoQuotidianoAscii();
        AvventuraNelCastelloBridge m = new AvventuraNelCastelloBridge(bbs);
    }

    public boolean fileExists(String filename) {
        return new File(
                new File(System.getProperty("user.dir")).getAbsolutePath()
                        + File.separator
                        + filename.toLowerCase()
                        + ".anc"
        ).exists();
    }

    public boolean save(String filename, String state) {
        try {
            if (filename == null || filename.isBlank()) return false;
            String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
            File saveFile = new File(currentdir + File.separator + filename.toLowerCase() + ".anc");
            try (RandomAccessFile raf = new RandomAccessFile(saveFile, "rw")) {
                raf.write(state.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String restore(String filename) {
        String result = "";
        try {
            if (filename == null || filename.isBlank()) return result;
            String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
            File loadFile = new File(currentdir + File.separator + filename.toLowerCase() + ".anc");
            try (RandomAccessFile raf = new RandomAccessFile(loadFile, "r")) {
                byte[] data = new byte[(int) raf.length()];
                raf.readFully(data);
                result = new String(data, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
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
        String result = bbs.readLine();
        if (result.trim().equalsIgnoreCase(".")) return "stop";
        if (result.trim().equalsIgnoreCase("q")) return "stop";
        if (result.trim().equalsIgnoreCase("restore")) return "save";
        return result;
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
    }

    public void clear() throws Exception {
        bbs.cls();
    }

    public int width() {
        return bbs.getScreenColumns() - 1;
    }

    public void flush() {
        bbs.flush();
    }

    public void revOn() {}

    public void revOff() {}

    public void flashOn() {}

    public void flashOff() {}

    public void underlineOn() {}

    public void underlineOff() {}

    public void beep() {}

    public void init(String lang) throws Exception {
        String locale;
        String script;

        if ("it".equalsIgnoreCase(lang)) {
            locale = "it-it.i18n.js";
            script = "AvventuraNelCastelloJS-it.js";
        } else {
            locale = "en-gb.i18n.js";
            script = "AvventuraNelCastelloJS-en.js";
        }
        bindings = new SimpleBindings();
        bindings.put("polyglot.js.allowAllAccess", true);

        bindings.put("bridge", this);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/" + locale), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/Sound.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/CRT.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/Thesaurus.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/Parser.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/IFEngine.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/IFEngine/AvventuraNelCastelloJSEngine.js"), UTF_8), bindings);
        engine.eval(new String(readBinaryFile("avventura-nel-castello/" + script), UTF_8), bindings);
    }

    public void start() throws Exception {
        engine.eval("new Avventura().start();", bindings);
    }

}
