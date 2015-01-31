/**
 * Created by parallels on 1/29/15.
 */
public class ProxyClass implements MyProxyInterface {
    @Override
    public int add(int a, int b) {
        int c = a+b;
        return c;
    }

    public int multiply(int a, int b) {
        int c = a*b;
        return c;
    }

}
