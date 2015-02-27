package pooja;

import java.io.IOException;

interface StorageAccessor {
    long length() throws IOException;

    void seek(int location) throws IOException;

    void writeInt(int i) throws IOException;

    int readInt() throws IOException;

    int read() throws IOException;

    void close() throws IOException;

    int getFilePointer() throws IOException;

    String returnFilePath();
}
