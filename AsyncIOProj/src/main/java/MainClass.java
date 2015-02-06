/**
 * Created by parallels on 2/5/15.
 */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class MainClass {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Path path = Paths.get("/home/parallels/test.txt");
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path,
                StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

        ByteBuffer byteBuffer =  ByteBuffer.allocate(100);
        Future<Integer> futureWrite = asynchronousFileChannel
                .write(ByteBuffer.wrap("new buffer".getBytes()),0);
           System.out.println("Number of bytes written: "+futureWrite.get());;
        Future<Integer> futureRead = asynchronousFileChannel
                .read(byteBuffer, 0);
        int readBytes = futureRead.get();
        System.out.println("Number of bytes read: " + readBytes);
        byteBuffer.flip();
        System.out.print(Charset.defaultCharset().decode(byteBuffer));
    }
}
