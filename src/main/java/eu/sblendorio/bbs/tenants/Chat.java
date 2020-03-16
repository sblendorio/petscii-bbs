package eu.sblendorio.bbs.tenants;

import static eu.sblendorio.bbs.core.Colors.CYAN;
import static eu.sblendorio.bbs.core.Colors.GREY3;
import static eu.sblendorio.bbs.core.Colors.WHITE;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import eu.sblendorio.bbs.core.CbmInputOutput;
import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.Utils;

public class Chat extends PetsciiThread {

    private static final int INPUT_COLOR = GREY3;

    public static class ChatMessage {
        final long receiverId;
        final String text;

        ChatMessage(long receiverId, String text) {
            this.receiverId = receiverId;
            this.text = text;
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
    private boolean canRedraw = true;

    private ConcurrentLinkedDeque<Row> rows = new ConcurrentLinkedDeque<Row>();

    @Override
    public void doLoop() throws Exception {
        try {
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

            String command = null;
            do {
                redraw();
                write(INPUT_COLOR);
                command = readCommandLine();
                command = defaultString(command).trim();
                if (StringUtils.isBlank(command)) {
                    continue;
                } else if (command.matches("(?is)/to [a-zA-Z0-9]+")) {
                    Long candidateRecipient = getClientIdByName(command.replaceAll("\\s+", " ").substring(4));
                    if (candidateRecipient != null && candidateRecipient != getClientId()) {
                        recipient = candidateRecipient;
                    }
                } else if (command.matches("(?is)/nick [a-zA-Z0-9]+")) {
                    String newName = command.replaceAll("\\s+", " ").substring(6);
                    int res = changeClientName(newName);
                    if (res != 0) {
                        println("Error: name already used. Press any key.");
                        readKey();
                    }
                } else if (command.equalsIgnoreCase("/users") ||
                           command.equalsIgnoreCase("/user")||
                           command.equalsIgnoreCase("/u")) {
                    showUsers();
                } else if (recipient != null) {
                    send(recipient, new ChatMessage(recipient, command));
                    send(getClientId(), new ChatMessage(recipient, command));
                }
            } while (!".".equals(command));
        } finally {
            changeClientName(UUID.randomUUID().toString());
        }
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
                commandLine += (char) ch;
            }
        } while (ch != Keys.RETURN && ch != 141);
        return commandLine;
    }

    private synchronized void redraw() {
        canRedraw = false;
        write(Keys.CLR, Colors.YELLOW);
        print("              BBS Chat 1.1");
        write(Colors.WHITE);
        println(StringUtils.repeat(' ', 13 - defaultString(getClientName()).length()) + getClientName());
        write(Colors.BLUE);
        println("Commands");
        write(Colors.LIGHT_BLUE);
        print("/users ");
        write(Colors.GREY2);
        println("        to list users");
        write(Colors.LIGHT_BLUE);
        print("/to <person>");
        write(Colors.GREY2);
        println("   to talk with someone");
        write(Colors.LIGHT_BLUE);
        print("/nick <name>");
        write(Colors.GREY2);
        println("   to change nick");
        write(Colors.WHITE);
        print(".           ");
        write(Colors.GREY2);
        println("   to exit chat");
        write(Colors.WHITE);
        print("<RETURN>");
        write(Colors.GREY2);
        println("       to refresh screen");
        println();
        displayMessages();

        write(Colors.GREY1);
        if (recipient != null) {
             ofNullable(getClients().get(recipient)).ifPresent(client -> print("[to "+client.getClientName()));
        }

        write(Colors.GREY1);
        print("]");
        canRedraw = true;
    }

    private void showUsers() throws IOException {
        canRedraw = false;
        cls();
        write(GREY3);
        List<PetsciiThread> users = getConnectedUsers();
        int i = 0;
        for (PetsciiThread u: users) {
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
            write(Colors.RED);
            println("NO USER CONNECTED.");
        }
        newline();
        write(WHITE); print("PRESS ANY KEY TO GO BACK "); write(GREY3);
        flush(); resetInput(); readKey(); resetInput();
        canRedraw = true;
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

        write(Colors.GREY2);
        write(Colors.GREY3);
        rows.forEach(row ->
            {
                String from = ofNullable(getClients().get(row.recipientId)).map(PetsciiThread::getClientName).orElse(null);
                String to = ofNullable(getClients().get(row.message.receiverId)).map(PetsciiThread::getClientName).orElse(null);
                String text = row.message.text;

                if (from == null || to == null)
                    return;

                if (getClientName().equals(from)) {
                    write(Colors.GREY1);
                    print("<to " + to + ">");
                    write(Colors.GREY3);
                    println(text);
                } else {
                    write(Colors.BROWN);
                    print("<" + from + ">");
                    write(Colors.YELLOW);
                    println(text);
                }
            }
        );
    }

    @Override
    public synchronized void receive(long senderId, Object message) {
        rows.addLast(new Row(senderId, (ChatMessage) message));
        while (rows.size() > 10) rows.removeFirst();
        if (canRedraw) {
            redraw();
            write(INPUT_COLOR);
            print(commandLine);
        }
    }
}
