package eu.sblendorio.bbs.games;

import eu.sblendorio.bbs.games.BattleshipAI.Board;
import eu.sblendorio.bbs.games.BattleshipAI.Cell;
import eu.sblendorio.bbs.games.BattleshipAI.Result;
import eu.sblendorio.bbs.games.BattleshipAI.Ship;
import eu.sblendorio.bbs.games.BattleshipAI.Tracker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static eu.sblendorio.bbs.games.BattleshipAI.SIZE;
import static org.junit.jupiter.api.Assertions.*;

class BattleshipAITest {

    @Test
    @DisplayName("Ships cannot overlap or touch each other, not even diagonally")
    void shipsCannotTouch() {
        Board board = new Board();
        assertTrue(board.place(0, 0, 0, true));
        assertFalse(board.canPlace(4, 0, 0, false), "overlapping");
        assertFalse(board.canPlace(4, 5, 1, true), "touching diagonally");
        assertFalse(board.canPlace(4, 0, 1, true), "touching side by side");
        assertTrue(board.canPlace(4, 0, 2, true));
        assertFalse(board.canPlace(4, 7, 5, true), "out of the sea");
    }

    @Test
    @DisplayName("Firing reports miss, hit, sunk and refuses repeated shots")
    void fireResults() {
        Board board = new Board();
        board.place(4, 3, 3, false); // destroyer on D3-D4
        assertEquals(Result.MISS, board.fire(0, 0));
        assertEquals(Result.INVALID, board.fire(0, 0));
        assertEquals(Result.HIT, board.fire(3, 3));
        assertEquals(Result.SUNK, board.fire(3, 4));
        assertEquals(3, board.getShotsFired());
    }

    @Test
    @DisplayName("Sinking a ship marks the surrounding water as clear")
    void sunkShipClearsSurroundings() {
        Board board = new Board();
        board.place(4, 3, 3, false);
        Tracker tracker = new Tracker();
        tracker.record(3, 3, board.fire(3, 3), board.shipAt(3, 3));
        tracker.record(3, 4, board.fire(3, 4), board.shipAt(3, 4));
        assertEquals(Cell.SUNK, tracker.get(3, 3));
        assertEquals(Cell.SUNK, tracker.get(3, 4));
        assertEquals(Cell.CLEAR, tracker.get(2, 2));
        assertEquals(Cell.CLEAR, tracker.get(4, 5));
        assertEquals(Cell.UNKNOWN, tracker.get(5, 5));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    @DisplayName("The computer sinks a random fleet without ever repeating a shot")
    void computerSinksWholeFleet(boolean hard) {
        Random random = new Random(1234);
        for (int game = 0; game < 200; ++game) {
            Board board = new Board();
            board.randomize(random);
            for (int i = 0; i < board.shipCount(); ++i) assertTrue(board.getShip(i).isPlaced());
            BattleshipAI ai = new BattleshipAI(hard, random);
            int shots = 0;
            while (!board.allSunk()) {
                int[] target = ai.nextShot();
                Result result = board.fire(target[0], target[1]);
                assertNotEquals(Result.INVALID, result);
                Ship ship = board.shipAt(target[0], target[1]);
                ai.registerShot(target[0], target[1], result, ship);
                assertTrue(++shots <= SIZE * SIZE);
            }
        }
    }
}
