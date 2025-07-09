package org.example.rq_admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RqAdminApplicationTests {

    public static void main(String[] args) {
        String str1 = "str";
        String str2 = "str1";
        boolean b1 = judgeStr(str1, str2);
        System.out.println(b1);
    }

    public static boolean judgeStr(String str1, String str2) {
        return str1.equals(str2);
    }

    @Test
    void contextLoads() {
    }

}
