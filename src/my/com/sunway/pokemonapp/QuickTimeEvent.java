package my.com.sunway.pokemonapp;

import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuickTimeEvent {
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generateRandomSequence(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (length-- != 0) {
            int character = (int)(random.nextFloat() * ALPHANUMERIC_STRING.length());
            builder.append(ALPHANUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public long performQTE() {
        Scanner scanner = new Scanner(System.in);
        AtomicBoolean success = new AtomicBoolean(false);
        Timer timer = new Timer();
        long startTime = System.currentTimeMillis();
        long[] endTime = new long[1];  // Array to store end time

        String randomSequence = generateRandomSequence(10);
        System.out.println("Enter the following sequence within 7 seconds: " + randomSequence);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!success.get()) {
                    System.out.println("Too slow!");
                }
            }
        };

        timer.schedule(task, 7000);  // 7 seconds for QTE

        Thread inputThread = new Thread(() -> {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase(randomSequence)) {
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

    public static void main(String[] args) {
        QuickTimeEvent qte = new QuickTimeEvent();
        long timeTaken = qte.performQTE();
        if (timeTaken != Long.MAX_VALUE) {
            System.out.println("Time taken: " + timeTaken + " ms");
        }
    }
}
