package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.petscii.UserLogon;
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
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.commons.text.WordUtils;

public class PrivateMessagesAscii extends AsciiThread {

    public PrivateMessagesAscii() {
        super();
    }

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
    private UserLogon.User user;

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
        private boolean isRead;
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
            user = (UserLogon.User) getRoot().getCustomObject(CUSTOM_KEY);
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
            write(LOGO_BYTES);
            newline();
            println("Enter 'P' for privacy policy");
            newline();
        }
        while (user == null) {
            do {
                print("USERID or 'NEW': ");
                flush(); resetInput();
                username = readLine();
                username = lowerCase(username);
                if (isBlank(username) || ".".equals(username)) return;
                if (equalsIgnoreCase(username, "p")) {
                    showPrivacyPolicy();
                    newline();
                    newline();
                    println("Enter 'P' as USERID for privacy policy");
                    newline();
                } else if (equalsIgnoreCase(username, "new")) {
                    if (createNewUser()) {
                        println("User created successfully.");
                    } else {
                        println("Operation aborted.");
                    }
                    newline();
                }
            } while (equalsIgnoreCase(username, "new") || equalsIgnoreCase(username, "p"));
            print("PASSWORD: ");
            flush(); resetInput(); password = readPassword();
            user = getUser(username, password);
            if (user == null) {
                newline();
                println("Wrong username or password");
                newline();
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
        newline();
        newline();
        List<UserLogon.User> users = getUsers();
        int i = 0;
        for (UserLogon.User u: users) {
            ++i;
            print(u.nick);
            String realname = u.realname;
            if (isNotBlank(realname) && (u.nick + realname).length() > 36)
                realname = realname.substring(0, 33 - u.nick.length()) + "...";
            println(isBlank(realname) ? EMPTY : " (" + realname + ")");
            if (i % 20 == 0 && i < users.size()) {
                newline();
                print("ANY KEY FOR NEXT PAGE, '.' TO GO BACK ");
                flush(); resetInput(); int ch = readKey(); resetInput();
                if (ch == '.') return;
                newline();
                newline();
            }
        }
        newline();
        print("PRESS ANY KEY TO GO BACK ");
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
                receipt = lowerCase(receipt);
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
            subject = lowerCase(subject);
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
            line = lowerCase(line);
            if (isNotBlank(line))
                message += line + "\n";
        } while (isNotBlank(line));

        sendMessage(user.nick, receipt, subject, message);
        newline();
        print("MESSAGE SENT - PRESS ANY KEY ");
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
        List<UserLogon.Message> messages = getMessages(user.nick, onlyUnread);

        int pagesize = getScreenRows() - 10;
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
            newline();
            newline();
            println("Got " + size  + (onlyUnread ? " unread" : EMPTY) + " message" + (size != 1 ? "s" : EMPTY) + (onlyUnread || unread == 0 ? EMPTY : " (" + unread + " unread)") + ".");
            newline();
            for (int i=offset; i<Math.min(offset+pagesize, size); ++i) {
                int i1=i+1;
                UserLogon.Message m = messages.get(i);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat tf = new SimpleDateFormat("hh:mm:ssa");
                String nowString= df.format(new Date(System.currentTimeMillis()));
                String date = df.format(m.dateTime);
                if (date.equals(nowString)) date = tf.format(m.dateTime);
                String subject = isNotBlank(m.subject) ? m.subject : defaultString(m.message).replaceAll("[\r\n]", " ");
                if (isNotBlank(subject) && (1+(""+i1).length()+1+10+1+m.userFrom.length()+1+m.subject.length() )> (getScreenColumns() - 1))
                    subject = subject.substring(0,(getScreenColumns() - 1)-(1+(""+i1).length()+1+10+1+m.userFrom.length()+1));
                print((m.isRead ? " " : "*"));
                print(i1 + " ");
                print(date + " ");
                print(m.receiverExists ? "" : "*(err)"); print(m.userFrom);
                print(" ");
                print(subject);
                newline();
            }
            println(repeat('-', 30));

            println("U=List users     M=New message");
            println("A=All messages   R=Only unread");
            println("*=Read message   K=User prefs.");
            println("N=Next page      -=Prev. page");
            println("P=Privacy        .=Exit");

            println(StringUtils.repeat('-', 30));
            print("> ");
            flush(); resetInput(); cmd = readLine();
            cmd = defaultString(trim(lowerCase(cmd)));
            int index = toInt(cmd.replace("#", EMPTY).replace("*", EMPTY));
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
            } else if (isNumeric(cmd.replace("#", EMPTY).replace("*", EMPTY)) && index>0 && index<=size) {
                displayMessage(messages.get(index - 1));
            }
            messages = getMessages(user.nick, onlyUnread);
        } while (!".".equals(cmd));

    }

    public void displayMessage(UserLogon.Message m) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        newline();
        println("From: "+ m.userFrom);
        println("To:   "+ m.userTo);
        println("Date: "+ df.format(m.dateTime));
        println("Subj: "+ m.subject);
        println(StringUtils.repeat('-',(getScreenColumns() - 1)));
        String[] lines = defaultString(m.message).split("\n");
        List<String> rows = new LinkedList<>();
        for (String line: lines) {
            rows.addAll(asList(WordUtils.wrap(line, getScreenColumns() - 1, "\r", true ).split("\r")));
        }
        int linecount = 0;
        for (String row: rows) {
            ++linecount;
            println(row);
            if (linecount % (getScreenRows() - 2) == 0) {
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
        print("press "); print("'R'"); print(" to REPLY, any key to go back.");
        flush();
        resetInput(); int ch = readKey();
        if (ch == 'r' || ch == 'R') {
            newline();
            newline();
            sendMessageGui(m.userFrom, m.subject);
        }
    }

    void markAsRead(UserLogon.Message m) throws Exception {
        m.setIsRead(true);
        try (PreparedStatement ps = conn.prepareStatement("update messages set is_read=1 where rowid=?")) {
            ps.setLong(1, m.rowId);
            ps.executeUpdate();
        }
    }

    public List<UserLogon.User> getUsers() throws Exception {
        List<UserLogon.User> result = new LinkedList<>();
        try (Statement s = conn.createStatement();
             ResultSet r = s.executeQuery("select id, nick, realname, email from users order by nick collate nocase")) {
            while (r.next())
                result.add(new UserLogon.User(
                    r.getLong("id"),
                    r.getString("nick"),
                    r.getString("realname"),
                    r.getString("email")
                ));
        }
        return result;
    }

    public List<UserLogon.Message> getMessages(String userTo, boolean onlyUnread) throws Exception {
        List<UserLogon.Message> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("" +
            "SELECT messages.rowid, user_from, user_to, datetime, is_read, subject, message, id FROM messages LEFT JOIN users ON user_from=nick " +
            "collate nocase WHERE user_to=? collate nocase "+
            (onlyUnread ? " AND is_read = 0 " : EMPTY) + " ORDER BY datetime DESC") ) {
            ps.setString(1, userTo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    result.add(new UserLogon.Message(
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
            newline();
            newline();
            println("User preferences for " + user.nick);
            newline();
            print("-1-"); println(" Change password");
            print("-2-"); println(" Change realname");
            print("-3-"); println(" Erase user");
            print(" . "); println(" Back to messages");
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
                    newName = lowerCase(newName);
                    user = changeUserName(user, newName);
                    newline();
                    println("Real name change successfully");
                    flush();
                    resetInput(); readKey();
                }
            } else if (ch == '3') {
                println("         ");
                println(" WARNING ");
                println("         ");
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
                    newline();
                    println("USER PERMANENTLY DELETED");
                    newline();
                    println("PRESS ANY KEY TO EXIT");
                    flush(); resetInput();
                    readKey();
                    throw new UserLogon.UserRemovedException();
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
        println("ADDING NEW USER");
        println(StringUtils.repeat('-', 15));
        do {
            print("Username: ");
            flush(); resetInput(); username = readLine();
            if (isBlank(username)) return false;
            username = lowerCase(username);
            notValid = existsUser(username) || userInVault(username) || "?".equals(username) || "p".equalsIgnoreCase(username);
            if (notValid) println("WARN: Username not available");
        } while (notValid);
        print("Real name: "); flush(); resetInput(); realname = readLine(); realname = lowerCase(realname);
        print("Email: "); flush(); resetInput(); email = readLine(); email = lowerCase(email);
        do {
            print("Password: ");
            flush(); resetInput(); password = readPassword();
        } while (isBlank(password));
        print("Do you confirm creation? (Y/N)");
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

    public UserLogon.User getUserById(Long id) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, nick, realname, email, salt, password from users where id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new UserLogon.User(rs.getLong("id"), rs.getString("nick"), rs.getString("realname"), rs.getString("email"));
            }
        }
    }

    public UserLogon.User changeUserName(UserLogon.User user, String newName) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("update users set realname=? where id=?")) {
            ps.setString(1, newName);
            ps.setLong(2, user.id);
            ps.executeUpdate();
        }

        return getUserById(user.id);
    }

    public boolean existsUser(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, realname, email, salt, password from users where nick=? collate nocase")) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean userInVault(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select hash from user_vault where hash=?")) {
            ps.setString(1, sha256Hex(nick));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public UserLogon.User getUser(String nick, String givenPassword) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, realname, email, salt, password from users where nick=? collate nocase");) {
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
                return new UserLogon.User(id, nick, realname, email);
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

    private static final byte[] LOGO_BYTES = Utils.bytes("BBS Private Messages\r\n--------------------\r\n\r\n");

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
        int pagesize = getScreenRows() - 2;
        int offset = 0;
        int cmd = 0;
        do {
            newline();
            for (int i = offset; i < Math.min(offset + pagesize, size); ++i) {
                println(text.get(i));

            }
            println();
            print("SPACE");
            print("=Next page [");
            print("-");
            print("]=Prev page [");
            print(".");
            print("]=EXIT");
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
