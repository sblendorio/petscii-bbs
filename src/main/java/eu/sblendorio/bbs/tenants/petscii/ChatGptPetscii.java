package eu.sblendorio.bbs.tenants.petscii;

import com.theokanning.openai.OpenAiHttpException;
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
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.theokanning.openai.completion.chat.ChatCompletionRequest.builder;
import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class ChatGptPetscii extends PetsciiThread {
    private static Logger logger = LogManager.getLogger(ChatGptPetscii.class);
    private static int CODE_LENGTH = 6;

    private static byte[] LOGO_AUTHENTICATE = readBinaryFile("petscii/gpt.seq");
    private static byte[] BIG_LOGO = readBinaryFile("petscii/gpt-biglogo.seq");

    private static final String WAIT_MESSAGE = "Please wait...";
    private static final String EXIT_ADVICE = "Type \".\" to EXIT";
    protected static final String CUSTOM_KEY = "PATREON_USER";
    private static final long TIMEOUT = 300_000;
    private String user = null;

    private static Random random;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new Random(System.currentTimeMillis());
        }
    }

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
        boolean keepGoing = authenticate();
        if (!keepGoing)
            return;

        registerFirstAccess(user);
        changeClientName(user+"/"+UUID.randomUUID());

        cls();
        write(BIG_LOGO);
        println();
        List<ChatMessage> conversation = new LinkedList<>();
        String input;
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
                    user,
                    input.replaceAll("\n", "\\\\n"));

            ChatCompletionRequest request = builder()
                    .model("gpt-3.5-turbo")
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
                            user,
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
                    user,
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

    private boolean authenticate() throws IOException {
        if (asList(getProperty("patreon.ip.whitelist", "none").split(",")).contains(serverAddress.getHostAddress())) {
            user = serverAddress.getHostAddress();
            return true;
        }

        try {
            user = (String) getRoot().getCustomObject(CUSTOM_KEY);
        } catch (NullPointerException | ClassCastException e) {
            log("User not logged " + e.getClass().getName() + " " + e.getMessage());
        }
        if (user != null)
            return true;

        cls();
        write(LOGO_AUTHENTICATE);
        println();
        write(GREY2);
        println("For security reasons, the BBS will log:");
        write(WHITE); print("IP address"); write(GREY2); print(", "); write(WHITE); print("email"); write(GREY2); print(" and "); write(WHITE); print("messages"); write(GREY2); println(".");
        println("If you proceed, you will accept this.");
        println();
        write(readBinaryFile("petscii/patreon-access.seq"));
        write(GREY3);
        println("Your Patreon email:");
        println();
        println(repeat(chr(163), 39));
        write(GREY2); print("You can use: "); write(YELLOW); print("\"-\""); write(GREY2); println(" for underscore");
        write(YELLOW); print("             \"!\""); write(GREY2); print(" in place of "); write(YELLOW); println("\"@\"");
        println();
        write(GREY2); print("Example: "); write(WHITE); print("johndoe"); write(YELLOW); print("!"); write(WHITE); println("gmail.com");
        write(/*RETURN, RETURN, RETURN, */RETURN, GREY1);
        print("www.patreon.com/FrancescoSblendorio");
        write(UP, UP, UP, UP, UP, RETURN);
        write(UP, UP, UP);
        flush(); resetInput();
        write(PetsciiColors.LIGHT_BLUE);
        String tempEmail = readLine();
        write(RETURN, RETURN, RETURN, RETURN, RETURN, RETURN, GREY1); print(repeat(' ', 39));
        write(UP, UP, UP, UP, UP, UP, UP, RETURN);
        final String userEmail = trimToEmpty(tempEmail);
        if (isBlank(userEmail) || ".".equals(trimToEmpty(userEmail)))
            return false;

        String email = readTxt(getProperty("PATREON_EMAILS", getProperty("user.home") + File.separator + "patreon_emails.txt"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
                .filter(row -> !row.startsWith(";"))
                .filter(row -> row.replace("_", "-").replace("*", "@").replace("!", "@")
                        .equalsIgnoreCase(userEmail.replace("_", "-").replace("*", "@").replace("!", "@")))
                .findFirst()
                .orElse("");

        if (isBlank(email)) {
            println();
            write(PetsciiColors.RED);
            print("         "); write(REVON); println("                       ");
            print("         "); write(REVON); println(" Not subscriber's mail ");
            print("         "); write(REVON); println(" Press any key to exit ");
            print("         "); write(REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }

        String secretCode = generateSecretCode(CODE_LENGTH);
        println();
        println(repeat(' ',31));
        println(repeat(' ',32));
        println();
        println(repeat(' ',26));
        write(UP, UP, UP, UP);
        waitOn();
        boolean success = sendSecretCode(email, secretCode);
        if (!success) {
            waitOff();
            println();
            write(PetsciiColors.RED);
            print("         "); write(REVON); println("                       ");
            print("         "); write(REVON); println("   Mail server error   ");
            print("         "); write(REVON); println(" Press any key to exit ");
            print("         "); write(REVON); print("                       "); write(REVOFF); print("  ");
            flush(); resetInput();
            readKey();
            return false;
        }
        waitOff();
        long startMillis = System.currentTimeMillis();
        write(GREY3);
        println();
        println("Please type " + CODE_LENGTH + "-digit code just sent");
        print("to your email: ");
        write(PetsciiColors.LIGHT_BLUE);
        flush(); resetInput();
        String userCode = readLine(CODE_LENGTH);
        userCode = trimToEmpty(userCode);
        long endMillis = System.currentTimeMillis();
        if (endMillis-startMillis > TIMEOUT) {
            write(UP, UP, UP, PetsciiColors.RED);
            print("         "); write(REVON); println("                       ");
            print("         "); write(REVON); print(" Timeout, try it again "); write(REVOFF); println("   ");
            print("         "); write(REVON); println(" Press any key to exit ");
            print("         "); write(REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }

        if (!userCode.equalsIgnoreCase(secretCode)) {
            write(UP, UP, UP, PetsciiColors.RED);
            print("         "); write(REVON); println("                       ");
            print("         "); write(REVON); print(" It was the wrong code "); write(REVOFF); println("   ");
            print("         "); write(REVON); println(" Press any key to exit ");
            print("         "); write(REVON); println("                       ");
            flush(); resetInput();
            readKey();
            return false;
        }

        getRoot().setCustomObject(CUSTOM_KEY, email);
        user = email;
        return true;
    }

    private void registerFirstAccess(String user) throws IOException {
        final String filename = getProperty("PATREON_EMAILS", getProperty("user.home") + File.separator + "consent_emails.txt");
        List<String> rows = readTxt(filename);
        boolean yetConnected = rows
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trimToEmpty)
                .map(row -> row.split(";")[0])
                .collect(toList())
                .contains(user);

        if (!yetConnected) {
            rows.add(user + ";" + Instant.now().toString());
            FileWriter writer = new FileWriter(filename);
            for(String str: rows) writer.write(str + System.lineSeparator());
            writer.close();
        }
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