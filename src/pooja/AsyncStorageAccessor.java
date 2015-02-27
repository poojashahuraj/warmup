package pooja;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

public class AsyncStorageAccessor {
    private final AsynchronousFileChannel channel;

    public AsyncStorageAccessor(File file) throws IOException {
        channel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    public CompletableFuture<Integer> write(ByteBuffer buffer, long position) {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        channel.write(buffer, position, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                cf.complete(result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                cf.completeExceptionally(exc);
            }
        });

        return cf;
    }


    public CompletableFuture<Integer> read(ByteBuffer buffer, long position) {
        CompletableFuture<Integer> cf = new CompletableFuture<>();

        channel.read(buffer, position, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                cf.complete(result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                cf.completeExceptionally(exc);
            }
        });
        return cf;
    }

    public CompletableFuture<Long> size() {
        CompletableFuture<Long> cf = new CompletableFuture<>();
        try {
            cf.complete(channel.size());
        } catch (IOException e) {
            cf.completeExceptionally(e);
        }
        return cf;
    }
}
