package eu.sblendorio.bbs.core;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BBServer {
    private int port;
    private int timeout;
    private Class<? extends PetsciiThread> bbs;
    private static List<Class<? extends PetsciiThread>> tenants = Tenants.INSTANCE.getTenantList();
    private static final long DEFAULT_TIMEOUT_IN_MILLIS = 3600000;
    private static final long DEFAULT_PORT = 6510;
    public static final BBServer INSTANCE = new BBServer();

    private BBServer() {
        //tenants = filterPetsciiThread();
    }

    public static void main(String[] args) throws Exception {
        // args = new String[] {"-b", "MenuRetroAcademy", "-p", "6510"};
        INSTANCE.readParameters(args);

        System.out.print(new Timestamp(System.currentTimeMillis())+" ");
        System.out.println("The BBS " + INSTANCE.bbs.getSimpleName() + " is running: port = " + INSTANCE.port + ", timeout = " + INSTANCE.timeout + " millis");
        try(ServerSocket listener = new ServerSocket(INSTANCE.port)) {
            listener.setSoTimeout(INTEGER_ZERO);
            while (true) {
                Socket socket = listener.accept();
                socket.setSoTimeout(INSTANCE.timeout);

                CbmInputOutput cbm = new CbmInputOutput(socket);
                PetsciiThread thread = INSTANCE.bbs.getDeclaredConstructor().newInstance();
                thread.setSocket(socket);
                thread.setCbmInputOutput(cbm);
                thread.start();
            }
        }
    }

    private void readParameters(String[] args) {
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
        Optional<Class<? extends PetsciiThread>> tenant = Tenants.INSTANCE.tenant(bbsName);
        if (tenant.isPresent()) {
            bbs = tenant.get();
        } else {
            System.out.println("BBS \"" + bbsName + "\" not recognized");
            displayHelp(options);
            System.exit(3);
        }
    }

    private void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(System.getProperty("sun.java.command"), options);
        System.out.println("List of available BBS:");
        tenants.forEach(c -> System.out.println(" * " + c.getSimpleName()));
    }
}
