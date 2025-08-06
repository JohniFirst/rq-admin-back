package org.example.rq_admin.lambda;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Base64;

@Slf4j
public class LambdaFunctional {
    public static void main(String[] args) {
        SecretKey key = Jwts.SIG.HS512.key().build();

        // 2. 存储为 Base64
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Saved Key (Base64): " + base64Key);
        
        commonUsage();

        anonymousUsage();

        lambdaUsage1();

        lambdaUsage2();

        lambdaUsage3();

        lambdaUsage4();

        addFnWithDefaultDecrementFn();
    }

    private static void commonUsage() {
        CommonAddFnImpl commonAddFn = new CommonAddFnImpl();
        commonAddFn.add(1, 2);
    }

    private static void anonymousUsage() {
        AddFn addFn = new AddFn() {
            @Override
            public int add(int a, int b) {
                int result = a + b;
                log.info("使用匿名实现类的计算结果：{} + {} = {}", a, b, result);
                return result;
            }
        };

        addFn.add(3, 5);
    }

    /**
     * lambda表达式，可以省略掉接口实现中确认的那些字符，
     * new AddFn() {
     *
     * @ Override public int add
     * }
     */
    private static void lambdaUsage1() {
        AddFn addFn = (int a, int b) -> {
            int result = a + b;
            log.info("使用lambda表达式的计算结果1：{} + {} = {}", a, b, result);
            return result;
        };

        addFn.add(1, 2);
    }

    /**
     * 更进一步的，由于参数类型是确认的，参数类型也可以省略
     */
    private static void lambdaUsage2() {
        AddFn addFn = ((a, b) -> {
            int result = a + b;
            log.info("使用lambda表达式的计算结果2：{} + {} = {}", a, b, result);
            return result;
        });
        addFn.add(1, 2);
    }

    /**
     * 如果方法体只有一行的话，大括号和return都可以省略
     */
    private static void lambdaUsage3() {
        AddFn addFn = (a, b) -> a + b;
        int result = addFn.add(1, 2);
        log.info("使用lambda表达式的计算结果3，这里就获取不到参数值：{}", result);
    }

    /**
     * 更进一步的，甚至可以省略成这样
     */
    private static void lambdaUsage4() {
        AddFn addFn = Integer::sum;
        int result = addFn.add(1, 2);
        log.info("使用lambda表达式的计算结果4，这里就获取不到参数值：{}", result);
    }

    /**
     * 形参如果只有一个的话，连括号也可以省略
     */
    private static void addFnWithDefaultDecrementFn() {
        AddFnWithDefaultDecrementFn addFnWithDefaultDecrementFn = a -> a;

        int result = addFnWithDefaultDecrementFn.returnArgument(1);
        log.info("使用lambda表达式的计算结果5，实现的是具有默认方法的接口的返回传入参数的结果：{}", result);

        int decrementResult = addFnWithDefaultDecrementFn.decrement(1, 2);
        log.info("使用lambda表达式的计算结果5，实现的是具有默认方法的接口的默认方法的结果：{}", decrementResult);
    }
}
