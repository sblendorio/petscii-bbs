package eu.sblendorio.bbs.tenants.petscii;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import eu.sblendorio.bbs.core.*;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.davidmoten.text.utils.WordWrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;

import static com.theokanning.openai.completion.chat.ChatCompletionRequest.builder;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toLong;


public class ChatGptPetscii extends PetsciiThread {
    private static final String WAIT_MESSAGE = "Please wait...";
    protected static final String CUSTOM_KEY = "CHATGPT_PETSCII";
    private static final long TIMEOUT = 300_000;
    private String user = null;

    private static Random random;

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            random = new Random(System.currentTimeMillis());
        }
    }

    private static int USER_COLOR = WHITE;
    private static int ASSISTANT_COLOR = PetsciiColors.LIGHT_BLUE;
    private static int WAIT_COLOR = PetsciiColors.GREY2;
    private static int MORE_COLOR = PetsciiColors.GREY2;

    private OpenAiService openAiService = null;

    private String apiKey() {
        return defaultString(getProperty("OPENAI_KEY", getenv("OPENAI_KEY")), "DUMMY");
    }

    private Duration timeout() {
        long seconds = toLong(defaultString(getProperty("OPENAI_TIMEOUT_SECS", getenv("OPENAI_TIMEOUT_SECS")), "60"));
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

        cls();
        write(readBinaryFile("petscii/gpt-biglogo.seq"));
        println();
        List<ChatMessage> conversation = new LinkedList<>();
        String input;
        do {
            flush(); resetInput();
            write(USER_COLOR);
            print("You> ");
            input = readLine();
            input = trimToEmpty(input);
            if (".".equalsIgnoreCase(input)) break;
            if (isBlank(input)) {
                write(PetsciiKeys.UP);
                continue;
            }
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

            // System.out.println("------------------------------------------------------------");
            // System.out.println(completion);

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
        print(WAIT_MESSAGE);
        flush();
    }

    private void waitOff() {
        for (int i = 0; i < WAIT_MESSAGE.length(); ++i) write(DEL);
        flush();
    }

    private boolean authenticate() throws IOException {
        try {
            user = (String) getRoot().getCustomObject(CUSTOM_KEY);
        } catch (NullPointerException | ClassCastException e) {
            log("User not logged " + e.getClass().getName() + " " + e.getMessage());
        }
        if (user != null)
            return true;

        if (!askForLogging())
            return false;

        cls();
        write(readBinaryFile("petscii/gpt.seq"));
        println();
        println();
        write(readBinaryFile("petscii/patreon-access.seq"));
        println();
        println();
        write(GREY3);
        println("Enter your Patreon email:");
        println();
        println(StringUtils.repeat(chr(163), 39));
        write(PetsciiKeys.UP, PetsciiKeys.UP);
        flush(); resetInput();
        write(PetsciiColors.LIGHT_BLUE);
        String tempEmail = readLine();
        final String userEmail = trimToEmpty(tempEmail);
        if (isBlank(userEmail))
            return false;

        String email = readTxt(getProperty("PATREON_EMAILS", getProperty("user.home") + File.separator + "patreon_emails.txt"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
                .filter(row -> row.equalsIgnoreCase(userEmail))
                .findFirst()
                .orElse("");

        if (isBlank(email)) {
            println();
            println();
            write(PetsciiColors.RED);
            print("         "); write(PetsciiKeys.REVON); println("                       ");
            print("         "); write(PetsciiKeys.REVON); println(" Not subscriber's mail ");
            print("         "); write(PetsciiKeys.REVON); println(" Press any key to exit ");
            print("         "); write(PetsciiKeys.REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }

        String secretCode = generateSecretCode(6);
        println();
        waitOn();
        boolean success = sendSecretCode(email, secretCode);
        if (!success) {
            println();
            write(PetsciiColors.RED);
            print("         "); write(PetsciiKeys.REVON); println("                       ");
            print("         "); write(PetsciiKeys.REVON); println("   Mail server error   ");
            print("         "); write(PetsciiKeys.REVON); println(" Press any key to exit ");
            print("         "); write(PetsciiKeys.REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }
        waitOff();
        long startMillis = System.currentTimeMillis();
        write(GREY3);
        println();
        println("Please enter 6-digit code just sent");
        print("to your email: ");
        write(PetsciiColors.LIGHT_BLUE);
        flush(); resetInput();
        String userCode = readLine();
        userCode = trimToEmpty(userCode);
        long endMillis = System.currentTimeMillis();
        if (endMillis-startMillis > TIMEOUT) {
            write(UP, UP, PetsciiColors.RED);
            print("         "); write(PetsciiKeys.REVON); print("                       "); write(REVOFF); println("   ");
            print("         "); write(PetsciiKeys.REVON); println(" Timeout, try it again ");
            print("         "); write(PetsciiKeys.REVON); println(" Press any key to exit ");
            print("         "); write(PetsciiKeys.REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }

        if (!userCode.equalsIgnoreCase(secretCode)) {
            write(UP, UP, PetsciiColors.RED);
            print("         "); write(PetsciiKeys.REVON); print("                       "); write(REVOFF); println("   ");
            print("         "); write(PetsciiKeys.REVON); println(" It was the wrong code ");
            print("         "); write(PetsciiKeys.REVON); println(" Press any key to exit ");
            print("         "); write(PetsciiKeys.REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }

        getRoot().setCustomObject(CUSTOM_KEY, email);
        user = email;
        return true;
    }

    private boolean askForLogging() throws IOException {
        cls();
        write(GREY3);
        println("For security reasons, will be logged:");
        println("- IP address");
        print("- Messages ("); write(WHITE); print("no"); write(GREY3); println(" username)");
        println();
        println("If you don't accept that, you won't");
        println("have access to the functionality.");
        println();
        print("Do you accept? (Y/N) ");
        flush(); resetInput();
        int ch = readKey();
        return Character.toLowerCase(ch) == 'y';
    }

    private boolean sendSecretCode(String email, String secretCode) {
        final String USERNAME = defaultString(getProperty("MAIL_FROM", getenv("MAIL_FROM")));
        final String PASSWORD = defaultString(getProperty("MAIL_FROM_PASSWORD", getenv("MAIL_FROM_PASSWORD")));
        final String MAIL_SMTP_HOST = defaultString(getProperty("MAIL_SMTP_HOST", getenv("MAIL_SMTP_HOST")));
        final String MAIL_SMTP_PORT = defaultString(getProperty("MAIL_SMTP_PORT", getenv("MAIL_SMTP_PORT")));
        final String MAIL_SMTP_AUTH = defaultString(getProperty("MAIL_SMTP_AUTH", getenv("MAIL_SMTP_AUTH")));
        final String MAIL_SMTP_STARTTLS_ENABLE = defaultString(getProperty("MAIL_SMTP_STARTTLS_ENABLE", getenv("MAIL_SMTP_STARTTLS_ENABLE")));

        Properties prop = new Properties();
        prop.put("mail.smtp.host", MAIL_SMTP_HOST);
        prop.put("mail.smtp.port", MAIL_SMTP_PORT);
        prop.put("mail.smtp.auth", MAIL_SMTP_AUTH);
        prop.put("mail.smtp.starttls.enable", MAIL_SMTP_STARTTLS_ENABLE);

        Session session = Session.getInstance(prop,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("BBS access code");
            message.setText("Your temporary code is " + secretCode);

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateSecretCode(int length) {
         return random
                 .ints(0, 10)
                 .limit(length)
                 .mapToObj(String::valueOf)
                 .collect(joining());
    }

    private List<String> readTxt(String filename) {
        List<String> result = new LinkedList<>();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                result.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }

}