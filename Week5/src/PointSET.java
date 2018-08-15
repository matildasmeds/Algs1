import java.util.Iterator;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

/**
 * Brute-force implementation of a data structure, that contains all points
 * in unit square, nearest search and range search methods.
 **/

public final class PointSET {

    private TreeSet<Point2D> points;

    public PointSET() {
        points = new TreeSet<Point2D>();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        return points.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        points.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return points.contains(p);
    }

    // Brute force iterator over all points that are inside the rectangle
    // (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        TreeSet<Point2D> pointsInRange = new TreeSet<Point2D>();
        Iterator<Point2D> it = points.iterator();
        Point2D point;
        while (it.hasNext()) {
            point = it.next();
            if (rect.contains(point)) {
                pointsInRange.add(point);
            }
        }
        Iterable<Point2D> iterable = new Iterable<Point2D>() {
            @Override
            public Iterator<Point2D> iterator() {
                return pointsInRange.iterator();
            }
        };
        return iterable;
    }

    // Nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D point) {
        if (point == null) {
            throw new IllegalArgumentException();
        }
        Point2D nearest = null;
        double minDist = 2; // actual max distance is square of 2
        double dist;
        for (Point2D that : points) {
            dist = point.distanceTo(that);
            if (dist < minDist) {
                nearest = that;
                minDist = dist;
            }
        }
        return nearest;
    }

    public static void main(String[] args) {
        System.out.println("Create new PointSET object");
        PointSET pointSet = new PointSET();
        Point2D p1 = new Point2D(0.1, 0.2);
        System.out.println("isEmpty() // " + pointSet.isEmpty());
        System.out.println("size() // " + pointSet.size());
        System.out.println("contains(p1) // " + pointSet.contains(p1));
        System.out.println("----");
        System.out.println("Add new Point");
        pointSet.insert(p1);
        System.out.println("isEmpty() // " + pointSet.isEmpty());
        System.out.println("size() // " + pointSet.size());
        System.out.println("contains(p1) // " + pointSet.contains(p1));
        System.out.println("----");
        System.out.println("Add same point (clone) again");
        pointSet.insert(new Point2D(0.1, 0.2));
        System.out.println("isEmpty() // " + pointSet.isEmpty());
        System.out.println("size() // " + pointSet.size());
        System.out.println("contains(p1) // " + pointSet.contains(p1));
        System.out.println("----");
        System.out.println("Testing Iterator...");
        Point2D[] points = { new Point2D(0.2, 0.1), new Point2D(0.3, 0.3),
                new Point2D(0.3, 0.7), new Point2D(0.8, 0.4),
                new Point2D(0.3, 0.6), };
        for (Point2D point : points)
            pointSet.insert(point);
        RectHV rect1 = new RectHV(0.3, 0.3, 0.6, 0.6);
        Iterator<Point2D> it = pointSet.range(rect1).iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("----");
        System.out.println("Testing nearest()...");
        System.out.println(pointSet.nearest(new Point2D(0.4, 0.4)));
        System.out.println(pointSet.nearest(new Point2D(1, 1)));
        System.out.println(pointSet.nearest(new Point2D(0, 0)));
    }
}
