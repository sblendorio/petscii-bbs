package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.PetsciiThread;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class IrcClient extends AsciiThread {
    private String _server;
    private int _port;
    private String _nickname;
    private boolean _useSSL;
    private Socket _socket;
    private PrintWriter _out;
    private BufferedReader _in;

    @Override
    public byte[] initializingBytes() {
        return "\377\375\042\377\373\001".getBytes(ISO_8859_1);
    }

    @Override
    public void doLoop() throws Exception {
        _server = "silver.libera.chat";
        _port = 6667;
        _nickname = "john77";
        _useSSL = false;

        try {
            this.connect();
            this.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IrcClient() {
        super();
    }

    public IrcClient(String server, int port, String nickname, boolean useSSL) {
        this._server = server;
        this._port = port;
        this._nickname = nickname;
        this._useSSL = useSSL;
    }

    public void connect() throws IOException {
        if (_useSSL) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            _socket = sslsocketfactory.createSocket(_server, _port);
        } else {
            _socket = new Socket(_server, _port);
        }
        _out = new PrintWriter(_socket.getOutputStream(), true);
        _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));

        // Invia le informazioni iniziali
        _out.println("NICK " + _nickname);
        _out.println("USER " + _nickname + " 8 * : Java IRC Client");

        // Thread per leggere i messaggi dal server
        new Thread(() -> {
            String response;
            try {
                while ((response = _in.readLine()) != null) {
                    println(response);

                    // Rispondi ai PING con PONG
                    if (response.startsWith("PING ")) {
                        String pingResponse = "PONG " + response.substring(5);
                        _out.println(pingResponse);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void joinChannel(String channel) {
        _out.println("JOIN " + channel);
    }

    public void partChannel(String channel, String message) {
        if (message == null || message.isEmpty()) {
            _out.println("PART " + channel);
        } else {
            _out.println("PART " + channel + " :" + message);
        }
    }

    public void changeNick(String newNickname) {
        _out.println("NICK " + newNickname);
        this._nickname = newNickname;
    }

    public void quit(String message) {
        if (message == null || message.isEmpty()) {
            _out.println("QUIT");
        } else {
            _out.println("QUIT :" + message);
        }
    }

    public void changeTopic(String channel, String newTopic) {
        _out.println("TOPIC " + channel + " :" + newTopic);
    }

    public void showNames(String channel) {
        _out.println("NAMES " + channel);
    }

    public void listChannels(String mask, String minUsers) {
        if (mask == null && minUsers == null) {
            _out.println("LIST");
        } else if (mask != null && minUsers == null) {
            _out.println("LIST " + mask);
        } else if (mask == null) {
            _out.println("LIST >" + minUsers);
        } else {
            _out.println("LIST " + mask + " >" + minUsers);
        }
    }

    public void whois(String nickname) {
        _out.println("WHOIS " + nickname);
    }

    public void invite(String nickname, String channel) {
        _out.println("INVITE " + nickname + " " + channel);
    }

    public void kick(String channel, String nickname, String reason) {
        if (reason == null || reason.isEmpty()) {
            _out.println("KICK " + channel + " " + nickname);
        } else {
            _out.println("KICK " + channel + " " + nickname + " :" + reason);
        }
    }

    public void changeMode(String target, String modes, String params) {
        _out.println("MODE " + target + " " + modes + " " + params);
    }

    public void setAway(String message) {
        if (message == null || message.isEmpty()) {
            _out.println("AWAY");
        } else {
            _out.println("AWAY :" + message);
        }
    }

    public void who(String name) {
        _out.println("WHO " + name);
    }

    public void ping(String server) {
        _out.println("PING " + server);
    }

    public void sendMessage(String target, String message) {
        _out.println("PRIVMSG " + target + " :" + message);
    }

    public void execute() throws IOException {
        println("Connesso a " + _server + " sulla porta " + _port + " come " + _nickname + " (SSL: " + _useSSL + ")");

        while (true) {
            String userCommand = IrcClient.this.readLine();
            if (userCommand.startsWith("/join ")) {
                String channel = userCommand.split(" ")[1];
                joinChannel(channel);
            } else if (userCommand.startsWith("/msg ")) {
                String[] parts = userCommand.split(" ", 3);
                if (parts.length == 3) {
                    String target = parts[1];
                    String message = parts[2];
                    sendMessage(target, message);
                }
            } else if (userCommand.startsWith("/part ")) {
                String[] parts = userCommand.split(" ", 3);
                String channel = parts[1];
                String message = parts.length == 3 ? parts[2] : "";
                partChannel(channel, message);
            } else if (userCommand.startsWith("/nick ")) {
                String newNickname = userCommand.split(" ")[1];
                changeNick(newNickname);
            } else if (userCommand.startsWith("/quit")) {
                String[] parts = userCommand.split(" ", 2);
                String message = parts.length == 2 ? parts[1] : "";
                quit(message);
                break;
            } else if (userCommand.startsWith("/topic ")) {
                String[] parts = userCommand.split(" ", 3);
                if (parts.length == 3) {
                    String channel = parts[1];
                    String newTopic = parts[2];
                    changeTopic(channel, newTopic);
                }
            } else if (userCommand.startsWith("/names ")) {
                String channel = userCommand.split(" ")[1];
                showNames(channel);
            } else if (userCommand.startsWith("/list")) {
                String[] parts = userCommand.split(" ", 3);
                String mask = parts.length >= 2 ? parts[1] : null;
                String minUsers = parts.length == 3 ? parts[2] : null;
                listChannels(mask, minUsers);
            } else if (userCommand.startsWith("/whois ")) {
                String target = userCommand.split(" ")[1];
                whois(target);
            } else if (userCommand.startsWith("/invite ")) {
                String[] parts = userCommand.split(" ", 3);
                if (parts.length == 3) {
                    String nickname = parts[1];
                    String channel = parts[2];
                    invite(nickname, channel);
                }
            } else if (userCommand.startsWith("/kick ")) {
                String[] parts = userCommand.split(" ", 4);
                if (parts.length >= 3) {
                    String channel = parts[1];
                    String nickname = parts[2];
                    String reason = parts.length == 4 ? parts[3] : "";
                    kick(channel, nickname, reason);
                }
            } else if (userCommand.startsWith("/mode ")) {
                String[] parts = userCommand.split(" ", 4);
                if (parts.length >= 3) {
                    String target = parts[1];
                    String modes = parts[2];
                    String params = parts.length == 4 ? parts[3] : "";
                    changeMode(target, modes, params);
                }
            } else if (userCommand.startsWith("/away")) {
                String[] parts = userCommand.split(" ", 2);
                String message = parts.length == 2 ? parts[1] : "";
                setAway(message);
            } else if (userCommand.startsWith("/who ")) {
                String name = userCommand.split(" ")[1];
                who(name);
            } else if (userCommand.startsWith("/ping ")) {
                String server = userCommand.split(" ")[1];
                ping(server);
            } else {
                println("Comando non riconosciuto.");
            }
        }

        _socket.close();
    }

    public static void main(String[] args) {
        if (args.length < 3 || args.length > 4) {
            System.out.println("Utilizzo: java IRCClient <server> <port> <nickname> [ssl]");
            return;
        }

        String server = args[0];
        int port = Integer.parseInt(args[1]);
        String nickname = args[2];
        boolean useSSL = args.length == 4 && args[3].equalsIgnoreCase("ssl");

        IrcClient client = new IrcClient(server, port, nickname, useSSL);
        try {
            client.connect();
            client.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
