package org.example.rq_admin.lambda;

/**
 * 接口里面只有一个未实现的方法的接口叫做函数式接口
 * <p>
 * 注解是让idea帮助检查函数式接口
 */
@FunctionalInterface
public interface AddFn {
    int add(int a, int b);
}
