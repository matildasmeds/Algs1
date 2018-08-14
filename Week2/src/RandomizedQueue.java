import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdRandom;

// RandomizedQueue enqueues a random item from the queue. All operations
// must have constant amortized time, except creating iterator.
// Iterator operations next() and hasNext() must have constant worst-case time,
// and constructor linear time.
// http://coursera.cs.princeton.edu/algs4/assignments/queues.html
// http://coursera.cs.princeton.edu/algs4/checklists/queues.html
public final class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] queue;
    private int size;
    private int capacity;

    public RandomizedQueue() {
        capacity = 2;
        size = 0;
        queue = (Item[]) new Object[capacity];
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            s.append(queue[i]);
            s.append(" ");
        }
        return s.toString();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void enqueue(final Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (capacity == size) {
            resizeQueue(2 * capacity);
        }
        queue[size++] = item;
    }

    private void resizeQueue(final int newCapacity) {
        Item[] newQueue = (Item[]) new Object[newCapacity];
        Item[] oldQueue = queue;

        for (int i = 0; i < size; i++) {
            newQueue[i] = oldQueue[i];
        }
        capacity = newCapacity;
        queue = newQueue;
    }

    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int randIndex = StdRandom.uniform(size);
        int lastIndex = size - 1;
        Item item = queue[randIndex];
        if (randIndex != lastIndex) {
            queue[randIndex] = queue[lastIndex];
        }
        queue[lastIndex] = null;
        if (4 * size <= capacity) {
            resizeQueue(capacity / 2);
        }
        size--;
        return item;
    }

    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int randIndex = StdRandom.uniform(size);
        return queue[randIndex];
    }

    private class RandomizedQueueIterator<Item> implements Iterator {
        private int[] randOrder;
        private int current;

       RandomizedQueueIterator() {
            randOrder = new int[RandomizedQueue.this.size()];
            for (int i = 0; i < randOrder.length; i++) {
                randOrder[i] = i;
            }
            StdRandom.shuffle(randOrder);
            current = 0;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int index = randOrder[current++];
            Item item = (Item) RandomizedQueue.this.queue[index];
            return item;
        }

        public boolean hasNext() {
            return current != randOrder.length;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public java.util.Iterator<Item> iterator() {
        return new RandomizedQueueIterator<Item>();
    }

    public static void main(final String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        rq.enqueue(1);
        rq.enqueue(2);
        rq.enqueue(3);
        rq.enqueue(4);
        rq.enqueue(5);
        rq.enqueue(7);
        rq.enqueue(8);
        rq.enqueue(9);
        Iterator<Integer> it = rq.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("size: " + rq.size());
    }
}
