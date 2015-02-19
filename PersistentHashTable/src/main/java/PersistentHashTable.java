import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;

/**
 * Created by parallels on 2/18/15.
 */
public class PersistentHashTable {
    private RandomAccessFile raf;
    private Hashtable<Integer, Integer> bucketAddressTable = new Hashtable<Integer, Integer>();
    public static int hashInt = 5;

    //constructor
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

        } else // second or later node in the bucket
        {
            int currentPos = startAddr + 12;
            for (int i = currentPos; i < endAddr; i++) {
                System.out.println("curr " + currentPos);
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
        int endAddr =startAddr+100;
        int currentPos =startAddr;
        for (int i=startAddr; i<endAddr; i++){
            raf.seek(currentPos);
            int readKey = raf.readInt();
            if(readKey == key){
                return raf.readInt();
            }else{
                currentPos+=12;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }


    public int getBucketLength() {
        return 0;
    }

    public void remove(int i) {
    }
}
