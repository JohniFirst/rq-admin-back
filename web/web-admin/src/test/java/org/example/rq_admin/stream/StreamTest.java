package org.example.rq_admin.stream;

import java.util.List;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        streamList();
    }

    private static void streamList() {
        List<Integer> integers = List.of(1, 2, 3, 4, 5);

        Stream<Integer> peek = integers.stream().filter(i -> i % 2 == 0).map(i -> i * 2).peek(System.out::println);

        System.out.println(peek);
    }
}
