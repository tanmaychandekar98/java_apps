package java8;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class StreamClient {
    
    public static void main(String[] a) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

        // list.forEach(x -> System.out.println(x));

        Function<Integer, Integer> squares = x -> x*x;
        // Consumer<Integer> print = 

        list.stream().map(squares).collect(Collectors.toList()).forEach(StreamClient::print);
    }

    private static void print(Integer x) {
        System.out.println(x);
    }
}
