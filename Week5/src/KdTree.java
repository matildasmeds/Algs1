import java.util.Iterator;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

// KdTree is a data structure for 2D points within a two-dimensional unit
// square. In general terms, KdTree is a K-dimensional space-partitioning data
// structure, that enables effective range searches and nearest neighbor
// searches. Here, KdTree uses a BST where points are nodes, and x- and y-
// coordinates are used as keys as a strictly alternating sequence.
//
// http://coursera.cs.princeton.edu/algs4/assignments/kdtree.html
// http://coursera.cs.princeton.edu/algs4/checklists/kdtree.html
public final class KdTree {
    private TreeSet<Node> nodes;
    private Node root;

    // Each node has an Orientation, that divides it's border rectangle in two,
    // either vertically or horizontally, into two subtrees. Orientation
    // alternates strictly, so that child node's orientation is always other
    // than its parent's.
    private enum Orientation {
        VERTICAL, HORIZONTAL
    };

    // Each node has two subtrees, which may or may not contain another node.
    private enum Subtree {
        LEFT, RIGHT
    };

    // Pen size is used for drawing nodes and their rectangles.
    private enum Pen {
        SMALL(0.005),
        MEDIUM(0.01),
        LARGE(0.015);
        private double size;
        Pen(double width) {
            this.size = width;
        }
        double width() {
            return size;
        }
    };

    private static final class Node implements Comparable<Node> {
        private Point2D p;
        private Orientation orientation;
        // Rectangle that includes the node, and spans the entire subtree
        // the node represents, in relation to its parent.
        private RectHV rect;
        private Node parent;
        private Node left;
        private Node right;

        Node(Point2D p) {
            this.p = p;
        }

        @Override
        public int compareTo(Node o) {
            Node n = (Node) o;
            return p.compareTo(n.p);
        }
    }

    public KdTree() {
        nodes = new TreeSet<Node>();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }

        Node newNode = new Node(p);
        nodes.add(newNode);

        // If node is the first one, set up as root and return
        if (root == null) {
            newNode.orientation = Orientation.VERTICAL;
            // root node's rectangle spans whole area
            newNode.rect = new RectHV(0, 0, 1, 1);
            root = newNode;
            return;
        }

        // Traverse tree to find correct position for the new node
        Node node = root;
        while (true) {
            Subtree subtree = detectSubtree(node, newNode.p);
            Node child = getChild(node, subtree);
            // if child exists, traverse subtree
            if (child != null) {
                node = child;
            // if child doesn't exist, place node
            } else {
                child = newNode;
                // Link node to parent
                newNode.parent = node;
                if (subtree == Subtree.LEFT) {
                    node.left = newNode;
                } else {
                    node.right = newNode;
                }
                // Switch child node orientation
                if (node.orientation == Orientation.VERTICAL) {
                    newNode.orientation = Orientation.HORIZONTAL;
                } else {
                    newNode.orientation = Orientation.VERTICAL;
                }
                newNode.rect = rectFor(newNode, subtree);
                break;
            }
        }
    }

    // Creates spanning rectangle for a node's subtree.
    // Each node has two subtrees, right and left.
    private RectHV rectFor(Node node, Subtree subtree) {
        RectHV newRect;
        Node parent = node.parent;
        if (subtree == Subtree.LEFT) {
            if (node.orientation == Orientation.HORIZONTAL) {
                newRect = new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                        parent.p.x(), parent.rect.ymax());
            } else {
                newRect = new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                        parent.rect.xmax(), parent.p.y());
            }
        } else { // subtree == RIGHT
            if (node.orientation == Orientation.HORIZONTAL) {
                newRect = new RectHV(parent.p.x(), parent.rect.ymin(),
                        parent.rect.xmax(), parent.rect.ymax());
            } else {
                newRect = new RectHV(parent.rect.xmin(), parent.p.y(),
                        parent.rect.xmax(), parent.rect.ymax());
            }
        }
        return newRect;
    }

    // To which Subtree  should the new point be placed?
    private Subtree detectSubtree(Node node, Point2D p) {
        if (node.orientation == Orientation.VERTICAL
                && (p.x() < node.p.x())) {
            return Subtree.LEFT;
        }
        if (node.orientation == Orientation.HORIZONTAL
                && (p.y() < node.p.y())) {
            return Subtree.LEFT;
        }
        return Subtree.RIGHT;
    }

    private Node getChild(Node node, Subtree subtree) {
        if (subtree == Subtree.LEFT) {
            return node.left;
        }
        return node.right;
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }

        Node node = root;
        while (true) {
            if (node == null) {
                return false;
            }
            if (node.p.equals(p)) {
                return true;
            }
            node = getChild(node, detectSubtree(node, p));
        }
    }

    public void draw() {
        for (Node n : nodes) {
            double y1, y2, parentY, y, x1, x2, parentX, x;
            // Draw vertical / horizontal line for node
            StdDraw.setPenRadius(Pen.MEDIUM.width());
            if (n.orientation == Orientation.VERTICAL) {
                y1 = 0;
                y2 = 1;
                if (n.parent != null) {
                    parentY = n.parent.p.y();
                    if (n.equals(n.parent.left)) {
                        y2 = parentY; // top limit
                    } else {
                        y1 = parentY; // bottom limit
                    }
                }
                x = n.p.x();
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(x, y1, x, y2);
            } else {
                x1 = 0;
                x2 = 1;
                if (n.parent != null) {
                    parentX = n.parent.p.x();
                    if (n.equals(n.parent.left)) {
                        x2 = parentX; // right limit
                    } else {
                        x1 = parentX; // left limit
                    }
                }
                y = n.p.y();
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(x1, y, x2, y);
            }
            // Draw point for node location
            StdDraw.setPenRadius(Pen.LARGE.width());
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.point(n.p.x(), n.p.y());
            // Draw rectangle that contains the node, e.g. rectangle for
            // parent's corresponding subtree
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setPenRadius(Pen.SMALL.width());
            StdDraw.line(n.rect.xmin(), n.rect.ymin(), n.rect.xmin(),
                    n.rect.ymax());
            StdDraw.line(n.rect.xmin(), n.rect.ymax(), n.rect.xmax(),
                    n.rect.ymax());
            StdDraw.line(n.rect.xmax(), n.rect.ymin(), n.rect.xmax(),
                    n.rect.ymax());
            StdDraw.line(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(),
                    n.rect.ymin());
        }
    }

    // Collection of all points inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }

        class Search {
            TreeSet<Point2D> pointsInRange = new TreeSet<Point2D>();

            Search() {
                if (root != null) {
                    next(root);
                }
            }

            void next(Node node) {
                if (rect.contains(node.p)) {
                    pointsInRange.add(node.p);
                }
                if ((node.left != null)
                        && (rect.intersects(node.left.rect))) {
                    next(node.left);
                }
                if ((node.right != null)
                        && (rect.intersects(node.right.rect))) {
                    next(node.right);
                }
            }
        }

        Search search = new Search();
        Iterable<Point2D> iterable = new Iterable<Point2D>() {
            @Override
            public Iterator<Point2D> iterator() {
                return search.pointsInRange.iterator();
            }
        };
        return iterable;
    }

    // Nearest search starts at the root, and recursively search both subtrees.
    // Subtree is skipped, if the closest point so far is closer to queried
    // point, than the subtree's rectangle to queried point.
    // When there are two eligible subtrees, the subtree that is on the same
    // side of splitting line, as the queried point, is traversed first.
    // This may allow skipping the other subtree.
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (nodes.isEmpty()) {
            return null;
        }

        class Search {
            Node nearest;
            double distance;

            Search() {
                distance = p.distanceTo(root.p);
                nearest = root;
                next(root);
            }

            void next(Node node) {
                if (p.distanceTo(node.p) < distance) {
                    nearest = node;
                    distance = p.distanceTo(node.p);
                }
                // if both subtrees are eligible, first traverse one on the
                // same side as p
                if (searchSubtree(node.left, distance, p)
                        && searchSubtree(node.right, distance, p)) {
                    boolean leftFirst;
                    if (node.orientation == Orientation.HORIZONTAL) {
                        if (p.y() < node.p.y()) {
                            leftFirst = true;
                        } else {
                            leftFirst = false;
                        }
                    } else {
                        if (p.x() < node.p.x()) {
                            leftFirst = true;
                        } else {
                            leftFirst = false;
                        }
                    }
                    if (leftFirst) {
                        next(node.left);
                        if (searchSubtree(node.right, distance, p)) {
                            next(node.right);
                        }
                    } else {
                        next(node.right);
                        if (searchSubtree(node.left, distance, p)) {
                            next(node.left);
                        }
                    }
                // only one subtree or neither are eligible
                } else {
                    if (searchSubtree(node.left, distance, p)) {
                        next(node.left);
                    }
                    if (searchSubtree(node.right, distance, p)) {
                        next(node.right);
                    }
                }
            }
        }
        Search search = new Search();
        return search.nearest.p;
    }

    // Is subtree eligible for search? Distance is the distance to the nearest
    // point so far, and point is the queried point
    private boolean searchSubtree(Node subtree, double distance, Point2D p) {
        return ((subtree != null) && (subtree.rect.distanceTo(p)) < distance);
    }

    private static void testBasic() {
        System.out.println("Create new KdTree object");
        KdTree kdTree = new KdTree();
        Point2D p1 = new Point2D(0.1, 0.2);
        System.out.println("isEmpty() // " + kdTree.isEmpty());
        System.out.println("size() // " + kdTree.size());
        System.out.println("contains(p1) // " + kdTree.contains(p1));
        System.out.println("----");
        System.out.println("Add new Point");
        kdTree.insert(p1);
        System.out.println("isEmpty() // " + kdTree.isEmpty());
        System.out.println("size() // " + kdTree.size());
        System.out.println("contains(p1) // " + kdTree.contains(p1));
        System.out.println("----");
        System.out.println("Add same point (clone) again");
        kdTree.insert(new Point2D(0.1, 0.2));
        System.out.println("isEmpty() // " + kdTree.isEmpty());
        System.out.println("size() // " + kdTree.size());
        System.out.println("contains(p1) // " + kdTree.contains(p1));
        System.out.println("----");

        Point2D[] points = { new Point2D(0.7, 0.2), new Point2D(0.5, 0.4),
                new Point2D(0.2, 0.3), new Point2D(0.4, 0.7),
                new Point2D(0.9, 0.6) };
        for (Point2D p : points) {
            kdTree.insert(p);
        }
        for (Point2D p : points) {
            System.out.println("Contains -> " + kdTree.contains(p));
        }
        System.out.println(
                "Contains -> " + kdTree.contains(new Point2D(0.1, 0.1)));
    }

    private static void testInsert() {
        System.out.println("Test insert()");
        KdTree kdTree = new KdTree();
        Point2D[] points = { new Point2D(0.7, 0.2), new Point2D(0.5, 0.4),
                new Point2D(0.2, 0.3), new Point2D(0.4, 0.7),
                new Point2D(0.9, 0.6) };
        for (Point2D p : points) {
            kdTree.insert(p);
        }
        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        kdTree.draw();
        StdDraw.show();
    }

    private static void testRange() {
        System.out.println("Test insert()");
        KdTree kdTree = new KdTree();
        Point2D a, b, c, d, e;
        a = new Point2D(0.7, 0.2);
        b = new Point2D(0.5, 0.4);
        c = new Point2D(0.2, 0.3);
        d = new Point2D(0.4, 0.7);
        e = new Point2D(0.9, 0.6);
        Point2D[] points = { a, b, c, d, e };
        for (Point2D p : points) {
            kdTree.insert(p);
        }
        RectHV rect1 = new RectHV(0.3, 0.3, 0.8, 1); // finds points b, d
        RectHV rect2 = new RectHV(0.1, 0.1, 0.8, 0.3); // finds points a, c
        RectHV rect3 = new RectHV(0.8, 0, 1, 0.2); // finds none
        System.out.println("Testing range 1, should find: " + b + ", " + d);
        for (Point2D p : kdTree.range(rect1)) {
            System.out.println(p);
        }
        System.out.println("Testing range 2, should find: " + a + ", " + c);
        for (Point2D p : kdTree.range(rect2)) {
            System.out.println(p);
        }
        System.out.println("Testing range 3, should find none");
        for (Point2D p : kdTree.range(rect3)) {
            System.out.println(p);
        }
    }

    private static void testNearest() {
        System.out.println("Test testNearest()");
        KdTree kdTree = new KdTree();
        Point2D a, b, c, d, e;
        a = new Point2D(0.7, 0.2);
        b = new Point2D(0.5, 0.4);
        c = new Point2D(0.2, 0.3);
        d = new Point2D(0.4, 0.7);
        e = new Point2D(0.9, 0.6);
        Point2D[] points = { a, b, c, d, e };
        for (Point2D p : points) {
            kdTree.insert(p);
        }
        System.out.println("Nearest for (0.1, 0.1) should be (0.2, 0.3)");
        System.out.println(kdTree.nearest(new Point2D(0.2, 0.3)));
        System.out.println("Nearest for (1, 1) should be (0.9, 0.6)");
        System.out.println(kdTree.nearest(new Point2D(1, 1)));
        System.out.println("Nearest for (0.5, 0.5) should be (0.5, 0.4)");
        System.out.println(kdTree.nearest(new Point2D(0.5, 0.4)));
    }

    public static void main(String[] args) {
        testBasic();
        testInsert();
        testRange();
        testNearest();
    }
}
