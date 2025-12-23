package org.example;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

interface TaxiInterface extends Runnable {
    void placeOrder(String order);
    boolean isBusy();
    void markAvailable();
    
}

class Taxi implements TaxiInterface {
    private final int taxiId;
    private final Dispatcher dispatcher;
    private boolean busy = false;
    private final Lock lock = new ReentrantLock();
    private final Condition orderReceived = lock.newCondition();
    private String currentOrder = null;

    public Taxi(int taxiId, Dispatcher dispatcher) {
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
            orderReceived.signal();
        }
        finally {
            lock.unlock();
        }
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

    public void markAvailable() {
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
                lock.lock();
                try {
                    while (!busy) {
                        orderReceived.await();
                    }
                } finally {
                    lock.unlock();
                }
                
                int deliveryTime = 1000 + ThreadLocalRandom.current().nextInt(5000);
                System.out.println("Taxi " + taxiId + " delivering " + deliveryTime);
                Thread.sleep(deliveryTime);
                System.out.println("Taxi " + taxiId + ": Order completed in " + deliveryTime);
                markAvailable();
            }
        } catch (InterruptedException e) {
            System.out.println("Taxi " + taxiId + ": Interrupted");
        }
    }
}