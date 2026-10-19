package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games.BattleshipAI;
import eu.sblendorio.bbs.games.BattleshipAI.Board;
import eu.sblendorio.bbs.games.BattleshipAI.Cell;
import eu.sblendorio.bbs.games.BattleshipAI.Result;
import eu.sblendorio.bbs.games.BattleshipAI.Ship;
import eu.sblendorio.bbs.games.BattleshipAI.Tracker;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static eu.sblendorio.bbs.games.BattleshipAI.SIZE;
import static org.apache.commons.lang3.StringUtils.rightPad;

public class Battleship extends PetsciiThread {

    // Screen layout: own sea on the left, enemy sea in the middle, fleet status on the right
    private static final int PLAYER_X = 2;
    private static final int ENEMY_X = 16;
    private static final int GRID_Y = 5;
    private static final int PANEL_X = 28;
    private static final int MSG_Y = 17;
    private static final int HELP_Y = 20;

    private static final int WATER_CHAR = 166;
    private static final int MISS_CHAR = 'O'; // lowercase "o" in PETSCII lowercase charset
    private static final int HIT_CHAR = '*';

    private static final String[] LOGO = {
            "                 ]#[",
            "               d#####c[",
            " LLLLd#c     L#########L    d#cLLLL",
            "  LLL###LLLL#############LLL###LLLLLL",
            " C###################################D",
            "   UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU",
            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    };
    private static final int[] LOGO_COLORS = {WHITE, WHITE, GREY3, GREY3, GREY2, RED, BLUE};

    private final Random random = new Random();

    private Board playerBoard;
    private Board cpuBoard;
    private Tracker playerView;
    private BattleshipAI ai;
    private boolean revealEnemy;
    private int curX;
    private int curY;

    // Terminal state, tracked to send as few bytes as possible (-1 = unknown)
    private int posX = -1;
    private int posY = -1;
    private int lastColor = -1;
    private int lastRev = -1;
    private final int[] lineLength = new int[25];

    @Override
    public void doLoop() throws Exception {
        log("BATTLESHIP. Start");
        int level;
        while ((level = intro()) != 0) {
            if (!playMatch(level == 2)) break;
        }
        log("BATTLESHIP. Exit");
    }

    private int intro() throws IOException {
        clearScreen();
        drawLogo(1);
        drawTitle(9);
        text(1, 11, GREY3, "Sink the computer's fleet before it");
        text(1, 12, GREY3, "sinks yours! Ships can't touch each");
        text(1, 13, GREY3, "other, not even diagonally.");
        text(1, 15, GREY3, "Aim with cursor keys or A-J, 0-9");
        text(1, 16, GREY3, "and fire with RETURN or SPACE.");
        text(1, 18, WHITE, "Choose your rank:");
        keyCap(2, 20, '1');
        text(6, 20, CYAN, "Cadet (easy)");
        keyCap(21, 20, '2');
        text(25, 20, CYAN, "Admiral (hard)");
        keyCap(32, 23, '.');
        text(35, 23, GREY3, "Exit");
        flush();
        resetInput();
        while (true) {
            final int k = readNormalizedKey();
            if (k == '1' || k == '2') return k - '0';
            if (k == '.') return 0;
        }
    }

    private boolean playMatch(boolean hard) throws Exception {
        log("BATTLESHIP. New match, hard=" + hard);
        playerBoard = new Board();
        cpuBoard = new Board();
        cpuBoard.randomize(random);
        playerView = new Tracker();
        ai = new BattleshipAI(hard, random);
        revealEnemy = false;
        drawScreen();
        if (!placeFleet()) return false;

        message(MSG_Y, WHITE, "Battle stations! Fire at will.");
        message(MSG_Y + 1, GREY3, "");
        help("Aim: cursor keys or A-J, 0-9", "RETURN or SPACE: fire!", ". exit");
        text(ENEMY_X, 16, GREY3, "Target:");
        curX = SIZE / 2;
        curY = SIZE / 2;
        while (true) {
            final int[] target = aim();
            if (target == null) return false;
            playerShot(target[0], target[1]);
            if (cpuBoard.allSunk()) return endMatch(true);
            cpuShot();
            if (playerBoard.allSunk()) return endMatch(false);
        }
    }

    // ---------------------------------------------------------------- fleet placement

    private boolean placeFleet() throws IOException {
        boolean horizontal = true;
        boolean error = false;
        int index = 0;
        int shown = -1;
        curX = 0;
        curY = 0;
        placementHelp();
        while (true) {
            if (index >= playerBoard.shipCount()) {
                highlightShip(shown, -1);
                shown = -1;
                final int choice = confirmFleet();
                if (choice == '.') return false;
                if (isReturn(choice)) return true;
                if (choice == 'R') {
                    randomFleet();
                } else {
                    playerBoard.clear();
                    redrawOwnGrid();
                    drawPanelShips();
                    index = 0;
                    placementHelp();
                }
                continue;
            }
            final Ship ship = playerBoard.getShip(index);
            final int size = ship.getSize();
            if (shown != index) {
                message(MSG_Y, WHITE, "Place your " + ship.getName() + " (" + size + " squares)");
                highlightShip(shown, index);
                shown = index;
            }
            curX = Math.min(curX, horizontal ? SIZE - size : SIZE - 1);
            curY = Math.min(curY, horizontal ? SIZE - 1 : SIZE - size);
            final boolean valid = playerBoard.canPlace(size, curX, curY, horizontal);
            drawGhost(size, horizontal, valid);
            flush();

            final int k = readNormalizedKey();
            eraseGhost(size, horizontal);
            if (error) {
                message(MSG_Y + 1, GREY3, "");
                error = false;
            }
            if (k == '.') {
                return false;
            } else if (k == ' ') {
                horizontal = !horizontal;
            } else if (isReturn(k)) {
                if (valid) {
                    playerBoard.place(index, curX, curY, horizontal);
                    drawShipCells(ship);
                    drawPanelShip(index);
                    ++index;
                } else {
                    message(MSG_Y + 1, LIGHT_RED, "Ships can't overlap or touch!");
                    error = true;
                }
            } else if (k == 'R') {
                randomFleet();
                index = playerBoard.shipCount();
            } else if (k == DEL && index > 0) {
                --index;
                final Ship last = playerBoard.getShip(index);
                playerBoard.remove(index);
                drawShipCells(last);
                drawPanelShip(index);
                curX = last.getX();
                curY = last.getY();
                horizontal = last.isHorizontal();
            } else {
                moveCursor(k);
            }
        }
    }

    private void placementHelp() {
        help("Move: cursor keys or A-J, 0-9", "SPACE rotate   RETURN place ship", "R random fleet   DEL undo   . exit");
    }

    private int confirmFleet() throws IOException {
        message(MSG_Y, WHITE, "Your fleet is ready!");
        help("RETURN start the battle", "R random fleet   M manual placement", ". exit");
        flush();
        while (true) {
            final int k = readNormalizedKey();
            if (isReturn(k) || k == 'R' || k == 'M' || k == '.') return k;
        }
    }

    private void randomFleet() {
        playerBoard.randomize(random);
        redrawOwnGrid();
        drawPanelShips();
    }

    private void drawGhost(int size, boolean horizontal, boolean valid) {
        for (int i = 0; i < size; ++i) {
            moveTo(PLAYER_X + curX + (horizontal ? i : 0), GRID_Y + curY + (horizontal ? 0 : i));
            glyph(valid ? LIGHT_GREEN : LIGHT_RED, true, ' ');
        }
    }

    private void eraseGhost(int size, boolean horizontal) {
        for (int i = 0; i < size; ++i)
            drawOwnCell(curX + (horizontal ? i : 0), curY + (horizontal ? 0 : i));
    }

    // ---------------------------------------------------------------- battle

    private int[] aim() throws IOException {
        resetInput();
        while (true) {
            drawEnemyCell(curX, curY, true);
            text(ENEMY_X + 8, 16, YELLOW, coords(curX, curY));
            flush();
            final int k = readNormalizedKey();
            if (k == '.') return null;
            if (isReturn(k) || k == ' ') {
                if (playerView.get(curX, curY) == Cell.UNKNOWN) return new int[] {curX, curY};
                message(MSG_Y, GREY3, "You already know what is in " + coords(curX, curY) + ".");
                continue;
            }
            final int oldX = curX;
            final int oldY = curY;
            moveCursor(k);
            if (oldX != curX || oldY != curY) drawEnemyCell(oldX, oldY, false);
        }
    }

    private void playerShot(int x, int y) {
        final Result result = cpuBoard.fire(x, y);
        final Ship ship = cpuBoard.shipAt(x, y);
        for (int[] c : playerView.record(x, y, result, ship)) drawEnemyCell(c[0], c[1], false);
        final String where = coords(x, y);
        log("BATTLESHIP. Player fires at " + where + ": " + result);
        switch (result) {
            case HIT -> message(MSG_Y, YELLOW, "You fire at " + where + ": HIT!");
            case SUNK -> {
                message(MSG_Y, LIGHT_GREEN, where + ": you sank the " + ship.getName() + "!");
                drawPanelShip(indexOf(cpuBoard, ship));
            }
            default -> message(MSG_Y, GREY3, "You fire at " + where + ": miss.");
        }
    }

    private void cpuShot() throws InterruptedException {
        message(MSG_Y + 1, GREY3, "The CPU is aiming...");
        flush();
        Thread.sleep(700);
        final int[] target = ai.nextShot();
        final int x = target[0];
        final int y = target[1];
        final Result result = playerBoard.fire(x, y);
        final Ship ship = playerBoard.shipAt(x, y);
        ai.registerShot(x, y, result, ship);

        moveTo(PLAYER_X + x, GRID_Y + y);
        glyph(YELLOW, true, ' ');
        flush();
        Thread.sleep(300);
        if (ship == null) drawOwnCell(x, y);
        else {
            drawShipCells(ship);
            drawPanelShip(indexOf(playerBoard, ship));
        }

        final String where = coords(x, y);
        log("BATTLESHIP. CPU fires at " + where + ": " + result);
        switch (result) {
            case HIT -> message(MSG_Y + 1, LIGHT_RED, "The CPU fires at " + where + ": HIT!");
            case SUNK -> message(MSG_Y + 1, LIGHT_RED, where + ": the CPU sank your " + ship.getName() + "!");
            default -> message(MSG_Y + 1, GREY3, "The CPU fires at " + where + ": miss.");
        }
    }

    private boolean endMatch(boolean won) throws IOException {
        log("BATTLESHIP. Match ended, winner=" + (won ? "human" : "cpu")
                + ", shots=" + (won ? cpuBoard : playerBoard).getShotsFired());
        if (won) {
            message(MSG_Y, YELLOW, "VICTORY! The enemy fleet is sunk.");
            message(MSG_Y + 1, WHITE, "You won with " + cpuBoard.getShotsFired() + " shots.");
        } else {
            revealEnemy = true;
            redrawEnemyGrid();
            message(MSG_Y, LIGHT_RED, "DEFEAT! Your fleet is sunk.");
            message(MSG_Y + 1, WHITE, "The CPU won with " + playerBoard.getShotsFired() + " shots.");
        }
        help("Play again? (Y/N)", "", "");
        flush();
        resetInput();
        while (true) {
            final int k = readNormalizedKey();
            if (k == 'Y') return true;
            if (k == 'N' || k == '.') return false;
        }
    }

    // ---------------------------------------------------------------- screen

    // drawn row by row, left to right, to keep cursor movements (and bytes sent) to a minimum
    private void drawScreen() {
        clearScreen();
        drawTitle(0);
        text(PLAYER_X, 2, WHITE, "Your fleet");
        text(ENEMY_X, 2, WHITE, "CPU fleet");
        text(PLAYER_X, GRID_Y - 2, CYAN, "ABCDEFGHIJ");
        text(ENEMY_X, GRID_Y - 2, CYAN, "ABCDEFGHIJ");
        text(PANEL_X, GRID_Y - 2, LIGHT_GREEN, "You");
        text(PANEL_X + 6, GRID_Y - 2, LIGHT_RED, "CPU");
        drawBorders(GRID_Y - 1, 176, 174);
        for (int y = 0; y < SIZE; ++y) {
            text(PLAYER_X - 2, GRID_Y + y, CYAN, String.valueOf(y));
            glyph(LIGHT_BLUE, false, 221);
            for (int x = 0; x < SIZE; ++x) drawOwnCell(x, y);
            glyph(LIGHT_BLUE, false, 221);
            text(ENEMY_X - 2, GRID_Y + y, CYAN, String.valueOf(y));
            glyph(LIGHT_BLUE, false, 221);
            for (int x = 0; x < SIZE; ++x) drawEnemyCell(x, y, false);
            glyph(LIGHT_BLUE, false, 221);
            if (y / 2 >= playerBoard.shipCount()) continue;
            if (y % 2 == 0) text(PANEL_X, GRID_Y + y, GREY3, playerBoard.getShip(y / 2).getName());
            else drawPanelShip(y / 2);
        }
        drawBorders(GRID_Y + SIZE, 173, 189);
    }

    private void drawBorders(int y, int leftCorner, int rightCorner) {
        for (int left : new int[] {PLAYER_X, ENEMY_X}) {
            moveTo(left - 1, y);
            glyph(LIGHT_BLUE, false, leftCorner);
            for (int i = 0; i < SIZE; ++i) glyph(LIGHT_BLUE, false, 192);
            glyph(LIGHT_BLUE, false, rightCorner);
        }
    }

    private void redrawOwnGrid() {
        for (int y = 0; y < SIZE; ++y) for (int x = 0; x < SIZE; ++x) drawOwnCell(x, y);
    }

    private void redrawEnemyGrid() {
        for (int y = 0; y < SIZE; ++y) for (int x = 0; x < SIZE; ++x) drawEnemyCell(x, y, false);
    }

    private void drawShipCells(Ship ship) {
        for (int i = 0; i < ship.getSize(); ++i) drawOwnCell(ship.cellX(i), ship.cellY(i));
    }

    private void drawOwnCell(int x, int y) {
        moveTo(PLAYER_X + x, GRID_Y + y);
        final Ship ship = playerBoard.shipAt(x, y);
        final boolean shot = playerBoard.isShot(x, y);
        if (ship == null) glyph(shot ? WHITE : BLUE, false, shot ? MISS_CHAR : WATER_CHAR);
        else if (!shot) glyph(GREY3, true, ' ');
        else glyph(ship.isSunk() ? GREY2 : RED, true, HIT_CHAR);
    }

    private void drawEnemyCell(int x, int y, boolean cursor) {
        moveTo(ENEMY_X + x, GRID_Y + y);
        final Cell cell = playerView.get(x, y);
        if (cursor) {
            glyph(YELLOW, true, switch (cell) {
                case MISS -> MISS_CHAR;
                case HIT, SUNK -> HIT_CHAR;
                default -> ' ';
            });
            return;
        }
        switch (cell) {
            case MISS -> glyph(WHITE, false, MISS_CHAR);
            case CLEAR -> glyph(BLUE, false, ' ');
            case HIT -> glyph(RED, true, HIT_CHAR);
            case SUNK -> glyph(GREY2, true, HIT_CHAR);
            default -> {
                if (revealEnemy && cpuBoard.shipAt(x, y) != null) glyph(GREY3, true, ' ');
                else glyph(BLUE, false, WATER_CHAR);
            }
        }
    }

    private void drawPanelShips() {
        for (int i = 0; i < playerBoard.shipCount(); ++i) drawPanelShip(i);
    }

    // own ship shows damaged sections, enemy ship shows only if it's sunk
    private void drawPanelShip(int index) {
        final Ship own = playerBoard.getShip(index);
        moveTo(PANEL_X, GRID_Y + 2 * index + 1);
        for (int i = 0; i < own.getSize(); ++i) {
            if (!own.isPlaced()) glyph(GREY1, true, ' ');
            else if (own.isSunk()) glyph(GREY2, true, HIT_CHAR);
            else if (playerBoard.isShot(own.cellX(i), own.cellY(i))) glyph(RED, true, HIT_CHAR);
            else glyph(GREY3, true, ' ');
        }
        final Ship enemy = cpuBoard.getShip(index);
        moveTo(PANEL_X + 6, GRID_Y + 2 * index + 1);
        for (int i = 0; i < enemy.getSize(); ++i) {
            if (enemy.isSunk()) glyph(GREY2, true, HIT_CHAR);
            else glyph(GREY3, true, ' ');
        }
    }

    private void highlightShip(int previous, int current) {
        if (previous >= 0) text(PANEL_X, GRID_Y + 2 * previous, GREY3, playerBoard.getShip(previous).getName());
        if (current >= 0) text(PANEL_X, GRID_Y + 2 * current, YELLOW, playerBoard.getShip(current).getName());
    }

    private void drawTitle(int y) {
        moveTo(0, y);
        setColor(LIGHT_BLUE);
        setRev(true);
        print(rightPad("          B A T T L E S H I P", 39));
        posX += 39;
    }

    private void drawLogo(int top) {
        for (int row = 0; row < LOGO.length; ++row) {
            final String line = LOGO[row];
            for (int i = 0; i < line.length(); ++i) {
                final char c = line.charAt(i);
                if (c == ' ') continue;
                moveTo(i, top + row);
                switch (c) {
                    case '#' -> glyph(LOGO_COLORS[row], true, ' ');
                    case 'L' -> glyph(LOGO_COLORS[row], false, 162); // lower half
                    case 'U' -> glyph(LOGO_COLORS[row], true, 162);  // upper half
                    case '[' -> glyph(LOGO_COLORS[row], false, 161); // left half
                    case ']' -> glyph(LOGO_COLORS[row], true, 161);  // right half
                    case 'c' -> glyph(LOGO_COLORS[row], false, 187); // lower left quadrant
                    case 'd' -> glyph(LOGO_COLORS[row], false, 172); // lower right quadrant
                    case 'C' -> glyph(LOGO_COLORS[row], true, 187);  // all but lower left
                    case 'D' -> glyph(LOGO_COLORS[row], true, 172);  // all but lower right
                    default -> glyph(LOGO_COLORS[row], false, WATER_CHAR);
                }
            }
        }
    }

    private void keyCap(int x, int y, char key) {
        moveTo(x, y);
        setColor(GREY3);
        setRev(true);
        write(161, key);
        setRev(false);
        write(161);
        posX += 3;
    }

    private void message(int y, int color, String s) {
        final int length = Math.max(s.length(), lineLength[y]);
        lineLength[y] = s.length();
        if (length > 0) text(1, y, color, rightPad(s, length));
    }

    private void help(String... lines) {
        for (int i = 0; i < lines.length; ++i) message(HELP_Y + i, GREY3, lines[i]);
    }

    // ---------------------------------------------------------------- low level output

    private void clearScreen() {
        write(CLR, LOWERCASE, CASE_LOCK);
        posX = 0;
        posY = 0;
        lastColor = -1;
        lastRev = -1;
        Arrays.fill(lineLength, 0);
    }

    private void moveTo(int x, int y) {
        if (posX < 0 || posY < 0 || Math.abs(x - posX) + Math.abs(y - posY) > x + y + 1) {
            write(HOME);
            posX = 0;
            posY = 0;
        }
        for (; posY < y; ++posY) write(DOWN);
        for (; posY > y; --posY) write(UP);
        for (; posX < x; ++posX) write(RIGHT);
        for (; posX > x; --posX) write(LEFT);
    }

    private void setColor(int color) {
        if (color != lastColor) write(color);
        lastColor = color;
    }

    private void setRev(boolean rev) {
        if ((rev ? 1 : 0) != lastRev) write(rev ? REVON : REVOFF);
        lastRev = rev ? 1 : 0;
    }

    private void glyph(int color, boolean rev, int ch) {
        if (rev || ch != ' ') setColor(color);
        setRev(rev);
        write(ch);
        ++posX;
    }

    private void text(int x, int y, int color, String s) {
        moveTo(x, y);
        setRev(false);
        setColor(color);
        print(s);
        posX += s.length();
    }

    // ---------------------------------------------------------------- input

    private int readNormalizedKey() throws IOException {
        final int k = readKey();
        return (k >= 'a' && k <= 'z') ? k - 32 : k;
    }

    private static boolean isReturn(int k) {
        return k == RETURN || k == 141 || k == 10;
    }

    private void moveCursor(int k) {
        switch (k) {
            case UP -> curY = Math.max(0, curY - 1);
            case DOWN -> curY = Math.min(SIZE - 1, curY + 1);
            case LEFT -> curX = Math.max(0, curX - 1);
            case RIGHT -> curX = Math.min(SIZE - 1, curX + 1);
            default -> {
                if (k >= 'A' && k < 'A' + SIZE) curX = k - 'A';
                else if (k >= '0' && k <= '9') curY = k - '0';
            }
        }
    }

    private static String coords(int x, int y) {
        return (char) ('A' + x) + String.valueOf(y);
    }

    private static int indexOf(Board board, Ship ship) {
        for (int i = 0; i < board.shipCount(); ++i) if (board.getShip(i) == ship) return i;
        return -1;
    }
}
