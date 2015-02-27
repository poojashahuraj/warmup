package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class AsyncPersistentLinkedListTest {
    private File file;
    private AsyncStorageAccessor accessor;

    @Test
    public void testAppendAndGet() throws Exception {
        AsyncPersistentLinkedList apll = new AsyncPersistentLinkedList(accessor);
        assertEquals(0, (long) apll.length().get());

        for (int i = 0; i < 10; i++) {
            apll.append(i).get();
            assertEquals(i, apll.get(i));
        }

        assertEquals(10, (long) apll.length().get());

    }

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteQuietly(file);
        file = File.createTempFile("foo", "bar");
        accessor = new AsyncStorageAccessor(file);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(file);
    }
}
