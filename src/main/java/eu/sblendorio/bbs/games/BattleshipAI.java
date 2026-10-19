package eu.sblendorio.bbs.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Battleship game model and computer player.
 * Rules: 10x10 sea, fleet of 5 ships (5, 4, 3, 3, 2), ships cannot touch each other (not even diagonally).
 */
public class BattleshipAI {

    public static final int SIZE = 10;
    public static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};
    public static final String[] SHIP_NAMES = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};

    public enum Result { INVALID, MISS, HIT, SUNK }

    // What a player knows about the opponent's sea: CLEAR is water deduced around a sunk ship
    public enum Cell { UNKNOWN, MISS, CLEAR, HIT, SUNK }

    public static class Ship {
        private final String name;
        private final int size;
        private int x;
        private int y;
        private boolean horizontal;
        private boolean placed;
        private int hits;

        public Ship(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public String getName() { return name; }
        public int getSize() { return size; }
        public int getX() { return x; }
        public int getY() { return y; }
        public boolean isHorizontal() { return horizontal; }
        public boolean isPlaced() { return placed; }
        public int getHits() { return hits; }
        public boolean isSunk() { return hits >= size; }
        public int cellX(int i) { return horizontal ? x + i : x; }
        public int cellY(int i) { return horizontal ? y : y + i; }

        // index of the ship section at (cx, cy), -1 if not covered
        public int sectionAt(int cx, int cy) {
            for (int i = 0; i < size; ++i) if (cellX(i) == cx && cellY(i) == cy) return i;
            return -1;
        }
    }

    public static class Board {
        private final Ship[] ships = new Ship[SHIP_SIZES.length];
        private final int[][] shipAt = new int[SIZE][SIZE];
        private final boolean[][] shot = new boolean[SIZE][SIZE];
        private int shotsFired;

        public Board() {
            for (int i = 0; i < ships.length; ++i) ships[i] = new Ship(SHIP_NAMES[i], SHIP_SIZES[i]);
            clear();
        }

        public void clear() {
            for (int x = 0; x < SIZE; ++x) for (int y = 0; y < SIZE; ++y) {
                shipAt[x][y] = -1;
                shot[x][y] = false;
            }
            for (Ship s : ships) {
                s.placed = false;
                s.hits = 0;
            }
            shotsFired = 0;
        }

        public int shipCount() { return ships.length; }
        public Ship getShip(int index) { return ships[index]; }

        public Ship shipAt(int x, int y) {
            return inside(x, y) && shipAt[x][y] >= 0 ? ships[shipAt[x][y]] : null;
        }

        public boolean isShot(int x, int y) { return shot[x][y]; }
        public int getShotsFired() { return shotsFired; }

        public boolean canPlace(int size, int x, int y, boolean horizontal) {
            final int x2 = horizontal ? x + size - 1 : x;
            final int y2 = horizontal ? y : y + size - 1;
            if (!inside(x, y) || !inside(x2, y2)) return false;
            for (int i = x - 1; i <= x2 + 1; ++i)
                for (int j = y - 1; j <= y2 + 1; ++j)
                    if (inside(i, j) && shipAt[i][j] >= 0) return false;
            return true;
        }

        public boolean place(int index, int x, int y, boolean horizontal) {
            final Ship s = ships[index];
            if (s.placed || !canPlace(s.size, x, y, horizontal)) return false;
            s.x = x;
            s.y = y;
            s.horizontal = horizontal;
            s.placed = true;
            for (int i = 0; i < s.size; ++i) shipAt[s.cellX(i)][s.cellY(i)] = index;
            return true;
        }

        public void remove(int index) {
            final Ship s = ships[index];
            if (!s.placed) return;
            for (int i = 0; i < s.size; ++i) shipAt[s.cellX(i)][s.cellY(i)] = -1;
            s.placed = false;
        }

        public void randomize(Random random) {
            boolean done;
            do {
                clear();
                done = true;
                for (int i = 0; i < ships.length && done; ++i) {
                    int tries = 0;
                    while (!place(i, random.nextInt(SIZE), random.nextInt(SIZE), random.nextBoolean()) && ++tries < 500);
                    done = ships[i].placed;
                }
            } while (!done);
        }

        public Result fire(int x, int y) {
            if (!inside(x, y) || shot[x][y]) return Result.INVALID;
            shot[x][y] = true;
            ++shotsFired;
            final Ship s = shipAt(x, y);
            if (s == null) return Result.MISS;
            ++s.hits;
            return s.isSunk() ? Result.SUNK : Result.HIT;
        }

        public boolean allSunk() {
            for (Ship s : ships) if (!s.isSunk()) return false;
            return true;
        }
    }

    public static class Tracker {
        private final Cell[][] cells = new Cell[SIZE][SIZE];

        public Tracker() {
            for (int x = 0; x < SIZE; ++x) for (int y = 0; y < SIZE; ++y) cells[x][y] = Cell.UNKNOWN;
        }

        public Cell get(int x, int y) { return cells[x][y]; }

        // records the outcome of a shot, returns the list of changed cells as {x, y}
        public List<int[]> record(int x, int y, Result result, Ship sunk) {
            final List<int[]> changed = new ArrayList<>();
            if (result == Result.MISS) {
                cells[x][y] = Cell.MISS;
                changed.add(new int[] {x, y});
            } else if (result == Result.HIT) {
                cells[x][y] = Cell.HIT;
                changed.add(new int[] {x, y});
            } else if (result == Result.SUNK) {
                for (int i = 0; i < sunk.size; ++i) {
                    cells[sunk.cellX(i)][sunk.cellY(i)] = Cell.SUNK;
                    changed.add(new int[] {sunk.cellX(i), sunk.cellY(i)});
                }
                for (int i = 0; i < sunk.size; ++i)
                    for (int dx = -1; dx <= 1; ++dx)
                        for (int dy = -1; dy <= 1; ++dy) {
                            final int cx = sunk.cellX(i) + dx;
                            final int cy = sunk.cellY(i) + dy;
                            if (inside(cx, cy) && cells[cx][cy] == Cell.UNKNOWN) {
                                cells[cx][cy] = Cell.CLEAR;
                                changed.add(new int[] {cx, cy});
                            }
                        }
            }
            return changed;
        }
    }

    private final Tracker tracker = new Tracker();
    private final List<Integer> remaining = new ArrayList<>();
    private final boolean hard;
    private final Random random;

    public BattleshipAI(boolean hard, Random random) {
        this.hard = hard;
        this.random = random;
        for (int size : SHIP_SIZES) remaining.add(size);
    }

    public void registerShot(int x, int y, Result result, Ship sunk) {
        if (result == Result.SUNK) remaining.remove(Integer.valueOf(sunk.size));
        tracker.record(x, y, result, sunk);
    }

    /*
     * Probability density: every placement of a ship still afloat that is consistent with the known
     * cells adds weight to the cells it covers. With unresolved hits only placements covering
     * them are counted ("target" mode). The hard player shoots at the most likely cell, the easy
     * one shoots at random, or at a plausible cell next to a hit in target mode.
     */
    public int[] nextShot() {
        boolean target = false;
        for (int x = 0; x < SIZE; ++x) for (int y = 0; y < SIZE; ++y) target |= tracker.get(x, y) == Cell.HIT;

        final long[][] score = new long[SIZE][SIZE];
        for (int size : remaining)
            for (int dir = 0; dir < 2; ++dir)
                for (int x = 0; x < SIZE; ++x)
                    for (int y = 0; y < SIZE; ++y) {
                        final int hits = placementHits(size, x, y, dir == 0);
                        if (hits < 0 || (target && hits == 0)) continue;
                        for (int i = 0; i < size; ++i) {
                            final int cx = dir == 0 ? x + i : x;
                            final int cy = dir == 0 ? y : y + i;
                            if (tracker.get(cx, cy) == Cell.UNKNOWN) score[cx][cy] += 1L + 10L * hits * hits;
                        }
                    }

        final List<int[]> candidates = new ArrayList<>();
        long best = 0;
        for (int x = 0; x < SIZE; ++x)
            for (int y = 0; y < SIZE; ++y) {
                final Cell cell = tracker.get(x, y);
                final long value;
                if (hard) value = score[x][y];
                else if (target) value = score[x][y] > 0 && nextToHit(x, y) ? 1 : 0;
                else value = cell == Cell.UNKNOWN || cell == Cell.CLEAR ? 1 : 0; // the cadet doesn't deduce while hunting
                if (value <= 0) continue;
                if (value > best) {
                    best = value;
                    candidates.clear();
                }
                if (value == best) candidates.add(new int[] {x, y});
            }

        if (candidates.isEmpty())
            for (int x = 0; x < SIZE; ++x)
                for (int y = 0; y < SIZE; ++y)
                    if (tracker.get(x, y) == Cell.UNKNOWN) candidates.add(new int[] {x, y});
        return candidates.isEmpty() ? new int[] {0, 0} : candidates.get(random.nextInt(candidates.size()));
    }

    // number of hits covered by a valid placement, -1 if the placement is impossible
    private int placementHits(int size, int x, int y, boolean horizontal) {
        final int x2 = horizontal ? x + size - 1 : x;
        final int y2 = horizontal ? y : y + size - 1;
        if (!inside(x2, y2)) return -1;
        int hits = 0;
        for (int i = x - 1; i <= x2 + 1; ++i)
            for (int j = y - 1; j <= y2 + 1; ++j) {
                if (!inside(i, j)) continue;
                final Cell c = tracker.get(i, j);
                final boolean covered = i >= x && i <= x2 && j >= y && j <= y2;
                if (covered && c == Cell.HIT) ++hits;
                else if (covered && c != Cell.UNKNOWN) return -1;
                else if (!covered && (c == Cell.HIT || c == Cell.SUNK)) return -1;
            }
        return hits;
    }

    private boolean nextToHit(int x, int y) {
        return (inside(x - 1, y) && tracker.get(x - 1, y) == Cell.HIT)
                || (inside(x + 1, y) && tracker.get(x + 1, y) == Cell.HIT)
                || (inside(x, y - 1) && tracker.get(x, y - 1) == Cell.HIT)
                || (inside(x, y + 1) && tracker.get(x, y + 1) == Cell.HIT);
    }

    private static boolean inside(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }
}
