package ua.naiksoftware.waronline;

public class Node {

    private Node parent;
    private final int passability;// проходимость
    private final int x, y;

    Node(Node parent, int x, int y, int p) {
        this.parent = parent;
        passability = p;
        this.x = x;
        this.y = y;
    }

    void setParent(Node n) {
        parent = n;
    }

    Node getParent() {
        return parent;
    }

    int getPassability() {
        return passability;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "node: " + x + ", " + y;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Node) {
            Node n = (Node) o;
            return getX() == n.getX() && getY() == n.getY();
        } else {
            return false;
        }
    }
}
