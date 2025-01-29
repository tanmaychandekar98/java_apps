package java8;

import java.util.function.*;

public class Client {
    public static void main(String[] s) {
        // MyInterface my = () -> System.out.println("jail");
        // my.getValue();

        // System.out.println(multiply.andThen(printEven).apply(4));
        // System.out.println(printEven.andThen(multiply).apply(2));

        // System.out.println(add.andThen(multiply).andThen(printEven).apply(1, 2));


        Consumer<String> print = x -> System.out.println(x);
        print.accept("jain ho");


        Supplier<Double> divider = () -> { return 1.00001; };

        print.accept(divider.get().toString());
    }

    static Predicate<Integer> isEven = num -> num%2==0;

    static Function<Integer, String> printEven = x -> {
        if(isEven.test(x)) {
            return "Even!!" + x;
        }
        return "Odd!!" + x;
    };

    static Function<Integer, Integer> multiply = x -> {
        return x*x;
    };

    static BiFunction<Integer, Integer, Integer> add = (x, y) -> x+y;
}
