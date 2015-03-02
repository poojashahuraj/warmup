package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by parallels on 3/2/15.
 */
public class AsyncPersistentHashTableTest {
    private File file;
    private AsyncStorageAccessor asyncStorageAccessor;
    private int BUCKET_COUNT = 5;

    @Test
    public void testPutAndGet() throws Exception {
        AsyncPersistentHashTable apht = new AsyncPersistentHashTable(asyncStorageAccessor, BUCKET_COUNT);
        assertEquals(0, (long) apht.length().get());
        populate(10, 5);
        for (int i = 0; i < 10; i++) {
            assertEquals(i + 10, apht.getValue(i));
        }
        assertEquals(10, (long) apht.length().get());
    }

    public void populate(int howMany, int bucketCount) throws Exception {
        AsyncPersistentHashTable apht = new AsyncPersistentHashTable(asyncStorageAccessor, bucketCount);
        for (int i = 0; i < howMany; i++) {
            apht.put(i, i + 10).get();
        }
    }

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteQuietly(file);
        file = File.createTempFile("foo", "bar");
        asyncStorageAccessor = new AsyncStorageAccessor(file);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(file);
    }
}
