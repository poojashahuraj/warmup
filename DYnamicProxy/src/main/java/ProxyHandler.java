import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by parallels on 1/30/15.
 */
public class ProxyHandler implements InvocationHandler {
    private Object impl;

    public ProxyHandler(Object object){
        this.impl=object;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("The method " + method.getName() + "() with parameters "+ Arrays.toString(args));
        Object result = method.invoke(impl,args);
        System.out.println("The method " + method.getName() + "() ends with " + result.toString());
        return result;
    }

}
