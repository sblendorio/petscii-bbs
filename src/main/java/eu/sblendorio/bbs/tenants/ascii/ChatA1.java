package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.petscii.Chat64;
import eu.sblendorio.bbs.tenants.petscii.Chat64.ChatMessage;
import eu.sblendorio.bbs.tenants.petscii.Chat64.Row;
import java.io.IOException;
import java.util.List;
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

@Hidden
public class ChatA1 extends AsciiThread {

    private boolean canRedraw = false;

    private ConcurrentLinkedDeque<Row> rows = new ConcurrentLinkedDeque<>();

    public ChatA1() {
        setLocalEcho(false);
        this.keepAlive = false; // CICCIO CICCIO TO REMOVE
    }

    @Override
    public void doLoop() throws Exception {
        try {
            canRedraw = false;
            int status;
            do {
                newline();
                newline();
                print("Enter your name: ");
                flush(); resetInput();
                String name = readLine();
                if (isBlank(name) || ".".equalsIgnoreCase(name)) {
                    return;
                }
                status = changeClientName(name);
                if (status != 0) {
                    println("Error: name already used.");
                }
            } while (status != 0);

            cls();
            println("              BBS Chat 2.0");
            newline();
            showUsers(false);
            newline();
            displayHelp();

            notifyEnteringUser();
            String rawCommand = null;
            redraw();
            do {
                resetInput();
                rawCommand = readLine();
                rawCommand = defaultString(rawCommand).trim();
                final String command =  rawCommand;
                if (isBlank(command)) {
                    redraw();
                } else if (command.matches("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$")) {
                    String text = defaultString(command.replaceAll("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$", "$1")).trim();
                    Long recipient =
                            getClientIdByName(command.replaceAll("(?is)^/to ([\\.a-zA-Z0-9-]+)(\\s+.*)?$", "$1"));
                    if (recipient != null && recipient != getClientId()) {
                        if (isNotBlank(text)) {
                            send(recipient, new ChatMessage(recipient, text));
                            redraw();
                        }
                    }
                    if (isBlank(text))
                        redraw();
                } else if (command.matches("(?is)/nick [\\.a-zA-Z0-9-]+")) {
                    String newName = command.replaceAll("\\s+", " ").substring(6);
                    int res = changeClientName(newName);
                    if (res != 0) {
                        println("Error: name already used. Press any key.");
                        readKey();
                    }
                    redraw();
                } else if (command.equalsIgnoreCase("/users") ||
                    command.equalsIgnoreCase("/user")  ||
                    command.equalsIgnoreCase("/u")) {
                    canRedraw = false;
                    showUsers(true);
                    redraw();
                } else if (command.equalsIgnoreCase("/help") ||
                    command.equalsIgnoreCase("/?") ||
                    command.equalsIgnoreCase("/h")) {
                    canRedraw = false;
                    displayHelp();
                    redraw();
                } else if (".".equals(command)) {
                    log("Exiting chat.");
                } else if (StringUtils.isNotBlank(command)) {
                    sendToAll(new ChatMessage(-3, "<"+getClientName()+"@all>"+ command));
                    redraw();
                } else {
                    redraw();
                }
            } while (!".".equals(rawCommand));
        } finally {
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
         && !getClientName().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))
            sendToAll(new ChatMessage(-2, getClientName() + " just left"));
    }

    private synchronized void displayHelp() {
        println("Commands");
        println("/users or /u     to list users");
        println("/to <user> msg   to talk with someone");
        println("/nick <name>     to change nick");
        println("/help or /h      to get this help");
        println(".                to exit chat");
        println();
    }

    private synchronized void redraw() {
        canRedraw = false;
        displayMessages();
        print(":");
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
                          && !x.getClientName().matches("(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
                .collect(Collectors.toList());
    }

    private void displayMessages() {
        if (isEmpty(rows))
            return;

        Row row;
        while ((row = rows.poll()) != null) {
            if (row.message.receiverId == -1) {
                println(row.message.text);
            } else if (row.message.receiverId == -2) {
                println(row.message.text);
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
        }
    }

    @Override
    public synchronized void receive(long senderId, Object message) {
        ChatMessage chatMessage = (ChatMessage) message;
        rows.addLast(new Row(senderId, chatMessage));
        if (canRedraw && (/* chatMessage.receiverId > 0 || */ readLineBuffer().length() == 0)) {
            redraw();
            if (senderId != this.clientId) {
                write(7);
            }
        }
    }
}
