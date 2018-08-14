import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Class Percolation
 *
 * First programming assignment of "Algorithms, Part 1" MOOC, as offered by
 * Princeton University on Coursera. LINK
 *
 * Percolation refers to the movement or filtration of fluids through porous
 * materials. Additionally percolation systems can approximate electric
 * conductivity, social connectivity, etc.
 *
 * Percolation system is modeled using an n-by-n grid of sites. Each site is
 * either open or blocked. A full site is an open site that can be connected to
 * an open site in the top row via a chain of neighboring (left, right, up,
 * down) open sites. We say the system percolates if there is a full site in the
 * bottom row.
 *
 * Percolation class uses Union-Find data structure to model a percolation
 * system, and its operations. Data structure must answer if two sites are
 * dynamically connected, exact route between two sites does not matter in this
 * case.
 *
 *
 * Assignment Instructions:
 * http://coursera.cs.princeton.edu/algs4/assignments/percolation.html
 * http://coursera.cs.princeton.edu/algs4/checklists/percolation.html
 *
 * 'algs4.jar' Package and API documentation:
 * https://algs4.cs.princeton.edu/code/
 *
 * Percolation as phenomenon: https://en.wikipedia.org/wiki/Percolation
 *
 * @see PercolationStats
 * @see WeightedQuickUnionUF
 */
public class Percolation {

    // Expresses illegal index values
    private static final int INDEX_OUT_OF_BOUNDS = -1;

    // Size of n*n grid
    private final int n;

    // Number of total sites: n*n
    private final int totalSites;

    // System percolates when any top element is connected to any bottom
    // element. Virtual top and bottom elements make this check faster.
    // Virtual top element is connected to all sites in top row.
    private final int topElement;

    // Virtual bottom element, that is connected to all sites in bottom row.
    private final int bottomElement;

    // WeightedQuickUnionUF is used to track connections between sites. The
    // first n items represent totalSites, and the last two topElement and
    // bottomElement.
    private WeightedQuickUnionUF connectedSites;

    // n-size array, that tracks if site at index is open or not.
    private boolean[] openedSites;

    private int numberOfOpenSites;

    /**
     * Instantiates a percolation object.
     *
     * @param n
     *            Size parameter for n*n grid.
     */
    public Percolation(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }
        this.n = n;
        totalSites = n * n;
        topElement = totalSites;
        bottomElement = totalSites + 1;
        connectedSites = new WeightedQuickUnionUF(totalSites + 2);
        openedSites = new boolean[totalSites];
        numberOfOpenSites = 0;
    }

    /**
     * Open site, if it is not already opened. Connect site to open neighbor
     * sites.
     *
     * @param row
     *            Row index from 0 to n-1, in n*n grid
     * @param col
     *            Column index from 0 to n-1, in n*n grid
     */
    public void open(final int row, final int col) {
        if (argsOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        if (isOpen(row, col)) {
            return;
        }

        int site = indexFromXY(row, col);
        openedSites[site] = true;
        numberOfOpenSites += 1;

        if (isTopRow(site)) {
            connectedSites.union(site, topElement);
        }
        if (isBottomRow(site)) {
            connectedSites.union(site, bottomElement);
        }

        int[] neighbors = getNeighbors(row, col);
        for (int neighbor : neighbors) {
            if (neighbor == INDEX_OUT_OF_BOUNDS) {
                continue;
            }
            if (openedSites[neighbor]) {
                connectedSites.union(site, neighbor);
            }
        }
    }

    /**
     * Percolates.
     *
     * @return true, if n*n grid percolates
     */
    public boolean percolates() {
        return connectedSites.connected(topElement, bottomElement);
    }

    /**
     * Checks if site is open.
     *
     * @param row
     *            Row index from 0 to n-1, in n*n grid
     * @param col
     *            Column index from 0 to n-1, in n*n grid
     * @return true, if is open
     */
    public boolean isOpen(final int row, final int col) {
        if (argsOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        return openedSites[indexFromXY(row, col)];
    }

    /**
     * Number of open sites.
     *
     * @return number of open sites
     */
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    /**
     * Can be used to visualize percolation from top to bottom. Current
     * implementation back washes, i.e. also visualized a flow from an open
     * and full site, to open sites above it, which is not ideal.
     *
     * @param row
     *            Row index from 0 to n-1, in n*n grid
     * @param col
     *            Column index from 0 to n-1, in n*n grid
     * @return true, if is full
     */
    public boolean isFull(final int row, final int col) {
        if (argsOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        return connectedSites.connected(topElement, indexFromXY(row, col));
    }

    /**
     * Checks if site is situated in top row.
     *
     * @param index
     *            index from 0 to n-1
     * @return true, if site is in top row
     */
    private boolean isTopRow(final int index) {
        return 0 <= index && index < n;
    }

    /**
     * Checks if site is situated in top row.
     *
     * @param index
     *            index from 0 to n-1
     * @return true, if is bottom row
     */
    private boolean isBottomRow(final int index) {
        return totalSites - n <= index && index < totalSites;
    }

    /**
     * Returns neighboring indices. Caller must check for INDEX_OUT_OF_BOUNDS.
     * This is faster, than filtering out results, or using other array types.
     *
     * @param row
     *            Row index from 0 to n-1
     * @param col
     *            Column index from 0 to n-1
     * @return indices for neighboring sites, caller must check for
     *         INDEX_OUT_OF_BOUNDS
     */
    private int[] getNeighbors(final int row, final int col) {
        int[] neighbors = new int[4];
        neighbors[0] = indexFromXY(row, col - 1);
        neighbors[1] = indexFromXY(row, col + 1);
        neighbors[2] = indexFromXY(row - 1, col);
        neighbors[3] = indexFromXY(row + 1, col);
        return neighbors;
    }

    /**
     * Returns array index for row index and column index. Caller must check
     * for INDEX_OUT_OF_BOUNDS.
     *
     * @param row
     *            Row index from 0 to n-1
     * @param col
     *            Column index from 0 to n-1
     * @return index, either a legal index of INDEX_OUT_OF_BOUNDS
     */
    private int indexFromXY(final int row, final int col) {
        if (argsOutOfBounds(row, col)) {
            return INDEX_OUT_OF_BOUNDS;
        }
        return (row - 1) * n + (col - 1);
    }

    /**
     * Simple helper to check if row and column indices are legal.
     *
     * @param row
     *            Row index
     * @param col
     *            Column index
     * @return true, if indices outside of n*n grid
     */
    private boolean argsOutOfBounds(final int row, final int col) {
        return (row < 1 || row > n || col < 1 || col > n);
    }
}
