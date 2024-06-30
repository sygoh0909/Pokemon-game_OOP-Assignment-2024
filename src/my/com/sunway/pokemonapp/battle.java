package my.com.sunway.pokemonapp;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Battle {
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

        // Get chosen Pokémon objects
        Pokemon userPokemon1 = userPokemons.get(userChoice1);
        Pokemon userPokemon2 = userPokemons.get(userChoice2);

        // Simulate battle logic (example: higher attack wins) - get from battle class later
        Pokemon wildPokemon1 = wildPokemons.get(0);
        Pokemon wildPokemon2 = wildPokemons.get(1);

        // Example battle logic (simplified)
        boolean userWins = userPokemon1.getAttack() > wildPokemon1.getAttack() && userPokemon2.getAttack() > wildPokemon2.getAttack();

        // Display battle result
        if (userWins) {
            System.out.println("You won the battle!");
            // Handle winning scenario
        } else {
            System.out.println("You lost the battle!");
            // Handle losing scenario
        }
    }
}
