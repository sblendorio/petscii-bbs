package eu.sblendorio.bbs.tenants.petscii;

import codeanticode.eliza.Eliza;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.davidmoten.text.utils.WordWrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class ElizaPetscii extends PetsciiThread {
    private static final String EXIT_ADVICE = "Enter \".\" to EXIT";

    @Override
    public void doLoop() throws Exception {
        Eliza eliza = new Eliza();
        cls();
        write(LIGHT_GREEN);
        println("ELIZA - First chatbot ever (1966)");
        println("---------------------------------");
        write(GREEN);
        wordWrap("ELIZA is an early natural language processing computer program created at MIT by professor Joseph "
                + "Weizenbaum from 1964 to 1966. Created to explore communication between humans and machines, "
                + "ELIZA simulated conversation by using a pattern matching and substitution methodology that gave "
                + "users an illusion of understanding on the part of the program.")
                .forEach(this::println);
        println();
        write(GREY2);
        println("Enter \".\" to exit");
        println();
        boolean exitAdvice = false;
        for (;;) {
            write(WHITE);
            print("You> ");
            flush(); resetInput();
            String input = readLine();
            input = trimToEmpty(input);
            if (".".equals(input) || "exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) break;
            if (isBlank(input)){
                exitAdvice = true;
                write(GREY2);
                print(EXIT_ADVICE);
                write(UP, UP, RETURN);
                continue;
            }
            if (exitAdvice)
                for (int i=0; i<EXIT_ADVICE.length(); ++i) write(SPACE_CHAR);
            println();
            String response = "Eliza> " + eliza.processInput(asciiToUtf8(input));
            final List<String> lines = wordWrap(HtmlUtils.utilHtmlDiacriticsToAscii(response));
            write(LIGHT_GREEN);
            lines.forEach(this::println);
            println();
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

}