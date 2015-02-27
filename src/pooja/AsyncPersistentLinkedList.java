package pooja;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class AsyncPersistentLinkedList {

    private AsyncStorageAccessor accessor;

    public AsyncPersistentLinkedList(AsyncStorageAccessor accessor) {
        this.accessor = accessor;
    }

    public CompletableFuture<Void> append(int value) {
        CompletableFuture<Integer> cf = accessor.size().thenCompose(fileSize -> {
            if (fileSize == 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(8);
                byteBuffer.putInt(value);
                byteBuffer.putInt(-1);
                // Write first node
                return accessor.write(byteBuffer, 0);
            } else {
                // Get last node address
                return lastNode(0).thenCompose(lastNodeAddress ->
                        // Get file size (that will be our insertion point)
                        accessor.size().thenCompose(insertedNodeAddress -> {

                            // Create a byte buffer for our new node
                            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
                            byteBuffer.putInt(value);
                            byteBuffer.putInt(-1);

                            // Insert our new node
                            return accessor.write(byteBuffer, insertedNodeAddress).thenCompose(x -> {
                                ByteBuffer buf = ByteBuffer.allocate(4);
                                buf.putInt(insertedNodeAddress.intValue());

                                // Update pointer of last node
                                return accessor.write(byteBuffer, lastNodeAddress + 4);
                            });
                        }));
            }
        });

        // need to convert an integer to Void
        return cf.thenApply(i -> null);

    }

    private CompletableFuture<Integer> lastNode(int startingPoint) {
        ByteBuffer node = ByteBuffer.allocate(8);
        return accessor.read(node, startingPoint).thenCompose(i -> {
            int value = node.getInt();
            int next = node.getInt();
            if (next == -1) {
                return CompletableFuture.completedFuture(startingPoint);
            } else {
                return lastNode(next);
            }
        });
    }

    public CompletableFuture<Integer> get(int n) {
        return null;
    }

    public CompletableFuture<Long> length() {
        CompletableFuture<Long> cf = accessor.size();
        return cf.thenApply(fileSize -> {
            if (fileSize == 0) {
                return 0l;
            } else {
                return Long.MAX_VALUE;
            }
        });
    }
}
