package pooja;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by parallels on 2/23/15.
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

    public PersistentHashTable populate(int howMany) throws Exception {
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
        raf = new fancypht(file, "rwd") {
            public int read() throws IOException {
                return super.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return super.read(b, off, len);
            }

            @Override
            public int read(byte[] b) throws IOException {
                return super.read(b);
            }

            @Override
            public int skipBytes(int n) throws IOException {
                return super.skipBytes(n);
            }

            @Override
            public void write(int b) throws IOException {
                super.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                super.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                super.write(b, off, len);
            }

            @Override
            public long getFilePointer() throws IOException {
                return super.getFilePointer();
            }

            @Override
            public void seek(long pos) throws IOException {
                super.seek(pos);
            }

            @Override
            public long length() throws IOException {
                return super.length();
            }

            @Override
            public void setLength(long newLength) throws IOException {
                super.setLength(newLength);
            }

            @Override
            public void close() throws IOException {
                super.close();
            }
        };

    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(file);
    }
}
class fancypht extends RandomAccessFile {
    public fancypht(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }
    public fancypht(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }
    @Override
    public int read() throws IOException {
        return super.read();
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }
    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }
    @Override
    public int skipBytes(int n) throws IOException {
        return super.skipBytes(n);
    }
    @Override
    public void write(int b) throws IOException {
        super.write(b);
    }
    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }
    @Override
    public long getFilePointer() throws IOException {
        return super.getFilePointer();
    }
    @Override
    public void seek(long pos) throws IOException {
        super.seek(pos);
    }
    @Override
    public long length() throws IOException {
        return super.length();
    }
    @Override
    public void setLength(long newLength) throws IOException {
        super.setLength(newLength);
    }
    @Override
    public void close() throws IOException {
        super.close();
    }
}
