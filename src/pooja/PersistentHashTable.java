package pooja;
import java.io.IOException;
import java.util.Iterator;

public class PersistentHashTable {
    private int bucketCount;
    private int endOfFileAddress;
    private StorageAccessor storageAccessor;

    public PersistentHashTable(StorageAccessor storageAccessor, int count) throws IOException {
        this.storageAccessor = storageAccessor;
        endOfFileAddress = count * 4;
        bucketCount = count;
        if (storageAccessor.length() == 0) {
            init();
        }
    }

    private void init() throws IOException {
        storageAccessor.seek(0);
        for (int i = 0; i < bucketCount; i++) {
            storageAccessor.writeInt(-1);
        }
    }

    public void put(int key, int value) throws IOException {
        int bucketNumber = key % bucketCount;
        storageAccessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAccessor.readInt();

        if (bucketStartPos == -1) {
            storageAccessor.seek(bucketNumber * 4);
            storageAccessor.writeInt(endOfFileAddress);
            storageAccessor.seek(endOfFileAddress);
            storageAccessor.writeInt(key);
            storageAccessor.writeInt(value);
            storageAccessor.writeInt(-1);
            endOfFileAddress = (int) storageAccessor.length();

        } else {//minimum one element is present
            int currentPos = bucketStartPos;
            for (int i = bucketStartPos; i < endOfFileAddress; i++) {
                storageAccessor.seek(currentPos + 8);
                if (storageAccessor.readInt() == -1) {//this is writing second node
                    storageAccessor.seek(currentPos + 8);
                    storageAccessor.writeInt(endOfFileAddress);
                    storageAccessor.seek(endOfFileAddress);
                    storageAccessor.writeInt(key);
                    storageAccessor.writeInt(value);
                    storageAccessor.writeInt(-1);
                    endOfFileAddress = (int) storageAccessor.length();
                    break;
                } else {
                    storageAccessor.seek(currentPos + 8);
                    currentPos = storageAccessor.readInt();
                }
            }
        }
    }

    public int get(int key) throws IOException {
        int bucketNumber = key % bucketCount;
        storageAccessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAccessor.readInt();
        int currentPos = bucketStartPos;
        if (bucketStartPos == -1) {
            return -1;
        } else {
            for (int i = 0; i < 10; i++) {
                storageAccessor.seek(currentPos);
                if (storageAccessor.readInt() == key) {
                    return storageAccessor.readInt();
                } else {
                    storageAccessor.seek(currentPos + 8);
                    int nextAddress = storageAccessor.readInt();
                    currentPos = nextAddress;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public int getBucketLength(int key) throws IOException {
        int bucketNumber = key % bucketCount;
        storageAccessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAccessor.readInt();
        int currentPosition = bucketStartPos;
        int numberOfNodes = 1;

        for (int i = 0; i < 10; i++) {
            storageAccessor.seek(currentPosition);
            if (storageAccessor.read() == -1) {
                return 0;
            } else {
                storageAccessor.seek(currentPosition + 8);
                int addr = storageAccessor.readInt();
                if (addr == -1) {
                    return numberOfNodes;
                } else {
                    numberOfNodes++;
                    storageAccessor.seek(currentPosition + 8);
                    int nextAddress = storageAccessor.readInt();
                    currentPosition = nextAddress;
                }
            }
        }
        return numberOfNodes;
    }

    public void remove(int removeKey) throws IOException {
        int bucketNumber = removeKey % bucketCount;
        storageAccessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAccessor.readInt();

        int currentPosition = bucketStartPos;
        int previousNodePos = bucketStartPos;
        storageAccessor.seek(bucketStartPos + 8);
        int nextNodePos = storageAccessor.readInt();

        for (int i = 0; i < 10; i++) {
            storageAccessor.seek(currentPosition);
            if (storageAccessor.readInt() == removeKey) {
                storageAccessor.seek(previousNodePos + 8);
                storageAccessor.writeInt(nextNodePos);
                break;
            } else {
                storageAccessor.seek(currentPosition);
                previousNodePos = storageAccessor.getFilePointer();
                currentPosition = nextNodePos;
                storageAccessor.seek(currentPosition + 8);
                nextNodePos = storageAccessor.readInt();
            }
        }
    }

    public Iterator<Integer> bucket(int key) throws IOException {
        return new BucketIterator(key);
    }

    public int size() throws IOException {
        int size = 0;
        for (int i = 0; i < bucketCount; i++) {
            size += getBucketLength(i);
        }
        return size;
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
                storageAccessor.seek(bucketNumber * 4);
                int bucketStartAddr = storageAccessor.readInt();
                currentPos = bucketStartAddr;


                for (int i = 0; i < 10; i++) {
                    storageAccessor.seek(currentPos);
                    if (storageAccessor.readInt() == key) {
                        storageAccessor.seek(currentPos);
                        key = storageAccessor.readInt();
                        value = storageAccessor.readInt();
                        nextAddress = storageAccessor.readInt();
                        break;
                    } else {
                        storageAccessor.seek(currentPos + 8);
                        if (storageAccessor.read() == -1) {
                            flag = false;
                            break;
                        } else {
                            storageAccessor.seek(currentPos + 8);
                            currentPos = storageAccessor.readInt();
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
