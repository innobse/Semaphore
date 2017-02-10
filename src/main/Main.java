package main;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.pow;

public class Main {
    private static final int ARR_LENGTH = 10;
    private static int[][] data = new int[3][ARR_LENGTH];
    private static Consumer consumer1;
    public enum POWER_TYPE {
            QUBE, SQUARE, SIMPLE
    }

    public static void main(String[] args) {
        init();
        consumer1 = new Consumer();

        for (int i = 0; i < ARR_LENGTH; i++) {
            new Qube(data[0][i]);
            new Square(data[1][i]);
            new Simple(data[2][i]);
        }

    }

    static Consumer getConsumer(){
        return consumer1;
    }

    //  инициализация рандомных данных
    private static void init(){
        Random rand = new Random();
        for (int[] tmpData : data)
            for (int i = 0; i < 10; i++) {
                tmpData[i] = rand.nextInt(10);
            }
    }
}

class Consumer {
    private final Semaphore semaphore = new Semaphore();
    private volatile AtomicInteger sum = new AtomicInteger(0);

    void message(double qube, double square, double simple){
        Main.POWER_TYPE type = ((MyThread) Thread.currentThread()).getType();
        while(!semaphore.catchMonitor(type)) System.out.println("-> " + Thread.currentThread().getName());
        System.out.println("Процесс " + type + " " + Thread.currentThread().getName() + " in and add " + (qube + square + simple));
        switch (type){
            case QUBE: sum.addAndGet((int) qube); break;
            case SQUARE: sum.addAndGet((int) square); break;
            case SIMPLE: sum.addAndGet((int) simple); break;
        }

        System.out.println("Sum: " + sum + " <- " + Thread.currentThread().getName());
        System.out.println("Процесс " + type + " " + Thread.currentThread().getName() + " out");
        semaphore.freeMonitor(type);
    }
}

//  класс-родитель для кубатора, квадратора и простатора
abstract class MyThread extends Thread {
    Main.POWER_TYPE type;
    int x;
    protected void calc(int x){}
    Main.POWER_TYPE getType(){
        return type;
    }
}

class Qube extends MyThread {

    Qube(int x){
        this.x = x;
        type = Main.POWER_TYPE.QUBE;
        start();
    }

    @Override
    public void run(){
        calc(x);
    }

    @Override
    protected void calc(int x){
        Main.getConsumer().message(pow(x, 3), 0, 0);
    }
}

class Square extends MyThread {

    Square(int x){
        this.x = x;
        type = Main.POWER_TYPE.SQUARE;
        start();
    }

    @Override
    public void run(){
        calc(x);
    }

    @Override
    protected void calc(int x){
        Main.getConsumer().message( 0, pow(x, 2), 0);
    }
}

class Simple extends MyThread{

    Simple(int x){
        this.x = x;
        type = Main.POWER_TYPE.SIMPLE;
        start();
    }

    @Override
    public void run(){
        calc(x);
    }

    @Override
    protected void calc(int x){
        Main.getConsumer().message(0, 0, x);
    }
}

class Semaphore {
    private volatile boolean qube;
    private volatile boolean square;
    private volatile boolean simple;

    boolean catchMonitor(Main.POWER_TYPE type){
        switch (type){
            case QUBE: if (!qube){
                qube = true;
                return true;
            } break;
            case SQUARE: if (!square){
                square = true;
                return true;
            } break;
            case SIMPLE: if (!simple){
                simple = true;
                return true;
            } break;
        }
        return false;
    }

    void freeMonitor(Main.POWER_TYPE type){
        switch (type){
            case QUBE: qube = false;
                break;
            case SQUARE: square = false;
                break;
            case SIMPLE: simple = false;
                break;
        }
    }

}