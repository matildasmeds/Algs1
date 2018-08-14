import java.util.Iterator;
import java.util.NoSuchElementException;

// Deque (a double-ended queue) must have constant worst-case time for all 
// operations, including construction. Iterator must support each operation
// in constant worst-case time, including construction.
// http://coursera.cs.princeton.edu/algs4/assignments/queues.html
// http://coursera.cs.princeton.edu/algs4/checklists/queues.html
public final class Deque<Item> implements Iterable<Item> {
    // Deque is implemented as a two-way linked list
    // Class keeps track of first and last node in the list, for faster access.
    private Node first = null;
    private Node last = null;
    private int size = 0;

    private class Node {
        Item item;
        Node next;
        Node prev;

        public Node(Item item) {
            this.item = item;
            prev = null;
            next = null;
        }

        public void setNext(Node node) {
            next = node;
        }

        public void setPrev(Node node) {
            prev = node;
        }
    }

    private void linkNodes(Node node1, Node node2) {
        if (node1 != null) {
            node1.setNext(node2);
        }
        if (node2 != null) {
            node2.setPrev(node1);
        }
    }

    private void unlinkNode(Node node) {
        if (node.prev != null) {
            node.prev.setNext(null);
        }
        if (node.next != null) {
            node.next.setPrev(null);
        }
        node.setPrev(null);
        node.setNext(null);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // Enqueue item to the front
    public void addFirst(final Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node node = new Node(item);
        if (isEmpty()) {
            first = last = node;
        } else {
            linkNodes(node, first);
            first = node;
        }
        size++;
    }

    // Enqueue item to the end
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node node = new Node(item);
        if (isEmpty()) {
            first = last = node;
        } else {
            linkNodes(last, node);
            last = node;
        }
        size++;
    }

    // Dequeue first item
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Node node = first;
        if (size == 1) { // last item
            first = last = null;
        } else {
            first = first.next;
        }
        unlinkNode(node);
        size--;
        return node.item;
    }

    // Dequeue last item
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Node node = last;
        if (size == 1) { // last item
            last = first = null;
        } else {
            last = last.prev;
        }
        unlinkNode(node);
        size--;
        return node.item;
    }

    private class DequeIterator<Item> implements Iterator<Item> {
        private Deque.Node current;

        DequeIterator() {
            current = (Node) Deque.this.first;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node node = current;
            current = current.next;
            return (Item) node.item;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public java.util.Iterator<Item> iterator() {
        return new DequeIterator<Item>();
    }
}
