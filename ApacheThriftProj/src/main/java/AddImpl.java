import org.apache.thrift.TException;

/**
 * Created by parallels on 1/29/15.
 */
public class AddImpl implements add.Iface {
    @Override
    public int addition(int num1, int num2) throws TException {
        return num1+num2;
    }

    @Override
    public long multiply(int num1, int num2) throws TException {
        return num1*num2;
    }
}
