package my.com.sunway.pokemonapp;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Battle {
    private QuickTimeEvent qte;

    public Battle() {
        this.qte = new QuickTimeEvent();
    }

    public void startBattle(List<Pokemon> userPokemons, List<Pokemon> wildPokemons) {
        Scanner scanner = new Scanner(System.in);

        // Display user's Pokémon for selection, including newly caught Pokémon
        System.out.println("\nYour Pokémon:");
        for (int i = 0; i < userPokemons.size(); i++) {
            Pokemon pokemon = userPokemons.get(i);
            System.out.println((i + 1) + ": " + pokemon.getName() + " | Type: " + String.join(", ", pokemon.getTypes()) + " | Stars: " + pokemon.getStars());
        }

        int userChoice1 = -1;
        int userChoice2 = -1;

        // Choose first Pokémon
        while (userChoice1 < 0 || userChoice1 >= userPokemons.size()) {
            System.out.println("Choose your first Pokémon (enter number): ");
            try {
                userChoice1 = scanner.nextInt() - 1;
                if (userChoice1 < 0 || userChoice1 >= userPokemons.size()) {
                    System.out.println("Invalid Pokémon choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }

        // Choose second Pokémon
        while (userChoice2 < 0 || userChoice2 >= userPokemons.size() || userChoice2 == userChoice1) {
            System.out.println("Choose your second Pokémon (enter number, different from the first): ");
            try {
                userChoice2 = scanner.nextInt() - 1;
                if (userChoice2 < 0 || userChoice2 >= userPokemons.size() || userChoice2 == userChoice1) {
                    System.out.println("Invalid Pokémon choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }

        System.out.println("Battle start!");

        // Get chosen Pokémon objects
        Pokemon userPokemon1 = userPokemons.get(userChoice1);
        Pokemon userPokemon2 = userPokemons.get(userChoice2);

        // Simulate battle logic
        Pokemon wildPokemon1 = wildPokemons.get(0);
        Pokemon wildPokemon2 = wildPokemons.get(1);

        // Turn-based battle logic
        while ((userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0) && (wildPokemon1.getHealth() > 0 || wildPokemon2.getHealth() > 0)) {
            // Player's Pokémon 1 turn
            if (userPokemon1.getHealth() > 0) {
                System.out.println("Player's Pokémon 1 turn!");
                long reactionTime1 = qte.performQTE();

                int attackStrength1;
                if (reactionTime1 < 1000) {
                    attackStrength1 = userPokemon1.getAttack()*2; // Fast reaction
                } else if (reactionTime1 < 2000) {
                    attackStrength1 = userPokemon1.getAttack(); // Moderate reaction
                } else {
                    attackStrength1 = userPokemon1.getAttack() / 2; // Slow reaction
                }

                wildPokemon1.takeDamage(attackStrength1);
                wildPokemon2.takeDamage(attackStrength1);

                // Display remaining HP after user's Pokémon 1 turn
                System.out.println("\nRemaining HP after Pokémon 1's attack:");
                System.out.println(wildPokemon1.getName() + ": " + wildPokemon1.getHealth());
                System.out.println(wildPokemon2.getName() + ": " + wildPokemon2.getHealth());
            }

            // Wild Pokémon 1 turn
            if (wildPokemon1.getHealth() > 0) {
                System.out.println("\nWild Pokémon 1's turn!");
                userPokemon1.takeDamage(wildPokemon1.getAttack());
                // Display remaining HP after Wild Pokémon 1's turn
                System.out.println("Remaining HP after Wild Pokémon 1's attack:");
                System.out.println(userPokemon1.getName() + ": " + userPokemon1.getHealth());
                System.out.println(userPokemon2.getName() + ": " + userPokemon2.getHealth());
            }

            // Player's Pokémon 2 turn
            if (userPokemon2.getHealth() > 0) {
                System.out.println("\nPlayer's Pokémon 2 turn!");
                long reactionTime2 = qte.performQTE();

                int attackStrength2;
                if (reactionTime2 < 1000) {
                    attackStrength2 = userPokemon2.getAttack()*2; // Fast reaction
                } else if (reactionTime2 < 2000) {
                    attackStrength2 = userPokemon2.getAttack(); // Moderate reaction
                } else {
                    attackStrength2 = userPokemon2.getAttack() / 2; // Slow reaction
                }

                wildPokemon1.takeDamage(attackStrength2);
                wildPokemon2.takeDamage(attackStrength2);

                // Display remaining HP after user's Pokémon 2 turn
                System.out.println("\nRemaining HP after Pokémon 2's attack:");
                System.out.println(wildPokemon1.getName() + ": " + wildPokemon1.getHealth());
                System.out.println(wildPokemon2.getName() + ": " + wildPokemon2.getHealth());
            }

            // Wild Pokémon 2 turn
            if (wildPokemon2.getHealth() > 0) {
                System.out.println("\nWild Pokémon 2's turn!");
                userPokemon2.takeDamage(wildPokemon2.getAttack());
                // Display remaining HP after Wild Pokémon 2's turn
                System.out.println("Remaining HP after Wild Pokémon 2's attack:");
                System.out.println(userPokemon1.getName() + ": " + userPokemon1.getHealth());
                System.out.println(userPokemon2.getName() + ": " + userPokemon2.getHealth());
            }

            // Check if any Pokémon has fainted
            if (userPokemon1.getHealth() <= 0 && userPokemon2.getHealth() <= 0) {
                System.out.println("You lost the battle!");
                return;
            }
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                System.out.println("You won the battle!");
                return;
            }
        }
    }
}
