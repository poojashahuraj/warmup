package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 * Created by parallels on 3/2/15.
 */
public class AsyncPersistentHashTableTest {
    private File file;
    private AsyncStorageAccessor asyncStorageAccessor;
    private int BUCKET_COUNT = 5;

    @Test
    public void testBucketEmpty() throws ExecutionException, InterruptedException {
        AsyncPersistentHashTable apht = new AsyncPersistentHashTable(asyncStorageAccessor, BUCKET_COUNT);
        int val = 0;
        int position = 0;
        for (int i = 0; i < BUCKET_COUNT; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            asyncStorageAccessor.read(buffer, position).get();
            buffer.flip();
            val = buffer.getInt();
            assertEquals(-1, val);
            position += 4;
        }
    }

    @Test
    public void testPutAndGet() throws Exception {
        AsyncPersistentHashTable apht = new AsyncPersistentHashTable(asyncStorageAccessor, BUCKET_COUNT);
        assertEquals(0, (long) apht.totalNumberOfNodes().get());

        int val = 0;
        for (int i = 0; i < 10; i++) {
            apht.put(i, i + 10).get();
            val = apht.getValue(i).get();
            assertEquals(i + 10, val);
        }
        assertEquals(10, (long) apht.totalNumberOfNodes().get());
    }

    @Test
    public void testBucketLength() throws Exception {
        AsyncPersistentHashTable apht = new AsyncPersistentHashTable(asyncStorageAccessor, BUCKET_COUNT);
        //populate
        assertEquals(0, (long) apht.totalNumberOfNodes().get());
        for (int i = 0; i < 40; i++) {
            apht.put(i, i + 10).get();
        }
        int numberOfNodes = 0;
        for (int i = 0; i < BUCKET_COUNT; i++) {
            numberOfNodes = apht.getBucketLength(i).get();
            assertEquals(8, numberOfNodes);
        }
        assertEquals(40, (long) apht.totalNumberOfNodes().get());
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
