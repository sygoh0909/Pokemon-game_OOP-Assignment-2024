package my.com.sunway.pokemonapp;

import java.io.*;
import java.util.*;

public class Battle {
    private QuickTimeEvent qte;
    private List<Pokemon> rentalPokemons;
    private static final String SCORE_FILE = "top_scores.txt";
    private static final int MAX_TOP_SCORES = 5;
    private TypeChart typeChart;
    private Player player;
    private BattleScoreCalculation scoreCalculation;

    public Battle() {
        this.qte = new QuickTimeEvent();
        this.rentalPokemons = new ArrayList<>();
        this.typeChart = new TypeChart();
        this.player = new Player("userID", "password");
        this.scoreCalculation = new BattleScoreCalculation();

        //rental pokemon
        rentalPokemons.add(new Pokemon("Pikachu", 35, 55, 40, 3, List.of("electric"), 90, 50, 50));
        rentalPokemons.add(new Pokemon("Typhlosion", 78, 84, 78, 3, List.of("fire"), 100, 109, 85));
        rentalPokemons.add(new Pokemon("Snorlax", 160, 110, 65, 3, List.of("normal"), 30, 65, 110));
        rentalPokemons.add(new Pokemon("Tyrunt", 58, 89, 77, 3, List.of("rock", "dragon"), 48, 45, 45));
    }

    public void startBattle(List<Pokemon> userPokemons, List<Pokemon> stageWildPokemons, String userId) {
        if (stageWildPokemons.isEmpty()) {
            System.out.println("No wild Pokémon available for battle. Exiting.");
            return;
        }

        List<Pokemon> wildPokemons = chooseWildPokemons(stageWildPokemons);

        System.out.println("\nTwo wild Pokémon appear for battle:");
        Pokemon wildPokemon1 = wildPokemons.get(0);
        Pokemon wildPokemon2 = wildPokemons.get(1);
        System.out.println("1: " + wildPokemon1.getName() + " | Type: " + String.join(", ", wildPokemon1.getTypes()) + " | Stars: " + wildPokemon1.getStars());
        System.out.println("2: " + wildPokemon2.getName() + " | Type: " + String.join(", ", wildPokemon2.getTypes()) + " | Stars: " + wildPokemon2.getStars());

        Scanner scanner = new Scanner(System.in);
        Pokemon userPokemon1 = null;
        Pokemon userPokemon2 = null;

        // Check if user has at least 2 Pokémon to choose
        if (userPokemons.size() < 2) {
            if (userPokemons.isEmpty()) {
                System.out.println("You don't have any Pokémon. Choosing rental Pokémon.");
                int rentalChoice1 = choosePokemon(rentalPokemons, scanner, -1);
                Pokemon rentalPokemon1 = rentalPokemons.get(rentalChoice1);

                System.out.println("Rental Pokémon chosen:");
                System.out.println("1: " + rentalPokemon1.getName() + " | Type: " + String.join(", ", rentalPokemon1.getTypes()) + " | Stars: " + rentalPokemon1.getStars());

                // Use the rental Pokémon for battle
                userPokemon1 = rentalPokemon1;
            } else {
                // Choose user's Pokémon
                userPokemon1 = userPokemons.get(0); // Only one user Pokémon available
                System.out.println("\nYour Pokémon:");
                System.out.println("1: " + userPokemon1.getName() + " | Type: " + String.join(", ", userPokemon1.getTypes()) + " | Stars: " + userPokemon1.getStars());
                System.out.println("You only have one Pokémon. Choosing one rental Pokémon.");

                // Choose one rental Pokémon
                int rentalChoice1 = choosePokemon(rentalPokemons, scanner, -1);
                Pokemon rentalPokemon1 = rentalPokemons.get(rentalChoice1);

                System.out.println("Rental Pokémon chosen:");
                System.out.println("2: " + rentalPokemon1.getName() + " | Type: " + String.join(", ", rentalPokemon1.getTypes()) + " | Stars: " + rentalPokemon1.getStars());

                userPokemon2 = rentalPokemon1;
            }
        } else {
            System.out.println("Choose your Pokémon for battle:");

            // Choose user's Pokémon for battle
            int userChoice1 = choosePokemon(userPokemons, scanner, -1);
            int userChoice2 = choosePokemon(userPokemons, scanner, userChoice1);

            System.out.println("You chose:");
            userPokemon1 = userPokemons.get(userChoice1);
            userPokemon2 = userPokemons.get(userChoice2);
            System.out.println("1: " + userPokemon1.getName() + " | Type: " + String.join(", ", userPokemon1.getTypes()) + " | Stars: " + userPokemon1.getStars());
            System.out.println("2: " + userPokemon2.getName() + " | Type: " + String.join(", ", userPokemon2.getTypes()) + " | Stars: " + userPokemon2.getStars());
        }

        // If user only chose one Pokémon, assign second Pokémon to another user Pokémon or another rental Pokémon
        if (userPokemon2 == null) {
            int rentalChoice2 = choosePokemon(rentalPokemons, scanner, -1);
            Pokemon rentalPokemon2 = rentalPokemons.get(rentalChoice2);

            System.out.println("Second Rental Pokémon chosen:");
            System.out.println("2: " + rentalPokemon2.getName() + " | Type: " + String.join(", ", rentalPokemon2.getTypes()) + " | Stars: " + rentalPokemon2.getStars());

            userPokemon2 = rentalPokemon2;
        }

        System.out.println("\nBattle start!");

        int attackStrength1 = 0;
        int attackStrength2 = 0;

        long startTime = System.currentTimeMillis();
        long battleTimeLimit = 3 * 60 * 1000; // 3 minutes in milliseconds

        while (true) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= battleTimeLimit) {
                System.out.println("Time's up!");
                break;
            }

            if (userPokemon1.getHealth() > 0) {
                System.out.println("\nPlayer's Pokémon 1 turn!");
                waitForEnter(scanner); // Pause and wait for Enter
                long reactionTime1 = qte.performQTE();
                if (reactionTime1 != -1) {
                    attackStrength1 = calculateAttackStrength(userPokemon1, reactionTime1, wildPokemon1);
                    wildPokemon1.takeDamage(attackStrength1);
                    wildPokemon2.takeDamage(attackStrength1);
                    displayRemainingHP(wildPokemon1, wildPokemon2);
                }
            }

            if (wildPokemon1.getHealth() > 0) {
                System.out.println("\nWild Pokémon 1's turn!");
                userPokemon1.takeDamage(wildPokemon1.getAttack());
                displayRemainingHP(userPokemon1, userPokemon2);
            }

            // Check if both wild Pokémon are defeated after Wild Pokémon 1's turn
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                System.out.println("You won the battle!");
                break;
            }

            // Only allow Pokémon 2 to attack if Wild Pokémon 1 or 2 is still active
            if (userPokemon2.getHealth() > 0 && (wildPokemon1.getHealth() > 0 || wildPokemon2.getHealth() > 0)) {
                System.out.println("\nPlayer's Pokémon 2 turn!");
                waitForEnter(scanner); // Pause and wait for Enter
                long reactionTime2 = qte.performQTE();
                if (reactionTime2 != -1) {
                    attackStrength2 = calculateAttackStrength(userPokemon2, reactionTime2, wildPokemon2);
                    wildPokemon1.takeDamage(attackStrength2);
                    wildPokemon2.takeDamage(attackStrength2);
                    displayRemainingHP(wildPokemon1, wildPokemon2);
                }
            }

            if (wildPokemon2.getHealth() > 0) {
                System.out.println("\nWild Pokémon 2's turn!");
                userPokemon2.takeDamage(wildPokemon2.getAttack());
                displayRemainingHP(userPokemon1, userPokemon2);
            }

            // Check if both wild Pokémon are defeated after Wild Pokémon 2's turn
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                System.out.println("You won the battle!");
                break;
            }

            if (userPokemon1.getHealth() <= 0 && userPokemon2.getHealth() <= 0) {
                System.out.println("You lost the battle!");
                break;
            }
        }

        // Handle catching a Pokémon if applicable
        if ((wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() > 0 && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0)) ||
                (wildPokemon2.getHealth() <= 0 && wildPokemon1.getHealth() > 0 && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0)) ||
                (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0 && (System.currentTimeMillis() - startTime) < battleTimeLimit && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0))) {

            System.out.println("You have the chance to catch one of the defeated wild Pokémon!");

            boolean caughtAnyPokemon = false; // Flag to track if any Pokémon was caught

            // Check if wildPokemon1 is defeated
            if (wildPokemon1.getHealth() <= 0) {
                System.out.println("1. Catch " + wildPokemon1.getName());
            }

            // Check if wildPokemon2 is defeated
            if (wildPokemon2.getHealth() <= 0) {
                System.out.println("2. Catch " + wildPokemon2.getName());
            }

            System.out.println("Choose which Pokémon you want to catch (enter number): ");
            int choice = scanner.nextInt();

            if (choice == 1 && wildPokemon1.getHealth() <= 0) {
                Pokeball chosenPokeball = player.chooseRandomPokeball();
                System.out.println("A " + chosenPokeball + " appeared!");

                System.out.println("Press Enter to continue...");
                Scanner keyboard = new Scanner(System.in);
                keyboard.nextLine(); // Wait for user to press Enter

                boolean isCaught = player.attemptCatch(chosenPokeball);
                if (isCaught) {
                    System.out.println("You caught " + wildPokemon1.getName() + "!");
                    player.saveChosenPokemon(wildPokemon1);
                    caughtAnyPokemon = true;
                } else {
                    System.out.println(wildPokemon1.getName() + " escaped!");
                }
            } else if (choice == 2 && wildPokemon2.getHealth() <= 0) {
                Pokeball chosenPokeball = player.chooseRandomPokeball();
                System.out.println("A " + chosenPokeball + " appeared!");

                System.out.println("Press Enter to continue...");
                Scanner keyboard = new Scanner(System.in);
                keyboard.nextLine(); // Wait for user to press Enter

                boolean isCaught = player.attemptCatch(chosenPokeball);
                if (isCaught) {
                    System.out.println("You caught " + wildPokemon2.getName() + "!");
                    player.saveChosenPokemon(wildPokemon2);
                    caughtAnyPokemon = true;
                } else {
                    System.out.println(wildPokemon2.getName() + " escaped!");
                }
            }

            // Inform the player if no Pokémon was caught
            if (!caughtAnyPokemon) {
                System.out.println("No Pokémon were caught during this opportunity.");
            }
        }

        int battleScore = scoreCalculation.calculateBattleScore(startTime, battleTimeLimit,
                userPokemon1, userPokemon2,
                wildPokemon1, wildPokemon2,
                typeChart, attackStrength1, attackStrength2);

        System.out.println("\nBattle ended. Your score: " + battleScore);
        updateTopScores(userId, battleScore);
        displayTopScores();

        //rank display
        System.out.println("\n" + scoreCalculation.determineRankMessage(battleScore));
    }

    List<Pokemon> chooseWildPokemons(List<Pokemon> chosenStagePokemons) {
        List<Pokemon> wildPokemons = new ArrayList<>();
        Random random = new Random();
        int firstIndex = random.nextInt(chosenStagePokemons.size());
        int secondIndex;
        do {
            secondIndex = random.nextInt(chosenStagePokemons.size());
        } while (secondIndex == firstIndex);

        wildPokemons.add(chosenStagePokemons.get(firstIndex));
        wildPokemons.add(chosenStagePokemons.get(secondIndex));

        return wildPokemons;
    }

    private int choosePokemon(List<Pokemon> userPokemons, Scanner scanner, int previousChoice) {
        int userChoice = -1;
        List<Pokemon> availablePokemons = new ArrayList<>(userPokemons);
        if (userPokemons.size() < 2) {
            availablePokemons.addAll(rentalPokemons);
        }

        System.out.println("\nAvailable Pokémon:");
        for (int i = 0; i < availablePokemons.size(); i++) {
            Pokemon pokemon = availablePokemons.get(i);
            String typesString = String.join(", ", pokemon.getTypes()); // Join types with a comma
            System.out.println((i + 1) + ". " + pokemon.getName() + " | Type: " + typesString + " | Stars: " + pokemon.getStars());
        }

        while (userChoice < 0 || userChoice >= availablePokemons.size() || userChoice == previousChoice) {
            System.out.println("Choose your Pokémon (enter number): ");
            try {
                userChoice = scanner.nextInt() - 1;
                if (userChoice < 0 || userChoice >= availablePokemons.size() || userChoice == previousChoice) {
                    System.out.println("Invalid Pokémon choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        return userChoice;
    }

    private void waitForEnter(Scanner scanner) {
        System.out.println("Press Enter to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateAttackStrength(Pokemon attacker, long reactionTime, Pokemon defender) {
        // Calculate base attack strength based on reaction time
        int baseAttack = calculateBaseAttack(attacker, reactionTime);
        // Get type effectiveness from TypeChart
        double effectiveness = typeChart.getEffectiveness(attacker.getTypes().get(0), defender.getTypes().get(0));
        // Apply type effectiveness to attack strength
        return (int) (baseAttack * effectiveness);
    }

    private int calculateBaseAttack(Pokemon pokemon, long reactionTime) {
        // Example implementation of calculating base attack
        if (reactionTime < 1000) {
            return pokemon.getAttack() * 2;
        } else if (reactionTime < 2000) {
            return pokemon.getAttack();
        } else {
            return pokemon.getAttack() / 2;
        }
    }

    private void displayRemainingHP(Pokemon... pokemons) {
        System.out.println("\nRemaining HP:");
        for (Pokemon pokemon : pokemons) {
            System.out.println(pokemon.getName() + ": " + pokemon.getHealth());
        }
    }

    private void updateTopScores(String userId, int newScore) {
        List<Score> topScores = readTopScores();
        topScores.add(new Score(userId, newScore));
        Collections.sort(topScores, Collections.reverseOrder());
        if (topScores.size() > MAX_TOP_SCORES) {
            topScores = topScores.subList(0, MAX_TOP_SCORES);
        }
        writeTopScores(topScores);
    }

    private List<Score> readTopScores() {
        List<Score> topScores = new ArrayList<>();
        File file = new File(SCORE_FILE);

        try {
            if (!file.exists()) {
                file.createNewFile(); // Create a new file if it doesn't exist
            }

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String userId = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    topScores.add(new Score(userId, score));
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Failed to read top scores: " + e.getMessage());
        }

        return topScores;
    }


    private void writeTopScores(List<Score> topScores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORE_FILE))) {
            for (Score score : topScores) {
                writer.println(score.getUserId() + "," + score.getScore());
            }
        } catch (IOException e) {
            System.out.println("Failed to write top scores: " + e.getMessage());
        }
    }

    private void displayTopScores() {
        List<Score> topScores = readTopScores();
        System.out.println("\nTop Scores:");
        for (Score score : topScores) {
            System.out.println(score.getUserId() + ": " + score.getScore());
        }
    }

    static class Score implements Comparable<Score> {
        private final String userId;
        private final int score;

        public Score(String userId, int score) {
            this.userId = userId;
            this.score = score;
        }

        public String getUserId() {
            return userId;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(Score other) {
            return Integer.compare(this.score, other.score);
        }
    }
}