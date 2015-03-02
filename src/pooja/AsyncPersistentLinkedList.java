package pooja;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
public class AsyncPersistentLinkedList {
    private AsyncStorageAccessor accessor;

    public AsyncPersistentLinkedList(AsyncStorageAccessor accessor) {
        this.accessor = accessor;
    }

    public CompletableFuture<Integer> append(int value) {
        CompletableFuture<Integer> cf;
        cf = accessor.size().thenCompose(fileSize -> {
            // If file size is zero, this is the first node
            if (fileSize == 0) {
                return accessor.write(makeNewNode(value), 0l);
            } else {
                // Obtain address of last node
                return lastNode(0).thenCompose(lastNodeAddress ->
                        // Obtain file size, which will be our insertion point
                        accessor.size().thenCompose(insertedNodeAddress -> {
                            // Prepare last node
                            ByteBuffer newNode = makeNewNode(value);
                            return accessor.write(newNode, insertedNodeAddress).thenCompose(x -> {
                                // update previous node pointer
                                ByteBuffer previousNodeAddress = ByteBuffer.allocate(4);
                                previousNodeAddress.putInt(insertedNodeAddress.intValue());
                                previousNodeAddress.flip();
                                return accessor.write(previousNodeAddress, lastNodeAddress + 4);
                            });
                        }));
            }
        });
        return cf;
    }

    private ByteBuffer makeNewNode(int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putInt(value);
        byteBuffer.putInt(-1);
        byteBuffer.flip();
        return byteBuffer;
    }

    public CompletableFuture<Integer> getValue(int index) {
        CompletableFuture<Integer> cf = accessor.size().thenCompose(fileSize -> {
            int position = 0;
            for (int i = 0; i < fileSize; i++) {
                if (i == index) {
                    break;
                } else {
                    position = position + 8;
                }
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            return accessor.read(byteBuffer, position).thenCompose(i -> {
                byteBuffer.flip();
                int value = byteBuffer.getInt();
                int addr = byteBuffer.getInt();
                return CompletableFuture.completedFuture(value);
            });
        });
        return cf;
    }

    private CompletableFuture<Integer> lastNode(int startingPoint) {
        ByteBuffer node = ByteBuffer.allocate(8);
        return accessor.read(node, startingPoint).thenCompose(i -> {
            node.flip();
            int value = node.getInt();
            int next = node.getInt();
            if (next == -1) {
                return CompletableFuture.completedFuture(startingPoint);
            } else {
                return lastNode(next);
            }
        });
    }

    public CompletableFuture<Long> length() {
        CompletableFuture<Long> cf = accessor.size();
        return cf.thenApply(fileSize -> {
            if (fileSize == 0) {
                return 0l;
            } else {
                return fileSize / 8;
            }
        });
    }
}