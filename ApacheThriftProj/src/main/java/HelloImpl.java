/**
 * Created by parallels on 1/28/15.
 */
import org.apache.thrift.TException;

public class HelloImpl implements Hello.Iface
{
public String hi() throws TException
    {
        return "Hello World using Thrift using maven";
    }
}
