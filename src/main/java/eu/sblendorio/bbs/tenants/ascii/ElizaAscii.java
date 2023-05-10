package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import codeanticode.eliza.*;
import eu.sblendorio.bbs.core.HtmlUtils;
import org.davidmoten.text.utils.WordWrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class ElizaAscii extends AsciiThread {
    private static final String EXIT_ADVICE = "Type \".\" to EXIT";

    @Override
    public void doLoop() throws Exception {
        Eliza eliza = new Eliza();
        cls();
        println("ELIZA - First chatbot ever (1966)");
        println("---------------------------------");
        wordWrap("ELIZA is an early natural language processing computer program created at MIT by professor Joseph "
                + "Weizenbaum from 1964 to 1966. Created to explore communication between humans and machines, "
                + "ELIZA simulated conversation by using a pattern matching and substitution methodology that gave "
                + "users an illusion of understanding on the part of the program.")
                .forEach(this::println);
        println();
        println(EXIT_ADVICE);
        println();
        for (;;) {
            print("You> ");
            flush(); resetInput();
            String input = readLine();
            input = trimToEmpty(input);
            if (".".equals(input) || "exit".equalsIgnoreCase(input) || "quit".equalsIgnoreCase(input)) break;
            if (isBlank(input)){
                println(EXIT_ADVICE);
                continue;
            }
            println();
            String response = "Eliza> " + eliza.processInput(asciiToUtf8(input));
            final List<String> lines = wordWrap(HtmlUtils.utilHtmlDiacriticsToAscii(response));
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