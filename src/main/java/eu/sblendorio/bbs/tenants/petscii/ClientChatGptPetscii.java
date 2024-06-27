package eu.sblendorio.bbs.tenants.petscii;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidmoten.text.utils.WordWrap;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.theokanning.openai.completion.chat.ChatCompletionRequest.builder;
import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class ClientChatGptPetscii extends PetsciiThread {
    private static Logger logger = LogManager.getLogger(ClientChatGptPetscii.class);
    private static Logger loggerAuthorizations = LogManager.getLogger("authorizations");
    public static byte[] PETSCII_BIG_LOGO = BbsThread.readBinaryFile("petscii/gpt-biglogo.seq");
    private static final String WAIT_MESSAGE = "Please wait...";
    private static final String EXIT_ADVICE = "Type \".\" to EXIT";

    private static int USER_COLOR = WHITE;
    private static int ASSISTANT_COLOR = PetsciiColors.LIGHT_BLUE;
    private static int WAIT_COLOR = GREY2;
    private static int MORE_COLOR = GREY2;

    private OpenAiService openAiService = null;

    private String apiKey() {
        return defaultString(getProperty("OPENAI_KEY", getenv("OPENAI_KEY")), "DUMMY");
    }

    private Duration timeout() {
        long seconds = toLong(defaultString(getProperty("OPENAI_TIMEOUT_SECS", getenv("OPENAI_TIMEOUT_SECS")), "180"));
        return Duration.ofSeconds(seconds);
    }

    private OpenAiService service() {
        if (openAiService == null)
            openAiService = new OpenAiService(apiKey(), timeout());
        return openAiService;
    }

    @Override
    public void doLoop() throws Exception {
        PatreonData patreonData = PatreonData.authenticatePetscii(this);
        if (patreonData == null) return;

        String model = toInt(patreonData.patreonLevel) > 0 ? "gpt-4" : "gpt-3.5-turbo";
        changeClientName(patreonData.user+"/"+UUID.randomUUID());

        cls();
        write(PETSCII_BIG_LOGO);
        println();
        List<ChatMessage> conversation = new LinkedList<>();
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

            conversation.add(new ChatMessage("user", input));
            logger.info("IP: '{}', email: '{}', role: 'user', message: {}",
                    ipAddress.getHostAddress(),
                    patreonData.user,
                    input.replaceAll("\n", "\\\\n"));

            ChatCompletionRequest request = builder()
                    .model(model)
                    .messages(conversation)
                    .build();

            waitOn(exitAdvice);
            if (exitAdvice) exitAdvice = false;
            final List<ChatCompletionChoice> choices;
            try {
                choices = service().createChatCompletion(request).getChoices();
            } catch (Exception e) {
                if (e instanceof OpenAiHttpException && e.getMessage() != null && e.getMessage().contains("maximum context length")) {
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