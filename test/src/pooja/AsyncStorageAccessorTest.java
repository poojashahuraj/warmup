package pooja;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
public class AsyncStorageAccessorTest {
    private File file;
    private AsyncStorageAccessor accessor;
    @Test
    public void testReadWrite() throws Exception {
        int bufSize = 42;
        byte[] inputBytes = new byte[bufSize];
        new Random().nextBytes(inputBytes);
        ByteBuffer inputBuffer = ByteBuffer.wrap(inputBytes);
        System.out.println("pos " + inputBuffer.position() + " : " + inputBuffer.hasRemaining());
        CompletableFuture<Integer> writeResult = accessor.write(inputBuffer, 0l);
        assertEquals(bufSize, (long) writeResult.get());
        ByteBuffer outputBuffer = ByteBuffer.allocate(bufSize);
        CompletableFuture<Integer> readResult = accessor.read(outputBuffer, 0l);
        assertEquals(bufSize, (long) readResult.get());
        byte[] outputBytes = new byte[bufSize];
        outputBuffer.flip();
        outputBuffer.get(outputBytes);
        assertArrayEquals(inputBytes, outputBytes);
        assertEquals(42l, (long) accessor.size().get());
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