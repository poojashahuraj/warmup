import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.nio.file.StandardOpenOption;

public class MainClass {
    public static AsynchronousFileChannel channel;

    public MainClass(AsynchronousFileChannel channel) {
        this.channel = channel;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Path path = Paths.get("/home/parallels/test.txt");
        MainClass m = new MainClass(channel);
        m.AsyncFileIo(path);
    }

    public void AsyncFileIo(Path path) throws IOException, ExecutionException, InterruptedException {
        byte[] bytes = "Hello world".getBytes();
        channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        System.out.println("Number of bytes read are: " + read(0l).get());
        System.out.println("Number of bytes written are: " + write(bytes, 0l).get());
    }

    public CompletableFuture<Integer> write(byte[] bytes, long position) throws ExecutionException, InterruptedException {
        CompletableFuture cf = new CompletableFuture();
        ByteBuffer bf = ByteBuffer.allocate(100);
        channel.write(bf.wrap(bytes), position, null, new CompletionHandler<Integer, String>() {
            @Override
            public void completed(Integer result, String attachment) {
                cf.complete(result);
            }
            @Override
            public void failed(Throwable exc, String attachment) {
                System.out.print("Failed to write");
            }
        });
        return cf;
    }

    public CompletableFuture<Integer> read(Long position) throws ExecutionException, InterruptedException {
        ByteBuffer bf = ByteBuffer.allocate(100);
        CompletableFuture cf = new CompletableFuture();
        channel.read(bf, position, null,
                new CompletionHandler<Integer, String>() {
                    @Override
                    public void completed(Integer result, String attachment) {
                        cf.complete(result);
                    }

                    @Override
                    public void failed(Throwable exc, String attachment) {
                        System.out.println("Failed to read");
                    }
                }
        );
        return cf;
    }
}