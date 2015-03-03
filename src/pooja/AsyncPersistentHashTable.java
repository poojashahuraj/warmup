package pooja;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by parallels on 3/2/15.
 */

public class AsyncPersistentHashTable {
    private int BUCKET_COUNT;
    private AsyncStorageAccessor asyncStorageAccessor;
    public AsyncPersistentHashTable(AsyncStorageAccessor accessor, int bucketCount) throws ExecutionException, InterruptedException {
        asyncStorageAccessor = accessor;
        BUCKET_COUNT = bucketCount;
        if (asyncStorageAccessor.size().get() == 0) {
            init();
        }
    }

    private void init() throws ExecutionException, InterruptedException {
        long position = 0;
        for (int i = 0; i < BUCKET_COUNT; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            byteBuffer.putInt(-1);
            byteBuffer.flip();
            asyncStorageAccessor.write(byteBuffer, position).get();
            position += 4;
        }
    }

    public CompletableFuture<Integer> put(int key, int value) throws ExecutionException, InterruptedException {
        int bucketStartPosition = getBucketStartAddress(key);
        int bucketNumber = key % BUCKET_COUNT;
        CompletableFuture<Integer> cf = asyncStorageAccessor.size().thenCompose(endOfFileAddress -> {
            // If bucket is empty , this is the first node in that bucket
            if (bucketStartPosition == -1) {
                //write the first node at the end of the file
                return asyncStorageAccessor.write(makeNewNode(key, value), endOfFileAddress).thenCompose(x -> {
                    //update teh list which is present at the start of the file with bucket start position
                    ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                    byteBuffer.putInt(endOfFileAddress.intValue());
                    byteBuffer.flip();

                    return asyncStorageAccessor.write(byteBuffer, bucketNumber * 4);
                });
            } else {
                //get the address of the last node in the bucket
                return lastNode(bucketStartPosition).thenCompose(lastNodeAddress ->
                        //seek for the EOF address
                        //write the new node at the end of the file
                {
                    ByteBuffer newNode = makeNewNode(key, value);
                    return asyncStorageAccessor.write(newNode, endOfFileAddress).thenCompose(x -> {

                        //update the address of the previous node in the bucket
                        ByteBuffer buffer1 = ByteBuffer.allocate(4);
                        buffer1.putInt(endOfFileAddress.intValue());
                        buffer1.flip();
                        return asyncStorageAccessor.write(buffer1, lastNodeAddress + 8);
                    });
                });
            }
        });
        return cf;
    }

    //given key this method returns value
    public CompletableFuture<Integer> getValue(int key) throws ExecutionException, InterruptedException {
        int value = traceNode(getBucketStartAddress(key), key).get();
        return CompletableFuture.completedFuture(value);
    }

    public CompletableFuture<Integer> totalNumberOfNodes() throws ExecutionException, InterruptedException {
        final int[] totalNumberOfNodes = {0};
        CompletableFuture<Long> cf = asyncStorageAccessor.size();
        cf.thenApply(fileSize -> {
            fileSize = fileSize - BUCKET_COUNT * 4;
            if (fileSize == 0) {
                return 0l;
            } else {
                int n = 0;
                for (int i = 0; i < BUCKET_COUNT; i++) {
                    try {
                        n = getBucketLength(i).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    totalNumberOfNodes[0] = totalNumberOfNodes[0] + n;
                }
                return totalNumberOfNodes[0];
            }
        });
        return CompletableFuture.completedFuture(totalNumberOfNodes[0]);
    }

    //recursive function for calculating number of nodes
    private CompletableFuture<Integer> numberOfNodes(int startingPoint, int numberOfNodes) {
        final ByteBuffer node = ByteBuffer.allocate(12);
        return asyncStorageAccessor.read(node, startingPoint).thenCompose(i -> {
            node.flip();
            node.position(8);
            int readAAddress = node.getInt();
            if (readAAddress == -1) {
                return CompletableFuture.completedFuture(numberOfNodes);
            } else {
                return numberOfNodes(readAAddress, numberOfNodes + 1);
            }
        });
    }

    //recursive function to give the address of the last when provided the stating node address for one bucket
    private CompletableFuture<Integer> lastNode(int startingPoint) {
        ByteBuffer node = ByteBuffer.allocate(12);
        return asyncStorageAccessor.read(node, startingPoint).thenCompose(i -> {
            node.flip();
            node.position(8);
            int nextAddress = node.getInt();
            if (nextAddress == -1) {
                return CompletableFuture.completedFuture(startingPoint);
            } else {
                return lastNode(nextAddress);
            }
        });
    }

    //this recursive method will trace particular linked in list in one bucket and return the node address matching to key
    private CompletableFuture<Integer> traceNode(int startingPoint, int key) {
        ByteBuffer node = ByteBuffer.allocate(12);
        return asyncStorageAccessor.read(node, startingPoint).thenCompose(i -> {
            node.flip();
            int readKey = node.getInt();
            int readValue = node.getInt();
            int readAAddress = node.getInt();
            if (key == readKey) {
                return CompletableFuture.completedFuture(readValue);
            } else {
                return traceNode(readAAddress, key);
            }
        });
    }


    //get length of the bucket (number of nodes present in that linked list) when given the key
    public CompletableFuture<Integer> getBucketLength(int key) throws ExecutionException, InterruptedException {
        int bucketStartAddress = getBucketStartAddress(key);
        int numberOfNodes = numberOfNodes(bucketStartAddress, 1).get();
        return CompletableFuture.completedFuture(numberOfNodes);
    }

    //method to create new node
    private ByteBuffer makeNewNode(int key, int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byteBuffer.putInt(key);
        byteBuffer.putInt(value);
        byteBuffer.putInt(-1);
        byteBuffer.flip();
        return byteBuffer;
    }

    //when key is provided this method returns the starting address of the bucket
    public int getBucketStartAddress(int key) throws ExecutionException, InterruptedException {
        int bucketNumber = key % BUCKET_COUNT;
        int start = bucketNumber * 4;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        asyncStorageAccessor.read(buffer, start).get();
        buffer.flip();
        int bucketStartAddress = buffer.getInt();
        return bucketStartAddress;
    }
}
