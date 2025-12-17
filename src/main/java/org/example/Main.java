package org.example;

public class Main {
    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher() ;

        // Create and start taxi threads
            for (int i = 1; i <= 5; i++) {
                Taxi taxi = new Taxiimpl(i, dispatcher);
                dispatcher.registerTaxi(taxi);
                new Thread(taxi).start();
            }

            new Thread(dispatcher).start();
        }
    }
