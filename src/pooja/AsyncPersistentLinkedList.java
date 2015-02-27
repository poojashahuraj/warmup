package pooja;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncPersistentLinkedList {
    AsynchronousFileChannel channel;
    File file;
    private StorageAccessor storageAccessor;

    public AsyncPersistentLinkedList(StorageAccessor storageAccessor, File file) {
        this.storageAccessor = storageAccessor;
        this.file = file;
    }

    public CompletableFuture<Void> append(int value) throws IOException, ExecutionException, InterruptedException {
        channel = AsynchronousFileChannel.open(Paths.get(storageAccessor.returnFilePath()),
                StandardOpenOption.WRITE);
        long position = 0;
        byte[] bytes = {};
        bytes = new byte[]{(byte) value, (byte) -1};
        if (file.length() == 0) {
            position = 0;
        } else {
            int currentPosition = 8;
            for (int i = 0; i < 10; i++) {
                storageAccessor.seek(currentPosition);
                if (storageAccessor.read() == -1) {
                    position = currentPosition - 4;
                } else {
                    currentPosition = currentPosition + 8;
                }
            }
        }
        CompletableFuture cf = new CompletableFuture();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        channel.write(byteBuffer.wrap(bytes), position, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                cf.complete(result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed to write");
            }
        });
        return cf;
    }

    public CompletableFuture<Integer> get(int index) throws IOException {
        Path path = FileSystems.getDefault().getPath("file");
        channel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ);
        int position = 0;

        for (int i = 0; i < file.length(); i++) {
            storageAccessor.seek(position);
            if (i == index) {
                break;
            } else {
                position += 8;
            }
        }

        CompletableFuture cf = new CompletableFuture();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        channel.read(byteBuffer, position, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                cf.complete(result);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed to read");
            }
        });
        return cf;
    }

    public CompletableFuture<Integer> size() {

        return null;
    }


}
