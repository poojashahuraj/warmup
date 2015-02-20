package pooja;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 * Created by parallels on 2/18/15.
 */
public class PersistentHashTable {
    private int bucketCount;
    private RandomAccessFile raf;
    private int endOfFileAddress = 20;

    public PersistentHashTable(RandomAccessFile file, int bucket_count) throws IOException {
        raf = file;
        bucketCount = bucket_count;
        if (raf.length() == 0) {
            init();
        }
    }

    private void init() throws IOException {
        raf.seek(0);
        for (int i = 0; i < 5; i++) {
            raf.writeInt(-1);
        }
    }


    public void put(int key, int value) throws IOException {
        int bucketNumber = key % bucketCount;
        raf.seek(bucketNumber * 4);
        int bucketStartPos = raf.readInt();

        if (bucketStartPos == -1) { //bucket is empty
            raf.seek(bucketNumber * 4);
            raf.writeInt(endOfFileAddress);
            raf.seek(endOfFileAddress);
            raf.writeInt(key);
            raf.writeInt(value);
            raf.writeInt(-1);
            endOfFileAddress = (int) raf.getFilePointer();
        } else {//minimum one element is present
            int currentPos = bucketStartPos;

            for (int i = bucketStartPos; i < endOfFileAddress; i++) {
                raf.seek(currentPos + 8);
                if (raf.readInt() == -1) {//this is writing second node
                    raf.seek(currentPos + 8);
                    raf.writeInt(endOfFileAddress);
                    raf.seek(endOfFileAddress);
                    raf.writeInt(key);
                    raf.writeInt(value);
                    raf.writeInt(-1);
                    endOfFileAddress = (int) raf.getFilePointer();
                    break;
                } else {
                    raf.seek(currentPos + 8);
                    currentPos = raf.readInt();
                }
            }
        }
    }

    public int get(int key) throws IOException {
        int bucketNumber = key % bucketCount;
        raf.seek(bucketNumber * 4);
        int bucketStartPos = raf.readInt();
        int currentPos = bucketStartPos;

        if (bucketStartPos == -1) {
            return -1;
        } else {
            for (int i = 0; i < 10; i++) {
                raf.seek(currentPos);
                if (raf.readInt() == key) {
                    return raf.readInt();
                } else {
                    raf.seek(currentPos + 8);
                    int nextAddress = raf.readInt();
                    currentPos = nextAddress;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public int getBucketLength(int key) throws IOException {
        int bucketNumber = key % bucketCount;
        raf.seek(bucketNumber * 4);
        int bucketStartPos = raf.readInt();
        int currentPosition = bucketStartPos;
        int numberOfNodes = 1;

        for (int i = 0; i < 10; i++) {
            raf.seek(currentPosition);
            if (raf.read() == -1) { //empty bucket
                return 0;
            } else {
                raf.seek(currentPosition + 8);
                if (raf.readInt() == -1) {
                    return numberOfNodes;
                } else {
                    numberOfNodes++;
                    raf.seek(currentPosition + 8);
                    int nextAddress = raf.readInt();
                    currentPosition = nextAddress;
                }
            }
        }
        return numberOfNodes;
    }

    public void remove(int removeKey) throws IOException {
        int bucketNumber = removeKey % bucketCount;
        raf.seek(bucketNumber * 4);
        int bucketStartPos = raf.readInt();

        int currentPosition = bucketStartPos;
        int previousNodePos = bucketStartPos;
        raf.seek(bucketStartPos + 8);
        int nextNodePos = raf.readInt();

        for (int i = 0; i < 10; i++) {
            raf.seek(currentPosition);
            if (raf.readInt() == removeKey) {
                raf.seek(previousNodePos + 8);
                raf.writeInt(nextNodePos);
            } else {
                raf.seek(currentPosition);
                previousNodePos = (int) raf.getFilePointer();
                currentPosition = nextNodePos;
                raf.seek(currentPosition + 8);
                nextNodePos = raf.readInt();
            }
        }
    }

    public Iterator<Integer> bucket(int key) throws IOException {
        return new BucketIterator(key);
    }

    public int size() throws IOException {
        int totalNumberOfNodes = 0;
        int currentPos = 20;
        for (int i = 20; i < endOfFileAddress; i++) {
            raf.seek(currentPos);
            if (raf.read() == -1) {
                break;

            } else {
                raf.seek(currentPos);
                currentPos = currentPos + 12;
                totalNumberOfNodes = totalNumberOfNodes + 1;
            }
        }
        return totalNumberOfNodes;
    }

    private class BucketIterator implements Iterator<Integer> {
        int currentPos = 0;
        int offset, value, nextAddress = 0;
        boolean flag = false;
        private int key;

        public BucketIterator(int i) {
            key = i;
        }

        @Override
        public boolean hasNext() {
            try {
                int bucketNumber = key % bucketCount;
                raf.seek(bucketNumber * 4);
                int bucketStartAddr = raf.readInt();
                currentPos = bucketStartAddr;


                for (int i = 0; i < 10; i++) {
                    raf.seek(currentPos);
                    if (raf.readInt() == key) {
                        raf.seek(currentPos);
                        key = raf.readInt();
                        value = raf.readInt();
                        nextAddress = raf.readInt();
                        break;
                    } else {
                        raf.seek(currentPos + 8);
                        if (raf.read() == -1) {
                            flag = false;
                            break;
                        } else {
                            raf.seek(currentPos + 8);
                            currentPos = raf.readInt();
                        }
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
