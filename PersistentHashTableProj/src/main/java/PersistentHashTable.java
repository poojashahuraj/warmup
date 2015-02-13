import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Hashtable;

/**
 * Created by parallels on 2/11/15.
 */
public class PersistentHashTable {
    Hashtable<Integer, Integer> hashtable;

    public static void main(String[] args) throws IOException {
        PersistentHashTable mainClass = new PersistentHashTable();
        String filename = "/home/parallels/test.txt";
        RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "rws");


        mainClass.put(1, 11, randomAccessFile);
        mainClass.put(2, 22, randomAccessFile);
        mainClass.put(3, 33, randomAccessFile);
        System.out.print(mainClass.get(3));
    }

    public void put(int key, int value, RandomAccessFile randomAccessFile) throws IOException {
        hashtable = new Hashtable<Integer, Integer>();
        hashtable.put(key, value);
        long filePos = randomAccessFile.getFilePointer();
        randomAccessFile.seek((filePos));
        randomAccessFile.writeBytes(key + "\t" + value + "\n");
    }

    public int get(int key) throws IOException {
        String filename = "/home/parallels/test.txt";
        System.out.println("Value from the hash table is:"+hashtable.get(key));

        RandomAccessFile randomAccessFile = new RandomAccessFile(filename, "rwd");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer, 0);
        byteBuffer.flip();

        String s = Charset.defaultCharset().decode(byteBuffer).toString();
        String[] args = s.split("\n");
        int value = 0;
        for (int j = 0; j < args.length; j++) {
            if (String.valueOf(key).startsWith(args[j].split("\t")[0])) {
                value = Integer.parseInt(args[j].split("\t")[1].trim());
            }
        }
        return value;
    }
//array that contains
    public boolean contains(int key) {
        return true;
    }
}

