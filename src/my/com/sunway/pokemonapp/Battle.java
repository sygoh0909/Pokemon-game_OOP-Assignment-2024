package my.com.sunway.pokemonapp;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Battle {
    private QuickTimeEvent qte;
    private List<Pokemon> rentalPokemons;

    public Battle() {
        this.qte = new QuickTimeEvent();
        this.rentalPokemons = new ArrayList<>();
        rentalPokemons.add(new Pokemon("Pikachu", 35, 55, 40, 3, List.of("electric"), 90, 50, 50));
        rentalPokemons.add(new Pokemon("Typhlosion", 78, 84, 78, 3, List.of("fire"), 100, 109, 85));
        rentalPokemons.add(new Pokemon("Snorlax", 160, 110, 65, 3, List.of("normal"), 30, 65, 110));
        rentalPokemons.add(new Pokemon("Tyrunt", 58, 89, 77, 3, List.of("rock", "dragon"), 48, 45, 45));
    }

    public void startBattle(List<Pokemon> userPokemons, List<Pokemon> stageWildPokemons) {
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

        System.out.println("\nYour Pokémon:");
        for (int i = 0; i < userPokemons.size(); i++) {
            Pokemon pokemon = userPokemons.get(i);
            System.out.println((i + 1) + ": " + pokemon.getName() + " | Type: " + String.join(", ", pokemon.getTypes()) + " | Stars: " + pokemon.getStars());
        }

        int userChoice1 = choosePokemon(userPokemons, scanner, -1);
        int userChoice2 = choosePokemon(userPokemons, scanner, userChoice1);

        System.out.println("Battle start!");

        Pokemon userPokemon1 = userPokemons.get(userChoice1);
        Pokemon userPokemon2 = userPokemons.get(userChoice2);

        while (true) {
            if (userPokemon1.getHealth() > 0) {
                System.out.println("\nPlayer's Pokémon 1 turn!");
                long reactionTime1 = qte.performQTE();
                int attackStrength1 = calculateAttackStrength(userPokemon1, reactionTime1);
                wildPokemon1.takeDamage(attackStrength1);
                wildPokemon2.takeDamage(attackStrength1);
                displayRemainingHP(wildPokemon1, wildPokemon2);
            }

            if (wildPokemon1.getHealth() > 0) {
                System.out.println("\nWild Pokémon 1's turn!");
                userPokemon1.takeDamage(wildPokemon1.getAttack());
                displayRemainingHP(userPokemon1, userPokemon2);
            }

            if (userPokemon2.getHealth() > 0) {
                System.out.println("\nPlayer's Pokémon 2 turn!");
                long reactionTime2 = qte.performQTE();
                int attackStrength2 = calculateAttackStrength(userPokemon2, reactionTime2);
                wildPokemon1.takeDamage(attackStrength2);
                wildPokemon2.takeDamage(attackStrength2);
                displayRemainingHP(wildPokemon1, wildPokemon2);
            }

            if (wildPokemon2.getHealth() > 0) {
                System.out.println("\nWild Pokémon 2's turn!");
                userPokemon2.takeDamage(wildPokemon2.getAttack());
                displayRemainingHP(userPokemon1, userPokemon2);
            }

            if (userPokemon1.getHealth() <= 0 && userPokemon2.getHealth() <= 0) {
                System.out.println("You lost the battle!");
                break;
            }
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                System.out.println("You won the battle!");
                break;
            }
        }
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
            System.out.println((i + 1) + ": " + pokemon.getName() + " | Type: " + String.join(", ", pokemon.getTypes()) + " | Stars: " + pokemon.getStars());
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

    private int calculateAttackStrength(Pokemon pokemon, long reactionTime) {
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
}

