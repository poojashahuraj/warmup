package pooja;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MemoryAccessor implements StorageAccessor {
    private ByteBuffer byteBuffer;
    private long length;

    public MemoryAccessor() {
        byteBuffer = ByteBuffer.allocate(2048 * 2048);
        length = 0;
    }

    @Override
    public long length() throws IOException {
        return length;
    }

    @Override
    public void seek(int location) throws IOException {
        byteBuffer.position(location);
    }

    @Override
    public void writeInt(int i) throws IOException {
        byteBuffer.putInt(i);
        length = Math.max(byteBuffer.position(), length);
    }

    @Override
    public int readInt() throws IOException {
        return byteBuffer.getInt();
    }

    @Override
    public int read() throws IOException {
        return byteBuffer.get();
    }

    @Override
    public int getFilePointer() throws IOException {
        return byteBuffer.position();
    }

    @Override
    public String returnFilePath() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
