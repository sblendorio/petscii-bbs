package eu.sblendorio.bbs.tenants.petscii;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.internal.StringUtil;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.theokanning.openai.completion.chat.ChatCompletionRequest.builder;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class OpenAiPetscii extends PetsciiThread {

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
        List<ChatMessage> conversation = new LinkedList<>();
        String input;
        do {
            flush(); resetInput();
            print(">");
            input = readLine();
            input = trimToEmpty(input);
            if (".".equalsIgnoreCase(input)) break;

            conversation.add(new ChatMessage("user", input));

            ChatCompletionRequest request = builder()
                    .model("gpt-3.5-turbo")
                    .messages(conversation)
                    .build();

            waitOn();
            List<ChatCompletionChoice> choices = service().createChatCompletion(request).getChoices();
            waitOff();
            if (size(choices) == 0) continue;

            String answer = choices.get(0).getMessage().getContent();
            conversation.add(new ChatMessage("assistant", answer));

            println(answer);

        } while (true);
    }


    protected void waitOn() {
        print("PLEASE WAIT...");
        flush();
    }

    protected void waitOff() {
        for (int i = 0; i < 14; ++i) write(DEL);
        flush();
    }
}
