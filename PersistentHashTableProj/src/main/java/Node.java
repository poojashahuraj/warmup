/**
 * Created by parallels on 2/12/15.
 */
class Node {
    protected int data;
    protected Node next, prev;

    public Node() {
        next = null;
        prev = null;
        data = 0;
    }
    public Node(int d, Node n, Node p) {
        data = d;
        next = n;
        prev = p;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node n) {
        next = n;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node p) {
        prev = p;
    }

    public int getValue() {
        return data;
    }

    public void setValue(int d) {
         data = d;
    }
}
