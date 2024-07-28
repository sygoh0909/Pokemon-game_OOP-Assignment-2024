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
    
    public static void println(String text) {  
    	final int DELAY = 15; //millisecond
    	
    	// convert the String text into char
        for (char ch : text.toCharArray()) {
            System.out.print(ch);
            try {
            	// pause execution (insert delay time)
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(); // Print a new line at the end
    }
    
    private long reactionTime; // Variable to store reaction time
    private boolean tooSlow;   // Flag to track if user was too slow

    public long performQTE() {
    	Scanner scanner = new Scanner(System.in);
        AtomicBoolean success = new AtomicBoolean(false);
        Timer timer = new Timer();
        long startTime = System.currentTimeMillis();
        tooSlow = false; // Initialize tooSlow to false at the start

        String randomSequence = generateRandomSequence(10);
        println("Enter the following sequence within 15 seconds: " + randomSequence);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!success.get()) {
                    tooSlow = true; // Set flag for too slow
                    println("Too slow! Attack Failed. Press ENTER to continue...");
                    timer.cancel();
                }
            }
        };

        timer.schedule(task, 12000);  // 12 seconds for QTE
        
        Thread inputThread = new Thread(() -> {
             String input = scanner.nextLine();
            if (!tooSlow && input.equalsIgnoreCase(randomSequence)) {
                success.set(true);
                reactionTime = System.currentTimeMillis() - startTime; // Calculate reaction time
                println("Success! Attack successful.");
                timer.cancel();
            } else if (!tooSlow){
            	println("Incorrect sequence!");
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
