package my.com.sunway.pokemonapp;

import java.io.*;
import java.util.*;

public class Battle {
    private QuickTimeEvent qte;
    private List<Pokemon> rentalPokemons;
    private TypeChart typeChart;
    private BattleScoreCalculation scoreCalculation;
    private boolean battleWins;
    private Player player;

    public Battle() {
        this.qte = new QuickTimeEvent();
        this.rentalPokemons = new ArrayList<>();
        this.typeChart = new TypeChart();
        this.scoreCalculation = new BattleScoreCalculation();
        this.battleWins = false; // Initialize battleWins to false
        this.player = new Player();

        //rental pokemon
        rentalPokemons.add(new Pokemon("Pikachu", 35, 55, 40, 3, List.of("electric"), 90, 50, 50, 3));
        rentalPokemons.add(new Pokemon("Typhlosion", 78, 84, 78, 3, List.of("fire"), 100, 109, 85, 2));
        rentalPokemons.add(new Pokemon("Snorlax", 160, 110, 65, 3, List.of("normal"), 30, 65, 110, 1));
        rentalPokemons.add(new Pokemon("Tyrunt", 58, 89, 77, 3, List.of("rock", "dragon"), 48, 45, 45, 7));
    }

    public void startBattle(List<Pokemon> userPokemons, List<Pokemon> stageWildPokemons, String userId) {
        this.player.setUserId(userId);
        this.battleWins = false;

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
        int defenseStrength1 = 0;
        int defenseStrength2 = 0;
        int specialAttackStrength1 = 0;
        int specialAttackStrength2 = 0;
        int specialDefenseStrength1 = 0;
        int specialDefenseStrength2 = 0;

        long startTime = System.currentTimeMillis();
        long battleTimeLimit = 3 * 60 * 1000; // 3 minutes in milliseconds

        while (true) {
            Scanner enter = new Scanner(System.in);

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= battleTimeLimit) {
                System.out.println("Time's up!");
                break;
            }

            if (userPokemon1.getHealth() > 0) {
                System.out.println("\nPlayer's Pokémon 1 turn!");
                waitForEnter(enter);
                long reactionTime1 = qte.performQTE();
                if (reactionTime1 != -1) {
                    System.out.print(wildPokemon1.getName() + " being attacked!! ");
                    // Calculate attack strengths against wildPokemon1
                    attackStrength1 = calculateAttackStrength(userPokemon1, reactionTime1, wildPokemon1);
                    specialAttackStrength1 = calculateSpecialAttackStrength(userPokemon1, reactionTime1, wildPokemon1);
                    System.out.print(userPokemon1.getName() + " attacks! Attack: " + (attackStrength1 + specialAttackStrength1) + ", HP: " + userPokemon1.getHealth() + ",");
                    printEffectivenessMessage(userPokemon1, wildPokemon1);

                    System.out.println();
                    System.out.print(wildPokemon2.getName() + " being attacked!! ");
                    // Calculate attack strengths against wildPokemon2
                    attackStrength2 = calculateAttackStrength(userPokemon1, reactionTime1, wildPokemon2);
                    specialAttackStrength2 = calculateSpecialAttackStrength(userPokemon1, reactionTime1, wildPokemon2);
                    System.out.print(userPokemon1.getName() + " attacks! Attack: " + (attackStrength2 + specialAttackStrength2) + ", HP: " + userPokemon1.getHealth() + ",");
                    printEffectivenessMessage(userPokemon1, wildPokemon2);

                    // Apply attack and special attack to wild Pokémons
                    wildPokemon1.takeDamage(attackStrength1 + specialAttackStrength1);
                    wildPokemon2.takeDamage(attackStrength2 + specialAttackStrength2);

                } else {
                    System.out.print(userPokemon1.getName() + " attack failed!");
                }
                System.out.println("\nOpponent's Remaining HP:");
                displayRemainingHP(wildPokemon1, wildPokemon2);
            }

            if (wildPokemon1.getHealth() > 0) {
                System.out.println("\nWild Pokémon 1's turn!");

                System.out.print(userPokemon1.getName() + " being attacked!!");
                // Calculate defense and special defense for userPokemon1
                defenseStrength1 = calculateDefenseStrength(userPokemon1, System.currentTimeMillis(), wildPokemon1);
                specialDefenseStrength1 = calculateSpecialDefenseStrength(userPokemon1, System.currentTimeMillis(), wildPokemon1);
                // Apply wild Pokémon's attack to user's Pokémon
                int wildAttack1 = wildPokemon1.getAttack() + wildPokemon1.getSpecialAttack() - (defenseStrength1 + specialDefenseStrength1);
                System.out.println("Opponent " + wildPokemon1.getName() + " attacks! Attack: " + wildAttack1 + ", HP: " + wildPokemon1.getHealth() + ",");
                printEffectivenessMessage(wildPokemon1, userPokemon1);

                System.out.println();
                System.out.print(userPokemon2.getName() + " being attacked!!");
                // Calculate defense and special defense for userPokemon1
                defenseStrength2 = calculateDefenseStrength(userPokemon2, System.currentTimeMillis(), wildPokemon1);
                specialDefenseStrength2 = calculateSpecialDefenseStrength(userPokemon2, System.currentTimeMillis(), wildPokemon1);
                // Apply wild Pokémon's attack to user's Pokémon
                int wildAttack2 = wildPokemon1.getAttack() + wildPokemon1.getSpecialAttack() - (defenseStrength2 + specialDefenseStrength2);
                System.out.println("Opponent " + wildPokemon1.getName() + " attacks! Attack: " + wildAttack2 + ", HP: " + wildPokemon1.getHealth() + ",");
                printEffectivenessMessage(wildPokemon1, userPokemon2);

                userPokemon1.takeDamage(wildAttack1);
                userPokemon2.takeDamage(wildAttack2);

                System.out.println("\nPlayer's Remaining HP:");
                displayRemainingHP(userPokemon1, userPokemon2);
            }

            // Check if both wild Pokémon are defeated after Wild Pokémon 1's turn
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                System.out.println("You won the battle!");
                battleWins = true;
                break;
            }

            // Only allow Pokémon 2 to attack if Wild Pokémon 1 or 2 is till active
            if (userPokemon2.getHealth() > 0 && (wildPokemon1.getHealth() > 0 || wildPokemon2.getHealth() > 0 )) {
                System.out.println("\nPlayer's Pokémon 2 turn!");
                waitForEnter(enter);
                long reactionTime2 = qte.performQTE();
                if (reactionTime2 != -1) {
                    System.out.print(wildPokemon1.getName() + " being attacked!!");
                    // Calculate attack strengths against wildPokemon1
                    attackStrength1 = calculateAttackStrength(userPokemon2, reactionTime2, wildPokemon1);
                    specialAttackStrength1 = calculateSpecialAttackStrength(userPokemon2, reactionTime2, wildPokemon1);
                    System.out.println(userPokemon2.getName() + " attacks! Attack: " + (attackStrength1 + specialAttackStrength1) + ", HP: " + userPokemon2.getHealth() + ",");
                    printEffectivenessMessage(userPokemon2, wildPokemon1);

                    System.out.println();
                    System.out.print(wildPokemon2.getName() + " being attacked!!");
                    // Calculate attack strengths against wildPokemon2
                    attackStrength2 = calculateAttackStrength(userPokemon2, reactionTime2, wildPokemon2);
                    specialAttackStrength2 = calculateSpecialAttackStrength(userPokemon2, reactionTime2, wildPokemon2);
                    System.out.println(userPokemon2.getName() + " attacks! Attack: " + (attackStrength2 + specialAttackStrength2) + ", HP: " + userPokemon2.getHealth() + ",");
                    printEffectivenessMessage(userPokemon2, wildPokemon2);

                    // Apply attack and special attack to wild Pokémons
                    wildPokemon1.takeDamage(attackStrength1 + specialAttackStrength1);
                    wildPokemon2.takeDamage(attackStrength2 + specialAttackStrength2);
                } else {
                    System.out.println(userPokemon2.getName()+ " attack failed!");
                }
                System.out.println("Opponent's Remaining HP:");
                displayRemainingHP(wildPokemon1, wildPokemon2);
            }

            if (wildPokemon2.getHealth() > 0) {
                System.out.println("\nWild Pokémon 2's turn!");

                System.out.print(userPokemon1.getName() + " being attacked!!");
                // Calculate defense and special defense for userPokemon1
                defenseStrength1 = calculateDefenseStrength(userPokemon1, System.currentTimeMillis(), wildPokemon2);
                specialDefenseStrength1 = calculateSpecialDefenseStrength(userPokemon1, System.currentTimeMillis(), wildPokemon2);
                // Apply wild Pokémon's attack to user's Pokémon
                int wildAttack1 = wildPokemon2.getAttack() + wildPokemon2.getSpecialAttack() - (defenseStrength1 + specialDefenseStrength1);
                System.out.println("Opponent " + wildPokemon2.getName() + " attacks! Attack: " + wildAttack1 + ", HP: " + wildPokemon2.getHealth() + ",");
                printEffectivenessMessage(wildPokemon2, userPokemon1);

                System.out.println();
                System.out.print(userPokemon2.getName() + " being attacked!!");
                // Calculate defense and special defense for userPokemon1
                defenseStrength2 = calculateDefenseStrength(userPokemon2, System.currentTimeMillis(), wildPokemon2);
                specialDefenseStrength2 = calculateSpecialDefenseStrength(userPokemon2, System.currentTimeMillis(), wildPokemon2);
                // Apply wild Pokémon's attack to user's Pokémon
                int wildAttack2 = wildPokemon2.getAttack() + wildPokemon2.getSpecialAttack() - (defenseStrength2 + specialDefenseStrength2);
                System.out.println("Opponent " + wildPokemon2.getName() + " attacks! Attack: " + wildAttack2 + ", HP: " + wildPokemon2.getHealth() + ",");
                printEffectivenessMessage(wildPokemon2, userPokemon2);

                userPokemon1.takeDamage(wildAttack1);
                userPokemon2.takeDamage(wildAttack2);
                System.out.println("Player's Remaining HP:");
                displayRemainingHP(userPokemon1, userPokemon2);
            }

            // Check if both wild Pokémon are defeated after Wild Pokémon 2's turn
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                System.out.println("You won the battle!");
                battleWins = true;
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

            battleWins = true;

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
                PokeballType chosenPokeball = player.chooseRandomPokeball();
                System.out.println("A " + chosenPokeball + " appeared!");

                System.out.println("Press Enter to continue...");
                Scanner keyboard = new Scanner(System.in);
                keyboard.nextLine(); // Wait for user to press Enter

                boolean isCaught = player.attemptCatch(chosenPokeball);
                if (isCaught) {
                    System.out.println("You caught " + wildPokemon1.getName() + "!");
                    wildPokemon1.resetHealth(); // Reset health to maxHealth before saving
                    player.saveChosenPokemon(wildPokemon1);
                    caughtAnyPokemon = true;
                } else {
                    System.out.println(wildPokemon1.getName() + " escaped!");
                }
            } else if (choice == 2 && wildPokemon2.getHealth() <= 0) {
                PokeballType chosenPokeball = player.chooseRandomPokeball();
                System.out.println("A " + chosenPokeball + " appeared!");

                System.out.println("Press Enter to continue...");
                Scanner keyboard = new Scanner(System.in);
                keyboard.nextLine(); // Wait for user to press Enter

                boolean isCaught = player.attemptCatch(chosenPokeball);
                if (isCaught) {
                    System.out.println("You caught " + wildPokemon2.getName() + "!");
                    wildPokemon2.resetHealth(); // Reset health to maxHealth before saving
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
                typeChart, attackStrength1, attackStrength2,
                defenseStrength1, defenseStrength2,
                specialAttackStrength1, specialAttackStrength2,
                specialDefenseStrength1, specialDefenseStrength2);

        System.out.println("\nBattle ended. Your score: " + battleScore);

        // Save the score to the user's file
        scoreCalculation.saveUserScore(player.getUserId(), battleScore);

        // Calculate battle points using instance method
        scoreCalculation.calculateBattlePoints(player, battleScore);

        // Display battle points earned
        System.out.println("Battle points earned: " + player.getBattlePoints());

        pokemonUpgrade(battleScore, userPokemon1, userPokemon2);

        scoreCalculation.updateTopScores(userId, battleScore);
        scoreCalculation.displayTopScores();

        //rank display
        System.out.println("\nYou got rank " + scoreCalculation.determineRankMessage(battleScore));
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

    private String capitalize(String type) {
        if (type == null || type.isEmpty()) {
            return type;
        }
        return type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
    }

    public void printEffectivenessMessage(Pokemon attacker, Pokemon defender) {
        double effectiveness = typeChart.getEffectiveness(capitalize(attacker.getTypes().get(0)), capitalize(defender.getTypes().get(0))); // get(0) is used to only get the first type of a pokemon
        System.out.print("Effectiveness value: " + effectiveness);
        if (effectiveness == 2.0) {
            System.out.println(" It's super effective! ");
        } else if (effectiveness == 0.5) {
            System.out.println(" It's not very effective... ");
        } else if (effectiveness == 0.0) {
            System.out.println(" It has no effect... ");
        } else {
            System.out.println(" It's a normal hit ");
        }
    }

    private int calculateAttackStrength(Pokemon attacker, long reactionTime, Pokemon defender) {
        int baseAttack = calculateBaseAttack(attacker, reactionTime);
        double effectiveness = typeChart.getEffectiveness(attacker.getTypes().get(0), defender.getTypes().get(0));
        return (int) (baseAttack * effectiveness);
    }

    private int calculateBaseAttack(Pokemon pokemon, long reactionTime) {
        if (reactionTime < 1000) {
            return pokemon.getAttack() * 2;
        } else if (reactionTime < 2000) {
            return pokemon.getAttack();
        } else {
            return pokemon.getAttack() / 2;
        }
    }

    private int calculateSpecialAttackStrength(Pokemon attacker, long reactionTime, Pokemon defender) {
        int baseSpecialAttack = calculateBaseSpecialAttack(attacker, reactionTime);
        double effectiveness = typeChart.getEffectiveness(attacker.getTypes().get(0), defender.getTypes().get(0));
        return (int) (baseSpecialAttack * effectiveness);
    }

    private int calculateBaseSpecialAttack(Pokemon pokemon, long reactionTime) {
        if (reactionTime < 1000) {
            return pokemon.getSpecialAttack() * 2;
        } else if (reactionTime < 2000) {
            return pokemon.getSpecialAttack();
        } else {
            return pokemon.getSpecialAttack() / 2;
        }
    }

    private int calculateDefenseStrength(Pokemon defender, long reactionTime, Pokemon attacker) {
        int baseDefense = calculateBaseDefense(defender, reactionTime);
        double effectiveness = typeChart.getEffectiveness(attacker.getTypes().get(0), defender.getTypes().get(0));
        return (int) (baseDefense / effectiveness);
    }

    private int calculateBaseDefense(Pokemon pokemon, long reactionTime) {
        if (reactionTime < 1000) {
            return pokemon.getDefense() * 2;
        } else if (reactionTime < 2000) {
            return pokemon.getDefense();
        } else {
            return pokemon.getDefense() / 2;
        }
    }

    private int calculateSpecialDefenseStrength(Pokemon defender, long reactionTime, Pokemon attacker) {
        int baseSpecialDefense = calculateBaseSpecialDefense(defender, reactionTime);
        double effectiveness = typeChart.getEffectiveness(attacker.getTypes().get(0), defender.getTypes().get(0));
        return (int) (baseSpecialDefense / effectiveness);
    }

    private int calculateBaseSpecialDefense(Pokemon pokemon, long reactionTime) {
        if (reactionTime < 1000) {
            return pokemon.getSpecialDefense() * 2;
        } else if (reactionTime < 2000) {
            return pokemon.getSpecialDefense();
        } else {
            return pokemon.getSpecialDefense() / 2;
        }
    }

    private void displayRemainingHP(Pokemon... pokemons) {
        for (Pokemon pokemon : pokemons) {
            System.out.println(pokemon.getName() + ": " + pokemon.getHealth());
        }
    }

    public void pokemonUpgrade(int battleScore, Pokemon userPokemon1, Pokemon userPokemon2) {
        if (battleWins) {
            System.out.println("Increasing attack and defense stats for your Pokémon...");

            if (userPokemon1 != null) {
                userPokemon1.increaseAttack(5);
                userPokemon1.increaseDefense(5);
            }

            if (userPokemon2 != null) {
                userPokemon2.increaseAttack(5);
                userPokemon2.increaseDefense(5);
            }

            if (battleScore > 2000) {
                System.out.println("Increasing special attack and defense stats for your Pokémon...");

                if (userPokemon1 != null) {
                    userPokemon1.increaseSpecialAttack(3);
                    userPokemon1.increaseSpecialDefense(3);
                }

                if (userPokemon2 != null) {
                    userPokemon2.increaseSpecialAttack(3);
                    userPokemon2.increaseSpecialDefense(3);
                }
            }

            // Update the details of the modified Pokémon in the file without resetting special stats
            if (userPokemon1 != null) {
                userPokemon1.resetHealth(); // Reset health to max
                userPokemon1.resetSpecialAttack(); // Reset special attack to original
                userPokemon1.resetSpecialDefense(); // Reset special defense to original
                player.updatePokemonDetailsToFile(userPokemon1);
            }

            if (userPokemon2 != null) {
                userPokemon2.resetHealth(); // Reset health to max
                userPokemon2.resetSpecialAttack(); // Reset special attack to original
                userPokemon2.resetSpecialDefense(); // Reset special defense to original
                player.updatePokemonDetailsToFile(userPokemon2);
            }
        }
    }
}