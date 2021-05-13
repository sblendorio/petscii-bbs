package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiColors;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.Utils;
import static eu.sblendorio.bbs.core.Utils.bytes;
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
public class Chat64 extends PetsciiThread {

    private static final String CUSTOM_KEY = "CHAT";
    private static final int INPUT_COLOR = GREY3;
    private static final byte[] LOGO_CHAT = bytes(readBinaryFile("petscii/bbschat20.seq"));

    public static class ChatMessage {
        public final long receiverId;
        public final String text;

        public ChatMessage(long receiverId, String text) {
            this.receiverId = receiverId;
            this.text = text;
        }

        @Override
        public String toString() {
            return "ChatMessage{" + "receiverId=" + receiverId + ", text='" + text + '\'' + '}';
        }
    }

    public static class Row {
        public final long recipientId;
        public final ChatMessage message;

        public Row(long recipientId, ChatMessage message) {
            this.recipientId = recipientId;
            this.message = message;
        }
    }

    private Long recipient = null;
    private boolean canRedraw = false;

    private ConcurrentLinkedDeque<Row> rows = new ConcurrentLinkedDeque<>();

    public Chat64() {
    }

    @Override
    public void doLoop() throws Exception {
        try {
            canRedraw = false;
            write(PetsciiColors.GREY3, PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME);
            int status;
            String previousName = (String) getRoot().getCustomObject(CUSTOM_KEY);

            boolean invalid = previousName == null ||
                clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(previousName));
            status = invalid ? -1 : changeClientName(previousName);

            while (status != 0) {
                print("Enter your name: ");
                flush(); resetInput();
                final String candidateName = readLine();
                if (isBlank(candidateName) || ".".equalsIgnoreCase(candidateName)) {
                    return;
                }
                final String name = candidateName.replace(" ", "");
                boolean alreadyPresent =
                    clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(name));
                status = alreadyPresent ? -1 : changeClientName(name);
                if (status != 0) {
                    println("Error: name already used.");
                }
            };

            getRoot().setCustomObject(CUSTOM_KEY, getClientName());
            cls();
            // write(PetsciiColors.YELLOW);
            // println("              BBS Chat 2.0");
            // newline();
            write(LOGO_CHAT);
            showUsers(false);
            newline();
            displayHelp();

            notifyEnteringUser();
            String rawCommand = null;
            redraw();
            do {
                write(INPUT_COLOR);
                resetInput();
                rawCommand = readLine();
                rawCommand = defaultString(rawCommand).trim();
                final String command =  rawCommand;
                if (isBlank(command)) {
                    write(145);
                    redraw();
                } else if (command.matches("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$")) {
                    String text = defaultString(command.replaceAll("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$", "$1")).trim();
                    final String recipientName = command.replaceAll("(?is)^/to ([\\.a-zA-Z0-9-]+)(\\s+.*)?$", "$1");
                    Long candidateRecipient = getClientIdByName(recipientName, String::compareToIgnoreCase);
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
                    final String newName = command.replaceAll("\\s+", " ").substring(6);
                    boolean alreadyPresent =
                        clients.values().stream().map(BbsThread::getClientName).anyMatch(x -> x.equalsIgnoreCase(newName));
                    int res = alreadyPresent ? -1 : changeClientName(newName);
                    if (res != 0) {
                        println("Error: name already used.");
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
                } else if (".".equals(command) || "/q".equalsIgnoreCase(command) || "/quit".equalsIgnoreCase(command)) {
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
            } while (!".".equals(rawCommand) && !"/q".equalsIgnoreCase(rawCommand) && !"/quit".equalsIgnoreCase(rawCommand));
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
        write(PetsciiColors.BLUE);
        println("Commands");

        write(PetsciiColors.LIGHT_BLUE);
        print("/users");
        write(PetsciiColors.GREY2);
        print(" or ");
        write(PetsciiColors.LIGHT_BLUE);
        print("/u");
        write(PetsciiColors.GREY2);
        println("      to list users");

        write(PetsciiColors.LIGHT_BLUE);
        print("/to <person>");
        write(PetsciiColors.GREY2);
        println("      to talk with someone");

        write(PetsciiColors.LIGHT_BLUE);
        print("/all");
        write(PetsciiColors.GREY2);
        println("              to talk with all");

        write(PetsciiColors.LIGHT_BLUE);
        print("/nick <name>");
        write(PetsciiColors.GREY2);
        println("      to change nick");

        write(PetsciiColors.LIGHT_BLUE);
        print("/help");
        write(PetsciiColors.GREY2);
        print(" or ");
        write(PetsciiColors.LIGHT_BLUE);
        print("/h");
        write(PetsciiColors.GREY2);
        println("       to get this help");

        write(PetsciiColors.LIGHT_BLUE);
        print("/quit");
        write(PetsciiColors.GREY2);
        print(" or ");
        write(PetsciiColors.LIGHT_BLUE);
        print("/q");
        write(PetsciiColors.GREY2);
        print(" or ");
        write(PetsciiColors.LIGHT_BLUE);
        print(".");
        write(PetsciiColors.GREY2);
        println("  to exit chat");

        println();
    }

    private synchronized void redraw() {
        canRedraw = false;
        displayMessages();
        write(PetsciiColors.GREY1);

        if (recipient != null
            && getClients().get(recipient) != null
            && getClients().get(recipient).getClientName().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            recipient = null;
            println();
        }

        if (recipient != null) {
             ofNullable(getClients().get(recipient)).ifPresent(client -> print("[to "+client.getClientName()));
        }

        write(PetsciiColors.GREY1);
        print("]");
        canRedraw = true;
    }

    private void showUsers(boolean showIfNoUserConnected) throws IOException {
        write(GREY3);
        List<BbsThread> users = getConnectedUsers();
        if (isNotEmpty(users)) {
            println("Connected users:");
            int i = 0;
            for (BbsThread u : users) {
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
            write(PetsciiColors.RED);
            println("NO OTHER USER CONNECTED.");
            newline();
        }
        write(GREY3); print("Your username: ");
        write(WHITE); println(defaultString(getClientName()));
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

        write(13, 145);
        println(StringUtils.repeat(' ', 39));
        write(145);

        write(PetsciiColors.GREY2);
        write(PetsciiColors.GREY3);
        Row row;
        while ((row = rows.poll()) != null) {
            if (row.message.receiverId == -1) {
                write(PetsciiColors.GREEN);
                println(row.message.text);
            } else if (row.message.receiverId == -2) {
                write(PetsciiColors.RED);
                println(row.message.text);
            } else if (row.message.receiverId == -3) {
                int index = row.message.text.indexOf(">");
                write(LIGHT_BLUE);
                print(row.message.text.substring(0, index+1));
                write(CYAN);
                println(row.message.text.substring(index+1));
            } else {
                String from = ofNullable(getClients().get(row.recipientId)).map(BbsThread::getClientName).orElse(null);
                String to = ofNullable(getClients().get(row.message.receiverId)).map(BbsThread::getClientName).orElse(null);
                String text = row.message.text;

                if (from == null || to == null)
                    continue;

                write(PetsciiColors.BROWN);
                print("<" + from + ">");
                write(PetsciiColors.YELLOW);
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
            write(INPUT_COLOR);
            if (senderId != this.clientId) {
                write(7);
            }
        }
    }
}
