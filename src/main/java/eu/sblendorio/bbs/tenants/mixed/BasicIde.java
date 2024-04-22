package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.getProperty;

public class BasicIde {
    private static final Logger logger = LogManager.getLogger(BasicIde.class);

    public static String BASIC_USER_PROGRAMS_DIR = getProperty("user.home") + File.separator + "basic_user_programs";

    public static String filter(String s) {
        if (s == null) return null;
        return s.replaceAll("[^A-Za-z-_.@]", "").replaceAll("\\.+", ".");
    }

    public static boolean kill(boolean privateMode, String user, String filename, Map<Long, String> program) {
        if (StringUtils.isBlank(filename) || !fileExists(privateMode, user, filename))
            return false;
        try {
            String dir = BASIC_USER_PROGRAMS_DIR + (privateMode && StringUtils.isNotBlank(user) ? File.separator + filter(user) : "");
            Utils.mkdir(dir);

            filename = dir + File.separator + filename.trim().toLowerCase().replaceAll("\\.bas$", "") + ".bas";
            new File(filename).delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean load(boolean privateMode, String user, String filename, Map<Long, String> program) {
        if (StringUtils.isBlank(filename) || !fileExists(privateMode, user, filename))
            return false;
        try {
            String dir = BASIC_USER_PROGRAMS_DIR + (privateMode && StringUtils.isNotBlank(user) ? File.separator + filter(user) : "");
            Utils.mkdir(dir);

            filename = dir + File.separator + filename.trim().toLowerCase().replaceAll("\\.bas$", "") + ".bas";
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

    public static boolean save(boolean privateMode, String user, String filename, Map<Long, String> program) {
        String dir = BASIC_USER_PROGRAMS_DIR + (privateMode && StringUtils.isNotBlank(user) ? File.separator + filter(user) : "");
        Utils.mkdir(dir);

        if (StringUtils.isBlank(filename))
            return false;

        filename = dir + File.separator + filename.trim().toLowerCase().replaceAll("\\.bas$", "") + ".bas";

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

    public static void dir(boolean privateMode, String user, BbsThread bbs) throws Exception {
        String dir = BASIC_USER_PROGRAMS_DIR + (privateMode && StringUtils.isNotBlank(user) ? File.separator + filter(user) : "");
        Utils.mkdir(dir);

        bbs.println(privateMode ? "PRIVATE DIR: " + user : "PUBLIC DIR:");
        List<String> fileList;
        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            fileList = paths.filter(Files::isRegularFile).map(Path::getFileName).map(Path::toString).sorted().toList();
        }

        List<String> files = columnList(fileList, bbs.getScreenColumns()-1, 1);

        int n = 0;
        for (String row: files) {
            bbs.println(row);
            int increment = row.length() / bbs.getScreenColumns();
            int mod = row.length() % bbs.getScreenColumns();
            n += (increment + (mod > 0 ? 1 : 0));
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

    public static boolean fileExists(boolean privateMode, String user, String filename) {
        String dir = BASIC_USER_PROGRAMS_DIR + (privateMode && StringUtils.isNotBlank(user) ? File.separator + filter(user) : "");
        Utils.mkdir(dir);

        filename = StringUtils.defaultString(filename).trim().toLowerCase().replaceAll("\\.bas$", "");
        return new File(dir
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
        String user = "";
        String patreonLevel = "0";
        try {
            user = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_USER);
            patreonLevel = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_LEVEL);
        } catch (NullPointerException | ClassCastException e) {
            logger.error("User not logged " + e.getClass().getName() + " " + e.getMessage());
        }
        boolean privateMode = false;

        prompt(bbs);
        do {
            bbs.flush();
            bbs.resetInput();
            String line = bbs.readLine();
            line = toUpper(StringUtils.defaultString(line));
            if (".".equals(line)) break;
            if (StringUtils.isBlank(line)) continue;

            String firstWord = line.replaceAll("^([A-Za-z][A-Za-z0-9]*).*$", "$1");
            if (Set.of("QUIT", "SYSTEM", "EXIT").contains(firstWord)) break;

            if (isNumber(line)) program.remove(NumberUtils.toLong(line));
            else if (startsWithNumber(line)) {
                String number = line.replaceAll("^([0-9]+)(.*)$", "$1").trim();
                String text = line.replaceAll("^([0-9]+)(.*)$", "$2").trim();
                program.put(NumberUtils.toLong(number), text);
            } else if ("LIST".equals(firstWord)) {
                bbs.newline();
                long lowerBound = Long.MIN_VALUE;
                long upperBound = Long.MAX_VALUE;
                if (line.matches("^LIST *([0-9]+)")) {
                    String strBound = line.replaceAll("^LIST *([0-9]*)", "$1").replace(" ", "");
                    lowerBound = upperBound = NumberUtils.toLong(strBound);
                } else if (line.matches("^LIST *([0-9]* *- *[0-9]*)")) {
                    String strBound = line.replaceAll("^LIST *([0-9]* *- *[0-9]*)", "$1").replace(" ", "");
                    if (!strBound.startsWith("-")) lowerBound = NumberUtils.toLong(strBound.substring(0, strBound.indexOf("-")));
                    if (!strBound.endsWith("-")) upperBound = NumberUtils.toLong(strBound.substring(strBound.indexOf("-")+1));
                } else if (!line.trim().equals("LIST")) {
                    bbs.println("?SYNTAX ERROR");
                    promptNoline(bbs);
                    continue;
                }
                int n = 0;
                final long lbound = lowerBound;
                final long ubound = upperBound;
                for (String row: program.entrySet().stream().filter(row -> (lbound <= row.getKey() && row.getKey() <= ubound)).map(row -> row.getKey() + " " + row.getValue()).toList()) {
                    bbs.println(row);
                    int increment = row.length() / bbs.getScreenColumns();
                    int mod = row.length() % bbs.getScreenColumns();
                    n += (increment + (mod > 0 ? 1 : 0));
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
            } else if (Set.of("PUBLIC", "PUB").contains(firstWord)) {
                privateMode = false;
                bbs.println("CURRENT MODE: " + (privateMode ? "PRIVATE" : "PUBLIC"));
                prompt(bbs);
            } else if (Set.of("PRIVATE", "PRIV").contains(firstWord)) {
                privateMode = true;
                bbs.println("CURRENT MODE: " + (privateMode ? "PRIVATE" : "PUBLIC"));
                prompt(bbs);
            } else if (Set.of("MODE").contains(firstWord)) {
                bbs.println("CURRENT MODE: " + (privateMode ? "PRIVATE" : "PUBLIC"));
                prompt(bbs);
            } else if ("NEW".equals(firstWord)) {
                program = new TreeMap<>();
                prompt(bbs);
            } else if ("KILL".equals(firstWord) && line.matches("^KILL *\"([a-zA-Z0-9-._ ]+)\"?$")) {
                if (!privateMode) {
                    bbs.newline();
                    bbs.println("CAN'T DELETE PUBLIC FILES");
                    prompt(bbs);
                    continue;
                }
                String filename = line.replaceAll("^KILL *\"([a-zA-Z0-9-._ ]+)\"?$", "$1").trim().toLowerCase().replaceAll("[^0-9A-Za-z-._ ]", "");
                bbs.newline();
                bbs.println("ERASING " + filename);
                bbs.print("ARE YOU SURE? ");
                bbs.flush(); bbs.resetInput();
                String isOk = bbs.readLine();
                if (Set.of("y", "yes").contains(isOk.trim().toLowerCase())) {
                    boolean ok = kill(privateMode, user, filename, program);
                    if (!ok) {
                        bbs.println("?FILE NOT FOUND ERROR");
                        promptNoline(bbs);
                    } else {
                        bbs.println("DELETED.");
                        prompt(bbs);
                    }
                }
            } else if("KILL".equals(firstWord)) {
                bbs.newline();
                bbs.println("?SYNTAX ERROR");
                promptNoline(bbs);
            } else if ("LOAD".equals(firstWord) && line.matches("^LOAD *\"([a-zA-Z0-9-._ ]+)\"?$")) {
                String filename = line.replaceAll("^LOAD *\"([a-zA-Z0-9-._ ]+)\"?$", "$1").trim().toLowerCase().replaceAll("[^0-9A-Za-z-._ ]", "");
                bbs.newline();
                bbs.println("LOADING " + filename);
                boolean ok = load(privateMode, user, filename, program);
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
            } else if ("SAVE".equals(firstWord) && line.matches("^SAVE *\"([a-zA-Z0-9-._ ]+)\"?$")) {
                String filename = line.replaceAll("^SAVE *\"([a-zA-Z0-9-._ ]+)\"?$", "$1").trim().toLowerCase().replaceAll("[^0-9A-Za-z-._ ]", "");
                bbs.newline();
                if (fileExists(privateMode, user, filename)) {
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
                save(privateMode, user, filename, program);
                prompt(bbs);
            } else if ("SAVE".equals(firstWord)) {
                bbs.newline();
                bbs.println("?SYNTAX ERROR");
                promptNoline(bbs);
            } else if (Set.of("DIR", "CAT", "CATALOG", "FILES").contains(firstWord)) {
                bbs.newline();
                dir(privateMode, user, bbs);
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
                bbs.println("- NEW: erase program from memory");
                bbs.println("- RUN: run current program");
                bbs.println("- CLS: clear screen");
                bbs.println("- LIST: list program");
                bbs.println("- PUBLIC: enter public files mode");
                bbs.println("- PRIVATE: enter private files mode");
                bbs.println("- MODE: show current mode");
                bbs.println("- DIR: show dir (public or private)");
                bbs.println("- SAVE\"name\": save program");
                bbs.println("- LOAD\"name\": load program");
                bbs.println("- KILL\"name\": delete program");
                bbs.println("- QUIT/EXIT/SYSTEM: exit from BASIC");
                prompt(bbs);
            } else {
                bbs.println("?NO DIRECT MODE  ERROR");
                bbs.println("USE 'HELP' FOR AVAILABLE COMMANDS");
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

    private static List<String> columnList(List<String> list, int screenWidth, int separatingSpaceLength) {
        int max = list.stream().mapToInt(String::length).max().orElse(1) + separatingSpaceLength;
        int div = screenWidth / max;
        int mod = screenWidth % max;
        int numCols = div + (mod > 0 ? 1 : 0);

        List<String> result = new LinkedList<>();
        int[] maxLength = new int[numCols];

        for (int i = 0; i < list.size(); i++) {
            int fileLength = list.get(i).length();
            int columnIndex = i % numCols;

            if (maxLength[columnIndex] < fileLength) {
                maxLength[columnIndex] = fileLength;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String fileName = list.get(i);
            sb.append(fileName);
            if ((i + 1) % numCols != 0) {
                sb.append(" ".repeat(Math.max(0, maxLength[i % numCols] - fileName.length() + separatingSpaceLength)));
            }

            if ((i + 1) % numCols == 0) {
                result.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        if (!sb.isEmpty()) {
            result.add(sb.toString());
        }
        return result;
    }

}
