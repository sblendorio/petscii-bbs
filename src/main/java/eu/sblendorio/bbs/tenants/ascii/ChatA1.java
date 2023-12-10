package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.petscii.Chat64.ChatMessage;
import eu.sblendorio.bbs.tenants.petscii.Chat64.Row;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import static eu.sblendorio.bbs.core.Utils.bytes;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.*;

@Hidden
public class ChatA1 extends AsciiThread {
    private static Logger logger = LogManager.getLogger(ChatA1.class);
    private static final String CUSTOM_KEY = "CHAT";
    private boolean toLowerCase = false;
    private boolean canRedraw = false;
    private boolean beep = true;
    private String termType;
    private BbsInputOutput interfaceType;

    private ConcurrentLinkedDeque<Row> rows = new ConcurrentLinkedDeque<>();

    public ChatA1() {
        this(null, "ascii", false);
    }

    public ChatA1(String termType) {
        this(null, termType, false);
    }

    public ChatA1(BbsInputOutput interfaceType, String termType) {
        this(interfaceType, termType, false);
    }
    public ChatA1(BbsInputOutput interfaceType, String termType, boolean toLowerCase) {
        super();
        this.interfaceType = interfaceType;
        this.termType = termType;
        this.toLowerCase = toLowerCase;
    }

    public String parametricLowerCase(String s) {
        return toLowerCase
            ? lowerCase(s)
            : s;
    }

    @Override
    public void doLoop() throws Exception {
        if (interfaceType != null) {
            this.setBbsInputOutput(interfaceType);
        }
        try {
            canRedraw = false;
            int status;
            String previousName = (String) getRoot().getCustomObject(CUSTOM_KEY);

            boolean invalid = previousName == null ||
                clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(previousName));
            status = invalid ? -1 : changeClientName(previousName);

            while (status != 0) {
                cls();
                println("Welcome to BBS chat");
                newline();
                print("Enter your name: ");
                resetInput();
                flush(); resetInput();
                String candidateName = readLine();
                final String name = defaultString(parametricLowerCase(candidateName)).replace(" ", "");
                if (isBlank(name) || ".".equals(name)) {
                    return;
                }
                boolean alreadyPresent =
                    clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(name));
                boolean notValid = name.matches("^client[0-9]+$") || name.matches("^.*[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
                status = (alreadyPresent || notValid) ? -1 : changeClientName(name);
                if (status != 0) {
                    println("Error: name already used.");
                }
            };

            getRoot().setCustomObject(CUSTOM_KEY, getClientName());
            cls();
            write(logos.get(termType));
            showUsers(false);
            newline();
            displayHelp();

            notifyEnteringUser();
            String rawCommand = null;
            String originalCommand = null;
            redraw(false);
            do {
                resetInput();
                originalCommand = readLine();
                rawCommand = defaultString(originalCommand).trim();
                rawCommand = parametricLowerCase(rawCommand);
                final String command =  rawCommand;
                if (isBlank(command)) {
                    redraw(false);
                } else if (command.matches("(?is)^/to +[\\.a-zA-Z0-9-]+(\\s+.*)?$")) {
                    String text = defaultString(command.replaceAll("(?is)^/to +[\\.a-zA-Z0-9-]+(\\s+.*)?$", "$1")).trim();
                    final String recipientName = command.replaceAll("(?is)^/to +([\\.a-zA-Z0-9-]+)(\\s+.*)?$", "$1");
                    Long recipient = getClientIdByName(recipientName, String::compareToIgnoreCase);
                    if (recipientName.matches("^client[0-9]+$")) recipient = null;
                    if (recipientName.matches("^.*[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) recipient = null;

                    if (recipient != null && recipient != getClientId()) {
                        if (isNotBlank(text)) {
                            displayPotentialUrl(originalCommand);
                            logger.debug("<" + clientName + "@" + recipientName + ">" + text);
                            send(recipient, new ChatMessage(recipient, text));
                            redraw(false);
                        }
                    }
                    if (isBlank(text))
                        redraw(false);
                } else if (command.matches("(?is)/nick +[\\.a-zA-Z0-9-]+")) {
                    String candidateName = command.replaceAll("\\s+", " ").substring(6);
                    final String newName = parametricLowerCase(candidateName);
                    boolean alreadyPresent =
                        clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(newName));
                    final String oldName = getClientName();
                    int res = alreadyPresent ? -1 : changeClientName(newName);
                    if (res != 0) {
                        println("Error: name already used..");
                    } else {
                        getRoot().setCustomObject(CUSTOM_KEY, getClientName());
                        sendToAll(new ChatMessage(-4, oldName + " is now known as " + getClientName()));
                    }
                    redraw(false);
                } else if (command.equalsIgnoreCase("/cls")) {
                    canRedraw = false;
                    cls();
                    redraw(false);
                } else if (command.equalsIgnoreCase("/beep")) {
                    beep = !beep;
                    println("* beep " + (beep ? "on" : "off"));
                    redraw(false);
                } else if (command.equalsIgnoreCase("/users") ||
                        command.equalsIgnoreCase("/user")  ||
                        command.equalsIgnoreCase("/u")) {
                    canRedraw = false;
                    showUsers(true);
                    redraw(false);
                } else if (command.equalsIgnoreCase("/help") ||
                    command.equalsIgnoreCase("/?") ||
                    command.equalsIgnoreCase("/h")) {
                    canRedraw = false;
                    displayHelp();
                    redraw(false);
                } else if (".".equals(command) || "/q".equalsIgnoreCase(command) || "/quit".equalsIgnoreCase(command)) {
                    log("Exiting chat.");
                } else if (StringUtils.isNotBlank(command)) {
                    displayPotentialUrl(originalCommand);
                    sendToAll(new ChatMessage(-3, "<"+getClientName()+"@all>"+ command));
                    redraw(false);
                } else {
                    redraw(false);
                }
            } while (!".".equals(rawCommand) && !"/q".equalsIgnoreCase(rawCommand) && !"/quit".equalsIgnoreCase(rawCommand));
        } finally {
            write(exitSeq.get(termType));
            notifyExitingUser();
            changeClientName(UUID.randomUUID().toString());
        }
    }

    private /*synchronized*/ void sendToAll(ChatMessage chatMessage) {
        logger.debug(chatMessage.text);
        getClients().keySet().stream()
                .filter(id -> getClients().get(id) != null)
                .filter(id -> id != getClientId()
                        && getClients().get(id).getClientClass() != null
                        && getClients().get(id).getClientClass().getSimpleName().startsWith("Chat")
                )
                .forEach(id -> send(id, chatMessage));
    }

    private void notifyEnteringUser() {
        sendToAll(new ChatMessage(-1, getClientName() + " has entered"));
    }

    private void notifyExitingUser() {
        if (!getClientName().matches("^client[0-9]+$")
         && !getClientName().matches("^.*[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))
            sendToAll(new ChatMessage(-2, getClientName() + " just left"));
    }

    private synchronized void displayHelp() {
        println("Commands");
        println("/cls              " + (getScreenColumns() < 40 ? "" : "to clear screen"));
        println("/beep             " + (getScreenColumns() < 40 ? "" : "to toggle beep"));
        println("/users or /u      " + (getScreenColumns() < 40 ? "" : "to list users"));
        println("/to <user> msg    " + (getScreenColumns() < 40 ? "" : "to talk with someone"));
        println("/nick <name>      " + (getScreenColumns() < 40 ? "" : "to change nick"));
        println("/help or /h       " + (getScreenColumns() < 40 ? "" : "to get this help"));
        println("/quit or /q or .  " + (getScreenColumns() < 40 ? "" : "to exit chat"));
        println();
    }

    private synchronized void redraw(boolean duringWait) {
        canRedraw = false;
        displayMessages(duringWait);
        checkBelowLine();
        print(":");
        afterReadLineChar();
        canRedraw = true;
    }

    private void showUsers(boolean showIfNoUserConnected) throws IOException {
        List<BbsThread> users = getConnectedUsers();
        if (isNotEmpty(users)) {
            println("Connected users:");
            int i = 0;
            for (BbsThread u : users) {
                ++i;
                println(u.getClientName());
                if (i % 21 == 0 && i < users.size()) {
                    newline();
                    print("ANY KEY FOR NEXT PAGE, '.' TO GO BACK ");
                    flush();
                    resetInput();
                    int ch = readKey();
                    resetInput();
                    if (ch == '.') return;
                }
            }
            newline();
        } else if (showIfNoUserConnected) {
            println("NO OTHER USER CONNECTED.");
            newline();
        }
        print("Your username: ");
        println(defaultString(getClientName()));
    }

    private List<BbsThread> getConnectedUsers() {
        return getClients().values()
                .stream()
                .filter(x -> x.getClientClass().getSimpleName().startsWith("Chat")
                          && x.getClientId() != this.getClientId()
                          && !x.getClientName().matches("(?i)^client[0-9]+$")
                          && !x.getClientName().matches("(?i)^.*[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
                .collect(Collectors.toList());
    }

    private void displayMessages(boolean duringWait) {
        if (isEmpty(rows))
            return;

        Row row;
        boolean isFirstRow = true;
        while ((row = rows.poll()) != null) {
            if (!isFirstRow || !duringWait) print(":");
            if (row.message.receiverId == -1) {
                println("* " + row.message.text);
            } else if (row.message.receiverId == -2) {
                println("* " + row.message.text);
            } else if (row.message.receiverId == -4) {
                println("* " + row.message.text);
            } else if (row.message.receiverId == -3) {
                int index = row.message.text.indexOf(">");
                print(row.message.text.substring(0, index+1));
                println(row.message.text.substring(index+1));
                displayPotentialUrl(row.message.text.substring(index+1));
            } else {
                String from = ofNullable(getClients().get(row.recipientId)).map(BbsThread::getClientName).orElse(null);
                String to = ofNullable(getClients().get(row.message.receiverId)).map(BbsThread::getClientName).orElse(null);
                String text = row.message.text;

                if (from == null || to == null)
                    continue;

                print("<" + from + ">");
                println(text);
                displayPotentialUrl(text);
            }
            isFirstRow = false;
        }
    }

    private void displayPotentialUrl(String text) {
        if (!(io instanceof MinitelInputOutput)) return;

        if (
            text == null
            || text.contains("@")
            || (countMatches(text, '.') == 1 && countMatches(text, '/') == 0)
        ) return;

        UrlDetector parser = new UrlDetector(text, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();
        if (found == null || found.size() == 0) return;

        String firstUrl = found.get(0).getFullUrl();
        log("Detected URL: " + firstUrl);
        try {
            String shortUrl = firstUrl.length() <= 24 ? firstUrl : shortenUrl(firstUrl);
            String[] strMatrix = stringToQr(shortUrl);
            println();
            write(MinitelControls.GRAPHICS_MODE);
            write(BlockGraphicsMinitel.getRenderedMidres(2, strMatrix));
        } catch (Exception e) {
            log("Malformed URL exception in text: \"" + text + "\"", e);
        } finally {
            write(MinitelControls.TEXT_MODE);
        }
    }

    @Override
    public /*synchronized*/ void receive(long senderId, Object message) {
        ChatMessage chatMessage = (ChatMessage) message;
        rows.addLast(new Row(senderId, chatMessage));
        if (canRedraw && (/* chatMessage.receiverId > 0 || */ readLineBuffer().length() == 0)) {
            redraw(true);
            if (beep && senderId != this.clientId) {
                write(7);
            }
        }
    }

    public static byte[] noattr = "\033[0m".getBytes(ISO_8859_1);
    public Map<String, byte[]> logos = ImmutableMap.of(
        "minitel", readBinaryFile("minitel/bbs-chat-2.vdt"),
        "prestel", bytes("** BBS Chat 2.0 **\r\n\r\n"),
        "ascii", bytes("** BBS Chat 2.0 **\r\n\r\n"),
        "ansi", bytes(readBinaryFile("ansi/BbsChat20.ans"), noattr, "\033[5r\033[?6l\033[5;1H\033[?7h"),
        "utf8", bytes(readBinaryFile("ansi/BbsChat20.utf8ans"), noattr, "\033[5r\033[?6l\033[5;1H")
    );

    public Map<String, byte[]> exitSeq = ImmutableMap.of(
        "minitel", bytes(),
        "prestel", bytes(),
        "ascii", bytes(),
        "ansi", bytes("\0337\033[r\0338\033[?7l"),
        "utf8", bytes("\0337\033[r\0338")
    );

    private String shortenUrl(String firstUrl) throws IOException, ParseException {
        final String urlShorter = "https://sblendorio.eu/shorten.php?url=" +
                URLEncoder.encode(firstUrl, UTF_8.toString());
        URL shortService = new URL(urlShorter);
        URLConnection conn = shortService.openConnection();
        InputStream inputStream = conn.getInputStream();
        String result = new BufferedReader(
                new InputStreamReader(inputStream, UTF_8)).lines().collect(Collectors.joining("\n"));
        inputStream.close();

        return "https://sblendorio.eu/"+result;
    }

    private String shortenUrlCuttly(String firstUrl) throws IOException, ParseException {
        String token = defaultString(getProperty("cutt_key", getenv("cutt_key")), "DUMMY");
        URL shortService = new URL("https://cutt.ly/api/api.php?key=" + token + "&short=" +
                URLEncoder.encode(firstUrl, UTF_8.toString()));
        URLConnection conn = shortService.openConnection();
        InputStream inputStream = conn.getInputStream();

        String result = new BufferedReader(
                new InputStreamReader(inputStream, UTF_8)).lines().collect(Collectors.joining("\n"));
        inputStream.close();
        JSONObject jtext = (JSONObject) new JSONParser().parse(result);
        int status = NumberUtils.toInt(((JSONObject) jtext.get("url")).get("status").toString());
        if (status == 1 ||
                status == 4 ||
                status == 6)
            return firstUrl;
        return ((JSONObject) jtext.get("url")).get("shortLink").toString();
    }

    private String[] stringToQr(String string) throws WriterException {
        ByteMatrix matrix = Encoder.encode(string, ErrorCorrectionLevel.H).getMatrix();
        String[] strMatrix = new String[matrix.getHeight()];
        for (int y=0; y < matrix.getHeight(); ++y) {
            strMatrix[y] = "";
            for (int x = 0; x < matrix.getWidth(); ++x) {
                strMatrix[y] += (matrix.get(x, y) == 1 ? "*" : ".");
            }
        }
        return strMatrix;
    }

}
