package pooja;

import java.util.concurrent.CompletableFuture;

/**
 * Created by parallels on 3/2/15.
 */

public class AsyncPersistentHashTable {
    private int BUCKET_COUNT;
    private int endOfFIleAddress;
    private AsyncStorageAccessor asyncStorageAccessor;

    public AsyncPersistentHashTable(AsyncStorageAccessor accessor, int bucketCount) {
        asyncStorageAccessor = accessor;
        BUCKET_COUNT = bucketCount;
    }

    public CompletableFuture<Integer> length() {
        return null;
    }

    public CompletableFuture<Integer> put(int key, int value) {
        return null;
    }

    public CompletableFuture<Integer> getValue(int key) {
        return null;
    }
}
