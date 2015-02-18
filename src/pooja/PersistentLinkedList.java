package pooja;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class PersistentLinkedList {
    private final RandomAccessFile raf;

    public PersistentLinkedList(RandomAccessFile file) {
        raf = file;
    }

    public void append(int value) throws IOException {
        if (raf.length() == 0) {
            raf.writeInt(value);
            raf.writeInt(-1);
        } else {
            int currentPosition = 8;
            for (int i = 0; i < raf.length(); i++) {
                raf.seek(currentPosition);
                if (raf.read() == -1) {
                    raf.seek(currentPosition - 4);
                    raf.writeInt(currentPosition);
                    raf.writeInt(value);
                    raf.writeInt(-1);
                    break;
                } else {
                    currentPosition += 8;
                }
            }
        }
    }

    public int getAddress(int index) throws IOException {
        int currentPosition = 0;
        for (int i = 0; i < raf.length(); i++) {
            raf.seek(currentPosition);
            if (i == index) {
                raf.seek(currentPosition);
                return raf.readInt();
            } else {
                currentPosition = currentPosition + 8;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public int getValue(int index) throws IOException {
        int currentPosition = 0;
        for (int i = 0; i < raf.length(); i++) {
            raf.seek(currentPosition);
            int value = raf.readInt();
            int addr = raf.readInt();
            if (i == index) {
                return value;
            } else {
                currentPosition = addr;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public int length() throws IOException {
        int currentPosition = 0;
        int numberOfNodes = 1;
        for (int i = 0; i < raf.length(); i++) {
            raf.seek(currentPosition);
            int value = raf.readInt();
            int addr = raf.readInt();
            if (addr == -1) {
                return numberOfNodes;
            }
            currentPosition = addr;
            numberOfNodes = numberOfNodes + 1;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public void remove(int index) throws IOException {
        int currentPosition = 4;
        for (int i = 0; i < raf.length(); i++) {
            if (i == index) {
                raf.seek(currentPosition);
                int address = raf.readInt();
                raf.seek(currentPosition - 8);
                raf.writeInt(address);
                break;
            } else {
                raf.seek(currentPosition);
                int address = raf.readInt();
                currentPosition = address + 4;
            }
        }
    }

    public Iterator<Integer> iterator() throws IOException {
        LinkedList linkedList = new LinkedList();
        return linkedList;
    }

    private class LinkedList implements Iterator<Integer> {
        int currentPosition = 0;
        int offset, address, value = 0;
        boolean flag = false;

        @Override
        public boolean hasNext() {
            try {
                raf.seek(currentPosition);
                value = raf.readInt();
                address = raf.readInt();
                if (address != -1 && offset < PersistentLinkedList.this.length()) {
                    flag = true;
                    offset++;
                    currentPosition = address;
                    return flag;
                } else {
                    flag = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return flag;
        }

        @Override
        public Integer next() {
            return value;
        }
    }
}