package eu.sblendorio.bbs.tenants.petscii;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import eu.sblendorio.bbs.core.*;
import org.apache.commons.lang3.StringUtils;
import org.davidmoten.text.utils.WordWrap;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.theokanning.openai.completion.chat.ChatCompletionRequest.builder;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static org.apache.commons.collections4.CollectionUtils.isFull;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toLong;


public class OpenAiPetscii extends PetsciiThread {

    private static int USER_COLOR = PetsciiColors.WHITE;
    private static int ASSISTANT_COLOR = PetsciiColors.LIGHT_BLUE;
    private static int WAIT_COLOR = PetsciiColors.GREY2;
    private static int MORE_COLOR = PetsciiColors.GREY2;

    private OpenAiService openAiService = null;

    private String apiKey() {
        return defaultString(getProperty("OPENAI_KEY", getenv("OPENAI_KEY")), "DUMMY");
    }

    private Duration timeout() {
        long seconds = toLong(defaultString(getProperty("OPENAI_TIMEOUT_SECS", getenv("OPENAI_TIMEOUT_SECS")), "30"));
        return Duration.ofSeconds(seconds);
    }

    private OpenAiService service() {
        if (openAiService == null)
            openAiService = new OpenAiService(apiKey(), timeout());
        return openAiService;
    }

    @Override
    public void doLoop() throws Exception {
        boolean keepGoing = authenticate();
        if (!keepGoing)
            return;

        displayLogo();
        List<ChatMessage> conversation = new LinkedList<>();
        String input;
        do {
            flush(); resetInput();
            write(USER_COLOR);
            print("You> ");
            input = readLine();
            input = trimToEmpty(input);
            if (".".equalsIgnoreCase(input)) break;
            if (isBlank(input)) continue;
            input = asciiToUtf8(input);

            conversation.add(new ChatMessage("user", input));

            ChatCompletionRequest request = builder()
                    .model("gpt-3.5-turbo")
                    .messages(conversation)
                    .build();

            waitOn();
            List<ChatCompletionChoice> choices = service().createChatCompletion(request).getChoices();
            waitOff();
            if (size(choices) == 0) continue;

            final ChatCompletionChoice completion = choices.get(0);
            final ChatMessage message = completion.getMessage();
            conversation.add(message);

            System.out.println("------------------------------------------------------------");
            System.out.println(completion);

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
        {
            return input
                    .replaceAll( "a'", "à")
                    .replaceAll( "A'", "À")
                    .replaceAll( "e'", "è")
                    .replaceAll( "E'", "È")
                    .replaceAll( "i'", "ì")
                    .replaceAll( "I'", "Ì")
                    .replaceAll( "o'", "ò")
                    .replaceAll( "O'", "Ò")
                    .replaceAll( "u'", "ù")
                    .replaceAll( "U'", "Ù")
                    ;
        }
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
            result.addAll(Arrays.asList(wrappedLine));
        }
        return result;
    }

    private void waitOn() {
        write(WAIT_COLOR);
        print("Please wait...");
        flush();
    }

    private void waitOff() {
        for (int i = 0; i < 14; ++i) write(DEL);
        flush();
    }

    private void displayLogo() {
        cls();
        write(readBinaryFile("petscii/gpt.seq"));
    }

    private boolean authenticate() throws IOException {
        displayLogo();
        println();
        write(readBinaryFile("petscii/patreon-access.seq"));
        println();
        write(PetsciiColors.GREY3);
        println("Enter your Patreon email:");
        println();
        println(StringUtils.repeat(chr(163), 39));
        write(PetsciiKeys.UP, PetsciiKeys.UP);
        flush(); resetInput();
        String email = readLine();

        return true;
    }

}