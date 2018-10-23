package eu.sblendorio.bbs.core;

import org.apache.commons.cli.*;
import org.reflections.Reflections;

import java.net.*;
import java.sql.Timestamp;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;
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
        ServerSocket listener = new ServerSocket(port);
        listener.setSoTimeout(INTEGER_ZERO);
        System.out.print(new Timestamp(System.currentTimeMillis())+" ");
        System.out.println("The BBS "+bbs.getSimpleName()+" is running: port = " + port + ", timeout = " + timeout + " millis");
        try {
            while (true) {
                Socket socket = listener.accept();
                socket.setSoTimeout(timeout);

                CbmInputOutput cbm = new CbmInputOutput(socket);
                PetsciiThread thread = bbs.newInstance();
                thread.setSocket(socket);
                thread.setCbmInputOutput(cbm);
                thread.start();
            }
        } finally {
            listener.close();
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

    private static Class<? extends PetsciiThread> findTenant(String bbsName) {
        try {
            for (Class<? extends PetsciiThread> c: tenants) {
                if (c.getSimpleName().equalsIgnoreCase(bbsName))
                    return c;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( System.getProperty("sun.java.command"), options);
        System.out.println("List of available BBS:");
        for (Class c: tenants) {
            System.out.println(" * " + c.getSimpleName());
        }
    }

    private static List<Class<? extends PetsciiThread>> filterPetsciiThread() {
        return filterPetsciiThread(null);
    }

    private static List<Class<? extends PetsciiThread>> filterPetsciiThread(String packageName) {
        List<Class<? extends PetsciiThread>> result = new LinkedList<>();
        for (Class<? extends PetsciiThread> elem: new LinkedList<>(new Reflections(defaultString(packageName)).getSubTypesOf(PetsciiThread.class)))
            if (!elem.isAnnotationPresent(Hidden.class)) result.add(elem);
        Collections.sort(result, new Comparator<Class<? extends PetsciiThread>>() {
            public int compare(Class<? extends PetsciiThread> o1, Class<? extends PetsciiThread> o2) { return o1.getSimpleName().compareTo(o2.getSimpleName()); }
        });
        return result;
    }
}
