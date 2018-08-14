import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Iterator;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

class DequeTest {

    @Test
    void addAndRemoveFirst() {
        Deque<Integer> deque = new Deque<Integer>();
        Integer a = new Integer(56);
        Integer b = new Integer(0);
        Integer i;
        deque.addFirst(a);
        deque.addFirst(b);
        i = (Integer) deque.removeFirst();
        assertEquals(b, i);
        i = (Integer) deque.removeFirst();
        assertEquals(a, i);
    }

    @Test
    void addAndRemoveLast() {
        Deque<Integer> deque = new Deque<Integer>();
        Integer a = new Integer(56);
        Integer b = new Integer(0);
        Integer i;
        deque.addLast(a);
        deque.addLast(b);
        i = (Integer) deque.removeLast();
        assertEquals(b, i);
        i = (Integer) deque.removeLast();
        assertEquals(a, i);
    }

    @Test
    void mixedAddAndRemoves() {
        Deque<Integer> deque = new Deque<Integer>();
        Integer a = new Integer(56);
        Integer b = new Integer(0);
        Integer c = new Integer(-5);
        Integer i;
        deque.addFirst(a);
        deque.addLast(b);
        deque.addFirst(c);
        i = (Integer) deque.removeLast();
        assertEquals(i, b);
        i = (Integer) deque.removeLast();
        assertEquals(i, a);
        i = (Integer) deque.removeLast();
        assertEquals(i, c);
    }

    @Test
    void sizeAndIsEmpty() {
        Deque<Integer> deque = new Deque<Integer>();
        Integer a = new Integer(12);
        assertTrue(deque.isEmpty());
        assertEquals(deque.size(), 0);
        deque.addLast(a);
        assertFalse(deque.isEmpty());
        assertEquals(deque.size(), 1);
        deque.removeFirst();
        assertTrue(deque.isEmpty());
        assertEquals(deque.size(), 0);
        deque.addFirst(a);
        assertFalse(deque.isEmpty());
        assertEquals(deque.size(), 1);
        deque.removeLast();
        assertTrue(deque.isEmpty());
        assertEquals(deque.size(), 0);
    }

    Deque<Integer> buildDeque() {
        Deque<Integer> deque = new Deque<Integer>();
        deque.addFirst(5);
        deque.addFirst(4);
        deque.addFirst(3);
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addFirst(0);
        return deque;
    }

    @Test
    void testIterator() {
        // Test iterator returns expected items
        Deque<Integer> deque = buildDeque();
        Iterator<Integer> it = deque.iterator();
        Integer[] expected = { 0, 1, 2, 3, 4, 5 };
        int i = 0;
        while (it.hasNext()) {
            assertEquals(expected[i], (Integer) it.next());
            i++;
        }
        // Check iterator didn't modify queue
        assertEquals(deque.size(), 6);
    }
}
