package com.czg.ali;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenzg
 * @date 2018.09.22 19:06
 * @description
 *
 *
 * 1. 三个线程交替打印1-100的整数，要求输出结果有序；
 * 样例：
 * Sample:
 * Thread1: 1
 * Thread2: 2
 * Thread3: 3
 * Thread1: 4
 * Thread2: 5
 * Thread3: 6
 * ....g
 * Thread3: 99
 * Thread1: 100
 *
 **/
public class ThreeThreadAbc {

    private static volatile int a = 10;

    static Lock lock = new ReentrantLock();
    static Condition conditionA = lock.newCondition();
    static Condition conditionB = lock.newCondition();
    static Condition conditionC = lock.newCondition();

    public static void main(String[] args) {
        Thread t1 = new Thread(new AddThread("A",conditionA,conditionB));
        Thread t2 = new Thread(new AddThread("B",conditionB,conditionC));
        Thread t3 = new Thread(new AddThread("C",conditionC,conditionA));

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(t1);
        executor.submit(t2);
        executor.submit(t3);

        executor.shutdown();
    }

    static class AddThread implements Runnable{

        private String threadName;
        private Condition condition1;
        private Condition condition2;

        public AddThread(String threadName,Condition condition1,Condition condition2){
            this.threadName = threadName;
            this.condition1 = condition1;
            this.condition2 = condition2;
        }
        @Override
        public void run() {
            doTask(condition1,condition2);
        }

        public void doTask(Condition conditionA, Condition conditionB){
            lock.lock();
            try {
                for  (int i=0; i < a; i ++) {

                    System.out.println(i + " :" +threadName);
                    conditionB.signalAll();
                    conditionA.await();
                }

            }catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }


}
