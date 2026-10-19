package eu.sblendorio.bbs;

import eu.sblendorio.bbs.core.MinitelThread;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngineFactory;
import java.nio.charset.StandardCharsets;

public class TestScript extends MinitelThread {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();

        System.out.println("Motori disponibili:");
        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            System.out.println("- " + factory.getEngineName() + " (Alias: " + factory.getNames() + ")");
        }
    }

    public void doLoop() throws Exception {
        print("Ciao"); write(13);
        print("Seconda linea"); write(13);
        newline();
        newline();
        print("2Ciao"); write(10);
        print("2Seconda linea"); write(10);
        newline();
        newline();
        readKey();
        println("DONE");
    }
}
