import edu.princeton.cs.algs4.StdIn;

// Client class for RandomizedQueue
public final class Permutation {

    private Permutation() { }

    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: <number of permutations> < <file.txt> \n"
                + "Example: 5 < distinct.txt \n"
                + "% where distinct.txt contains\n"
                + "% 1 2 3 4 5 6 7 8 9 10");
            return;
        }
        int n = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            rq.enqueue(StdIn.readString());
        }
        for (int i = 0; i < n; i++) {
            System.out.println(rq.dequeue());
        }
    }
}
