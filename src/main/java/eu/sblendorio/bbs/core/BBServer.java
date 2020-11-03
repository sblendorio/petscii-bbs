package eu.sblendorio.bbs.core;

import com.google.common.reflect.ClassPath;
import java.io.PrintWriter;
import static java.lang.System.currentTimeMillis;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import sun.nio.cs.ISO_8859_1;

public class BBServer {
    private static int port;
    private static int servicePort;
    private static int timeout;
    private static Class<? extends PetsciiThread> bbs;
    private static List<Class<? extends PetsciiThread>> tenants = filterPetsciiThread();
    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 3600000;
    private static final long DEFAULT_PORT = 6510;
    private static final long DEFAULT_SERVICE_PORT = 0;

    private static final Logger logger = LoggerFactory.getLogger(BBServer.class);

    public static void main(String[] args) throws Exception {
        // args = new String[] {"-b", "MainMenu", "-p", "6510"};
        readParameters(args);

        logger.info("{} The BBS {} is running: port = {}, timeout = {} millis" + (servicePort != 0 ? ", serviceport = {}" : ""),
                    new Timestamp(currentTimeMillis()),
                    bbs.getSimpleName(),
                    port,
                    timeout,
                    servicePort);

        new Thread(() -> {
            try (ServerSocket listener = new ServerSocket(port)) {
                listener.setSoTimeout(0);
                while (true) {
                    Socket socket = listener.accept();
                    socket.setSoTimeout(timeout);

                    CbmInputOutput cbm = new CbmInputOutput(socket);
                    PetsciiThread thread = bbs.getDeclaredConstructor().newInstance();
                    thread.setSocket(socket);
                    thread.setCbmInputOutput(cbm);
                    thread.keepAliveTimeout = thread.keepAliveTimeout <= 0 ? timeout : thread.keepAliveTimeout;
                    thread.start();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        if (servicePort != 0)
            new Thread(() -> {
                try (ServerSocket listener = new ServerSocket(servicePort)) {
                    listener.setSoTimeout(0);
                    while (true) {
                        Socket socket = listener.accept();
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(getConfigAsString());
                        socket.shutdownOutput();
                        socket.close();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
    }

    private static void readParameters(String[] args) {
        Options options = new Options();
        options.addOption("p", "port", true, "TCP port used by server process (default "+DEFAULT_PORT+")");
        options.addOption("s", "serviceport", true, "TCP port used by service process, 0 for no-service (default "+DEFAULT_PORT+")");
        options.addOption("t", "timeout", true, "Socket timeout in millis (default " + (DEFAULT_TIMEOUT_IN_MILLIS /60000) + " minutes)");
        options.addOption("h", "help", false, "Displays help");
        options.addOption("b", "bbs", true, "Run specific BBS (mandatory - see list below)");
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
        port = toInt(cmd.getOptionValue("port", String.valueOf(DEFAULT_PORT)));
        servicePort = toInt(cmd.getOptionValue("serviceport", String.valueOf(DEFAULT_SERVICE_PORT)));
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
        final String bbsName = cmd.getOptionValue("bbs");
        bbs = findTenant(bbsName);
        if (bbs == null) {
            logger.error("BBS \"{}\" not recognized", bbsName);
            displayHelp(options);
            System.exit(3);
        }
    }

    private static Class<? extends PetsciiThread> findTenant(final String bbsName) {
        return findTenant(tenants, bbsName);
    }

    static Class<? extends PetsciiThread> findTenant(final List<Class<? extends PetsciiThread>> tenants,
                                                     final String bbsName) {
        return tenants.stream()
            .filter(c -> c.getSimpleName().equalsIgnoreCase(bbsName))
            .findFirst()
            .orElse(null);
    }

    private static void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(System.getProperty("sun.java.command"), options);
        logger.info("List of available BBS:");
        tenants.forEach(c -> logger.info(" * {}", c.getSimpleName()));
    }

    private static String getConfigAsString() {
        return "HTTP/1.1 200 OK\n"
            + "Server: Dummy HTTP connection\n"
            + "Content-Type: text/html; charset=ISO-8859-1\n"
            + "Connection: Closed\n"
            + "\n"
            + "<html><head><meta http-equiv=\"refresh\" content=\"5\"></head><body><pre>\n"
            + "Number of clients: " + PetsciiThread.clients.size() + "\n"
            + "\n" +
            PetsciiThread.clients.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getKey))
                .map(entry -> "#" + entry.getKey()
                    + ": " + entry.getValue().getClientClass().getSimpleName()
                    + " (uptime=" + showMillis(currentTimeMillis() - entry.getValue().startTimestamp)
                    + (entry.getValue().keepAlive
                        ? ", idle="+showMillis(currentTimeMillis()-entry.getValue().keepAliveThread.getStartTimestamp())
                        : "")
                    + ", clientName=" + entry.getValue().getClientName()
                    + ", IP=" + entry.getValue().ipAddress
                    + ", serverIP=" + entry.getValue().serverAddress
                    + ")"
                    + "\n"
                )
                .collect(Collectors.joining());
    }

    private static String showMillis(long millis) {
        long s = (millis / 1000) % 60;
        long m = (millis / 60000) % 60;
        long h = (millis / 3600000); // % 24;

        return (millis > 3600000 ? h+"h" : "")
             + (millis > 60000 ? m+"m" : "")
             + s+"s";
    }

    private static List<Class<? extends PetsciiThread>> filterPetsciiThread() {
        List<Class<? extends PetsciiThread>> result = new LinkedList<>();
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
                if (PetsciiThread.class.isAssignableFrom(c) && !c.isAnnotationPresent(Hidden.class))
                    result.add(c);
            } catch (LinkageError e) {
                // SKIP
            }
        }
        result.sort(comparing(Class::getSimpleName));
        return result;
    }
}
