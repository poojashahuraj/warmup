/**
 * Created by parallels on 1/28/15.
 */

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

public class ThriftClient
{
    public static void main(String[] args) throws TException {
        TSocket transport = new TSocket("localhost", 8080);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        add.Client client = new add.Client(protocol);
//        Hello.Client client = new Hello.Client(protocol);
        int resultadd = client.addition(4,5);
        long resultmul = client.multiply(4,5);
//        String result = client.hi();
        System.out.println("Addition is : " + resultadd);
        System.out.print("result multiplication"+resultmul);
        transport.close();
    }
}