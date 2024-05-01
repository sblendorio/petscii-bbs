package eu.sblendorio.bbs.tenants.ascii;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidmoten.text.utils.WordWrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.theokanning.openai.completion.chat.ChatCompletionRequest.builder;
import static eu.sblendorio.bbs.core.Utils.*;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class ClientChatGptAscii extends AsciiThread {
    private BbsInputOutput interfaceType = null;
    private byte[] mainLogo = null;
    private static final String EXIT_ADVICE = "Type \".\" to EXIT";

    private static Logger logger = LogManager.getLogger(ClientChatGptAscii.class);
    private static Logger loggerAuthorizations = LogManager.getLogger("authorizations");
    private static final String WAIT_MESSAGE = "...";
    private static byte[] USER_COLOR = new byte[] {};
    private static byte[] ASSISTANT_COLOR = new byte[] {};
    private static byte[] WAIT_COLOR = new byte[] {};
    private static byte[] MORE_COLOR = new byte[] {};

    private OpenAiService openAiService = null;

    private String apiKey() {
        return defaultString(getProperty("OPENAI_KEY", getenv("OPENAI_KEY")), "DUMMY");
    }

    private Duration timeout() {
        long seconds = toLong(defaultString(getProperty("OPENAI_TIMEOUT_SECS", getenv("OPENAI_TIMEOUT_SECS")), "180"));
        return Duration.ofSeconds(seconds);
    }

    public ClientChatGptAscii(BbsInputOutput interfaceType, byte[] mainLogo) {
        super();
        this.interfaceType = interfaceType;
        this.mainLogo = mainLogo;
    }

    public ClientChatGptAscii(BbsInputOutput interfaceType) {
        this(interfaceType, null);
    }

    public ClientChatGptAscii() {
        this(null, null);
    }

    private OpenAiService service() {
        if (openAiService == null)
            openAiService = new OpenAiService(apiKey(), timeout());
        return openAiService;
    }

    @Override
    public void doLoop() throws Exception {
        if (interfaceType != null) {
            this.setBbsInputOutput(interfaceType);
        }

        PatreonData patreonData = PatreonData.authenticateAscii(this);
        if (patreonData == null) return;

        String model = toInt(patreonData.patreonLevel) > 0 ? "gpt-4" : "gpt-3.5-turbo";
        changeClientName(patreonData.user+"/"+UUID.randomUUID());

        cls();
        if (mainLogo == null) {
            println("Chat GPT - Classic Client");
            println("-------------------------");
            if (toInt(patreonData.patreonLevel) > 0) println("Model: " + model);
            println();
            println(EXIT_ADVICE);
            println();
        } else {
            write(mainLogo);
        }
        List<ChatMessage> conversation = new LinkedList<>();
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

            conversation.add(new ChatMessage("user", input));
            logger.info("IP: '{}', email: '{}', role: 'user', message: {}",
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    input.replaceAll("\n", "\\\\n"));

            ChatCompletionRequest request = builder()
                    .model(model)
                    .messages(conversation)
                    .build();

            waitOn();
            final List<ChatCompletionChoice> choices;
            try {
                choices = service().createChatCompletion(request).getChoices();
            } catch (Exception e) {
                if (e instanceof OpenAiHttpException && e.getMessage() != null && e.getMessage().contains("maximum context length")) {
                    cls();
                    println("This conversation exceeded max. token");
                    println("length.  But you can close this alert");
                    println("pressing a key and starting a new one");
                    println();
                    flush(); resetInput();
                    readKey();
                    break;

                } else {
                    cls();
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
            optionalCls();
            if (size(choices) == 0) continue;

            final ChatCompletionChoice completion = choices.get(0);
            final ChatMessage message = completion.getMessage();
            conversation.add(message);

            logger.info("IP: '{}', email: '{}', role: '{}', message: {}",
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    message.getRole(),
                    message.getContent().replaceAll("\n", "\\\\n"));

            final String answer = "ChatGPT> " + message.getContent();
            println();
            optionalCls();
            printPagedText(answer);
        } while (true);
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

        if (!(io instanceof MinitelInputOutput)) result = HtmlUtils.utilHtmlDiacriticsToAscii(result);

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
