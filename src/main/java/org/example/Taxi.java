package org.example;
import java.util.Random;
import  java.util.concurrent.locks.Lock;
import  java.util.concurrent.locks.ReentrantLock;

// Interface Taxi
interface Taxi extends Runnable {
    void placeOrder(String order);
    void start();
    void stop();
    boolean isBusy();
    void isAvailable();
    
}

class Taxiimpl implements Taxi {
    private final int taxiId;
    private final Dispatcher dispatcher;
    private boolean busy = false;
    private final Lock lock = new ReentrantLock();
    private String currentOrder = null;

    public Taxiimpl(int taxiId, Dispatcher dispatcher) {
        this.taxiId = taxiId;
        this.dispatcher = dispatcher;
    }

    public int getTaxiId() {
        return taxiId;
    }

    @Override
    public void placeOrder(String order) {
        lock.lock();
        try{
            busy = true;
            currentOrder = order;
            System.out.println("Taxi " + taxiId + ": Received " + order);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isBusy() {
        lock.lock();
        try{
            return busy;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void isAvailable() {
        lock.lock();
        try
            {
            busy = false;
            dispatcher.notifyOrderComplete(this);
            }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        try{
            while (!Thread.currentThread().isInterrupted()) {
                if(isBusy()) {
                    int deliveryTime = 1000 + new Random().nextInt(5000);
                    System.out.println("Taxi " + taxiId + " delivering " + deliveryTime);
                    Thread.sleep(deliveryTime);
                    System.out.println("Taxi " + taxiId + ": Order completed in " + deliveryTime);
                    isAvailable();
                }
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Taxi " + taxiId + ": Interrupted");
        }
    }
}
