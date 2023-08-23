package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.*;

import static eu.sblendorio.bbs.core.Utils.bytes;

import eu.sblendorio.bbs.tenants.petscii.Chat64.ChatMessage;
import eu.sblendorio.bbs.tenants.petscii.Chat64.Row;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.List;
import java.util.Map;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;

@Hidden
public class ChatA1 extends AsciiThread {

    private static final String CUSTOM_KEY = "CHAT";
    private boolean canRedraw = false;
    private String termType;
    private BbsInputOutput interfaceType;

    private ConcurrentLinkedDeque<Row> rows = new ConcurrentLinkedDeque<>();

    public ChatA1() {
        this(null, "ascii");
    }

    public ChatA1(String termType) {
        this(null, termType);
    }

    public ChatA1(BbsInputOutput interfaceType, String termType) {
        super();
        this.interfaceType = interfaceType;
        this.termType = termType;
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
                final String name = defaultString(lowerCase(candidateName)).replace(" ", "");
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
            redraw(false);
            do {
                resetInput();
                rawCommand = readLine();
                rawCommand = defaultString(rawCommand).trim();
                rawCommand = lowerCase(rawCommand);
                final String command =  rawCommand;
                if (isBlank(command)) {
                    redraw(false);
                } else if (command.matches("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$")) {
                    String text = defaultString(command.replaceAll("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$", "$1")).trim();
                    final String recipientName = command.replaceAll("(?is)^/to ([\\.a-zA-Z0-9-]+)(\\s+.*)?$", "$1");
                    Long recipient = getClientIdByName(recipientName, String::compareToIgnoreCase);
                    if (recipientName.matches("^client[0-9]+$")) recipient = null;
                    if (recipientName.matches("^.*[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) recipient = null;

                    if (recipient != null && recipient != getClientId()) {
                        if (isNotBlank(text)) {
                            send(recipient, new ChatMessage(recipient, text));
                            redraw(false);
                        }
                    }
                    if (isBlank(text))
                        redraw(false);
                } else if (command.matches("(?is)/nick [\\.a-zA-Z0-9-]+")) {
                    String candidateName = command.replaceAll("\\s+", " ").substring(6);
                    final String newName = lowerCase(candidateName);
                    boolean alreadyPresent =
                        clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(newName));
                    int res = alreadyPresent ? -1 : changeClientName(newName);
                    if (res != 0) {
                        println("Error: name already used..");
                    }
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

    private synchronized void sendToAll(ChatMessage chatMessage) {
        getClients().keySet().stream()
                .filter(id -> getClients().get(id) != null)
                .filter(id -> id != getClientId()
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
            } else if (row.message.receiverId == -3) {
                int index = row.message.text.indexOf(">");
                print(row.message.text.substring(0, index+1));
                println(row.message.text.substring(index+1));
            } else {
                String from = ofNullable(getClients().get(row.recipientId)).map(BbsThread::getClientName).orElse(null);
                String to = ofNullable(getClients().get(row.message.receiverId)).map(BbsThread::getClientName).orElse(null);
                String text = row.message.text;

                if (from == null || to == null)
                    continue;

                print("<" + from + ">");
                println(text);
            }
            isFirstRow = false;
        }
    }

    @Override
    public synchronized void receive(long senderId, Object message) {
        ChatMessage chatMessage = (ChatMessage) message;
        rows.addLast(new Row(senderId, chatMessage));
        if (canRedraw && (/* chatMessage.receiverId > 0 || */ readLineBuffer().length() == 0)) {
            redraw(true);
            if (senderId != this.clientId) {
                write(7);
            }
        }
    }

    public static byte[] noattr = "\033[0m".getBytes(ISO_8859_1);
    public Map<String, byte[]> logos = ImmutableMap.of(
        "minitel", bytes("** BBS Chat 2.0 **\r\n\r\n"),
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
}
