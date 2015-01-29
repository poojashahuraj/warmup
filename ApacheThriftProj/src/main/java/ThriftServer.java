import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
/**
 * Created by parallels on 1/28/15.
 */
public class ThriftServer {
    public static void main(String[] args) throws TTransportException {
        TServerSocket serverTransport = new TServerSocket(8080);
        add.Processor processor = new add.Processor(new AddImpl());
//        Hello.Processor processor = new Hello.Processor(new HelloImpl());
        TServer server;
        server = new TThreadPoolServer(
                new TThreadPoolServer.Args(serverTransport).processor(processor));
        System.out.println("Starting server on port 8080");
        server.serve();
    }
}