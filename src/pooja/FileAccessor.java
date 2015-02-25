package pooja;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileAccessor implements StorageAcessor {
    private RandomAccessFile raf;

    public FileAccessor(File file) throws IOException {
        this.raf = new RandomAccessFile(file, "rwd");
    }

    @Override
    public long length() throws IOException {
        return raf.length();
    }

    @Override
    public void seek(int location) throws IOException {
        raf.seek(location);
    }

    @Override
    public void writeInt(int i) throws IOException {
        raf.writeInt(i);
    }

    @Override
    public int readInt() throws IOException {
        return raf.readInt();
    }

    @Override
    public int read() throws IOException {
        return raf.read();
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }
}
