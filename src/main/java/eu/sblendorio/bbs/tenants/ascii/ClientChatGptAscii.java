package eu.sblendorio.bbs.tenants.ascii;

import com.google.gson.Gson;
import eu.sblendorio.bbs.core.AsciiKeys;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidmoten.text.utils.WordWrap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import static eu.sblendorio.bbs.core.Utils.STR_ALPHANUMERIC;
import static eu.sblendorio.bbs.core.Utils.setOfChars;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class ClientChatGptAscii extends AsciiThread {
    private BbsInputOutput interfaceType = null;
    private byte[] mainLogo = null;
    public String model;
    public String apiUrl;
    public String keyName;
    public String aiName;
    private static final String EXIT_ADVICE = "Type \".\" to EXIT";

    private static final Logger logger = LogManager.getLogger(ClientChatGptAscii.class);
    private static final String WAIT_MESSAGE = "...";
    private static final byte[] USER_COLOR = new byte[] {};
    private static final byte[] ASSISTANT_COLOR = new byte[] {};
    private static final byte[] WAIT_COLOR = new byte[] {};
    private static final byte[] MORE_COLOR = new byte[] {};

    private final HttpClient client;

    public ClientChatGptAscii(String aiName, String apiUrl, String keyName, String model, BbsInputOutput interfaceType, byte[] logo) {
        super();
        this.interfaceType = interfaceType;
        this.mainLogo = logo;
        this.model = model;
        this.apiUrl = apiUrl;
        this.keyName = keyName;
        this.aiName = aiName;
        this.client = HttpClient.newBuilder()
                .connectTimeout(timeout())
                .build();
    }

    private String apiKey() {
        return Objects.toString(getProperty(keyName, getenv(keyName)), "DUMMY");
    }

    private Duration timeout() {
        long seconds = toLong(Objects.toString(getProperty("AI_TIMEOUT_SECS", getenv("AI_TIMEOUT_SECS")), "180"));
        return Duration.ofSeconds(seconds);
    }

    @Override
    public void doLoop() throws Exception {
        if (interfaceType != null) {
            this.setBbsInputOutput(interfaceType);
        }

        PatreonData patreonData = PatreonData.authenticateAscii(this);
        if (patreonData == null) return;

        // String model = toInt(patreonData.patreonLevel) > 0 ? "gpt-4" : "gpt-3.5-turbo";
        changeClientName(patreonData.user+"/"+UUID.randomUUID());

        cls();
        if (mainLogo == null) {
            println(aiName + " - Classic Client");
            println(StringUtils.repeat('-', 17 + Objects.toString(aiName, "").length()));
            if (toInt(patreonData.patreonLevel) > 0) println("Model: " + model);
            println();
            println(EXIT_ADVICE);
            println();
        } else {
            write(mainLogo);
        }
        List<Map<String, Object>> conversation = new LinkedList<>();
        String input;
        do {
            flush(); resetInput();
            write(USER_COLOR);
            print("You> ");
            input = readLine(setOfChars(STR_ALPHANUMERIC, ".:,;_ \"{}[]()<>@+-*/^='?!$%&#"));
            input = trimToEmpty(input);
            if (".".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) break;
            if (isBlank(input)) {
                println(EXIT_ADVICE);
                continue;
            }

            input = asciiToUtf8(input);
            conversation.add(Map.of(
                    "role", "user",
                    "content", input
            ));

            logger.info("model: '{}', IP: '{}', email: '{}', role: 'user', message: {}",
                    model,
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    input.replaceAll("\n", "\\\\n"));

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", conversation
            );
            Gson gson = new Gson();

            waitOn();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey())
                    .timeout(timeout())
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody), StandardCharsets.UTF_8))
                    .build();
            String responseBody = "";
            String assistantResponse = "";

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                responseBody = response.body();
                assistantResponse = parseAssistantResponse(responseBody);
            } catch (Exception e) {
                logger.error("model: '{}', IP: '{}', email: '{}', exception: '{}', responseBody: '{}'",
                        model,
                        ipAddress.getHostAddress(),
                        patreonData.user,
                        e,
                        responseBody);
                if (e.getMessage() != null && e.getMessage().contains("maximum context length")) {
                    e.printStackTrace();
                    cls();
                    println("This conversation exceeded max. token");
                    println("length.  But you can close this alert");
                    println("pressing a key and starting a new one");
                    println();
                    flush(); resetInput();
                    readKey();
                    break;

                } else {
                    e.printStackTrace();
                    cls();
                    println("Unexpected error. Please write to sysop");
                    println("Press any key to EXIT");
                    flush();
                    resetInput();
                    readKey();
                    break;
                }
            }
            waitOff();
            optionalCls();

            conversation.add(Map.of(
                    "role", "assistant",
                    "content", assistantResponse
            ));

            logger.info("model: '{}', IP: '{}', email: '{}', role: '{}', message: {}",
                    model,
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    "assistant",
                    assistantResponse.replaceAll("\n", "\\\\n"));

            final String answer = aiName + "> " + assistantResponse;
            println();
            optionalCls();
            printPagedText(answer);
        } while (true);
    }

    public String parseAssistantResponse(String jsonResponse) {
        Gson gson = new Gson();
        Map<String, Object> response = gson.fromJson(jsonResponse, Map.class);
        String content = ((Map)((List<Map>) response.get("choices")).get(0).get("message")).get("content").toString();
        return content;
    }

    private void printPagedText(String answerContent) throws IOException {
        final String formattedContent = formatContent(answerContent);
        final List<String> lines = wordWrap(formattedContent);
        lines.add(EMPTY);
        int count = 0;

        write(ASSISTANT_COLOR);
        for (String line: lines) {
            println(line);
            count++;
            if (count % (this.getScreenRows() - 1) == 0) {
                write(MORE_COLOR);
                print("-- More --");
                write(ASSISTANT_COLOR);
                flush(); resetInput();
                readKey();
                println();
                optionalCls();
            }
        }
    }

    private String formatContent(String input) {
        String result =  input
                .replaceAll("\r\n", "\n")
                .replaceAll("\r", "\n")
                .replaceAll("```", "---")
                .replaceAll("`", "")
                .replaceAll("\n", "\r\n")
                ;

        return htmlClean(result);
    }

    private String asciiToUtf8(String input) {
        return input
                .replaceAll("a'", "à")
                .replaceAll("A'", "À")
                .replaceAll("e'", "è")
                .replaceAll("E'", "È")
                .replaceAll("i'", "ì")
                .replaceAll("I'", "Ì")
                .replaceAll("o'", "ò")
                .replaceAll("O'", "Ò")
                .replaceAll("u'", "ù")
                .replaceAll("U'", "Ù")
                ;
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = s.split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordWrap
                    .from(item)
                    .includeExtraWordChars("0123456789()")
                    .maxWidth(this.getScreenColumns() - 1)
                    .newLine("\n")
                    .breakWords(false)
                    .wrap()
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    private void waitOn() {
        write(WAIT_COLOR);
        print(WAIT_MESSAGE);
        flush();
    }

    private void waitOff() {
        for (int i = 0; i < WAIT_MESSAGE.length(); ++i) write(AsciiKeys.BACKSPACE);
        flush();
    }

}
