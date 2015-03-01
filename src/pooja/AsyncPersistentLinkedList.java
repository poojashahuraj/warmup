package pooja;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CompletableFuture;

public class AsyncPersistentLinkedList {
    private AsyncStorageAccessor accessor;

    public AsyncPersistentLinkedList(AsyncStorageAccessor accessor) {
        this.accessor = accessor;
    }

    public CompletableFuture<Integer> append(int value) {
        CompletableFuture<Integer> cf;
        cf = accessor.size().thenCompose(fileSize -> {
            if (fileSize == 0) {
                int[] data = {value, -1};
                ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(data);
                byte[] array = byteBuffer.array();

                return accessor.write(ByteBuffer.wrap(array), 0l);
            } else {
                return lastNode(0).thenCompose(lastNodeAddress ->
                        accessor.size().thenCompose(insertedNodeAddress -> {
                            int[] data = {value, -1};
                            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
                            IntBuffer intBuffer = byteBuffer.asIntBuffer();
                            intBuffer.put(data);
                            byte[] array = byteBuffer.array();

                            return accessor.write(ByteBuffer.wrap(array), insertedNodeAddress).thenCompose(x -> {

                                int[] data1 = {insertedNodeAddress.intValue()};
                                ByteBuffer byteBuffer1 = ByteBuffer.allocate(data1.length * 4);
                                IntBuffer intBuffer1 = byteBuffer1.asIntBuffer();
                                intBuffer1.put(data1);
                                byte[] array1 = byteBuffer.array();
                                return accessor.write(ByteBuffer.wrap(array1), lastNodeAddress + 4);
                            });
                        }));
            }
        });
        return cf;
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
                return Long.MAX_VALUE;
            }
        });
    }
}