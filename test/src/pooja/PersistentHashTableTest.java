package pooja;
/**
 * Created by parallels on 2/19/15.
 */

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

    @Test
    public void testDifferentBucketSizes() throws Exception {
        int bucketCount = 10;
        PersistentHashTable pht = new PersistentHashTable(raf, bucketCount);
        for (int i = 0; i < 20; i++) {
            pht.put(i, i * 11);
            assertEquals(i * 11, pht.get(i));
        }
        assertEquals(20, pht.size());
    }

    @Test
    public void testReopen() throws Exception {
        PersistentHashTable pht = populate(10);
        assertEquals(10, pht.size());
        raf.close();
        raf = new RandomAccessFile(file, "rwd");
        pht = new PersistentHashTable(raf, 5);
        assertEquals(10, pht.size());
    }

    @Test
    public void testPutAndGet() throws Exception {
        PersistentHashTable pht = populate(50);
        for (int i = 0; i < 40; i++) {
            pht.put(i, i * 11);
            assertEquals(i * 11, pht.get(i));
        }
    }

    @Test
    public void testLength() throws Exception {
        PersistentHashTable pht = populate(50);
        for (int i = 0; i < 5; i++) {
            assertEquals(10, pht.getBucketLength(i));
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
        PersistentHashTable pht = new PersistentHashTable(raf, 5);
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
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(file);
    }

    // TODO:
    // - Make PHTT fast -  2 options:
    //   A/ Encapsulate RandomAccesFile behind an API of your design, provide a bridge to use the actual RAF, provide a memory implementation for the test
    //   B/ Extend/override RandomAccessFile behavior
    // - Research async file and socket IO APIs available in the JDK
    // - Write 2 paragraphs describing how you're going to implement async IO for your linked list (how your async IO operations will be sequenced).

}
