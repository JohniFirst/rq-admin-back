package org.example.rq_admin.lambda;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

// 这是常规的实现一个接口的方法
@Data
@Slf4j
public class CommonAddFnImpl implements AddFn {

    private String name;

    @Override
    public int add(int a, int b) {
        int result = a + b;
        log.info("常规的接口重写的方式的计算结果：" + a + " + " + b + " = " + result );
        return result;
    }
}
