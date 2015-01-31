/**
 * Created by parallels on 1/30/15.
 */
import java.lang.reflect.Proxy;
public class Main {
    public static void main(String[] args){
        MyProxyInterface withoutProxy = new ProxyClass();

        MyProxyInterface withProxy =
                (MyProxyInterface) Proxy.newProxyInstance(
                        withoutProxy.getClass().getClassLoader(),
                        withoutProxy.getClass().getInterfaces(),
                        new ProxyHandler(withoutProxy));
        withoutProxy.add(1,2);
        withoutProxy.multiply(1,2);
        withProxy.add(11, 12);
        withProxy.multiply(11,12);
    }
}
