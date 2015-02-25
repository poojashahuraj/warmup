package pooja;

import java.io.IOException;
import java.util.Iterator;

public class PersistentHashTable {
    private int bucketCount;
    private int endOfFileAddress;
    private StorageAcessor storageAcessor;

    public PersistentHashTable(StorageAcessor storageAcessor, int count) throws IOException {
        this.storageAcessor = storageAcessor;
        endOfFileAddress = count * 4;
        bucketCount = count;
        if (storageAcessor.length() == 0) {
            init();
        }
    }

    private void init() throws IOException {
        storageAcessor.seek(0);
        for (int i = 0; i < bucketCount; i++) {
            storageAcessor.writeInt(-1);
        }
    }

    public void put(int key, int value) throws IOException {
        int bucketNumber = key % bucketCount;
        storageAcessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAcessor.readInt();

        if (bucketStartPos == -1) {
            storageAcessor.seek(bucketNumber * 4);
            storageAcessor.writeInt(endOfFileAddress);
            storageAcessor.seek(endOfFileAddress);
            storageAcessor.writeInt(key);
            storageAcessor.writeInt(value);
            storageAcessor.writeInt(-1);
            endOfFileAddress = (int) storageAcessor.length();
        } else {//minimum one element is present
            int currentPos = bucketStartPos;
            for (int i = bucketStartPos; i < endOfFileAddress; i++) {
                storageAcessor.seek(currentPos + 8);
                if (storageAcessor.readInt() == -1) {//this is writing second node
                    storageAcessor.seek(currentPos + 8);
                    storageAcessor.writeInt(endOfFileAddress);
                    storageAcessor.seek(endOfFileAddress);
                    storageAcessor.writeInt(key);
                    storageAcessor.writeInt(value);
                    storageAcessor.writeInt(-1);
                    endOfFileAddress = (int) storageAcessor.length();
                    break;
                } else {
                    storageAcessor.seek(currentPos + 8);
                    currentPos = storageAcessor.readInt();
                }
            }
        }
    }

    public int get(int key) throws IOException {
        int bucketNumber = key % bucketCount;
        storageAcessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAcessor.readInt();
        int currentPos = bucketStartPos;
        if (bucketStartPos == -1) {
            return -1;
        } else {
            for (int i = 0; i < 10; i++) {
                storageAcessor.seek(currentPos);
                if (storageAcessor.readInt() == key) {
                    return storageAcessor.readInt();
                } else {
                    storageAcessor.seek(currentPos + 8);
                    int nextAddress = storageAcessor.readInt();
                    currentPos = nextAddress;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public int getBucketLength(int key) throws IOException {
        int bucketNumber = key % bucketCount;
        storageAcessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAcessor.readInt();
        int currentPosition = bucketStartPos;
        int numberOfNodes = 1;

        for (int i = 0; i < 10; i++) {
            storageAcessor.seek(currentPosition);
            if (storageAcessor.read() == -1) {
                return 0;
            } else {
                storageAcessor.seek(currentPosition + 8);
                if (storageAcessor.readInt() == -1) {
                    return numberOfNodes;
                } else {
                    numberOfNodes++;
                    storageAcessor.seek(currentPosition + 8);
                    int nextAddress = storageAcessor.readInt();
                    currentPosition = nextAddress;
                }
            }
        }
        return numberOfNodes;
    }

    public void remove(int removeKey) throws IOException {
        int bucketNumber = removeKey % bucketCount;
        storageAcessor.seek(bucketNumber * 4);
        int bucketStartPos = storageAcessor.readInt();

        int currentPosition = bucketStartPos;
        int previousNodePos = bucketStartPos;
        storageAcessor.seek(bucketStartPos + 8);
        int nextNodePos = storageAcessor.readInt();

        for (int i = 0; i < 10; i++) {
            storageAcessor.seek(currentPosition);
            if (storageAcessor.readInt() == removeKey) {
                storageAcessor.seek(previousNodePos + 8);
                storageAcessor.writeInt(nextNodePos);
            } else {
                storageAcessor.seek(currentPosition);
                previousNodePos = (int) storageAcessor.length();
                currentPosition = nextNodePos;
                storageAcessor.seek(currentPosition + 8);
                nextNodePos = storageAcessor.readInt();
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
                storageAcessor.seek(bucketNumber * 4);
                int bucketStartAddr = storageAcessor.readInt();
                currentPos = bucketStartAddr;


                for (int i = 0; i < 10; i++) {
                    storageAcessor.seek(currentPos);
                    if (storageAcessor.readInt() == key) {
                        storageAcessor.seek(currentPos);
                        key = storageAcessor.readInt();
                        value = storageAcessor.readInt();
                        nextAddress = storageAcessor.readInt();
                        break;
                    } else {
                        storageAcessor.seek(currentPos + 8);
                        if (storageAcessor.read() == -1) {
                            flag = false;
                            break;
                        } else {
                            storageAcessor.seek(currentPos + 8);
                            currentPos = storageAcessor.readInt();
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
