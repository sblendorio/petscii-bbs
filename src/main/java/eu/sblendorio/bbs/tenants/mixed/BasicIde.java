package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.getProperty;

public class BasicIde {
    private static final Logger logger = LogManager.getLogger(BasicIde.class);

    public static String BASIC_USER_PROGRAMS_DIR = getProperty("user.home") + File.separator + "basic_user_programs";

    public static void mkdir(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean load(String filename, Map<Long, String> program) {
        if (StringUtils.isBlank(filename) || !fileExists(filename))
            return false;
        try {
            filename = BASIC_USER_PROGRAMS_DIR + File.separator + filename.trim().toLowerCase().replaceAll("\\.bas$", "") + ".bas";
            program.clear();
            program.putAll(
                    Utils.readExternalTxt(filename).stream()
                            .filter(StringUtils::isNotBlank)
                            .map(StringUtils::trim)
                            .collect(Collectors.toMap(
                                    row -> Long.valueOf(row.replaceAll("^([0-9]+).*$", "$1")),
                                    row -> row.replaceAll("^[0-9]+ *(.*)$", "$1")
                            ))
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean save(String filename, Map<Long, String> program) {
        if (StringUtils.isBlank(filename))
            return false;

        filename = BASIC_USER_PROGRAMS_DIR + File.separator + filename.trim().toLowerCase().replaceAll("\\.bas$", "") + ".bas";

        String programText = program.entrySet().stream()
                .map(row -> row.getKey() + " " + row.getValue())
                .collect(Collectors.joining("\n"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(programText);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void dir(BbsThread bbs) throws Exception {
        List<String> files;
        try (Stream<Path> paths = Files.walk(Paths.get(BASIC_USER_PROGRAMS_DIR))) {
            files = paths.filter(Files::isRegularFile).map(Path::getFileName).map(Path::toString).sorted().toList();
        }

        int n = 0;
        for (String row: files) {
            bbs.println(row);
            Double incrementDouble = Double.valueOf(row.length()) / Double.valueOf(bbs.getScreenColumns());
            int increment = row.length() / bbs.getScreenColumns();
            n += (increment + (incrementDouble - increment > 0 ? 1 : 0));
            if (n > bbs.getScreenRows() - 3) {
                n = 0;
                bbs.print("--- SPACE FOR MORE, '.' FOR STOP ------");
                bbs.flush(); bbs.resetInput();
                int ch = bbs.readKey();
                bbs.newline();
                bbs.newline();
                if (ch == '.') break;
            }
        }
    }

    public static boolean fileExists(String filename) {
        filename = StringUtils.defaultString(filename).trim().toLowerCase().replaceAll("\\.bas$", "");
        return new File(BASIC_USER_PROGRAMS_DIR
                        + File.separator
                        + filename
                        + ".bas"
        ).exists();
    }

    private static void promptNoline(BbsThread bbs) throws Exception {
        bbs.println("READY.");
    }

    private static void prompt(BbsThread bbs) throws Exception {
        bbs.newline();
        promptNoline(bbs);
    }

    public static void execute(BbsThread bbs, Map<Long, String> program) throws Exception {
        mkdir(BASIC_USER_PROGRAMS_DIR);

        String user = "";
        String patreonLevel = "0";
        try {
            user = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_USER);
            patreonLevel = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_LEVEL);
        } catch (NullPointerException | ClassCastException e) {
            logger.error("User not logged " + e.getClass().getName() + " " + e.getMessage());
        }

        prompt(bbs);
        do {
            bbs.flush();
            bbs.resetInput();
            String line = bbs.readLine();
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
                int n = 0;
                for (String row: program.entrySet().stream().map(row ->  row.getKey() + " " + row.getValue()).toList()) {
                    bbs.println(row);
                    Double incrementDouble = Double.valueOf(row.length()) / Double.valueOf(bbs.getScreenColumns());
                    int increment = row.length() / bbs.getScreenColumns();
                    n += (increment + (incrementDouble - increment > 0 ? 1 : 0));
                    if (n > bbs.getScreenRows() - 3) {
                        n = 0;
                        bbs.print("--- SPACE FOR MORE, '.' FOR STOP ------");
                        bbs.flush(); bbs.resetInput();
                        int ch = bbs.readKey();
                        bbs.newline();
                        bbs.newline();
                        if (ch == '.') break;
                    }
                }
                prompt(bbs);
            } else if ("NEW".equals(firstWord)) {
                program = new TreeMap<>();
                prompt(bbs);
            } else if ("LOAD".equals(firstWord) && line.matches("^LOAD *\"([a-zA-Z0-9 ]+)\"?$")) {
                String filename = line.replaceAll("^LOAD *\"([a-zA-Z0-9 ]+)\"?$", "$1").trim().toLowerCase().replaceAll("[^0-9A-Za-z ]", "");
                bbs.newline();
                bbs.println("LOADING " + filename);
                boolean ok = load(filename, program);
                if (!ok) {
                    bbs.println("?FILE NOT FOUND ERROR");
                    promptNoline(bbs);
                } else {
                    prompt(bbs);
                }
            } else if("LOAD".equals(firstWord)) {
                bbs.newline();
                bbs.println("?SYNTAX ERROR");
                promptNoline(bbs);
            } else if ("SAVE".equals(firstWord) && line.matches("^SAVE *\"([a-zA-Z0-9 ]+)\"?$")) {
                String filename = line.replaceAll("^SAVE *\"([a-zA-Z0-9 ]+)\"?$", "$1").trim().toLowerCase().replaceAll("[^0-9A-Za-z ]", "");
                bbs.newline();
                if (fileExists(filename)) {
                    bbs.print("FILE ALREADY EXISTS. OVERWRITE? ");
                    bbs.flush(); bbs.resetInput();
                    String confirm = bbs.readLine();
                    confirm = confirm.trim().toLowerCase();
                    if (!Set.of("yes", "y").contains(confirm)) {
                        bbs.println("ABORTED.");
                        promptNoline(bbs);
                        continue;
                    }
                }
                bbs.println("SAVING " + filename);
                save(filename, program);
                prompt(bbs);
            } else if ("SAVE".equals(firstWord)) {
                bbs.newline();
                bbs.println("?SYNTAX ERROR");
                promptNoline(bbs);
            } else if ("DIR".equals(firstWord) || "CATALOG".equals(firstWord) || "FILES".equals(firstWord)) {
                dir(bbs);
                prompt(bbs);
            } else if ("CLS".equals(firstWord)) {
                bbs.cls();
                prompt(bbs);
            } else if ("RUN".equals(firstWord)) {
                String programText = program.entrySet().stream()
                        .map(row -> row.getKey() + " " + row.getValue())
                        .collect(Collectors.joining("\n"));

                logger.info("user={}, program={}", user, programText.replaceAll("\n", "\\\\n"));

                SwBasicBridge bridge = new SwBasicBridge(bbs);
                bridge.initWithProgramText(programText);
                bridge.start();

                prompt(bbs);
            } else if ("HELP".equals(firstWord)) {
                bbs.println("AVAILABLE COMMANDS IN DIRECT MODE:");
                bbs.println("- NEW");
                bbs.println("- RUN");
                bbs.println("- CLS");
                bbs.println("- LIST");
                bbs.println("- SAVE");
                bbs.println("- LOAD");
                bbs.println("- DIR");
                bbs.println("- QUIT");
                prompt(bbs);
            } else {
                bbs.println("?ILLEGAL DIRECT MODE ERROR");
                bbs.println("USE: NEW, LIST, RUN, SAVE, LOAD, DIR");
                prompt(bbs);
            }
        } while (true);
    }

    public static String toUpper(String s) {
        if (StringUtils.isBlank(s)) return StringUtils.EMPTY;

        StringBuilder result = new StringBuilder();
        boolean quote = false;
        for (int i=0; i<s.length(); i++) {
            String c = s.substring(i, i+1);
            if ("\"".equalsIgnoreCase(c)) quote = !quote;
            result.append(quote ? c : c.toUpperCase());
        }
        return result.toString().trim();
    }

    public static boolean isNumber(String line) {
        return line.matches("^[0-9]+$");
    }

    public static boolean startsWithNumber(String line) {
        return line.matches("^[0-9]+.*$");
    }
}
