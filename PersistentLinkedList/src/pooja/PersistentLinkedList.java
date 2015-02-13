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

    public int get(int index) throws IOException {
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

    public int length() throws IOException {
        return (int) raf.length();
    }

    public void remove(int index) throws IOException {
        int currentPosition = 0;
        for (int i = 0; i < raf.length(); i++) {
            if (i == index) {
                int nextPosition = currentPosition + 8;
                raf.seek(currentPosition - 4);
                raf.writeInt(nextPosition);
                break;
            } else {
                currentPosition = currentPosition + 8;
            }
        }
    }

    // TODO: implement me
    public Iterator<Integer> iterator() {
        return null;
    }
}
