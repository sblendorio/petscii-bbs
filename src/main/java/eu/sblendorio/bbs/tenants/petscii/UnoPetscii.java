package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import java.io.IOException;
import java.util.*;

import static eu.sblendorio.bbs.core.PetsciiColors.*;

public class UnoPetscii extends PetsciiThread {

    private static final String[] COLORS = {"Red", "Yellow", "Green", "Blue"};
    private static final int NUMBERS = 9; // 1-9, no 0
    private static final int INITIAL_HAND_SIZE = 7;

    private List<Card> deck;
    private List<Card> discardPile;
    private List<Card> playerHand;
    private List<Card> computerHand;

    // Track current active color (for wilds)
    private String currentColor;

    // Play direction: 1 for normal, -1 for reverse
    //
    // d
    private int playDirection = 1;

    private Card lastPlayedCard = null;
    private boolean lastPlayedByPlayer = true;

    @Override
    public void doLoop() throws Exception {
        initGame();
        try {
            gameLoop();
        } catch (IOException e) {
            e.printStackTrace();
            delayedPrintln("An error occurred during input. Exiting game.");
        } catch (ExitGameException e) {
            delayedPrintln("Exiting game to menu...");
        }
        delayedPrintln("Game Over. Press any key to return.");
        resetInput();
        readKey();
    }

    private void initGame() {
        deck = createDeck();
        Collections.shuffle(deck);
        discardPile = new ArrayList<>();
        playerHand = new ArrayList<>();
        computerHand = new ArrayList<>();
        currentColor = null;
        playDirection = 1;

        // Deal initial hands
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            playerHand.add(drawCard());
            computerHand.add(drawCard());
        }

        // Start discard pile with a non-special, non-wild card
        Card topCard;
        do {
            topCard = drawCard();
        } while (topCard == null || topCard.isWild() || topCard.type.equals("skip") || topCard.type.equals("reverse") || topCard.type.equals("draw2"));
        discardPile.add(topCard);
        if (topCard.isWild()) {
            currentColor = pickRandomColor();
        } else {
            currentColor = topCard.color;
        }
        lastPlayedCard = topCard;
        lastPlayedByPlayer = false; // since it's the start
    }

    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (String color : COLORS) {
            // Zero is removed as per request
            for (int num = 1; num <= NUMBERS; num++) {
                deck.add(new Card(color, "number", num));
                deck.add(new Card(color, "number", num));
            }
            // Special cards: skip, reverse, draw2 (two each)
            for (int i = 0; i < 2; i++) {
                deck.add(new Card(color, "skip", -1));
                deck.add(new Card(color, "reverse", -1));
                deck.add(new Card(color, "draw2", -1));
            }
        }
        // Wild cards
        for (int i = 0; i < 4; i++) {
            deck.add(new Card("Wild", "wild", -1));
            deck.add(new Card("Wild", "wild4", -1));
        }
        return deck;
    }

    private Card drawCard() {
        if (deck.isEmpty()) {
            // Reshuffle discard pile into deck
            if (discardPile.size() <= 1) {
                // No cards to reshuffle
                return null;
            }
            Card top = discardPile.remove(discardPile.size() - 1);
            deck.addAll(discardPile);
            discardPile.clear();
            discardPile.add(top);
            Collections.shuffle(deck);
        }
        return deck.isEmpty() ? null : deck.remove(deck.size() - 1);
    }

    private void gameLoop() throws IOException {
        boolean playerTurn = true;
        while (true) {
            newline();
            write(LIGHT_RED);
            delayedPrintln("Top card: " + getTopCard());
            write(PURPLE);
            delayedPrintln("Current color: " + currentColor);
            write(WHITE);

            if (playerTurn) {
                // Player's turn
                write(5);
                delayedPrintln("Your Hand:");
                for (int i = 0; i < playerHand.size(); i++) {
                    println("  " + (i + 1) + ". " + playerHand.get(i));
                }
                write(153);
                delayedPrintln("Enter card number to play or 0 to draw:");
                int choice = readInt(0, playerHand.size());
                if (choice == 0) {
                    Card drawn = drawCard();
                    if (drawn == null) {
                        delayedPrintln("No cards left to draw.");
                        continue;
                    }
                    delayedPrintln("You drew: " + drawn);
                    if (canPlay(drawn)) {
                        delayedPrintln("Play " + drawn + "? (y/n)");
                        if (readYesNo()) {
                            playCard(playerHand, drawn);
                            handleCardEffect(drawn, true);
                        } else {
                            playerHand.add(drawn);
                        }
                    } else {
                        delayedPrintln("Cannot play that card. Added to your hand.");
                        playerHand.add(drawn);
                    }
                } else {
                    Card chosenCard = playerHand.get(choice - 1);
                    if (canPlay(chosenCard)) {
                        playCard(playerHand, chosenCard);
                        handleCardEffect(chosenCard, true);
                    } else {
                        delayedPrintln("Cannot play that card. Try again.");
                        continue;
                    }
                }
                if (playerHand.isEmpty()) {
                    delayedPrintln("Congratulations! You won!");
                    break;
                }
            } else {
                // Computer's turn
                newline();
                write(31);
                delayedPrintln("Computer's turn...");
                Card playable = null;
                for (Card c : computerHand) {
                    if (canPlay(c)) {
                        playable = c;
                        break;
                    }
                }
                if (playable != null) {
                    // Use playCard() for consistency
                    playCard(computerHand, playable);
                    handleCardEffect(playable, false);
                    delayedPrintln("Computer played: " + playable);
                } else {
                    Card drawn = drawCard();
                    if (drawn == null) {
                        delayedPrintln("No cards left for computer to draw.");
                    } else {
                        computerHand.add(drawn);
                        delayedPrintln("Computer drew a card");
                    }
                }
                if (computerHand.isEmpty()) {
                    delayedPrintln("Computer wins!");
                    break;
                }
            }

            // Handle skip
            if (lastPlayedCard != null && lastPlayedCard.type.equals("skip")) {
                if (lastPlayedByPlayer) {
                    delayedPrintln("Turn Skipped.");
                }
                lastPlayedCard = null; // Reset skip effect
                continue;
            }

            // Handle reverse
            if (lastPlayedCard != null && lastPlayedCard.type.equals("reverse")) {
                playDirection = -playDirection;
                delayedPrintln("Play direction reversed!");
                lastPlayedCard = null;
            }

            // Switch turns
            playerTurn = !playerTurn;
        }
    }

    private Card getTopCard() {
        return discardPile.get(discardPile.size() - 1);
    }

    private boolean canPlay(Card card) {
        Card top = getTopCard();

        if (card.isWild()) {
            return true; // Wilds can always be played
        }

        if (top.isWild()) {
            // Match currentColor
            return card.color.equals(currentColor);
        }

        // Match color
        if (card.color.equals(currentColor)) {
            return true;
        }
        // Match number
        if (card.type.equals("number") && top.type.equals("number") && card.number == top.number) {
            return true;
        }
        // Match type (skip, reverse, draw2)
        if (card.type.equals(top.type) && (card.type.equals("skip") || card.type.equals("reverse") || card.type.equals("draw2"))) {
            return true;
        }
        return false;
    }

    private void playCard(List<Card> hand, Card card) throws IOException {
        hand.remove(card);
        discardPile.add(card);
        lastPlayedCard = card;
        lastPlayedByPlayer = (hand == playerHand);

        if (card.type.equals("wild")) {
            if (lastPlayedByPlayer) {
                // Player's turn: prompt for color
                String chosenColor = promptForColor();
                currentColor = chosenColor;
            } else {
                // Computer's turn: auto-select color based on opponent's hand
                String chosenColor = pickMostCommonColor(getOpponentHand());
                currentColor = chosenColor;
                delayedPrintln("Computer chose color: " + currentColor);
            }
        } else if (card.type.equals("wild4")) {
            if (lastPlayedByPlayer) {
                String chosenColor = promptForColor();
                currentColor = chosenColor;
            } else {
                String chosenColor = pickMostCommonColor(getOpponentHand());
                currentColor = chosenColor;
                delayedPrintln("Computer chose color: " + currentColor);
                drawMultipleCards(getOpponentHand(), 4);
            }
        } else {
            // For normal cards, update currentColor to the card's color
            currentColor = card.color;
        }
    }

    private List<Card> getOpponentHand() {
        return lastPlayedByPlayer ? computerHand : playerHand;
    }

    private String pickMostCommonColor(List<Card> opponentHand) {
        Map<String, Integer> colorCount = new HashMap<>();
        for (String color : COLORS) {
            colorCount.put(color, 0);
        }
        for (Card c : opponentHand) {
            if (!c.isWild()) {
                colorCount.put(c.color, colorCount.get(c.color) + 1);
            }
        }
        String maxColor = COLORS[0];
        int maxCount = -1;
        for (String color : COLORS) {
            if (colorCount.get(color) > maxCount) {
                maxCount = colorCount.get(color);
                maxColor = color;
            }
        }
        return maxColor;
    }

    private void handleCardEffect(Card card, boolean isPlayer) throws IOException {
        lastPlayedByPlayer = isPlayer;
        switch (card.type) {
            case "skip":
                if (isPlayer) {
                    delayedPrintln(" ");
                }
                break;
            case "reverse":
                playDirection = -playDirection;
                delayedPrintln(" ");
                break;
            case "draw2":
                drawMultipleCards(getOpponentHand(), 2);
                break;
            case "wild":
            case "wild4":
                // Color already chosen in playCard()
                break;
            default:
                // Number card or other
                break;
        }
    }

    private void drawMultipleCards(List<Card> hand, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            Card c = drawCard();
            if (c == null) {
                delayedPrintln("No more cards to draw.");
                return;
            }
            hand.add(c);
        }
    }

    private String pickRandomColor() {
        Random rand = new Random();
        return COLORS[rand.nextInt(COLORS.length)];
    }

    private String promptForColor() throws IOException {
        delayedPrintln("Choose a color:");
        for (int i = 0; i < COLORS.length; i++) {
            println("  " + (i + 1) + ". " + COLORS[i]);
        }
        int choice = readInt(1, COLORS.length);
        return COLORS[choice - 1];
    }

    // Helper input methods
    private boolean readYesNo() throws IOException {
        String input = readLineWithExit(1);
        if (input == null) throw new ExitGameException();
        input = input.trim().toLowerCase();
        return input.startsWith("y");
    }

    private int readInt(int min, int max) throws IOException {
        while (true) {
            String line = readLineWithExit(2);
            if (line == null) throw new ExitGameException();
            try {
                int val = Integer.parseInt(line.trim());
                if (val >= min && val <= max) {
                    return val;
                }
            } catch (NumberFormatException e) {
                // ignore
            }
            delayedPrintln("Please enter a number between " + min + " and " + max);
        }
    }

    private String readLineWithExit(int timeoutSeconds) throws IOException {
        String input = readLine(timeoutSeconds);
        if (input.trim().equals(".")) {
            throw new ExitGameException();
        }
        return input;
    }

    // Card class
    private static class Card {
        String color;
        String type; // "number", "skip", "reverse", "draw2", "wild", "wild4"
        int number; // only for number cards

        Card(String color, String type, int number) {
            this.color = color;
            this.type = type;
            this.number = number;
        }

        boolean isWild() {
            return type.equals("wild") || type.equals("wild4");
        }

        @Override
        public String toString() {
            switch (type) {
                case "number":
                    return color + " " + number;
                case "skip":
                    return color + " Skip";
                case "reverse":
                    return color + " Reverse";
                case "draw2":
                    return color + " Draw Two";
                case "wild":
                    return "Wild";
                case "wild4":
                    return "Wild Draw Four";
                default:
                    return "Unknown Card";
            }
        }
    }

    private class ExitGameException extends RuntimeException {
    }

    // Add delay before printing for better UX
    private void delayedPrintln(String message) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        println(message);
    }
}