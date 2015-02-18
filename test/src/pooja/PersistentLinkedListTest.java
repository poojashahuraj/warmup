package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import static org.junit.Assert.*;

public class PersistentLinkedListTest {
    private RandomAccessFile raf;
    private File file;
    private PersistentLinkedList pll;

    @Test
    public void testAppendAndGet() throws IOException {
        for (int i = 0; i < 10; i++) {
            pll.append(i * 2);
            assertEquals((i + 1) * 8, raf.length());
            assertEquals(i * 2, pll.getValue(i));
        }

    }

    /*        @Test(expected = ArrayIndexOutOfBoundsException.class)
            public void testOutOfBounds() throws Exception {
                PersistentLinkedList pll = populate(20);
                pll.getValue(200);
            }
    */
    @Test
    public void testLength() throws Exception {
        PersistentLinkedList pll = populate(5);
        assertEquals(5, pll.length());
    }

    @Test
    public void testDelete() throws Exception {
        PersistentLinkedList pll = populate(10);
        pll.remove(1);
        assertEquals(9, pll.length());
    }

    @Test
    public void testIterator() throws Exception {
        PersistentLinkedList pll = populate(10);
        Iterator<Integer> iterator = pll.iterator();

        for (int i = 0; i < 9; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(i * 2, (int) iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    private PersistentLinkedList populate(int howMany) throws Exception {
        for (int i = 0; i < howMany; i++) {
            pll.append(i * 2);
        }
        return pll;
    }

    @Before
    public void setUp() throws Exception {
        file = File.createTempFile("foo", "bar");
        FileUtils.forceDelete(file);
        raf = new RandomAccessFile(file, "rwd");
        pll = new PersistentLinkedList(raf);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(file);
    }
}
