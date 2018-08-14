/**
 * Class PercolationStats
 *
 * Calculates approximate vacancy percentage for system that percolates,
 * mean, standard deviation and 95% confidence intervals.
 *
 * If sites are independently set open with probability p, what is the
 * probability that the system percolates? In Monte Carlo simulation, vacancy
 * percentage at the point where system percolates, approximates probability p.
 *
 * @see Percolation
 * @see StdStats
 * @see StdRandom
 */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    // number of independent simulation runs
    private int trials;
    // Probability p for each simulation run, i.e. percolation thresholds
    private double[] results;
    private double mean;
    private double stddev;
    // Coefficient to approximate 95% confidence intervals
    // https://en.wikipedia.org/wiki/1.96
    private static final double STDDEV_COEF = 1.96;

    /**
     * Instantiates a percolation object.
     *
     * @param n
     *            Size parameter for n*n grid.
     * @param trials
     *            Number of independent simulation runs
     */
    public PercolationStats(final int n, final int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Args must be positive");
        }

        this.trials = trials;
        results = new double[trials];

        if (n == 0 || trials == 0) {
            stddev = Double.NaN;
            mean = Double.NaN;
            return;
        }

        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                perc.open(row, col);
            }
            results[i] = (double) perc.numberOfOpenSites() / (n * n);
            mean = StdStats.mean(results);
            stddev = StdStats.stddev(results);
        }
    }

    public final double mean() {
        return mean;
    }

    public final double stddev() {
        return stddev;
    }

    public final double confidenceLo() {
        if (results.length == 0) {
            return Double.NaN;
        }
        return mean - (STDDEV_COEF * stddev / Math.sqrt(trials));
    }

    public final double confidenceHi() {
        if (results.length == 0) {
            return Double.NaN;
        }
        return mean + (STDDEV_COEF * stddev / Math.sqrt(trials));
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            return;
        }
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats p = new PercolationStats(n, t);
        System.out.println("mean: " + p.mean());
        System.out.println("stddev: " + p.stddev());
        System.out.println("confidenceLo: " + p.confidenceLo());
        System.out.println("confidenceHi: " + p.confidenceHi());
    }
}
