package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class AsyncPersistentLinkedListTest {
    private StorageAccessor storageAccessor;
    private File file;

    @Test
    public void testAppendAndGet() throws IOException, ExecutionException, InterruptedException {
        AsyncPersistentLinkedList asyncPersistentLinkedList = new AsyncPersistentLinkedList(storageAccessor, file);
        for (int i = 0; i < 10; i++) {
            asyncPersistentLinkedList.append(i * 2);
            assertEquals(i * 2, asyncPersistentLinkedList.get(i));

        }
    }

    @Before
    public void setUp() throws Exception {
        file = File.createTempFile("foo", "bar");
        FileUtils.forceDelete(file);
        storageAccessor = new FileAccessor(file);
//        storageAccessor = new MemoryAccessor();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(file);
    }
}
