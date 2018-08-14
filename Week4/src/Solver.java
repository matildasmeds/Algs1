import java.util.Comparator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

// Solver uses best-first search algorithm A*, to solve the puzzle in smallest
// possible number of moves.
//
// http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
// http://coursera.cs.princeton.edu/algs4/checklists/8puzzle.html

public final class Solver {
    private boolean isSolvable;
    private int moves = -1;
    private Stack<Board> solution = new Stack<Board>();

    // SearchNode represents a particular composition of tiles, that can be
    // reached from the initial board. SearchNode keeps track of predecessor,
    // so that already processed board is not processed twice, actual board
    // composition, and number of moves needed to reach this board.
    private class SearchNode {
        Board board;
        SearchNode predecessor;
        int moves;
        int manhattan;

        SearchNode(Board board, SearchNode predecessor, int moves) {
            this.board = board;
            this.predecessor = predecessor;
            this.moves = moves;
            manhattan = board.manhattan();
        }

        int priority() {
            return moves + manhattan;
        }
    }

    // SearchNodeComparator calculates comparative priority for two
    // search nodes. Needed for Priority Queue.
    private class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode a, SearchNode b) {
            return a.priority() - b.priority();
        }
    }

    // Solver class has two priority queues, one for initial board, and
    // another for its twin board. If a board is unsolvable, interestingly its
    // twin board is solvable, and vice versa.
    // 
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }

        Comparator<SearchNode> comparator = new SearchNodeComparator();
        MinPQ<SearchNode> queue = new MinPQ<SearchNode>(comparator);
        MinPQ<SearchNode> twinQ = new MinPQ<SearchNode>(comparator);
        SearchNode node, twin;
        queue.insert(new SearchNode(initial, null, 0));
        twinQ.insert(new SearchNode(initial.twin(), null, 0));

        // Try to solve both initial board and twin board. 
        // Only one can be solved.
        while (true) {
            node = queue.delMin();
            twin = twinQ.delMin();
            if (node.board.isGoal()) {
                isSolvable = true;
                break;
            } else if (twin.board.isGoal()) {
                isSolvable = false;
                break;
            }
            insertNeighbors(node, queue);
            insertNeighbors(twin, twinQ);
        }

        if (!isSolvable) {
            return;
        }
        
        // Construct solution stack, beginning from solved board
        while (true) {
            if (node == null) {
                break;
            }
            solution.push(node.board);
            node = node.predecessor;
        }

        moves = solution.size() - 1;
    }

    // Inserts neighbor boards to priority queue
    private void insertNeighbors(SearchNode node, MinPQ<SearchNode> queue) {
        Iterable<Board> neighbors = node.board.neighbors();
        for (Board neighbor : neighbors) {
            // don't enqueue preceding board
            if (node.predecessor != null 
                    && neighbor.equals(node.predecessor.board)) {
                continue;
            }
            SearchNode newNode = new SearchNode(neighbor, node, node.moves + 1);
            queue.insert(newNode);
        }
    }

    // Is initial board solvable?
    public boolean isSolvable() {
        return isSolvable;
    }

    // Number of moves to solution
    public int moves() {
        return moves;
    }

    // Sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (solution.isEmpty()) {
            return null;
        }
        return solution;
    } 

    // Use this with test files provided at 
    // http://coursera.cs.princeton.edu/algs4/checklists/8puzzle.html
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }

        Board initial = new Board(blocks);
        System.out.println(initial.toString());
        System.out.println("--");

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }
}