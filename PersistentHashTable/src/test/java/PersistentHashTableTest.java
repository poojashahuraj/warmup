import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;

import static org.junit.Assert.assertEquals;

/**
 * Created by parallels on 2/18/15.
 */
public class PersistentHashTableTest {

    private RandomAccessFile raf;
    private File file;
    private PersistentHashTable pht;

    @Test
    public void testAppendAndGet() throws Exception {
        for (int i = 0; i < 10; i++) {
            pht.add(i, i * 11);
            assertEquals((i + 1) * 8, raf.length());
            assertEquals(i * 2, pht.getValue(i));
        }

    }/*
    @Test
    public void testLength() throws Exception {
        PersistentHashTable pht = populate(5);
        assertEquals(5, pht.getBucketLength());
    }

    @Test
    public void testDelete() throws Exception {
        PersistentHashTable pht = populate(10);
        pht.remove(1);
        assertEquals(9, pht.getBucketLength());
    }*/

    private PersistentHashTable populate(int howMany) throws Exception {
        for (int i = 0; i < howMany; i++) {
            pht.add(i, i * 11);
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

