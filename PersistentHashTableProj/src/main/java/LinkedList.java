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

        System.out.println("Size of the list is: " + list.getSize());

        System.out.println("Before deleting any element");
        list.print();
        list.remove(2);
        System.out.println("\nAfter deleting any element");
        list.print();

        if(list.get(10) !=0){
            System.out.println(list.get(10));
        }else {
            System.out.println("Key is not present is the map");
        }
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
        for(int i=0; i<size; i++){
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
        Node ptr = start;

        for (int i = 0; i < size; i++) {
            if (i == index) {
                Node p = ptr.getPrev();
                Node n = ptr.getNext();
                p.setNext(n);
                n.setPrev(p);
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
