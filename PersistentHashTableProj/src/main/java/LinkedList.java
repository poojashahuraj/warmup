/**
 * Created by parallels on 2/12/15.
 */

class LinkedList {
    public int size;
    protected Node start;
    protected Node end;

    public LinkedList() {
        start = null;
        end = null;
        size = 0;
    }

    public static void main(String[] args) {
        LinkedList list = new LinkedList();

        list.append(5);
        list.append(4);
        list.append(3);
        list.append(2);
        list.append(1);
        list.append(0);

        System.out.println("Size = " + list.getSize() + " \n");
        System.out.println("Before deleting any element");
        list.print();
        list.remove(5);
        System.out.println("\nAfter deleting any element");
        list.print();


    }

//First method
    public void append(int val) {
        Node newNode = new Node(val, null, null);
        if (start == null) {
            start = newNode;
            end = start;
        } else {
            start.setPrev(newNode);
            newNode.setNext(start);
            start = newNode;
        }
        size++;
    }

//second method
    public int getSize() {
        return size;
    }

//Third method
    public int get(int index) {
        Node current = start;
        for(int i=0; i<=size; i++){
            System.out.print("data for this index is:" + current.getValue() + "\t");
            System.out.println("index is " + index + " Iteration for i " + i);
            if(i == index){
                System.out.println("came inside the if cond");
                return current.getValue();
            }
            current = current.getNext();
        }
        return 00;
    }

 //fourth method
    public void remove(int index) {
        Node ptr = start;// so we are starting from node 2

        for (int i = 0; i < size; i++) {
            if (i == index) {
                Node p = ptr.getPrev(); // get previous node
                Node n = ptr.getNext();// get next node
                p.setNext(n);// middle node is gone hence prev node link will be set to next node
                n.setPrev(p);// and for next node previous node link is set to previous node.
                size--;
                return;
            }
            ptr = ptr.getNext();
        }

    }
    public void print(){
        Node current = start;
        for(int i=0; i< size; i++){
            System.out.print(current.getValue() +"  <-> ");
            current = current.getNext();
        }
    }
}
