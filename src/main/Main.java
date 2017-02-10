package main;

import java.util.Random;
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

    public static Consumer getConsumer(){
        return consumer1;
    }

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
    private double sum = 0;

    public void message(double qube, double square, double simple){
        Main.POWER_TYPE type = ((MyThread) Thread.currentThread()).getType();
        while(!semaphore.catchMonitor(type));
        System.out.println("Процесс " + type + " " + Thread.currentThread().getName() + " in");
        sum += qube;
        sum += square;
        sum += simple;

        System.out.println("Sum: " + sum);
        System.out.println("Процесс " + type + " " + Thread.currentThread().getName() + " out");
        semaphore.freeMonitor(type);
    }
}

abstract class MyThread extends Thread {
    protected Main.POWER_TYPE type;
    protected int x;
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
    volatile boolean qube;
    volatile boolean square;
    volatile boolean simple;

    public boolean catchMonitor(Main.POWER_TYPE type){
        switch (type){
            case QUBE: if (!qube){
                return qube = true;
            } break;
            case SQUARE: if (!square){
                return square = true;
            } break;
            case SIMPLE: if (!simple){
                return simple = true;
            } break;
        }
        return false;
    }

    public void freeMonitor(Main.POWER_TYPE type){
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