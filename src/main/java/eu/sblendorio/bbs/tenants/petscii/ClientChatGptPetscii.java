package eu.sblendorio.bbs.tenants.petscii;

import com.google.gson.Gson;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
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

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class ClientChatGptPetscii extends PetsciiThread {
    private static final Logger logger = LogManager.getLogger(ClientChatGptPetscii.class);
    public byte[] petsciiBigLogo;
    public String model;
    public String apiUrl;
    public String keyName;
    public String aiName;
    public int assistantColor;
    private static final String WAIT_MESSAGE = "Please wait...";
    private static final String EXIT_ADVICE = "Type \".\" to EXIT";

    private static final int USER_COLOR = WHITE;
    private static final int WAIT_COLOR = GREY2;
    private static final int MORE_COLOR = GREY2;

    private final HttpClient client;

    public ClientChatGptPetscii(String aiName, String apiUrl, String keyName, String model, int assistantColor, byte[] logo) {
        super();
        this.petsciiBigLogo = logo;
        this.model = model;
        this.apiUrl = apiUrl;
        this.keyName = keyName;
        this.aiName = aiName;
        this.assistantColor = assistantColor;
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
        PatreonData patreonData = PatreonData.authenticatePetscii(this);
        if (patreonData == null) return;

        //String model = toInt(patreonData.patreonLevel) > 0 ? "gpt-4" : "gpt-3.5-turbo";
        changeClientName(patreonData.user+"/"+UUID.randomUUID());

        cls();
        write(petsciiBigLogo);
        println();
        List<Map<String, Object>> conversation = new LinkedList<>();
        String input;
        if (toInt(patreonData.patreonLevel) > 0) {
            write(GREY3);
            String s = "Model: " + model;
            println(repeat(' ',Math.abs(37-s.length())) + s);
        }
        boolean exitAdvice = false;
        do {
            flush(); resetInput();
            write(USER_COLOR);
            print("You> ");
            input = readLine();
            input = trimToEmpty(input);
            if (".".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) break;
            if (isBlank(input)) {
                exitAdvice = true;
                write(LIGHT_RED);
                print(EXIT_ADVICE);
                write(UP, UP, RETURN);
                continue;
            }
            input = asciiToUtf8(input);
            conversation.add(Map.of(
                    "role", "user",
                    "content", input
            ));

            logger.info("IP: '{}', email: '{}', role: 'user', message: {}",
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    input.replaceAll("\n", "\\\\n"));

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", conversation
            );
            Gson gson = new Gson();

            waitOn(exitAdvice);
            if (exitAdvice) exitAdvice = false;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey())
                    .timeout(timeout())
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String assistantResponse;

            try {
                assistantResponse = parseAssistantResponse(response.body());
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage().contains("maximum context length")) {
                    cls();
                    write(RED);
                    println("                                       ");
                    println(" This conversation exceeded max. token ");
                    println(" length.  But you can close this alert ");
                    println(" pressing a key and starting a new one ");
                    println("                                       ");
                    println();
                    write(GREY3);
                    flush(); resetInput();
                    readKey();
                    break;
                } else {
                    cls();
                    write(RED);
                    println("Unexpected error. Please write to sysop");
                    println("Press any key to EXIT");
                    logger.error("IP: '{}', email: '{}', exception: {}",
                            ipAddress.getHostAddress(),
                            patreonData.user,
                            e);
                    flush();
                    resetInput();
                    readKey();
                    break;
                }
            }
            waitOff();

            conversation.add(Map.of(
                    "role", "assistant",
                    "content", assistantResponse
            ));

            logger.info("IP: '{}', email: '{}', role: '{}', message: {}",
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    "assistant",
                    assistantResponse.replaceAll("\n", "\\\\n"));

            final String answer = aiName +  "> " + assistantResponse;
            println();
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

        write(assistantColor);
        for (String line: lines) {
            println(line);
            count++;
            if (count % (this.getScreenRows() - 1) == 0) {
                write(MORE_COLOR);
                print("-- More --");
                write(assistantColor);
                flush(); resetInput();
                int key = readKey();
                println();
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

        result = HtmlUtils.utilHtmlDiacriticsToAscii(result);

        return result;
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
        waitOn(false);
    }

    private void waitOn(boolean exitAdvice) {
        write(WAIT_COLOR);
        print(WAIT_MESSAGE);
        if (exitAdvice && WAIT_MESSAGE.length() < EXIT_ADVICE.length()) {
            for (int i=0; i<(EXIT_ADVICE.length() - WAIT_MESSAGE.length()); i++) write(SPACE_CHAR);
            for (int i=0; i<(EXIT_ADVICE.length() - WAIT_MESSAGE.length()); i++) write(DEL);
        }
        flush();
    }

    private void waitOff() {
        for (int i = 0; i < WAIT_MESSAGE.length(); ++i) write(DEL);
        flush();
    }
}