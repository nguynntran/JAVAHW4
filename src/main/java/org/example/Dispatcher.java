package org.example;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

interface DispatcherInterface extends Runnable {
    void registerTaxi(Taxi taxi);
    void notifyOrderComplete(Taxi taxi);
}

class Dispatcher implements DispatcherInterface {
    private final Queue<Taxi> taxiQueue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition taxiAvailable = lock.newCondition();

    @Override
    public void registerTaxi(Taxi taxi) {
        lock.lock();
        try{
            taxiQueue.add(taxi);
            System.out.println("Dispatcher: Registered Taxi: ");
            taxiAvailable.signal();
    } finally {
            lock.unlock();
        }
    }

    @Override
    public void notifyOrderComplete(Taxi taxi) {
        lock.lock();
        try {
            taxiQueue.add(taxi); 
            System.out.println("Taxi " + ((Taxiimpl) taxi).getTaxiId() + "is available again for new order");
            taxiAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                lock.lock();
                try {
                    while (taxiQueue.isEmpty()) {
                        System.out.println("Dispatcher: Waiting for Taxi to be available");
                        taxiAvailable.await();
                    }
                    Taxi taxi = taxiQueue.poll();
                    if (taxi != null) {
                        String order = "Order for customer at address: " + new Random().nextInt(100);
                        System.out.println("Dispatcher: Assigning " + order + " to Taxi" + ((Taxiimpl) taxi).getTaxiId());
                        taxi.placeOrder(order);
                    }
                } finally {
                    lock.unlock();
                }
                // Simulate processing delay
                Thread.sleep(500 + new Random().nextInt(1000));
            }
        } catch(InterruptedException e) {
            System.out.println("Dispatcher: Interrupted");
        }
    }
}

