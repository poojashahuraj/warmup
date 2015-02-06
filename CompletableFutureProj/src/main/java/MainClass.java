import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainClass {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        MainClass mainClass = new MainClass();
        mainClass.execute();
    }

    private void execute() throws IOException, ExecutionException, InterruptedException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        Path path = Paths.get("/home/parallels/test.txt");
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

        CompletableFuture<Integer> completableFutureWrite = CompletableFuture.supplyAsync(() -> {
/*here*/
            Future<Integer> futureWrite = asynchronousFileChannel
                    .write(ByteBuffer.wrap("Formation data System, Boulder".getBytes()), 0);
            int wroteBytes = 0;
            try {
                wroteBytes = futureWrite.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return wroteBytes;
        });
        CompletableFuture<Integer> completableFutureRead = completableFutureWrite.thenApplyAsync((r) -> {
/*here*/
            Future<Integer> futureRead = asynchronousFileChannel.read(byteBuffer, 0);
            int readBytes = 0;
            try {
                readBytes = futureRead.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return readBytes;
        });
        System.out.println("Bytes written in file: " + completableFutureWrite.get());
        System.out.println("Bytes read from the file: " + completableFutureRead.get());
    }
}

