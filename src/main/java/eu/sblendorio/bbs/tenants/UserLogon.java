package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import static org.apache.commons.lang3.StringUtils.*;

public class UserLogon extends PetsciiThread {

    public static final String dbFile = System.getProperty("user.home") + "/mydatabase.db";
    public static Properties properties;

    static {
        properties = new Properties();
        properties.setProperty("characterEncoding", "UTF-8");
        properties.setProperty("encoding", "\"UTF-8\"");
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
        public final boolean isRead;
        public final String subject;
        public final String message;

        public Message(Long rowId, String userFrom, String userTo, Date dateTime, boolean isRead, String subject, String message) {
            this.rowId = rowId;
            this.userFrom = userFrom;
            this.userTo = userTo;
            this.dateTime = dateTime;
            this.isRead = isRead;
            this.subject = subject;
            this.message = message;
        }
    }

    private void openConnection() throws Exception {
        if (!new File(dbFile).exists()) createDatabase(properties);
        if (conn == null || conn.isClosed())
            conn = DriverManager.getConnection("jdbc:sqlite:"+dbFile, properties);
    }

    public UserLogon() throws Exception {
        openConnection();
    }

    @Override
    public void doLoop() throws Exception {
        String username;
        String password;
        do {
            do {
                print("username or NEW to sign up: ");
                username = readLine();
                if (isBlank(username)) return;
                if (equalsIgnoreCase(username, "new")) {
                    if (createNewUser())
                        println("User created successfully");
                    else
                        println("Error during creation");
                    newline();
                }
            } while (equalsIgnoreCase(username, "new"));
            print("password: ");
            password = readPassword();
            user = getUser(username, password);
            if (user == null) println("Wrong username and/or password");
        } while (user == null);
        println("Welcome, " + user.realname + "!");
        mainMenu();
    }

    public void mainMenu() throws Exception {
        int choice;
        do {
            //cls();
            println("Welcome, " + user.realname);
            newline();
            println("1. List all messages");
            println("2. List unread messages");
            println("3. Send a message");
            println(". = EXIT");
            print(">");
            flush(); resetInput(); choice = readKey();
            newline();

            switch (choice) {
                case '1': listMessages(false); break;
                case '2': listMessages(true); break;
                case '3': sendMessageGui(); break;
            }

        } while (choice != '.');
    }

    public void sendMessageGui() throws Exception {
        String receipt;
        String subject;
        String message;
        boolean ok = false;

        do {
            print("send to: ");
            receipt = readLine();
            if (isBlank(receipt)) return;
            ok = existsUser(receipt);
            if (!ok) println("WARN: not existing user");
        } while (!ok);

        print("subject: ");
        subject = readLine();
        if (isBlank(subject)) return;

        print("message: ");
        message = readLine();

        sendMessage(user.nick, receipt, subject, message);
        println("Message sent!\n");
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
        long i = 1;
        Map<Long, Message> map = new TreeMap<>();
        List<Message> messages = getMessages(user.nick, onlyUnread);
        println("Got "+ messages.size()+" messages.");
        for (Message m: messages) {
            map.put(i, m);
            println("["+i+"] "+m.userFrom+" "+m.subject);
            i++;
        }
    }

    public List<Message> getMessages(String userTo, boolean onlyUnread) throws Exception {
        List<Message> result = new LinkedList<>();
        try (PreparedStatement ps = conn.prepareStatement("" +
                "SELECT rowid, user_from, user_to, datetime, is_read, subject, message FROM messages WHERE user_to=? "+
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
                            rs.getString("message")
                    ));
            }
        }
        return result;
    }

    public boolean createNewUser() throws Exception {
        String username;
        String password;
        String realname;
        String email;
        boolean exists;
        do {
            print("Username: ");
            username = readLine();
            if (isBlank(username)) return false;
            exists = existsUser(username);
            if (exists) println("WARN: Username not available");
        } while (exists);
        print("Real name: "); realname = readLine();
        print("Email: "); email = readLine();
        do {
            print("Password: ");
            password = readPassword();
        } while (isBlank(password));
        return addUser(username, realname, email, password);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("START");
        UserLogon u = new UserLogon();
        //u.addUser("sblendorio", "Francesco Sblendorio", "sblendorio@gmail.com", "abc123");
        System.out.println("User="+u.getUser("sblendorio","abc123"));
        System.out.println("END");
    }

    public void read() throws Exception {

    }

    public boolean addUser(String nick, String realname, String email, String password) throws Exception {
        if (existsUser(nick)) return false;

        PreparedStatement ps = conn.prepareStatement("insert into users (nick, realname, email, salt, password) values (?,?,?,?,?)");
        String salt = UUID.randomUUID().toString();
        String hash = sha1Hex(salt+password);
        ps.setString(1, nick);
        ps.setString(2, realname);
        ps.setString(3, email);
        ps.setString(4, salt);
        ps.setString(5, hash);
        ps.execute();
        ps.close();
        return true;
    }

    public boolean existsUser(String nick) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, realname, email, salt, password from users where nick=?")) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User getUser(String nick, String givenPassword) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("select id, realname, email, salt, password from users where nick=?");) {
            ps.setString(1, nick);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = rs.next();
                if (!found) return null;
                String salt = defaultString(rs.getString("salt"));
                String password = defaultString(rs.getString("password"));
                if (!sha1Hex(salt + givenPassword).equals(password)) return null;
                Long id = rs.getLong("id");
                String realname = rs.getString("realname");
                String email = rs.getString("email");
                return new User(id, nick, realname, email);
            }
        }
    }

    public void createDatabase(Properties properties) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+dbFile, properties);
        Statement s;

        s = conn.createStatement();
        s.executeUpdate("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, nick TEXT, realname TEXT, email TEXT, salt text, password TEXT)");
        s.close();

        s = conn.createStatement();
        s.executeUpdate("CREATE TABLE messages (user_from TEXT, user_to TEXT, datetime INTEGER, is_read INTEGER, subject TEXT, message TEXT)");
        s.close();

        conn.close();
    }

}
