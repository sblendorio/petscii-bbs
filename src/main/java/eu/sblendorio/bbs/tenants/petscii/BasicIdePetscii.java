package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games.SwBasicBridge;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BasicIdePetscii extends PetsciiThread {

    private Map<Long, String> program = new TreeMap<>();

    private void prompt() throws Exception {
        newline();
        println("READY.");
    }

    @Override
    public void doLoop() throws Exception {
        prompt();
        do {
            flush();
            resetInput();
            String line = readLine();
            line = toUpper(StringUtils.defaultString(line));
            if (".".equals(line)) break;
            if (StringUtils.isBlank(line)) continue;

            String firstWord = line.replaceAll("^([A-Za-z][A-Za-z0-9]*).*$", "$1");
            if ("QUIT".equals(firstWord) || "SYSTEM".equals(firstWord)) break;

            if (isNumber(line)) program.remove(NumberUtils.toLong(line));
            else if (startsWithNumber(line)) {
                String number = line.replaceAll("^([0-9]+)(.*)$", "$1").trim();
                String text = line.replaceAll("^([0-9]+)(.*)$", "$2").trim();
                program.put(NumberUtils.toLong(number), text);
            } else if ("LIST".equals(firstWord)) {
                program.entrySet().forEach(
                    row -> this.println(row.getKey() + " " + row.getValue())
                );
                prompt();
            } else if ("NEW".equals(firstWord)) {
                program = new TreeMap<>();
                prompt();
            } else if ("LOAD".equals(firstWord) && line.matches("^LOAD *\"([a-zA-Z0-9 ]+)\"$")) {
                String filename = line.replaceAll("^LOAD *\"([a-zA-Z0-9 ]+)\"$", "$1");
                println("LOADING " + filename);
                prompt();
            } else if ("SAVE".equals(firstWord) && line.matches("^LOAD *\"([a-zA-Z0-9 ]+)\"$")) {
                String filename = line.replaceAll("^SAVE *\"([a-zA-Z0-9 ]+)\"$", "$1");
                println("SAVING " + filename);
                prompt();
            } else if ("DIR".equals(firstWord) || "CATALOG".equals(firstWord)) {
                prompt();
            } else if ("CLS".equals(firstWord)) {
                cls();
                prompt();
            } else if ("RUN".equals(firstWord)) {
                String programText = program.entrySet().stream()
                        .map(row -> row.getKey() + " " + row.getValue())
                        .collect(Collectors.joining("\n"));

                SwBasicBridge bridge = new SwBasicBridge(this);
                bridge.initWithProgramText(programText);
                bridge.start();

                prompt();
            } else if ("HELP".equals(firstWord)) {
                println("AVAILABLE COMMANDS IN DIRECT MODE:");
                println("- NEW");
                println("- CLS");
                println("- LIST");
                println("- RUN");
                println("- SAVE");
                println("- LOAD");
                println("- DIR");
                prompt();
            } else {
                println("?ILLEGAL DIRECT MODE ERROR");
                println("USE: NEW, LIST, RUN, SAVE, LOAD, DIR");
                prompt();
            }
        } while (true);
    }

    public String toUpper(String s) {
        if (StringUtils.isBlank(s)) return StringUtils.EMPTY;

        String result = "";
        boolean quote = false;
        for (int i=0; i<s.length(); i++) {
            String c = s.substring(i, i+1);
            if ("\"".equalsIgnoreCase(c)) quote = !quote;
            result += (quote ? c : c.toUpperCase());
        }
        return result.trim();
    }

    public boolean isNumber(String line) {
        return line.matches("^[0-9]+$");
    }

    public boolean startsWithNumber(String line) {
        return line.matches("^[0-9]+.*$");
    }
}
