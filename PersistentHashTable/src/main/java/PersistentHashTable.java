import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by parallels on 2/18/15.
 */
public class PersistentHashTable {
    public static int hashInt = 5;
    private RandomAccessFile raf;
    private Hashtable<Integer, Integer> bucketAddressTable = new Hashtable<Integer, Integer>();

    public PersistentHashTable(RandomAccessFile file) {
        raf = file;
    }

    public void add(int key, int value) throws IOException {
        int bucketNumber = key % hashInt;
        int startAddr = bucketNumber * 100;
        int endAddr = startAddr + 100;
        bucketAddressTable.put(bucketNumber, startAddr);

        raf.seek(startAddr);
        if (raf.read() == -1) {
            raf.seek(startAddr);
            raf.writeInt(key);
            raf.writeInt(value);
            raf.writeInt(-1);
        } else {
            int currentPos = startAddr + 12;
            for (int i = currentPos; i < endAddr; i++) {
                raf.seek(currentPos - 4);
                if (raf.readInt() == -1) {
                    raf.seek(currentPos - 4);
                    raf.writeInt(currentPos);
                    raf.writeInt(key);
                    raf.writeInt(value);
                    raf.writeInt(-1);
                    break;
                } else {
                    currentPos += 12;
                }
            }
        }
    }


    public int getValue(int key) throws IOException {
        int bucketNumber = key % hashInt;
        int startAddr = bucketAddressTable.get(bucketNumber);
        int endAddr = startAddr + 100;
        int currentPos = startAddr;
        for (int i = startAddr; i < endAddr; i++) {
            raf.seek(currentPos);
            int readKey = raf.readInt();
            if (readKey == key) {
                return raf.readInt();
            } else {
                currentPos += 12;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }


    public int getBucketLength(int bucketNumber) throws IOException {
        int startAddr = bucketAddressTable.get(bucketNumber);
        int endAddr = startAddr + 100;
        int numberOfNodes = 1;
        int currentPos = startAddr + 8;

        for (int i = startAddr; i < endAddr; i++) {
            raf.seek(currentPos);
            int readKey = raf.readInt();
            if (readKey == -1) {
                break;
            } else {
                numberOfNodes++;
                currentPos = readKey + 8;
            }
        }
        return numberOfNodes;
    }

    public void remove(int removeKey) throws IOException {
        int bucketNumber = removeKey % hashInt;
        int startAddr = bucketAddressTable.get(bucketNumber);
        int endAddr = startAddr + 100;
        int currentPos = startAddr;

        for (int i = startAddr; i < endAddr; i++) {
            raf.seek(currentPos);
            int readKey = raf.readInt();
            if (readKey == removeKey) {
                raf.seek(currentPos + 8);
                int nextAddr = raf.readInt();
                raf.seek(currentPos - 4);
                raf.writeInt(nextAddr);
            } else {
                currentPos += 12;
            }
        }
    }

    public Iterator<Integer> iterator(int key) throws IOException {
        IteratorClass iteratorClass = new IteratorClass(key);
        return iteratorClass;
    }

    private class IteratorClass implements Iterator<Integer> {
        int currentPos = 0;
        int offset, value, nextAddress = 0;
        boolean flag = false;
        private int key;

        public IteratorClass(int i) {
            key = i;
        }

        @Override
        public boolean hasNext() {
            try {
                currentPos = bucketAddressTable.get(key % hashInt);
                raf.seek(currentPos);
                for (int i = 0; i < 10; i++) {
                    if (raf.readInt() == key) {
                        raf.seek(currentPos);
                        key = raf.readInt();
                        value = raf.readInt();
                        nextAddress = raf.readInt();
                        break;
                    } else {
                        raf.seek(currentPos + 8);
                        currentPos = raf.readInt();
                    }
                }
                if (nextAddress != -1 && offset < PersistentHashTable.this.getBucketLength(key % 5)) {
                    flag = true;
                    offset++;
                    currentPos = nextAddress;
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
            return key;
        }
    }
}
