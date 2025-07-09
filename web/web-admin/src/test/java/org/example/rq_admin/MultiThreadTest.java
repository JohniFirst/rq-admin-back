package org.example.rq_admin;

class EvenPrintTest extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() +  "   Even Print ========  " + i);
            }
        }
    }
}

class OddPrintTest extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() +  "   Odd Print ----------  " + i);
            }
        }
    }
}

public class MultiThreadTest {
    public static void main(String[] args) {
        EvenPrintTest even = new EvenPrintTest();
        OddPrintTest odd = new OddPrintTest();
        
        even.start();
        odd.start();
    }
}
