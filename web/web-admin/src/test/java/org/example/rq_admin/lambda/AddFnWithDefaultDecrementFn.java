package org.example.rq_admin.lambda;

/**
 * 接口里面不止一个方法，但是只有一个方法没有默认实现的时候，这个接口也可以被叫做函数式接口
 */
public interface AddFnWithDefaultDecrementFn {
    int returnArgument(int a);

    /**
     * 默认实现的减法
     * @param a
     * @param b
     * @return
     */
    default int decrement(int a, int b) {
        return a - b;
    }
}
