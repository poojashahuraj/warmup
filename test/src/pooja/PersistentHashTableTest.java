package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Created by parallels on 2/18/15.
 */
public class PersistentHashTableTest {

    private RandomAccessFile raf;
    private File file;
    private PersistentHashTable pht;

    @Test
    public void testPutAndGet() throws Exception {
        for (int i = 0; i < 10; i++) {
            pht.put(i, i * 11);
            assertEquals(i * 11, pht.get(i));
        }
    }

    @Test
    public void testLength() throws Exception {
        PersistentHashTable pht = populate(20);
        for (int i = 0; i < 5; i++) {
            assertEquals(4, pht.getBucketLength(i));
        }
    }

    @Test
    public void testDelete() throws Exception {
        PersistentHashTable pht = populate(40);
        for (int i = 5; i < 10; i++) {
            pht.remove(i);
            assertEquals(7, pht.getBucketLength(i % 5));
        }
        for (int i = 15; i < 20; i++) {
            pht.remove(i);
            assertEquals(6, pht.getBucketLength(i % 5));
        }
    }

    @Test
    public void testIterator() throws Exception {
        PersistentHashTable pht = populate(20);
        for (int i = 0; i < 15; i++) {
            Iterator<Integer> iterator = pht.bucket(i);
            assertTrue(iterator.hasNext());
        }
        for (int i = 15; i < 20; i++) {
            Iterator<Integer> iterator = pht.bucket(i);
            assertFalse(iterator.hasNext());
        }

        for (int i = 0; i < 20; i++) {
            Iterator<Integer> iterator = pht.bucket(i);
            assertEquals(i, (int) iterator.next());
        }
    }

    private PersistentHashTable populate(int howMany) throws Exception {
        for (int i = 0; i < howMany; i++) {
            pht.put(i, i * 11);
        }
        return pht;
    }

    @Before
    public void setUp() throws Exception {
        file = File.createTempFile("foo", "bar");
        FileUtils.forceDelete(file);
        raf = new RandomAccessFile(file, "rwd");
        pht = new PersistentHashTable(raf);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(file);
    }
}

