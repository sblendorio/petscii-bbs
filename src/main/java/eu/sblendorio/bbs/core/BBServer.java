package eu.sblendorio.bbs.core;

import com.google.common.reflect.ClassPath;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class BBServer {
    private static int port;
    private static int timeout;
    private static Class<? extends PetsciiThread> bbs;
    private static List<Class<? extends PetsciiThread>> tenants = filterPetsciiThread();
    private static final long DEFAULT_TIMEOUT_IN_MILLIS = 3600000;
    private static final long DEFAULT_PORT = 6510;

    public static void main(String[] args) throws Exception {
        // args = new String[] {"-b", "MenuRetroAcademy", "-p", "6510"};
        readParameters(args);

        System.out.print(new Timestamp(System.currentTimeMillis())+" ");
        System.out.println("The BBS "+bbs.getSimpleName()+" is running: port = " + port + ", timeout = " + timeout + " millis");
        try(ServerSocket listener = new ServerSocket(port)) {
            listener.setSoTimeout(INTEGER_ZERO);
            while (true) {
                Socket socket = listener.accept();
                socket.setSoTimeout(timeout);

                CbmInputOutput cbm = new CbmInputOutput(socket);
                PetsciiThread thread = bbs.getDeclaredConstructor().newInstance();
                thread.setSocket(socket);
                thread.setCbmInputOutput(cbm);
                thread.start();
            }
        }
    }

    private static void readParameters(String[] args) {
        Options options = new Options();
        options.addOption("p", "port", true, "TCP port used by server process (default "+DEFAULT_PORT+")");
        options.addOption("t", "timeout", true, "Socket timeout in millis (default " + (DEFAULT_TIMEOUT_IN_MILLIS /60000) + " minutes)");
        options.addOption("h", "help", false, "Displays help");
        options.addOption("b", "bbs", true, "Run specific BBS (mandatory - see list below)");
        CommandLineParser parser = new DefaultParser();
        final CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
            displayHelp(options);
            System.exit(2);
            return;
        }
        if (cmd.hasOption("help") || !cmd.hasOption("bbs")) {
            displayHelp(options);
            System.exit(1);
        }
        port = toInt(cmd.getOptionValue("port", String.valueOf(DEFAULT_PORT)));
        timeout = toInt(cmd.getOptionValue("timeout", String.valueOf(DEFAULT_TIMEOUT_IN_MILLIS)));
        final String bbsName = cmd.getOptionValue("bbs");
        bbs = findTenant(bbsName);
        if (bbs == null) {
            System.out.println("BBS \"" + bbsName + "\" not recognized");
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
        System.out.println("List of available BBS:");
        tenants.forEach(c -> System.out.println(" * " + c.getSimpleName()));
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
