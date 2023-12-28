package eu.sblendorio.bbs.core;

import com.google.common.reflect.ClassPath;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class BBServer {
    static class EndPoint {
        Class<? extends BbsThread> bbs;
        int port;
        EndPoint(Class<? extends BbsThread> bbs, int port) {
            this.bbs = bbs;
            this.port = port;
        }
        @Override
        public String toString() {
            return bbs.getSimpleName() + ":" + port;
        }
    }

    private static int servicePort;
    private static int timeout;
    private static List<EndPoint> endPoints = new ArrayList<>();
    private static Map<String, Class<? extends BbsThread>> tenantMap = filterBBSThreadMap();
    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 3600000;
    private static final long DEFAULT_SERVICE_PORT = 0;
    private static Set<Integer> usedPorts = new HashSet<>();

    private static Logger logger = LogManager.getLogger(BBServer.class);

    public static Set<String> diagIpSet = initDiagIpSet();

    public static Set<String> initDiagIpSet() {
        String ipSetString = defaultString(getenv("DIAG_IP_SET"), getProperty("DIAG_IP_SET", EMPTY)).replace(" ", "");
        if (isBlank(ipSetString)) return emptySet();
        return new HashSet<>(asList(ipSetString.split(",")));
    }

    public static void main(String[] args) throws Exception {
        // args = new String[] {"-b", "MainMenu", "-p", "6510"};
        readParameters(args);

        initDiagIpSet();

        Thread.currentThread().setName("BBSMain-" + Thread.currentThread().getId());
        logger.info("The BBS {} is running: timeout = {} millis" + (servicePort != 0 ? ", serviceport = {}" : ""),
                    endPoints.toString(),
                    timeout,
                    servicePort);

        for (EndPoint endPoint: endPoints) {
            logger.info("Starting {}:{}", endPoint.bbs.getSimpleName(), endPoint.port);
            new Thread(() -> {
                Thread.currentThread().setName("BBS Dispatcher-" + Thread.currentThread().getId());
                try (ServerSocket listener = new ServerSocket(endPoint.port)) {
                    listener.setSoTimeout(0);
                    while (true) {
                        Socket socket = listener.accept();
                        socket.setSoTimeout(timeout);

                        final String clientIp = socket.getInetAddress().getHostAddress();
                        if (diagIpSet.contains(clientIp)) {
                            logger.info("DIAG: Connection by diagnostic IP {}, port {}", clientIp, endPoint.port);
                            try {
                                socket.getOutputStream().write("OK\n".getBytes(UTF_8));
                                socket.close();
                            } catch (Exception e) {
                                logger.error("DIAG: ERROR during closing connection to diagnostic IP {}, port {}", clientIp, endPoint.port);
                            }
                            continue;
                        }

                        BbsThread thread = endPoint.bbs.getDeclaredConstructor().newInstance();
                        BbsInputOutput io = thread.buildIO(socket);
                        thread.setSocket(socket);
                        thread.setBbsInputOutput(io);
                        io.localEcho = thread.localEcho;
                        io.socket = socket;

                        thread.keepAliveTimeout = thread.keepAliveTimeout <= 0 ? timeout : thread.keepAliveTimeout;
                        thread.start();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        if (servicePort != 0)
            new Thread(() -> {
                Thread.currentThread().setName("Diagnostic Dispatcher-" + Thread.currentThread().getId());
                try (ServerSocket listener = new ServerSocket(servicePort)) {
                    listener.setSoTimeout(0);
                    while (true) {
                        Socket socket = listener.accept();
                        new Thread(() -> {
                            Thread.currentThread().setName("Diagnostic-" + Thread.currentThread().getId());
                            try {
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                out.println(getConfigAsString());
                                socket.shutdownOutput();
                                socket.close();
                            } catch (IOException e) {
                                logger.error(e);
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
    }

    private static void readParameters(String[] args) {
        Options options = new Options();
        options.addOption("s", "serviceport", true, "TCP port used by service process, 0 for no-service (default "+DEFAULT_SERVICE_PORT+")");
        options.addOption("t", "timeout", true, "Socket timeout in millis (default " + (DEFAULT_TIMEOUT_IN_MILLIS /60000) + " minutes)");
        options.addOption("h", "help", false, "Displays help");

        Option bbses = Option.builder()
            .longOpt("bbs")
            .argName("bbsName:port")
            .desc("Run specific BBSes (mandatory - see list below) in the form <name1>:<port1> <name2>:<port2> ...")
            .hasArg(true)
            .numberOfArgs(Option.UNLIMITED_VALUES)
            .build();
        options.addOption(bbses);

        CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            logger.error(pe.getMessage());
            displayHelp(options);
            System.exit(2);
            return;
        }
        if (cmd.hasOption("help") || !cmd.hasOption("bbs")) {
            displayHelp(options);
            System.exit(1);
        }

        final String timeoutStr = cmd.getOptionValue("timeout", String.valueOf(DEFAULT_TIMEOUT_IN_MILLIS));
        if (timeoutStr.matches("^[0-9]*$"))
            timeout = toInt(timeoutStr);
        else if (timeoutStr.matches("^[0-9]*[hH]$"))
            timeout = 1000 * 60 * 60 * toInt(timeoutStr.replaceAll("[^0-9]", ""));
        else if (timeoutStr.matches("^[0-9]*[mM]$"))
            timeout = 1000 * 60 * toInt(timeoutStr.replaceAll("[^0-9]", ""));
        else if (timeoutStr.matches("^[0-9]*[sS]$"))
            timeout = 1000 * toInt(timeoutStr.replaceAll("[^0-9]", ""));
        else
            timeout = DEFAULT_TIMEOUT_IN_MILLIS;

        String[] bbsList = cmd.getOptionValues("bbs");
        boolean validList = true;
        for (String bbsSpec : bbsList) {
            if (!defaultString(bbsSpec).contains(":")) {
                logger.error("Missing port {}", bbsSpec);
                validList = false;
                break;
            }
            int pos = bbsSpec.indexOf(":");
            String bbsName = bbsSpec.substring(0, pos);
            String portStr = bbsSpec.substring(pos + 1);
            if (!NumberUtils.isCreatable(portStr)) {
                logger.error("Invalid port {}", bbsSpec);
                validList = false;
                break;
            }
            int port = NumberUtils.createInteger(portStr);
            if (usedPorts.contains(port)) {{
                logger.error("Port {} already used in {}", port, bbsSpec);
                validList = false;
                break;
            }}
            Class<? extends BbsThread> bbs = tenantMap.get(bbsName.toLowerCase());
            if (bbs == null) {
                logger.error("BBS \"{}\" not recognized", bbsName);
                validList = false;
                break;
            }
            endPoints.add(new EndPoint(bbs, port));
            usedPorts.add(port);
        }
        servicePort = toInt(cmd.getOptionValue("serviceport", String.valueOf(DEFAULT_SERVICE_PORT)));
        if (usedPorts.contains(servicePort)) {
            logger.error("Declared service port {} is already used by a BBS.", servicePort);
            validList = false;
        }
        if (!validList) {
            displayHelp(options);
            System.exit(1);
        }
    }

    private static void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(System.getProperty("sun.java.command"), options);
        System.out.println("List of available BBS:");
        tenantMap.values().stream().map(Class::getSimpleName).sorted().forEach(c -> System.out.println(" * " + c));
    }

    private final static String THREAD_ROW_FORMAT = "%20s %-40s %-35s %-15s %-7s %4s %-5s";
    private static String getConfigAsString() {
        return "HTTP/1.1 200 OK\n"
            + "Server: Dummy HTTP connection\n"
            + "Content-Type: text/html; charset=ISO-8859-1\n"
            + "Connection: Closed\n"
            + "\n"
            + "<html><head><title>"+ BbsThread.clients.size()+" client"+(BbsThread.clients.size()==1?"":"s")+"</title>"
            + "<meta http-equiv=\"refresh\" content=\"5\"></head><body><pre>\n"
            + "Number of clients: " + BbsThread.clients.size() + "\n"
            + "\n" +
            BbsThread.clients.entrySet().stream()
                .sorted(comparingLong(Map.Entry::getKey))
                .map(entry -> "#" + entry.getKey()
                    + ". " + entry.getValue().getClientClass().getSimpleName() + ":" + entry.getValue().serverPort
                    + " (uptime=" + showMillis(currentTimeMillis() - entry.getValue().startTimestamp)
                    + (entry.getValue().keepAlive
                        ? ", idle="+showMillis(currentTimeMillis()-entry.getValue().keepAliveThread.getStartTimestamp())
                        : "")
                    + ", clientName=" + obfuscate(entry.getValue().getClientName())
                    + ", IP=" + entry.getValue().ipAddress
                    + ", serverIP=" + entry.getValue().serverAddress
                    + ")\n"
                )
                .collect(Collectors.joining())
            + "\n"
            + "Thread list:\n"
            + "\n"
            + String.format(THREAD_ROW_FORMAT,
            "Id", "Class[Name]", "Client", "State", "Type", "Pri", "Alive")
            + "\n" +
              String.format(THREAD_ROW_FORMAT,
                  "--------------------", "----------------------------------------",
                  "-----------------------------------", "---------------", "-------", "----", "-----")
            + "\n"
            + Thread.getAllStackTraces().keySet().stream().sorted(comparingLong(Thread::getId)).map(t -> {
                    final Class clientClass = (t instanceof BbsThread) ? ((BbsThread) t).getClientClass() : null;
                    final String clientStr = clientClass == null ? null : clientClass.getSimpleName();
                    final Integer serverPort = (t instanceof BbsThread) ? ((BbsThread) t).serverPort : null;
                    final Long clientId = (t instanceof BbsThread) ? ((BbsThread) t).clientId : null;
                    return String.format(THREAD_ROW_FORMAT,
                        t.getId(),
                        substring(t.getClass().getSimpleName()+"[" + t.getName()+"] ", 0, 40),
                        substring(clientStr != null ? clientStr + ":" + serverPort + " (#"+clientId+")" : "", 0, 35),
                        t.getState(),
                        (t.isDaemon() ? "Daemon" : "Normal"),
                        t.getPriority(),
                        t.isAlive()
                        )
                    + "\n";
                }).collect(Collectors.joining());

    }

    private static String obfuscate(String s) {
        return s.replaceAll("@.*?/", "/");
    }

    private static String showMillis(long millis) {
        long s = (millis / 1000) % 60;
        long m = (millis / 60000) % 60;
        long h = (millis / 3600000); // % 24;

        return (millis > 3600000 ? h+"h" : "")
             + (millis > 60000 ? m+"m" : "")
             + s+"s";
    }

    private static List<Class<? extends BbsThread>> filterBBSThread() {
        List<Class<? extends BbsThread>> result = new LinkedList<>();
        final ClassLoader classLoader = BBServer.class.getClassLoader();
        final Set<ClassPath.ClassInfo> classes;
        try {
            classes = ClassPath.from(classLoader).getTopLevelClasses();
        } catch (IOException ioe) {
            return emptyList();
        }
        for (ClassPath.ClassInfo classInfo : classes) {
            try {
                Class c = classInfo.load();
                if (!Modifier.isAbstract(c.getModifiers())
                        && BbsThread.class.isAssignableFrom(c)
                        && !c.isAnnotationPresent(Hidden.class))
                    result.add(c);
            } catch (LinkageError e) {
                // SKIP
            }
        }
        result.sort(comparing(Class::getSimpleName));
        return result;
    }

    private static Map<String, Class<? extends BbsThread>> filterBBSThreadMap() {
        Map<String, Class<? extends BbsThread>> map = new HashMap<>();
        final ClassLoader classLoader = BBServer.class.getClassLoader();
        final Set<ClassPath.ClassInfo> classes;
        try {
            classes = ClassPath.from(classLoader).getTopLevelClasses();
        } catch (IOException ioe) {
            return emptyMap();
        }
        for (ClassPath.ClassInfo classInfo : classes) {
            try {
                Class c = classInfo.load();
                if (!Modifier.isAbstract(c.getModifiers())
                        && BbsThread.class.isAssignableFrom(c)
                        && !c.isAnnotationPresent(Hidden.class))
                    map.put(c.getSimpleName().toLowerCase(), c);
            } catch (LinkageError e) {
                // SKIP
            }
        }
        return map;
    }
}
