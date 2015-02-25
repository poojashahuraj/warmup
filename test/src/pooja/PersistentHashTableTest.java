package pooja;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by parallels on 2/23/15.
 */
public class PersistentHashTableTest {
    private StorageAcessor storageAcessor;

    @Test
    public void testDifferentBucketSizes() throws Exception {
        int bucketCount = 10;
        PersistentHashTable pht = new PersistentHashTable(storageAcessor, bucketCount);
        for (int i = 0; i < 20; i++) {
            pht.put(i, i * 11);
            assertEquals(i * 11, pht.get(i));
        }
        assertEquals(20, pht.size());
    }

    @Test
    public void testReopen() throws Exception {
        PersistentHashTable pht = populate(10, 5);
        assertEquals(10, pht.size());
        storageAcessor.close();
        pht = new PersistentHashTable(storageAcessor, 5);
        assertEquals(10, pht.size());
    }

    @Test
    public void testPutAndGet() throws Exception {
        PersistentHashTable pht = populate(50, 5);
        for (int i = 0; i < 40; i++) {
            pht.put(i, i * 11);
            assertEquals(i * 11, pht.get(i));
        }
    }

    @Test
    public void testLength() throws Exception {
        PersistentHashTable pht = populate(50, 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(10, pht.getBucketLength(i));
        }
    }

    @Test // TODO: fixme :)
    public void testDelete() throws Exception {
        PersistentHashTable pht = populate(40, 7);
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
        PersistentHashTable pht = populate(20, 5);
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

    public PersistentHashTable populate(int howMany, int bucketSize) throws Exception {
        PersistentHashTable pht = new PersistentHashTable(storageAcessor, bucketSize);
        for (int i = 0; i < howMany; i++) {
            pht.put(i, i * 11);
        }
        return pht;
    }

    @Before
    public void setUp() throws Exception {
        storageAcessor = new MemoryAccessor();
    }

    @After
    public void tearDown() throws Exception {
    }
}
