package my.com.sunway.pokemonapp;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuickTimeEvent {
    public long performQTE() {
        Scanner scanner = new Scanner(System.in);
        AtomicBoolean success = new AtomicBoolean(false);
        Timer timer = new Timer();
        long startTime = System.currentTimeMillis();
        long[] endTime = new long[1];  // Array to store end time

        System.out.println("Press 'Q' within 3 seconds!");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!success.get()) {
                    System.out.println("Too slow!");
                }
            }
        };

        timer.schedule(task, 3000);

        Thread inputThread = new Thread(() -> {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("Q")) {
                success.set(true);
                endTime[0] = System.currentTimeMillis();
                System.out.println("Success!");
                timer.cancel();
            }
        });

        inputThread.start();

        try {
            inputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return success.get() ? (endTime[0] - startTime) : Long.MAX_VALUE;
    }
}
