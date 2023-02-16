package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_RED;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import static java.util.Arrays.asList;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import eu.sblendorio.bbs.core.PetsciiThread;

@Hidden
public class UserLogon extends PetsciiThread {

    protected static final String CUSTOM_KEY = "PRIVATE_MESSAGES";
    protected static final String DB_FILE = System.getProperty("user.home") + "/bbs-data.db";
    protected static final Properties properties;
    SecureRandom random;

    static {
        properties = new Properties();
        properties.setProperty("characterEncoding", UTF_8);
        properties.setProperty("encoding", "\"" + UTF_8 + "\"");
    }

    private Connection conn = null;
    private User user;

    public static class User {
        public final Long id;
        public final String nick;
        public final String realname;
        public final String email;

        public User(Long id, String nick, String realname, String email) {
            this.id = id;
            this.nick = nick;
            this.realname = realname;
            this.email = email;
        }
    }

    public static class Message {
        public final Long rowId;
        public final String userFrom;
        public final String userTo;
        public final Date dateTime;
        public boolean isRead;
        public final String subject;
        public final String message;
        public final boolean receiverExists;

        public Message(Long rowId, String userFrom, String userTo, Date dateTime, boolean isRead, String subject, String message, boolean receiverExists) {
            this.rowId = rowId;
            this.userFrom = userFrom;
            this.userTo = userTo;
            this.dateTime = dateTime;
            this.isRead = isRead;
            this.subject = subject;
            this.message = message;
            this.receiverExists = receiverExists;
        }

        public void setIsRead(boolean value)  {
            this.isRead = value;
        }
    }

    private void openConnection() throws Exception {
        if (!new File(DB_FILE).exists()) createDatabase(properties);
        if (conn == null || conn.isClosed())
            conn = DriverManager.getConnection("jdbc:sqlite:"+ DB_FILE, properties);
    }

    public void init() throws Exception {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = null;
        }
        user = null;
        try {
            user = (User) getRoot().getCustomObject(CUSTOM_KEY);
        } catch (NullPointerException | ClassCastException e) {
            log("User not logged " + e.getClass().getName() + " " + e.getMessage() );
        }
        openConnection();
    }

    @Override
    public void doLoop() throws Exception {
        init();
        String username;
        String password;
        if (user == null) {
            cls();
            write(CASE_LOCK, LOWERCASE);
            write(LOGO_BYTES);
            write(GREY3);
            newline();
            println("Enter 'P' for privacy policy");
            newline();
        }
        while (user == null) {
            do {
                print("USERID or 'NEW': ");
                flush(); resetInput();
                username = readLine();
                if (isBlank(username) || ".".equals(username)) return;
                if (equalsIgnoreCase(username, "p")) {
                    showPrivacyPolicy();
                    cls();
                    write(CASE_LOCK, LOWERCASE);
                    write(LOGO_BYTES);
                    write(GREY3);
                    newline();
                    println("Enter 'P' as USERID for privacy policy");
                    newline();
                } else if (equalsIgnoreCase(username, "new")) {
                    if (createNewUser()) {
                        write(GREEN); println("User created successfully.");
                    } else {
                        write(RED); println("Operation aborted.");
                    }
                    write(GREY3);
                    newline();
                }
            } while (equalsIgnoreCase(username, "new") || equalsIgnoreCase(username, "p"));
            print("PASSWORD: ");
            flush(); resetInput(); password = readPassword();
            user = getUser(username, password);
            if (user == null) {
                write(RED);
                newline();
                write(REVON); println("Wrong username or password"); write(REVOFF);
                newline();
                write(GREY3);
            }
        }
        try {
            getRoot().setCustomObject(CUSTOM_KEY, user);
        } catch (NullPointerException e) {
            // do nothing
        }
        listMessages(false);
    }

    public void listUsers() throws Exception {
        cls();
        write(LOGO_BYTES);
        write(GREY3);
        List<User> users = getUsers();
        int i = 0;
        for (User u: users) {
            ++i;
            write(CYAN);
            print(u.nick);
            write(GREY3);
            String realname = u.realname;
            if (isNotBlank(realname) && (u.nick + realname).length() > 36)
                realname = realname.substring(0, 33 - u.nick.length()) + "...";
            println(isBlank(realname) ? EMPTY : " (" + realname + ")");
            if (i % 19 == 0 && i < users.size()) {
                newline();
                write(WHITE); print("ANY KEY FOR NEXT PAGE, '.' TO GO BACK "); write(GREY3);
                flush(); resetInput(); int ch = readKey(); resetInput();
                if (ch == '.') return;
                cls();
                write(LOGO_BYTES);
                write(GREY3);
            }
        }
        newline();
        write(WHITE); print("PRESS ANY KEY TO GO BACK "); write(GREY3);
        flush(); resetInput(); readKey(); resetInput();
    }

    public void sendMessageGui() throws Exception {
        sendMessageGui(null, null);
    }

    public void sendMessageGui(String toUser, String toSubject) throws Exception {
        String receipt;
        String subject;
        boolean ok = false;

        if (toUser != null) {
            receipt = toUser;
            subject = defaultString(toSubject);
            print("send to: "); println(receipt);
            print("subject: "); println(subject);
        }
        else {
            do {
                print("send to (? for user list): ");
                flush(); resetInput(); receipt = readLine();
                if (isBlank(receipt)) return;
                ok = existsUser(receipt);
                if (!ok && !"?".equals(receipt)) println("WARN: not existing user");
                if ("?".equals(receipt)) {
                    listUsers();
                    newline();
                    newline();
                }
            } while (!ok);

            print("subject: ");
            flush(); resetInput(); subject = readLine();
            if (isBlank(subject)) return;
        }
        newline();
        println("Message (end with EMPTY LINE)");
        println("-----------------------------");
        String line;
        String message = EMPTY;
        do {
            flush(); resetInput();
            line = readLine();
            if (isNotBlank(line))
                message += line + "\n";
        } while (isNotBlank(line));

        sendMessage(user.nick, receipt, subject, message);
        newline(); write(WHITE);
        print("MESSAGE SENT - PRESS ANY KEY ");
        write(GREY3);
        flush(); resetInput(); readKey(); resetInput();
    }

    public void sendMessage(String from, String to, String subject, String message) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO messages (user_from, user_to, datetime, is_read, subject, message) values (?,?,?,?,?,?)")) {
            ps.setString(1, from);
            ps.setString(2, to);
            ps.setLong(3, System.currentTimeMillis());
            ps.setLong(4, 0);
            ps.setString(5, subject);
            ps.setString(6, message);
            ps.executeUpdate();
        }
    }

    public void listMessages(boolean onlyUnread) throws Exception {
        List<Message> messages = getMessages(user.nick, onlyUnread);

        int pagesize = 12;
        int offset = 0;
        String cmd;
        do {
            int size = messages.size();
            if (onlyUnread && size == 0) {
                onlyUnread = false;
                messages = getMessages(user.nick, onlyUnread);
                size = messages.size();
            }
            long unread = countUnreadMessages(user.nick);
            cls();
            write(CASE_LOCK, LOWERCASE);
            write(LOGO_BYTES);
            write(GREY3);
            println("Got " + size  + (onlyUnread ? " unread" : EMPTY) + " message" + (size != 1 ? "s" : EMPTY) + (onlyUnread || unread == 0 ? EMPTY : " (" + unread + " unread)") + ".");
            newline();
            for (int i=offset; i<Math.min(offset+pagesize, size); ++i) {
                int i1=i+1;
                Message m = messages.get(i);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat tf = new SimpleDateFormat("hh:mm:ssa");
                String nowString= df.format(new Date(System.currentTimeMillis()));
                String date = df.format(m.dateTime);
                if (date.equals(nowString)) date = tf.format(m.dateTime);
                String subject = isNotBlank(m.subject) ? m.subject : defaultString(m.message).replaceAll("[\r\n]", " ");
                if (isNotBlank(subject) && (1+(""+i1).length()+1+10+1+m.userFrom.length()+1+m.subject.length() )>(getScreenColumns() - 1))
                    subject = subject.substring(0,(getScreenColumns() - 1)-(1+(""+i1).length()+1+10+1+m.userFrom.length()+1));
                write(LIGHT_RED); print((m.isRead ? " " : "*"));
                write(WHITE); print(i1 + " ");
                write(GREY3); print(date + " ");
                write(m.receiverExists ? CYAN : RED); print(m.userFrom);
                print(" ");
                write(WHITE); print(subject);
                newline();
            }
            write(GREY3);
            write(WHITE);println(repeat('_', getScreenColumns() - 1));write(GREY3);

            write(REVON); print(" U "); write(REVOFF); print(" List users ");
            write(REVON); print(" M "); write(REVOFF); print(" New message ");
            write(REVON); print(" . "); write(REVOFF); println(" Exit");

            write(REVON); print(" A "); write(REVOFF); print(" All messag ");
            write(REVON); print(" R "); write(REVOFF); println(" Only unread messages");

            write(REVON); print(" # "); write(REVOFF); print(" Read message number  ");
            write(REVON); print(" K "); write(REVOFF); println(" User prefs");

            write(REVON); print(" N "); write(REVOFF); print(" Next page ");
            write(REVON); print(" - "); write(REVOFF); print(" Prev page ");
            write(REVON); print(" P "); write(REVOFF); println(" Privacy");

            write(WHITE);println(StringUtils.repeat(chr(163), getScreenColumns() - 1));write(GREY3);
            print("> ");
            flush(); resetInput(); cmd = readLine();
            cmd = defaultString(trim(lowerCase(cmd)));
            int index = toInt(cmd.replace("#", EMPTY));
            if (("+".equals(cmd) || "n".equals(cmd)) && (offset+pagesize<size)) {
                offset += pagesize;
            } else if ("-".equals(cmd) && offset > 0) {
                offset -= pagesize;
            } else if ("u".equals(cmd)) {
                listUsers();
            } else if ("a".equals(cmd)) {
                onlyUnread = false;
            } else if ("r".equals(cmd)) {
                onlyUnread = true;
            } else if ("m".equals(cmd)) {
                sendMessageGui();
            } else if ("p".equals(cmd)) {
                showPrivacyPolicy();
            } else if ("k".equals(cmd)) {
                userPreferences();
            } else if (isNumeric(cmd.replace("#", EMPTY)) && index>0 && index<=size) {
                displayMessage(messages.get(index - 1));
            }
            messages = getMessages(user.nick, onlyUnread);
        } while (!".".equals(cmd));

    }

    public void displayMessage(Message m) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cls();
        write(LOGO_BYTES);
        write(GREY3);
        println("From: "+ m.userFrom);
        println("To:   "+ m.userTo);
        println("Date: "+ df.format(m.dateTime));
        println("Subj: "+ m.subject);
        println(StringUtils.repeat(chr(163),getScreenColumns() - 1));
        String[] lines = defaultString(m.message).split("\n");
        List<String> rows = new LinkedList<>();
        for (String line: lines) {
            rows.addAll(asList(WordUtils.wrap(line, getScreenColumns() - 1, "\r", true ).split("\r")));
        }
        int linecount = 0;
        for (String row: rows) {
            ++linecount;
            println(row);
            if (linecount % 23 == 0) {
                println();
                print("-- More --");
                resetInput();
                readKey();
                newline();
                newline();
            }
        }
        markAsRead(m);
        newline();
        print("press "); write(WHITE); print("'R'"); write(GREY3); print(" to REPLY, any key to go back.");
        flush();
        resetInput(); int ch = readKey();
        if (ch == 'r' || ch == 'R') {
            newline();
            newline();
            sendMessageGui(m.userFrom, m.subject);
        }
    }

    void markAsRead(Message m) throws Exception {
        m.setIsRead(true);
        try (PreparedStatement ps = conn.prepareStatement("update messages set is_read=1 where rowid=?")) {
            ps.setLong(1, m.rowId);
            ps.executeUpdate();
        }
    }

    public List<User> getUsers() throws Exception {
        List<User> result = new LinkedList<>();
        try (Statement s = conn.createStatement();
                ResultSet r = s.executeQuery("select id, nick, realname, email from users order by nick collate nocase")) {
            while (r.next())
                result.add(new User(
                    r.getLong("id"),
                    r.getString("nick"),
                    r.getString("realname"),
                    r.getString("email")
            ));
        }
        return result;
    }

    public List<Message> getMessages(String userTo, boolean onlyUnread) throws Exception {
        List<Message> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("" +
            "SELECT messages.rowid, user_from, user_to, datetime, is_read, subject, message, id FROM messages LEFT JOIN users ON user_from=nick " +
            "collate nocase WHERE user_to=? collate nocase "+
            (onlyUnread ? " AND is_read = 0 " : EMPTY) + " ORDER BY datetime DESC") ) {
            ps.setString(1, userTo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    result.add(new Message(
                            rs.getLong("rowid"),
                            rs.getString("user_from"),
                            rs.getString("user_to"),
                            new Date(rs.getLong("datetime")),
                            rs.getLong("is_read") != 0,
                            rs.getString("subject"),
                            rs.getString("message"),
                            rs.getString("id") != null
                    ));
            }
        }
        return result;
    }

    public void userPreferences() throws Exception {
        int ch;
        do {
            cls();
            write(LOGO_BYTES);
            write(GREY3);
            println("User preferences [" + user.nick + "]");
            newline();
            write(REVON); print(" 1 "); write(REVOFF); println(" Change password");
            write(REVON); print(" 2 "); write(REVOFF); println(" Change realname");
            write(REVON); print(" 3 "); write(REVOFF); println(" Erase user");
            write(REVON); print(" . "); write(REVOFF); println(" Back to messages");
            newline();
            flush(); resetInput();
            ch = readKey();

            if (ch == '1')  {

            } else if (ch == '2') {
                newline();
                print("Real name: ");
                flush(); resetInput();
                String newName = readLine();
                if (isNotBlank(newName)) {
                    user = changeUserName(user, newName);
                    newline();
                    write(LIGHT_GREEN); println("Real name change successfully"); write(GREY3);
                    flush();
                    resetInput(); readKey();
                }
            } else if (ch == '3') {
                write(RED);
                write(REVON); println("         ");
                write(REVON); println(" WARNING ");
                write(REVON); println("         "); write(REVOFF);
                newline();
                println("This choice will erase your account");
                print("Are you sure? (Y/N) ");
                flush(); resetInput();
                int erase = readKey();
                if (erase == 'y' || erase == 'Y') {
                    newline();
                    newline();
                    killUser(user.nick);
                    getRoot().setCustomObject(CUSTOM_KEY, null);
                    write(REVON); println("                          ");
                    write(REVON); println(" USER PERMANENTLY DELETED ");
                    write(REVON); println("                          "); write(REVOFF);
                    write(GREY3); newline();
                    println("PRESS ANY KEY TO EXIT");
                    flush(); resetInput();
                    readKey();
                    throw new UserRemovedException();
                }
            }
        } while (ch != '.');
    }

    public boolean createNewUser() throws Exception {
        String username;
        String password;
        String realname;
        String email;
        boolean notValid;
        newline();
        write(WHITE);
        println("ADDING NEW USER");
        println(StringUtils.repeat(chr(163), 15));
        write(GREY3);
        do {
            print("Username: ");
            flush(); resetInput(); username = readLine();
            if (isBlank(username)) return false;
            notValid = existsUser(username) || userInVault(username) || "?".equals(username) || "p".equalsIgnoreCase(username);
            if (notValid) println("WARN: Username not available");
        } while (notValid);
        print("Real name: "); flush(); resetInput(); realname = readLine();
        print("Email: "); flush(); resetInput(); email = readLine();
        do {
            print("Password: ");
            flush(); resetInput(); password = readPassword();
        } while (isBlank(password));
        write(LIGHT_RED); print("Do you confirm creation? (Y/N)"); write(GREY3);
        flush(); resetInput(); int key = readKey(); resetInput();
        newline();
        return (key=='Y' || key=='y') ? addUser(username, realname, email, password) : false;
    }

    public boolean addUser(String nick, String realname, String email, String password) throws Exception {
        if (existsUser(nick)) {
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "insert into users (nick, realname, email, salt, password) values (?,?,?,?,?)")) {
            String salt = generateId();
            String hash = sha256Hex(salt + password);
            ps.setString(1, nick);
            ps.setString(2, realname);
            ps.setString(3, email);
            ps.setString(4, salt);
            ps.setString(5, hash);
            ps.execute();
        }
        return true;
    }

    public long countTotalMessages(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select count(*) from messages where user_to=? collate nocase")) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }
    public long countUnreadMessages(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select count(*) from messages where user_to=? and is_read=0 collate nocase")) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    public User getUserById(Long id) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, nick, realname, email, salt, password from users where id=? limit 1")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new User(rs.getLong("id"), rs.getString("nick"), rs.getString("realname"), rs.getString("email"));
            }
        }
    }

    public User changeUserName(User user, String newName) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("update users set realname=? where id=?")) {
            ps.setString(1, newName);
            ps.setLong(2, user.id);
            ps.executeUpdate();
        }

        return getUserById(user.id);
    }

    public boolean existsUser(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, realname, email, salt, password from users where nick=? collate nocase limit 1")) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean userInVault(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select hash from user_vault where hash=? limit 1")) {
            ps.setString(1, sha256Hex(nick));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User getUser(String nick, String givenPassword) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, realname, email, salt, password from users where nick=? collate nocase limit 1");) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = rs.next();
                if (!found) return null;
                String salt = defaultString(rs.getString("salt"));
                String password = defaultString(rs.getString("password"));
                if (!sha256Hex(salt + givenPassword).equals(password)) return null;
                Long id = rs.getLong("id");
                String realname = rs.getString("realname");
                String email = rs.getString("email");
                return new User(id, nick, realname, email);
            }
        }
    }

    public void killUser(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("delete from users where nick=? collate nocase")) {
            ps.setString(1, nick);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("delete from messages where user_to=? collate nocase")) {
            ps.setString(1, nick);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("insert into user_vault (hash) values (?)")) {
            ps.setString(1, sha256Hex(nick));
            ps.executeUpdate();
        }
    }

    public void createDatabase(Properties properties) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE, properties)) {

            try (Statement s = conn.createStatement()) {
                s.executeUpdate(
                        "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, nick TEXT, realname TEXT, email TEXT, salt text, password TEXT)");
            }

            try (Statement s = conn.createStatement()) {
                s.executeUpdate(
                        "CREATE TABLE messages (user_from TEXT, user_to TEXT, datetime INTEGER, is_read INTEGER, subject TEXT, message TEXT)");
            }

            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE user_vault (hash TEXT)");
            }
        }
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, -97, 32, -94, -68, -102, -95, -84, -69, -110, -69, 18, 31, -66, -94, -68,
        -110, 13, 18, -97, 32, -110, -94, 18, -84, -102, -95, -68, -66, -110, -66, 18,
        31, -69, -110, -94, -69, 32, 32, 18, -101, 32, -94, -68, -95, -84, -69, -110,
        -69, 18, 32, -95, -110, -95, 18, -95, -110, -95, -84, 18, 32, -110, -69, 18,
        -94, 32, -94, -95, -84, -94, -110, 13, 18, -97, 32, -110, 32, 18, 32, -102,
        -95, -110, -95, 18, -95, -110, -95, 31, -94, 32, 18, 32, -110, 32, 32, 18,
        -101, 32, -94, -110, -66, 18, -95, -84, -69, -110, -69, 18, 32, -110, -68, 18,
        -68, -66, -110, -66, 18, 32, -110, -94, 18, 32, -110, 32, 18, 32, -110, 32,
        18, -95, -84, -110, -66, 32, -97, -51, -63, -55, -52, 13, 18, -94, -94, -110,
        -66, -102, -68, 18, -94, -94, -110, 32, 31, -68, 18, -94, -110, -66, 32, 32,
        18, -101, -94, -110, 32, 32, -68, -66, -68, -66, 18, -94, -110, 32, -68, -66,
        32, 18, -94, -110, 32, 18, -94, -110, 32, 18, -94, -110, 32, -68, 18, -94,
        -94, -110, 32, -97, -93, -93, -93, -93, 13
    };

    public String generateId() {
        if (random == null) return UUID.randomUUID().toString();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String theHex = Integer.toHexString(bytes[i] & 0xFF).toLowerCase();
            sb.append(theHex.length() == 1 ? "0" + theHex : theHex);
        }
        return sb.toString();
    }

    public void showPrivacyPolicy() throws Exception {
        List<String> rawText = readTextFile("gdpr/privacy-statement.txt");
        List<String> text = new ArrayList<>();
        for (String row: rawText)
            text.addAll(asList(WordUtils.wrap(row, getScreenColumns() - 1, "\n", true).split("\n")));
        if (isEmpty(text)) return;
        int size = text.size();
        int pagesize = 18;
        int offset = 0;
        int cmd = 0;
        do {
            cls();
            write(LOGO_BYTES);
            write(GREY3);
            newline();
            for (int i = offset; i < Math.min(offset + pagesize, size); ++i) {
                println(text.get(i));

            }
            println();
            write(WHITE); print("SPACE");
            write(GREY3); print("=Next page [");
            write(WHITE); print("-");
            write(GREY3); print("]=Prev page [");
            write(WHITE); print(".");
            write(GREY3); print("]=EXIT");
            flush();
            resetInput();
            cmd = readKey();
            if (cmd == '.') {
                return;
            } else if (cmd == '-' && offset > 0) {
                offset -= pagesize;
            } else if (offset + pagesize < size) {
                offset += pagesize;
            }
        } while (true);
    }

    public static class UserRemovedException extends RuntimeException {}
}
