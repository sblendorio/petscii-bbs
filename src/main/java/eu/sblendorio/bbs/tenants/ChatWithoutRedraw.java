package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.Colors;
import static eu.sblendorio.bbs.core.Colors.CYAN;
import static eu.sblendorio.bbs.core.Colors.GREY2;
import static eu.sblendorio.bbs.core.Colors.GREY3;
import static eu.sblendorio.bbs.core.Colors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.Colors.WHITE;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.Utils;
import java.io.IOException;
import java.util.List;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ChatWithoutRedraw extends PetsciiThread {

    private static final int INPUT_COLOR = GREY3;

    public static class ChatMessage {
        final long receiverId;
        final String text;

        ChatMessage(long receiverId, String text) {
            this.receiverId = receiverId;
            this.text = text;
        }

        @Override
        public String toString() {
            return "ChatMessage{" + "receiverId=" + receiverId + ", text='" + text + '\'' + '}';
        }
    }
    public static class Row {
        final long recipientId;
        final ChatMessage message;

        Row(long recipientId, ChatMessage message) {
            this.recipientId = recipientId;
            this.message = message;
        }
    }

    private Long recipient = null;
    private String commandLine = EMPTY;
    private boolean canRedraw = false;

    private ConcurrentLinkedDeque<Row> rows = new ConcurrentLinkedDeque<Row>();

    public ChatWithoutRedraw() {
        this.keepAlive = true;
    }

    @Override
    public void doLoop() throws Exception {
        try {
            canRedraw = false;
            write(Colors.GREY3, Keys.CLR, Keys.LOWERCASE, Keys.CASE_LOCK, Keys.HOME);
            int status;
            do {
                print("Enter your name: ");
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
            write(Colors.YELLOW);
            println("              BBS Chat 2.0");
            newline();
            showUsers(false);
            newline();
            displayHelp();

            notifyEnteringUser();
            String rawCommand = null;
            redraw();
            do {
                write(INPUT_COLOR);
                rawCommand = readCommandLine();
                rawCommand = defaultString(rawCommand).trim();
                final String command =  rawCommand;
                if (isBlank(command)) {
                    write(145);
                    redraw();
                } else if (command.matches("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$")) {
                    String text = defaultString(command.replaceAll("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$", "$1")).trim();
                    Long candidateRecipient =
                            getClientIdByName(command.replaceAll("(?is)^/to ([\\.a-zA-Z0-9-]+)(\\s+.*)?$", "$1"));
                    if (candidateRecipient != null && candidateRecipient != getClientId()) {
                        recipient = candidateRecipient;
                        if (isNotBlank(text)) {
                            send(recipient, new ChatMessage(recipient, text));
                            redraw();
                        }
                    }
                    if (isBlank(text))
                        redraw();
                } else if (command.matches("(?is)^/all(\\s+.*)?$")) {
                    recipient = null;
                    String text = defaultString(command.replaceAll("(?is)^/all(\\s+.*)?$", "$1")).trim();
                    if (isNotBlank(text)) {
                        sendToAll(new ChatMessage(-3, "<"+getClientName()+"@all>" + text));
                        redraw();
                    } else {
                        redraw();
                    }
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
                    command.equalsIgnoreCase("/h")) {
                    canRedraw = false;
                    displayHelp();
                    redraw();
                } else if (".".equals(command)) {
                    log("Exiting chat.");
                } else if (recipient != null) {
                    send(recipient, new ChatMessage(recipient, command));
                    redraw();
                    //send(getClientId(), new ChatMessage(recipient, command));
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
                .filter(id -> id != getClientId() && getClientClass().equals(getClients().get(id).getClientClass()))
                .forEach(id -> send(id, chatMessage));
    }

    private void notifyEnteringUser() {
        sendToAll(new ChatMessage(-1, getClientName() + " has entered"));
    }

    private void notifyExitingUser() {
        if (!getClientName().matches("^client[0-9]+$"))
            sendToAll(new ChatMessage(-2, getClientName() + " just left"));
    }

    private String readCommandLine() throws IOException {
        int ch;
        commandLine = EMPTY;
        do {
            ch = readKey();
            if (ch == Keys.DEL || ch == Keys.INS) {
                if (commandLine.length() > 0) {
                    write(Keys.DEL);
                    commandLine = commandLine.substring(0, commandLine.length()-1);
                }
            } else if (ch == 34) {
                write(34, 34, Keys.DEL);
                commandLine += "\"";
            } else if (ch == Keys.RETURN || ch == 141) {
                write(Keys.RETURN);
            } else if (Utils.isPrintableChar(ch)) {
                write(ch);
                if (ch >= 'a' && ch <= 'z')
                    ch = Character.toUpperCase(ch);
                else if (ch >= 'A' && ch <= 'Z')
                    ch = Character.toLowerCase(ch);
                else if (ch >= 193 && ch <= 218)
                    ch -= 128;
                commandLine += (char) ch;
            }
        } while (ch != Keys.RETURN && ch != 141);
        final String result = commandLine;
        commandLine = EMPTY;
        return result;
    }

    private synchronized void displayHelp() {
        write(Colors.BLUE);
        println("Commands");

        write(Colors.LIGHT_BLUE);
        print("/users");
        write(Colors.GREY2);
        print(" or ");
        write(Colors.LIGHT_BLUE);
        print("/u");
        write(Colors.GREY2);
        println("   to list users");

        write(Colors.LIGHT_BLUE);
        print("/to <person>");
        write(Colors.GREY2);
        println("   to talk with someone");

        write(Colors.LIGHT_BLUE);
        print("/all");
        write(Colors.GREY2);
        println("           to talk with all");

        write(Colors.LIGHT_BLUE);
        print("/nick <name>");
        write(Colors.GREY2);
        println("   to change nick");

        write(Colors.LIGHT_BLUE);
        print("/help");
        write(Colors.GREY2);
        print(" or ");
        write(Colors.LIGHT_BLUE);
        print("/h");
        write(Colors.GREY2);
        println("    to get this help");

        write(Colors.WHITE);
        print(".           ");
        write(Colors.GREY2);
        println("   to exit chat");

        println();
    }

    private synchronized void redraw() {
        canRedraw = false;
        displayMessages();

        write(Colors.GREY1);
        if (recipient != null) {
             ofNullable(getClients().get(recipient)).ifPresent(client -> print("[to "+client.getClientName()));
        }

        write(Colors.GREY1);
        print("]");
        canRedraw = true;
    }

    private void showUsers(boolean showIfNoUserConnected) throws IOException {
        write(GREY3);
        List<PetsciiThread> users = getConnectedUsers();
        if (isNotEmpty(users)) {
            println("Connected users:");
            int i = 0;
            for (PetsciiThread u : users) {
                ++i;
                write(CYAN);
                println(u.getClientName());
                write(GREY3);
                if (i % 22 == 0 && i < users.size()) {
                    newline();
                    write(WHITE);
                    print("ANY KEY FOR NEXT PAGE, '.' TO GO BACK ");
                    write(GREY3);
                    flush();
                    resetInput();
                    int ch = readKey();
                    resetInput();
                    if (ch == '.') return;
                    write(GREY3);
                }
            }
            newline();
        } else if (showIfNoUserConnected) {
            write(Colors.RED);
            println("NO OTHER USER CONNECTED.");
            newline();
        }
        write(GREY3); print("Your username: ");
        write(WHITE); println(defaultString(getClientName()));
    }

    private List<PetsciiThread> getConnectedUsers() {
        return getClients().values()
                .stream()
                .filter(x -> x.getClientClass().equals(this.getClientClass()) &&
                             x.getClientId() != this.getClientId())
                .collect(Collectors.toList());
    }

    private void displayMessages() {
        if (isEmpty(rows))
            return;

        write(13, 145);
        println(StringUtils.repeat(' ', 39));
        write(145);

        write(Colors.GREY2);
        write(Colors.GREY3);
        Row row;
        while ((row = rows.poll()) != null) {
            if (row.message.receiverId == -1) {
                write(Colors.GREEN);
                println(row.message.text);
            } else if (row.message.receiverId == -2) {
                write(Colors.RED);
                println(row.message.text);
            } else if (row.message.receiverId == -3) {
                int index = row.message.text.indexOf(">");
                write(LIGHT_BLUE);
                print(row.message.text.substring(0, index+1));
                write(CYAN);
                println(row.message.text.substring(index+1));
            } else {
                String from = ofNullable(getClients().get(row.recipientId)).map(PetsciiThread::getClientName).orElse(null);
                String to = ofNullable(getClients().get(row.message.receiverId)).map(PetsciiThread::getClientName).orElse(null);
                String text = row.message.text;

                if (from == null || to == null)
                    continue;

                write(Colors.BROWN);
                print("<" + from + ">");
                write(Colors.YELLOW);
                println(text);
            }
        }
    }

    @Override
    public synchronized void receive(long senderId, Object message) {
        ChatMessage chatMessage = (ChatMessage) message;
        rows.addLast(new Row(senderId, chatMessage));
        if (canRedraw && (/* chatMessage.receiverId > 0 || */ commandLine.length() == 0)) {
            redraw();
            write(INPUT_COLOR);
            print(commandLine);
            if (senderId != this.clientId) {
                write(7);
            }
        }
    }
}
