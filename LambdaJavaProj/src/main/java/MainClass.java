/**
 * Created by parallels on 2/3/15.
 */
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MainClass {
    public static void main(String[] args) {
        MainClass mainClass =new MainClass();
        mainClass.execute();
    }

    private void execute() {
        HelloService helloService= (String name) -> {return "Hello"+" "+name+" ! Basic";};
        String resultGreeting = helloService.hello("Formation");
        System.out.println(resultGreeting);

        Runnable runnable = () -> {System.out.println("Hello from runnable thread");};
        Thread thread = new Thread(runnable);
        thread.start();

        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Consumer<Integer>  consumer = x -> System.out.println(x * x);
        Supplier <String> supplier = () ->{return "Hello from supplier";};
        BiFunction<Integer , Integer, String> bifunction = (x,y) ->{return "Addition is "+(String.valueOf(x+y));};

        integers.forEach(consumer);
        System.out.println(supplier.get());
        System.out.println(bifunction.apply(3,5));

        Function<String, Integer> func = x -> Integer.parseInt(x);
        System.out.println("Result from function: " + func.apply("10"));
    }
    public interface HelloService {
            String hello(String name);
        }
}

