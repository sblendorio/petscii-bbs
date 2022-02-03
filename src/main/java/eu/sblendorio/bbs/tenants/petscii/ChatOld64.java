package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiColors;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY2;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import java.io.IOException;
import java.util.Deque;
import java.util.List;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Hidden
public class ChatOld64 extends PetsciiThread {

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
    private boolean canRedraw = false;

    private Deque<Row> rows = new ConcurrentLinkedDeque<>();

    @Override
    public void doLoop() throws Exception {
        try {
            canRedraw = false;
            write(PetsciiColors.GREY3, PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME);
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

            notifyEnteringUser();
            String rawCommand = null;
            redraw();
            do {
                write(INPUT_COLOR);
                rawCommand = readLine();
                rawCommand = defaultString(rawCommand).trim();
                final String command =  rawCommand;
                if (StringUtils.isBlank(command)) {
                    redraw();
                } else if (command.matches("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$")) {
                    String text = defaultString(command.replaceAll("(?is)^/to [\\.a-zA-Z0-9-]+(\\s+.*)?$", "$1")).trim();
                    Long candidateRecipient =
                            getClientIdByName(command.replaceAll("(?is)^/to ([\\.a-zA-Z0-9-]+)(\\s+.*)?$", "$1"));
                    if (candidateRecipient != null && candidateRecipient != getClientId()) {
                        recipient = candidateRecipient;
                        if (isNotBlank(text)) {
                            send(recipient, new ChatMessage(recipient, text));
                            send(getClientId(), new ChatMessage(recipient, text));
                        }
                    }
                    if (isBlank(text))
                        redraw();
                } else if (command.matches("(?is)^/all(\\s+.*)?$")) {
                    recipient = null;
                    String text = defaultString(command.replaceAll("(?is)^/all(\\s+.*)?$", "$1")).trim();
                    if (isNotBlank(text)) {
                        send(getClientId(), new ChatMessage(-4, text));
                        sendToAll(new ChatMessage(-3, "<"+getClientName()+"@all>" + text));
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
                    showUsers();
                    redraw();
                } else if (".".equals(command)) {
                    log("Exiting chat.");
                } else if (recipient != null) {
                    send(recipient, new ChatMessage(recipient, command));
                    send(getClientId(), new ChatMessage(recipient, command));
                } else if (StringUtils.isNotBlank(command)) {
                    send(getClientId(), new ChatMessage(-4, command));
                    sendToAll(new ChatMessage(-3, "<"+getClientName()+"@all>"+ command));
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

    private synchronized void redraw() {
        canRedraw = false;
        write(PetsciiKeys.CLR, PetsciiColors.YELLOW);
        print("              BBS Chat 1.2");
        write(PetsciiColors.WHITE);
        println(StringUtils.repeat(' ', 13 - defaultString(getClientName()).length()) + getClientName());
        write(PetsciiColors.BLUE);
        println("Commands");

        write(PetsciiColors.LIGHT_BLUE);
        print("/users");
        write(PetsciiColors.GREY2);
        print(" or ");
        write(PetsciiColors.LIGHT_BLUE);
        print("/u");
        write(PetsciiColors.GREY2);
        println("   to list users");

        write(PetsciiColors.LIGHT_BLUE);
        print("/to <person>");
        write(PetsciiColors.GREY2);
        println("   to talk with someone");

        write(PetsciiColors.LIGHT_BLUE);
        print("/all");
        write(PetsciiColors.GREY2);
        println("           to talk with all");

        write(PetsciiColors.LIGHT_BLUE);
        print("/nick <name>");
        write(PetsciiColors.GREY2);
        println("   to change nick");

        write(PetsciiColors.WHITE);
        print(".           ");
        write(PetsciiColors.GREY2);
        println("   to exit chat");

        write(PetsciiColors.WHITE);
        print("<RETURN>");
        write(PetsciiColors.GREY2);
        println("       to refresh screen");

        println();
        displayMessages();

        write(PetsciiColors.GREY1);
        if (recipient != null) {
             ofNullable(getClients().get(recipient)).ifPresent(client -> print("[to "+client.getClientName()));
        }

        write(PetsciiColors.GREY1);
        print("]");
        canRedraw = true;
    }

    private void showUsers() throws IOException {
        canRedraw = false;
        cls();
        write(GREY3);
        List<BbsThread> users = getConnectedUsers();
        int i = 0;
        for (BbsThread u: users) {
            ++i;
            write(CYAN);
            println(u.getClientName());
            write(GREY3);
            if (i % 22 == 0 && i < users.size()) {
                newline();
                write(WHITE); print("ANY KEY FOR NEXT PAGE, '.' TO GO BACK "); write(GREY3);
                flush(); resetInput(); int ch = readKey(); resetInput();
                if (ch == '.') return;
                cls();
                write(GREY3);
            }
        }
        if (isEmpty(users)) {
            write(PetsciiColors.RED);
            println("NO USER CONNECTED.");
        }
        newline();
        write(WHITE); print("PRESS ANY KEY TO GO BACK "); write(GREY3);
        flush(); resetInput(); readKey(); resetInput();
        canRedraw = true;
    }

    private List<BbsThread> getConnectedUsers() {
        return getClients().values()
                .stream()
                .filter(x -> x.getClientClass().equals(this.getClientClass()) &&
                             x.getClientId() != this.getClientId())
                .collect(Collectors.toList());
    }

    private void displayMessages() {
        if (isEmpty(rows))
            return;

        write(PetsciiColors.GREY2);
        write(PetsciiColors.GREY3);
        rows.forEach(row ->
            {
                if (row.message.receiverId == -1) {
                    write(PetsciiColors.GREEN);
                    println(row.message.text);
                    return;
                }

                if (row.message.receiverId == -2) {
                    write(PetsciiColors.RED);
                    println(row.message.text);
                    return;
                }

                if (row.message.receiverId == -3) {
                    int index = row.message.text.indexOf(">");
                    write(LIGHT_BLUE);
                    print(row.message.text.substring(0, index+1));
                    write(CYAN);
                    println(row.message.text.substring(index+1));
                    return;
                }

                if (row.message.receiverId == -4) {
                    write(GREY2);
                    print("<you@all>");
                    write(WHITE);
                    println(row.message.text);
                    return;
                }

                String from = ofNullable(getClients().get(row.recipientId)).map(BbsThread::getClientName).orElse(null);
                String to = ofNullable(getClients().get(row.message.receiverId)).map(BbsThread::getClientName).orElse(null);
                String text = row.message.text;

                if (from == null || to == null)
                    return;

                if (getClientName().equals(from)) {
                    write(PetsciiColors.GREY1);
                    print("<to " + to + ">");
                    write(PetsciiColors.GREY3);
                    println(text);
                } else {
                    write(PetsciiColors.BROWN);
                    print("<" + from + ">");
                    write(PetsciiColors.YELLOW);
                    println(text);
                }
            }
        );
    }

    @Override
    public synchronized void receive(long senderId, Object message) {
        ChatMessage chatMessage = (ChatMessage) message;
        rows.addLast(new Row(senderId, chatMessage));
        while (rows.size() > 10) rows.removeFirst();
        if (canRedraw && (/* chatMessage.receiverId > 0 || */ readLineBuffer().length() == 0)) {
            redraw();
            write(INPUT_COLOR);
            print(readLineBuffer());
            if (senderId != this.clientId) {
                write(7);
            }
        }
    }
}
