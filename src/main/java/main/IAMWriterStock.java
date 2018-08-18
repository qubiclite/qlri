package main;

import iam.IAMWriter;

import java.util.LinkedList;

public class IAMWriterStock {

    private static final int MIN_STOCK_SIZE = 5;
    private static final LinkedList<IAMWriter> STOCK = new LinkedList<>();

    static {
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    refillStock();
                    waitUntilStockRequiresRefill();
                }
            }
        }.start();
    }

    private static void waitUntilStockRequiresRefill() {
        try {
            synchronized (STOCK) { STOCK.wait(); }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void refillStock() {
        while(STOCK.size() < MIN_STOCK_SIZE)
            STOCK.add(new IAMWriter());
    }

    public static IAMWriter receive() {
        IAMWriter iamWriter = STOCK.poll();
        if(iamWriter == null)
            iamWriter = new IAMWriter();
        synchronized (STOCK) { STOCK.notify(); }
        return iamWriter;
    }
}
