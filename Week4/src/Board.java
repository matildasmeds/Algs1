import edu.princeton.cs.algs4.Stack;

// 8-puzzle is a sliding puzzle, that consists of 8 numbered square tiles, and
// one empty slot, in random order. (https://en.wikipedia.org/wiki/15_puzzle)
//
// Board class represents one composition of square tiles. 0 marks the empty
// slot. Board can be used for a similar square puzzle of any size.
//
// http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
// http://coursera.cs.princeton.edu/algs4/checklists/8puzzle.html

public final class Board {
    private final int n;
    private final int[][] blocks;
    private final boolean isGoal;

    public Board(int[][] blocks) {
        n = blocks.length;
        this.blocks = deepCopyBlocks(blocks);
        isGoal = isGoal();
    }

    public int dimension() {
        return n;
    }

    public int hamming() {
        if (isGoal) {
            return 0;
        }
        int score = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] == 0) {
                    continue; // don't count empty tile
                }
                if (blocks[i][j] != goalValue(i, j)) {
                    score++;
                }
            }
        }
        return score;
    }

    // Manhattan distance is the sum of vertical and horizontal distances from
    // the blocks to their goal positions.
    public int manhattan() {
        if (isGoal) {
            return 0;
        }
        int score = 0;
        int colShouldBe, rowShouldBe, val;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                val = blocks[i][j];
                if (val == 0) {
                    continue;
                }
                if (val == goalValue(i, j)) {
                    continue;
                }
                rowShouldBe = (int) Math.floor(val / n);
                if (val % n == 0) {
                    rowShouldBe--;
                }
                colShouldBe = val - 1 - rowShouldBe * n;
                score += Math.abs(rowShouldBe - i);
                score += Math.abs(colShouldBe - j);
            }
        }
        return score;
    }

    public boolean isGoal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] != goalValue(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int goalValue(int i, int j) {
        if (i == n - 1 && j == n - 1) {
            return 0;
        }
        return i * n + j + 1;
    }

    // Twin board is constructed by swapping (direct exchange, no sliding) two
    // neighbor tiles. Twin board is used to check if a board can be solved or
    // not, and if not, search can be stopped. If initial board can be solved,
    // twin board is unsolvable, and vice versa.
    public Board twin() {
        int[][] copy = deepCopyBlocks();
        // Figure out which tiles to swap
        int[] a = new int[2];
        int[] b = new int[2];
        // Empty slot, marked by 0, cannot be part of swap.
        if (copy[0][0] == 0) {
            a[0] = 0;
            a[1] = 1;
            b[0] = 1;
            b[1] = 1;
        } else if (copy[0][1] == 0) {
            a[0] = 0;
            a[1] = 0;
            b[0] = 1;
            b[1] = 0;
        } else {
            a[0] = 0;
            a[1] = 0;
            b[0] = 0;
            b[1] = 1;
        }
        // Swap tiles
        int temp = copy[a[0]][a[1]];
        copy[a[0]][a[1]] = copy[b[0]][b[1]];
        copy[b[0]][b[1]] = temp;
        return new Board(copy);
    }

    private int[][] deepCopyBlocks() {
        return deepCopyBlocks(blocks);
    }

    private int[][] deepCopyBlocks(final int[][] blocks) {
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                copy[i][j] = blocks[i][j];
            }
        }
        return copy;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final Board that = (Board) o;
        if (n != that.n) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] != that.blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        Stack<Board> stack = new Stack<Board>();
        int[][] left, right, top, bottom;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] != 0) {
                    continue;
                }
                // get left neighbor
                if (j > 0) {
                    left = deepCopyBlocks();
                    left[i][j] = left[i][j - 1];
                    left[i][j - 1] = 0;
                    stack.push(new Board(left));
                }
                // get right neighbor
                if (j < n - 1) {
                    right = deepCopyBlocks();
                    right[i][j] = right[i][j + 1];
                    right[i][j + 1] = 0;
                    stack.push(new Board(right));
                }
                // get top neighbor
                if (i > 0) {
                    top = deepCopyBlocks();
                    top[i][j] = top[i - 1][j];
                    top[i - 1][j] = 0;
                    stack.push(new Board(top));
                }
                // get bottom neighbor
                if (i < n - 1) {
                    bottom = deepCopyBlocks();
                    bottom[i][j] = bottom[i + 1][j];
                    bottom[i + 1][j] = 0;
                    stack.push(new Board(bottom));
                }
            }
        }
        return stack;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(n);
        for (int i = 0; i < n; i++) {
            str.append("\n");
            for (int j = 0; j < n; j++) {
                str.append(" ");
                str.append(blocks[i][j]);
            }
        }
        return str.toString();
    }
}
