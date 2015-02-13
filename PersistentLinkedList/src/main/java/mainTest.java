import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.assertEquals;

/**
 * Created by parallels on 2/13/15.
 */
public class mainTest
{
    RandomAccessFile raf;

    @Before
    public void setUp(){

    }
    @After
    public void tearDown(){
    }
    @Test
    @Category(PersistentLinkedList.class)
    public void doTest() throws IOException {

        raf = new RandomAccessFile("hello","rwd");
        PersistentLinkedList persistentLinkedList = new PersistentLinkedList(raf);
        raf.setLength(0);
        assertEquals(raf.length(), 0);

        //checking the length after every apped in the file
        persistentLinkedList.append(1111);
        assertEquals(raf.length(), 8);

        persistentLinkedList.append(2222);
        assertEquals(raf.length(), 16);

        persistentLinkedList.append(3333);
        assertEquals(raf.length(), 24);

        persistentLinkedList.append(4444);
        assertEquals(raf.length(), 32);

        persistentLinkedList.append(5555);
        assertEquals(raf.length(), 40);

        //Checking the get method in the order it was inserted
        assertEquals(persistentLinkedList.get(0), 1111);
        assertEquals(persistentLinkedList.get(1), 2222);
        assertEquals(persistentLinkedList.get(2), 3333);
        assertEquals(persistentLinkedList.get(3), 4444);
        assertEquals(persistentLinkedList.get(4), 5555);

        //**removing element from the list
        persistentLinkedList.remove(1);

        System.out.println("-------Simply for test purpose printing nodes and values-----------------------------");
        raf.seek(0);System.out.println(raf.readInt());
        raf.seek(4);System.out.println(raf.readInt());
        raf.seek(16);System.out.println(raf.readInt());
        raf.seek(20);System.out.println(raf.readInt());
        raf.seek(24);System.out.println(raf.readInt());
        raf.seek(28);System.out.println(raf.readInt());
        raf.seek(32);System.out.println(raf.readInt());
        raf.seek(36);System.out.println(raf.readInt());
        System.out.println("------------------------------------");

    }
}
