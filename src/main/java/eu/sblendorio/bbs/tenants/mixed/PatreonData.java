package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiKeys;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiColors;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static eu.sblendorio.bbs.core.Utils.*;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.*;

public class PatreonData {
    private static Logger loggerAuthorizations = LogManager.getLogger("authorizations");
    protected static final String DB_FILE = System.getProperty("user.home") + "/patreon.db";
    protected static final Properties properties;
    static {
        properties = new Properties();
        properties.setProperty("characterEncoding", UTF_8);
        properties.setProperty("encoding", "\"" + UTF_8 + "\"");
    }

    public static byte[] PETSCII_LOGO_AUTHENTICATE = BbsThread.readBinaryFile("petscii/patreon_login.seq");
    public static int CODE_LENGTH = 6;
    public static final String PATREON_USER = "PATREON_USER";
    public static final String PATREON_LEVEL = "PATREON_LEVEL";
    public static final long TIMEOUT = 300_000;

    private static final String WAIT_MESSAGE_ASCII = "...";
    private static final String WAIT_MESSAGE_PETSCII = "Please wait...";
    private static int WAIT_COLOR_PETSCII = GREY2;

    public static Random random;

    public final String user;
    public final String patreonLevel;

    public PatreonData(String user, String patreonLevel) {
        this.user = user;
        this.patreonLevel = patreonLevel;
    }

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new Random(System.currentTimeMillis());
        }
    }

    public static PatreonData authenticatePetscii(BbsThread bbs) throws Exception {
        final String DEFAULT = "0";
        String user = null;
        String patreonLevel = DEFAULT;
        String hostRow;

        try (Connection conn = openConnection()) {
            if (isNotBlank(hostRow = getFirstColumn(conn, "select ip from ipwhitelist")
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .filter(row -> !row.startsWith(";"))
                    .filter(row -> row.replaceAll(",.*$", "").equalsIgnoreCase(bbs.getIpAddress().getHostAddress()))
                    .findFirst().orElse(""))
            ) {
                if (bbs.getIpAddress().getHostAddress() != null) {
                    user = bbs.getIpAddress().getHostAddress();
                    patreonLevel = !hostRow.contains(",") ? DEFAULT : hostRow.replaceAll("^.*,", "");
                    bbs.write(GREY3);
                    registerFirstAccess(conn, user);
                    bbs.getRoot().setCustomObject(PatreonData.PATREON_USER, user);
                    bbs.getRoot().setCustomObject(PatreonData.PATREON_LEVEL, patreonLevel);
                    return new PatreonData(user, patreonLevel);
                }
            }

            try {
                user = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_USER);
                patreonLevel = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_LEVEL);
            } catch (NullPointerException | ClassCastException e) {
                loggerAuthorizations.info("User not logged " + e.getClass().getName() + " " + e.getMessage());
            }
            if (user != null && !"null".equalsIgnoreCase(user)) {
                bbs.write(GREY3);
                registerFirstAccess(conn, user);
                return new PatreonData(user, patreonLevel);
            }

            bbs.cls();
            bbs.write(PatreonData.PETSCII_LOGO_AUTHENTICATE);
            bbs.println();
            bbs.write(GREY2);
            bbs.println("For security reasons, the BBS will log:");
            bbs.write(WHITE);
            bbs.print("IP address");
            bbs.write(GREY2);
            bbs.print(", ");
            bbs.write(WHITE);
            bbs.print("email");
            bbs.write(GREY2);
            bbs.print(" and ");
            bbs.write(WHITE);
            bbs.print("messages");
            bbs.write(GREY2);
            bbs.println(".");
            bbs.println("If you proceed, you will accept this.");
            bbs.println();
            bbs.write(BbsThread.readBinaryFile("petscii/patreon-access.seq"));
            bbs.write(GREY3);
            bbs.println("Your Patreon email:");
            bbs.println();
            bbs.println(repeat(BbsThread.chr(163), 39));
            bbs.write(GREY2);
            bbs.print("You can use: ");
            bbs.write(YELLOW);
            bbs.print("\"-\"");
            bbs.write(GREY2);
            bbs.println(" for underscore");
            bbs.write(YELLOW);
            bbs.print("             \"!\"");
            bbs.write(GREY2);
            bbs.print(" in place of ");
            bbs.write(YELLOW);
            bbs.println("\"@\"");
            bbs.println();
            bbs.write(GREY2);
            bbs.print("Example: ");
            bbs.write(WHITE);
            bbs.print("johndoe");
            bbs.write(YELLOW);
            bbs.print("!");
            bbs.write(WHITE);
            bbs.println("gmail.com");
            bbs.write(/*RETURN, RETURN, RETURN, */RETURN, GREY1);
            bbs.print("www.patreon.com/FrancescoSblendorio");
            bbs.write(UP, UP, UP, UP, UP, RETURN);
            bbs.write(UP, UP, UP);
            bbs.flush();
            bbs.resetInput();
            bbs.write(PetsciiColors.LIGHT_BLUE);
            String tempEmail = bbs.readLine();
            bbs.write(RETURN, RETURN, RETURN, RETURN, RETURN, RETURN, GREY1);
            bbs.print(repeat(' ', 39));
            bbs.write(UP, UP, UP, UP, UP, UP, UP, RETURN);
            final String userEmail = trimToEmpty(tempEmail);
            if (isBlank(userEmail) || ".".equals(userEmail)) {
                bbs.write(GREY3);
                return null;
            }

            String emailRow = getFirstColumn(conn, "select email from members union select email from fixed where disabled=0")
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .filter(row -> !row.startsWith(";"))
                    .filter(row -> row.replace("_", "-").replace("*", "@").replace("!", "@")
                            .replaceAll(",.*$", "")
                            .equalsIgnoreCase(userEmail.replace("_", "-").replace("*", "@").replace("!", "@")))
                    .findAny()
                    .orElse("");

            if (isBlank(emailRow)) {
                loggerAuthorizations.info(
                        "Patreon unknown email. Email:{}, Host:{}, Port:{}",
                        userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort()
                );
                bbs.println();
                bbs.write(PetsciiColors.RED);
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println(" Not subscriber's mail ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println(" Press any key to exit ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                bbs.write(GREY3);
                return null;
            }

            String email = emailRow.replaceAll(",.*$", "");

            String secretCode = generateSecretCode(PatreonData.CODE_LENGTH);
            bbs.println();
            bbs.println(repeat(' ', 31));
            bbs.println(repeat(' ', 32));
            bbs.println();
            bbs.println(repeat(' ', 26));
            bbs.write(UP, UP, UP, UP);
            waitOnPetscii(bbs);
            boolean success = sendSecretCode(email, secretCode);
            if (!success) {
                waitOffPetscii(bbs);
                bbs.println();
                bbs.write(PetsciiColors.RED);
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("   Mail server error   ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println(" Press any key to exit ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.print("                       ");
                bbs.write(REVOFF);
                bbs.print("  ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                bbs.write(GREY3);
                return null;
            }
            waitOffPetscii(bbs);
            long startMillis = System.currentTimeMillis();
            bbs.write(GREY3);
            bbs.println();
            bbs.println("Please type " + PatreonData.CODE_LENGTH + "-digit code just sent");
            bbs.print("to your email: ");
            bbs.write(PetsciiColors.LIGHT_BLUE);
            bbs.flush();
            bbs.resetInput();
            String userCode = bbs.readLine(PatreonData.CODE_LENGTH);
            userCode = trimToEmpty(userCode);
            long endMillis = System.currentTimeMillis();
            if (endMillis - startMillis > PatreonData.TIMEOUT) {
                loggerAuthorizations.info("Patreon timeout. Email:{}, Host:{}, Port:{}", userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort());
                bbs.write(UP, UP, UP, PetsciiColors.RED);
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.print(" Timeout, try it again ");
                bbs.write(REVOFF);
                bbs.println("   ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println(" Press any key to exit ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                bbs.write(GREY3);
                return null;
            }

            if (!userCode.equalsIgnoreCase(secretCode)) {
                loggerAuthorizations.info("Patreon wrong code. Email:{}, Host:{}, Port:{}", userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort());
                bbs.write(UP, UP, UP, PetsciiColors.RED);
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.print(" It was the wrong code ");
                bbs.write(REVOFF);
                bbs.println("   ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println(" Press any key to exit ");
                bbs.print("         ");
                bbs.write(REVON);
                bbs.println("                       ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                bbs.write(GREY3);
                return null;
            }

            user = email;
            patreonLevel = !emailRow.contains(",") ? DEFAULT : emailRow.replaceAll("^.*,", "");
            bbs.getRoot().setCustomObject(PatreonData.PATREON_USER, user);
            bbs.getRoot().setCustomObject(PatreonData.PATREON_LEVEL, patreonLevel);
            loggerAuthorizations.info("Patreon login. Email: {}, Host:{}, Port: {}", userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort());
            bbs.write(GREY3);
            registerFirstAccess(conn, user);
            return new PatreonData(user, patreonLevel);
        }
    }

    public static PatreonData authenticateAscii(BbsThread bbs) throws Exception {
        final String DEFAULT = "0";
        String user = "";
        String patreonLevel = DEFAULT;
        String hostRow;

        try (Connection conn = openConnection()) {
            if (isNotBlank(hostRow = getFirstColumn(conn, "select ip from ipwhitelist where disabled=0")
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .filter(row -> !row.startsWith(";"))
                    .filter(row -> row.replaceAll(",.*$", "").equalsIgnoreCase(bbs.getIpAddress().getHostAddress()))
                    .findFirst().orElse(""))
            ) {
                if (bbs.getIpAddress().getHostAddress() != null) {
                    user = bbs.getIpAddress().getHostAddress();
                    patreonLevel = !hostRow.contains(",") ? DEFAULT : hostRow.replaceAll("^.*,", "");
                    bbs.getRoot().setCustomObject(PatreonData.PATREON_USER, user);
                    bbs.getRoot().setCustomObject(PatreonData.PATREON_LEVEL, patreonLevel);
                    registerFirstAccess(conn, user);
                    return new PatreonData(user, patreonLevel);
                }
            }

            try {
                user = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_USER);
                patreonLevel = (String) bbs.getRoot().getCustomObject(PatreonData.PATREON_LEVEL);
            } catch (NullPointerException | ClassCastException e) {
                loggerAuthorizations.info("User not logged " + e.getClass().getName() + " " + e.getMessage());
            }
            if (user != null && !"null".equalsIgnoreCase(user)) {
                registerFirstAccess(conn, user);
                return new PatreonData(user, patreonLevel);
            }

            bbs.cls();
            bbs.println("Patreon Login - Authenticate");
            bbs.println("----------------------------");
            bbs.println();
            bbs.println("For security reasons:");
            bbs.println("- IP address");
            bbs.println("- email");
            bbs.println("- messages");
            bbs.println("will be logged. If you proceed,");
            bbs.println("you will accept this.");
            bbs.println();
            bbs.println("Functionality reserved to Patrons");
            bbs.println("https://patreon.com/FrancescoSblendorio");
            bbs.println();
            bbs.println("Your Patreon email ('-' for underscore)");
            bbs.println("   you can use ! instead of @, example:");
            bbs.println("                      johndoe!gmail.com");
            bbs.print(">");
            bbs.flush();
            bbs.resetInput();
            String tempEmail = bbs.readLine(setOfChars(STR_ALPHANUMERIC, ".:,;_ {}[]()<>@+-*/^='?!$%&#"));
            final String userEmail = trimToEmpty(tempEmail);
            if (isBlank(userEmail) || ".".equals(userEmail))
                return null;

            String emailRow = getFirstColumn(conn, "select email from members union select email from fixed")
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .filter(row -> !row.startsWith(";"))
                    .filter(row -> row.replace("_", "-").replace("*", "@").replace("!", "@")
                            .replaceAll(",.*$", "")
                            .equalsIgnoreCase(userEmail.replace("_", "-").replace("*", "@").replace("!", "@")))
                    .findAny()
                    .orElse("");

            if (isBlank(emailRow)) {
                loggerAuthorizations.info(
                        "Patreon unknown email. Email:{}, Host:{}, Port:{}",
                        userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort()
                );
                bbs.println();
                bbs.println("Not subscriber's mail");
                bbs.println("Press any key to exit");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                return null;
            }

            String email = emailRow.replaceAll(",.*$", "");

            waitOnAscii(bbs);
            String secretCode = generateSecretCode(PatreonData.CODE_LENGTH);
            boolean success = sendSecretCode(email, secretCode);
            waitOffAscii(bbs);
            if (!success) {
                bbs.println();
                bbs.println("Mail server error");
                bbs.println("Press any key to exit ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                return null;
            }
            long startMillis = System.currentTimeMillis();
            bbs.println();
            bbs.println("Please type " + PatreonData.CODE_LENGTH + "-digit code just sent");
            bbs.print("to your email: ");
            bbs.flush();
            bbs.resetInput();
            String userCode = bbs.readLine(PatreonData.CODE_LENGTH);
            userCode = trimToEmpty(userCode);
            long endMillis = System.currentTimeMillis();
            if (endMillis - startMillis > PatreonData.TIMEOUT) {
                loggerAuthorizations.info("Patreon timeout. Email:{}, Host:{}, Port:{}", userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort());
                bbs.println();
                bbs.println("Timeout, try it again ");
                bbs.println("Press any key to exit ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                return null;
            }

            if (!userCode.equalsIgnoreCase(secretCode)) {
                loggerAuthorizations.info("Patreon wrong code. Email:{}, Host:{}, Port:{}", userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort());
                bbs.println();
                bbs.println("It was the wrong code ");
                bbs.println("Press any key to exit ");
                bbs.flush();
                bbs.resetInput();
                bbs.readKey();
                return null;
            }

            user = email;
            patreonLevel = !emailRow.contains(",") ? DEFAULT : emailRow.replaceAll("^.*,", "");
            bbs.getRoot().setCustomObject(PatreonData.PATREON_USER, user);
            bbs.getRoot().setCustomObject(PatreonData.PATREON_LEVEL, patreonLevel);
            loggerAuthorizations.info("Patreon login. Email: {}, Host:{}, Port: {}", userEmail, bbs.getSocket().getInetAddress().getHostAddress(), bbs.getSocket().getLocalPort());
            registerFirstAccess(conn, user);
            return new PatreonData(user, patreonLevel);
        }
    }

    private static void waitOnPetscii(BbsThread bbs) {
        bbs.write(WAIT_COLOR_PETSCII);
        bbs.print(WAIT_MESSAGE_PETSCII);
        bbs.flush();
    }

    private static void waitOffPetscii(BbsThread bbs) {
        for (int i = 0; i < WAIT_MESSAGE_PETSCII.length(); ++i) bbs.write(AsciiKeys.BACKSPACE);
        bbs.flush();
    }

    private static void waitOnAscii(BbsThread bbs) {
        bbs.print(WAIT_MESSAGE_ASCII);
        bbs.flush();
    }

    private static void waitOffAscii(BbsThread bbs) {
        for (int i = 0; i < WAIT_MESSAGE_ASCII.length(); ++i) bbs.write(AsciiKeys.BACKSPACE);
        bbs.flush();
    }

    private static boolean sendSecretCode(String email, String secretCode) {
        final String USERNAME = defaultString(getProperty("MAIL_FROM", getenv("MAIL_FROM")));
        final String PASSWORD = defaultString(getProperty("MAIL_FROM_PASSWORD", getenv("MAIL_FROM_PASSWORD")));
        final String MAIL_SMTP_HOST = defaultString(getProperty("MAIL_SMTP_HOST", getenv("MAIL_SMTP_HOST")));
        final String MAIL_SMTP_PORT = defaultString(getProperty("MAIL_SMTP_PORT", getenv("MAIL_SMTP_PORT")));
        final String MAIL_SMTP_AUTH = defaultString(getProperty("MAIL_SMTP_AUTH", getenv("MAIL_SMTP_AUTH")));
        final String MAIL_SMTP_STARTTLS_ENABLE = defaultString(getProperty("MAIL_SMTP_STARTTLS_ENABLE", getenv("MAIL_SMTP_STARTTLS_ENABLE")));

        Properties prop = new Properties();
        prop.put("mail.smtp.host", MAIL_SMTP_HOST);
        prop.put("mail.smtp.port", MAIL_SMTP_PORT);
        prop.put("mail.smtp.auth", MAIL_SMTP_AUTH);
        prop.put("mail.smtp.starttls.enable", MAIL_SMTP_STARTTLS_ENABLE);

        Session session = Session.getInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("BBS access code");
            message.setText("Your temporary code is " + secretCode);

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            loggerAuthorizations.info("Send Email Exception:", e);
            return false;
        }
    }

    private static String generateSecretCode(int length) {
        return PatreonData.random
                .ints(0, 10)
                .limit(length)
                .mapToObj(String::valueOf)
                .collect(joining());
    }

    private static void registerFirstAccess(Connection conn, String user) throws Exception {
        final String filename = getProperty("PATREON_CONSENT_EMAILS", getProperty("user.home") + File.separator + "consent_emails.txt");
        List<String> rows = readExternalTxt(filename);
        boolean yetConnected = rows
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trimToEmpty)
                .map(row -> row.split(";")[0])
                .toList()
                .contains(user);

        if (!yetConnected) {
            rows.add(user + ";" + Instant.now().toString());
            FileWriter writer = new FileWriter(filename);
            for(String str: rows) writer.write(str + System.lineSeparator());
            writer.close();
        }
/*
        try (PreparedStatement ps = conn.prepareStatement("select 1 from consentlist where user = ?")) {
            ps.setString(1, user);
            if (ps.executeQuery().next()) return;
        }
        try (PreparedStatement ps = conn.prepareStatement("insert or ignore into consentlist (user, timestamp) values (?, ?)")) {
            ps.setString(1, user);
            ps.setString(2, Instant.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            loggerAuthorizations.warn("Error during inserting {} in consentlist at {}: {}", user, Instant.now().toString(), e);
        }
 */
    }

    private static List<String> getFirstColumn(Connection conn, String query) throws SQLException {
        List<String> result = new LinkedList<>();
        try (Statement s = conn.createStatement();
             ResultSet r = s.executeQuery(query)) {
            while (r.next())
                result.add(r.getString(1));
        }
        return result;
    }

    private static Connection openConnection() throws Exception {
        if (!new File(DB_FILE).exists()) createDatabase(properties);
        return DriverManager.getConnection("jdbc:sqlite:" + DB_FILE, properties);
    }

    public static List<String> getPatrons() throws Exception {
        try (Connection conn = openConnection()) {
            return getFirstColumn(conn, "select name from members order by name");
        }
    }

    public static List<String> getPatronsWithTier() throws Exception {
        try (Connection conn = openConnection()) {
            return getFirstColumn(conn, "select name || case type when 'Supporter' then '' when '' then '' else ' - ' || type end from members order by name");
        }
    }

    public static void createDatabase(Properties properties) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE, properties)) {

            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE members (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), email VARCHAR(100), type VARCHAR(50))");
            }

            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE fixed (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(100), email VARCHAR(100), type VARCHAR(50), disabled INTEGER default 0)");
            }

            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE ipwhitelist (id INTEGER PRIMARY KEY AUTOINCREMENT, ip VARCHAR(100), type VARCHAR(50), notes VARCHAR(100), disabled INTEGER default 0)");
            }

            /*
            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE consentlist (id INTEGER PRIMARY KEY AUTOINCREMENT, user VARCHAR(100), timestamp VARCHAR(100), UNIQUE(user))");
            }
            */
        }
    }

}
