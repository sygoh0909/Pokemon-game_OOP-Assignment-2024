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

    private long reactionTime; // Variable to store reaction time
    private boolean tooSlow;   // Flag to track if user was too slow

    public long performQTE() {
        Scanner scanner = new Scanner(System.in);
        AtomicBoolean success = new AtomicBoolean(false);
        Timer timer = new Timer();
        long startTime = System.currentTimeMillis();

        String randomSequence = generateRandomSequence(10);
        System.out.println("Enter the following sequence within 12 seconds: " + randomSequence);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!success.get()) {
                    System.out.println("Too slow! Attack failed.");
                    tooSlow = true;  // Set flag for too slow
                    timer.cancel();
                }
            }
        };

        timer.schedule(task, 12000);  // 15 seconds for QTE

        Thread inputThread = new Thread(() -> {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase(randomSequence)) {
                success.set(true);
                reactionTime = System.currentTimeMillis() - startTime; // Calculate reaction time
                System.out.println("Success! Attack successful.");
                timer.cancel();
            } else {
                System.out.println("Wrong sequence! Attack failed.");
                timer.cancel();
            }
        });

        inputThread.start();

        try {
            inputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (success.get() && !tooSlow) {
            return reactionTime;
        } else {
            return -1; // Return -1 to indicate failure or too slow
        }
    }
}
