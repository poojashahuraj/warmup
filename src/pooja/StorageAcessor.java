package pooja;

import java.io.IOException;

/**
 * Created by parallels on 2/18/15.
 */
interface StorageAcessor {
    long length() throws IOException;

    void seek(int location) throws IOException;

    void writeInt(int i) throws IOException;

    int readInt() throws IOException;

    int read() throws IOException;

    void close() throws IOException;
}
