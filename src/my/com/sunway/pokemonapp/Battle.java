package my.com.sunway.pokemonapp;

import java.io.*;
import java.util.*;

public class Battle {
    private QuickTimeEvent qte;
    private List<Pokemon> rentalPokemons;
    private boolean battleWins;
    private TypeChart typeChart;
    private Player player;
    private BattleScoreCalculation scoreCalculation;

    public Battle() {
    	this.qte = new QuickTimeEvent();
        this.rentalPokemons = new ArrayList<>();
        this.typeChart = new TypeChart();
        this.scoreCalculation = new BattleScoreCalculation();
        this.battleWins = false; // Initialize battleWins to false
        this.player = new Player();
        
        //rental Pokémon 
        rentalPokemons.add(new Pokemon("Pikachu", 35, 55, 40, 3, List.of("electric"), 90, 50, 50, 3));
        rentalPokemons.add(new Pokemon("Typhlosion", 78, 84, 78, 3, List.of("fire"), 100, 109, 85, 2));
        rentalPokemons.add(new Pokemon("Snorlax", 160, 110, 65, 3, List.of("normal"), 30, 65, 110, 1));
        rentalPokemons.add(new Pokemon("Tyrunt", 58, 89, 77, 3, List.of("rock", "dragon"), 48, 45, 45, 7));
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
    
    public static void print(String text) {  
    	final int DELAY = 70; //millisecond
    	
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
    
    public void startBattle(List<Pokemon> userPokemons, List<Pokemon> stageWildPokemons, String userId) {
        this.player.setUserId(userId);
        this.battleWins = false;
    	
    	if (stageWildPokemons.isEmpty()) {
            println("No wild Pokémon available for battle. Exiting.");
            return;
        }

        List<Pokemon> wildPokemons = chooseWildPokemons(stageWildPokemons);

        println("\nTwo wild Pokémon appear for battle:");
        Pokemon wildPokemon1 = wildPokemons.get(0);
        Pokemon wildPokemon2 = wildPokemons.get(1);
        println("1: " + wildPokemon1.getName() + " | Type: " + String.join(", ", wildPokemon1.getTypes()) + " | Stars: " + wildPokemon1.getStars());
        println("2: " + wildPokemon2.getName() + " | Type: " + String.join(", ", wildPokemon2.getTypes()) + " | Stars: " + wildPokemon2.getStars());

        Scanner scanner = new Scanner(System.in);
        
        
        Pokemon userPokemon1 = null;
        Pokemon userPokemon2 = null;

        // Check if user has at least 2 Pokémon to choose
        if (userPokemons.size() < 2) {
        	if (userPokemons.size() < 2) {
        		println("You don't have any Pokémon. Choosing rental Pokémon.");
        		int rentalChoice1 = choosePokemon(rentalPokemons, scanner, -1);
        		Pokemon rentalPokemon1 = rentalPokemons.get(rentalChoice1);
        	
        		print("Rental Pokémon chosen:");
        		println("1: " + rentalPokemon1.getName() + " | Type: " + String.join(", ", rentalPokemon1.getTypes()) + " | Stars: " + rentalPokemon1.getStars());
        		
        		// Use the rental Pokémon for battle
        		userPokemon1 = rentalPokemon1;
        	} else {
        		// Choose user's Pokémon
                userPokemon1 = userPokemons.get(0); // Only one user Pokémon available
                print("\nYour Pokémon:");
                println("1: " + userPokemon1.getName() + " | Type: " + String.join(", ", userPokemon1.getTypes()) + " | Stars: " + userPokemon1.getStars());
                println("You only have one Pokémon. Choosing one rental Pokémon.");

                // Choose one rental Pokémon
                int rentalChoice1 = choosePokemon(rentalPokemons, scanner, -1);
                Pokemon rentalPokemon1 = rentalPokemons.get(rentalChoice1);

                print("Rental Pokémon chosen:");
                println("2: " + rentalPokemon1.getName() + " | Type: " + String.join(", ", rentalPokemon1.getTypes()) + " | Stars: " + rentalPokemon1.getStars());

                userPokemon2 = rentalPokemon1;
            	}	
        	} else {
        		print("\nChoose your Pokémon for battle:");
            
        		// Choose user's Pokémon for battle
        		int userChoice1 = choosePokemon(userPokemons, scanner, -1);
        		int userChoice2 = choosePokemon(userPokemons, scanner, userChoice1);

        		println("You chose: ");
        		userPokemon1 = userPokemons.get(userChoice1);
        		userPokemon2 = userPokemons.get(userChoice2);
        		println("1: " + userPokemon1.getName() + " | Type: " + String.join(", ", userPokemon1.getTypes()) + " | Stars: " + userPokemon1.getStars());
        		println("2: " + userPokemon2.getName() + " | Type: " + String.join(", ", userPokemon2.getTypes()) + " | Stars: " + userPokemon2.getStars());
        }

        // If user only chose one Pokémon, assign second Pokémon to another user Pokémon or another rental Pokémon
        if (userPokemon2 == null){
        	int rentalChoice2 = choosePokemon(rentalPokemons, scanner, -1);
        	Pokemon rentalPokemon2 = rentalPokemons.get(rentalChoice2);
        	
        	print("Second Rental Pokémon chosen:");
        	println("2: " + rentalPokemon2.getName() + " | Type: " + String.join(", ", rentalPokemon2.getTypes()) + " | Stars: " + rentalPokemon2.getStars());
        
        	userPokemon2 = rentalPokemon2;
        }
       
        System.out.println("\r\n"
        		+ "  ____        _   _   _         _____ _             _   _ \r\n"
        		+ " |  _ \\      | | | | | |       / ____| |           | | | |\r\n"
        		+ " | |_) | __ _| |_| |_| | ___  | (___ | |_ __ _ _ __| |_| |\r\n"
        		+ " |  _ < / _` | __| __| |/ _ \\  \\___ \\| __/ _` | '__| __| |\r\n"
        		+ " | |_) | (_| | |_| |_| |  __/  ____) | || (_| | |  | |_|_|\r\n"
        		+ " |____/ \\__,_|\\__|\\__|_|\\___| |_____/ \\__\\__,_|_|   \\__(_)\r\n"
        		+ "                                                          \r\n"
        		+ "                                                          \r\n"
        		+ "");

        //initialize for attack and defense strength
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

       
        boolean battleEnded = false;
        
        
        while (!battleEnded) {
            Scanner enter = new Scanner(System.in);

        	long elapsedTime = System.currentTimeMillis() - startTime;
        	if (elapsedTime >= battleTimeLimit) {
        		println("Time's up!");
        		break;
        	}
        	
        	// Determine atack order based on speed
        	List<Pokemon> attackOrder = Arrays.asList(userPokemon1, userPokemon2, wildPokemon1, wildPokemon2);
        	attackOrder.sort(Comparator.comparingInt(Pokemon::getSpeed).reversed());
        	
        	
        	for (Pokemon attacker : attackOrder) {
        		if (attacker.getHealth() <= 0) {
        			continue; // Skip if the attacker is fainted
        		}
        	
            if (attacker == userPokemon1 || attacker == userPokemon2) {
                println("\nPlayer's Pokémon " + attacker.getName() + " turn! Press Enter to continue...");
                enter.nextLine();
                long reactionTime1 = qte.performQTE();
                if (reactionTime1 != -1) {
                	print(wildPokemon1.getName() + " being attacked!!");
                	attackStrength1 = calculateAttackStrength(attacker, reactionTime1, wildPokemon1);
                	specialAttackStrength1 = calculateSpecialAttackStrength(attacker, reactionTime1, wildPokemon1);
                    print(userPokemon1.getName() + " attacks! Attack: " + (attackStrength1 + specialAttackStrength1) + ", HP: " + attacker.getHealth());
                	printEffectivenessMessage(attacker, wildPokemon1);
                	
                	System.out.println();
                	print(wildPokemon2.getName() + " being attacked!!");
                	attackStrength2 = calculateAttackStrength(attacker, reactionTime1, wildPokemon2);
                	specialAttackStrength2 = calculateSpecialAttackStrength(attacker, reactionTime1, wildPokemon2);
                    print(userPokemon1.getName() + " attacks! Attack: " + (attackStrength2 + specialAttackStrength2) + ", HP: " + attacker.getHealth());
                	printEffectivenessMessage(attacker, wildPokemon2);
                	
                	// Apply attack and special attack to wild Pokémons
                    wildPokemon1.takeDamage(attackStrength1 + specialAttackStrength1);
                    wildPokemon2.takeDamage(attackStrength2 + specialAttackStrength2);
                    
                } else {
                	print(attacker.getName() + " attack failed!");
                }
                print("\nOpponent's Remaining HP:");
                displayRemainingHP(wildPokemon1, wildPokemon2);
            
            } else if (attacker == wildPokemon1 || attacker == wildPokemon2) {
                println("\nWild Pokémon " + attacker.getName() + "'s turn!");
                
                print(userPokemon1.getName() + " being attacked!!");
                defenseStrength1 = calculateDefenseStrength(userPokemon1, System.currentTimeMillis(), attacker);
                specialDefenseStrength1 = calculateSpecialDefenseStrength(userPokemon1, System.currentTimeMillis(), attacker);
                int wildAttack1 = Math.max(attacker.getAttack() + attacker.getSpecialAttack() - (defenseStrength1 + specialDefenseStrength1), 0);
                print("Opponent " + attacker.getName() + " attacks! Attack: " + wildAttack1 + ", HP: " + attacker.getHealth());
            	printEffectivenessMessage(attacker, userPokemon1);

            	System.out.println();
                print(userPokemon2.getName() + " being attacked!!");
                defenseStrength2 = calculateDefenseStrength(userPokemon2, System.currentTimeMillis(), attacker);
                specialDefenseStrength2 = calculateSpecialDefenseStrength(userPokemon2, System.currentTimeMillis(), attacker);
                int wildAttack2 = Math.max(attacker.getAttack() + attacker.getSpecialAttack() - (defenseStrength2 + specialDefenseStrength2), 0);
                print("Opponent " + attacker.getName() + " attacks! Attack: " + wildAttack2 + ", HP: " + attacker.getHealth());
            	printEffectivenessMessage(attacker, userPokemon2);
                
                userPokemon1.takeDamage(wildAttack1);
                userPokemon2.takeDamage(wildAttack2);
                
                print("\nPlayer's Remaining HP:");
                displayRemainingHP(userPokemon1, userPokemon2);
            }
            
            // Check if both wild Pokémon are defeated after each turn
            if (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0) {
                println("You won the battle!");
                battleWins = true;
                battleEnded = true;
                break;
            }
            
            // Check if both user Pokémon are defeated after each turn
            if (userPokemon1.getHealth() <= 0 && userPokemon2.getHealth() <= 0) {
                println("You lost the battle!");
                battleWins = false;
                battleEnded = true;
                break;
            }
        }
    }
        
                
        // Handle catching a Pokémon if applicable
        if ((wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() > 0 && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0)) ||
            (wildPokemon2.getHealth() <= 0 && wildPokemon1.getHealth() > 0 && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0)) ||
            (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0 && (System.currentTimeMillis() - startTime) < battleTimeLimit && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0))) {
        	
        System.out.println("\r\n"
        		+ "   _____      _       _       _______ _                \r\n"
        		+ "  / ____|    | |     | |     |__   __(_)               \r\n"
        		+ " | |     __ _| |_ ___| |__      | |   _ _ __ ___   ___ \r\n"
        		+ " | |    / _` | __/ __| '_ \\     | |  | | '_ ` _ \\ / _ \\\r\n"
        		+ " | |___| (_| | || (__| | | |    | |  | | | | | | |  __/\r\n"
        		+ "  \\_____\\__,_|\\__\\___|_| |_|    |_|  |_|_| |_| |_|\\___|\r\n"
        		+ "                                                       \r\n"
        		+ "                                                       \r\n"
        		+ "");

        	battleWins = true;
        	
            println("You have the chance to catch one of the defeated wild Pokémon!");

            boolean caughtAnyPokemon = false; // Flag to track if any Pokémon was caught

            // Check if wildPokemon1 is defeated
            if (wildPokemon1.getHealth() <= 0) {
                println("1. Catch " + wildPokemon1.getName());
            }

            // Check if wildPokemon2 is defeated
            if (wildPokemon2.getHealth() <= 0) {
                println("2. Catch " + wildPokemon2.getName());
            }

            print("Choose which Pokémon you want to catch (enter number): ");
            int choice = scanner.nextInt();

            while (true) {
            	try {
            		if (choice == 1 && wildPokemon1.getHealth() <= 0) {
            			PokeballType chosenPokeball = player.chooseRandomPokeball();
            			print("A " + chosenPokeball + " appeared!");

            			println("Press Enter to continue...");
            			Scanner keyboard = new Scanner(System.in);
            			keyboard.nextLine(); // Wait for user to press Enter

            			boolean isCaught = player.attemptCatch(chosenPokeball);
            			if (isCaught) {
            				print("You caught " + wildPokemon1.getName() + "!");
            				wildPokemon1.resetHealth(); // Reset heath to maxHealth before saving
            				player.saveChosenPokemon(wildPokemon1);
            				caughtAnyPokemon = true;
            				break;
            			} else {
            				print(wildPokemon1.getName() + " escaped!");
            				break;
            			}
            		} else if (choice == 2 && wildPokemon2.getHealth() <= 0) {
            			PokeballType chosenPokeball = player.chooseRandomPokeball();
            			print("A " + chosenPokeball + " appeared!");

            			println("Press Enter to continue...");
            			Scanner keyboard = new Scanner(System.in);
            			keyboard.nextLine(); // Wait for user to press Enter

            			boolean isCaught = player.attemptCatch(chosenPokeball);
            			if (isCaught) {
            				print("You caught " + wildPokemon2.getName() + "!");
            				wildPokemon2.resetHealth();
            				player.saveChosenPokemon(wildPokemon2);
            				caughtAnyPokemon = true;
            				break;
            			} else {
            				print(wildPokemon2.getName() + " escaped!");
            				break;
            			}
            		}
            	} catch (Exception e){
            		println("Invalid input! Please try again");
            		continue;
            	}
            }

            // Inform the player if no Pokémon was caught
            if (!caughtAnyPokemon) {
                print("No Pokémon were caught during this opportunity.");
            }
        }
        
        int battleScore = scoreCalculation.calculateBattleScore(startTime, battleTimeLimit,
        		userPokemon1, userPokemon2,
        		wildPokemon1, wildPokemon2,
        		typeChart, attackStrength1, attackStrength2,
        		defenseStrength1, defenseStrength2,
        		specialAttackStrength1, specialAttackStrength2,
        		specialDefenseStrength1, specialDefenseStrength2);
        
        println("\nBattle ended. Your score: " + battleScore);
        
        
        // Save the score to the user's file
        scoreCalculation.saveUserScore(player.getUserId(), battleScore);
        
        
        // Calculate and update battle points
        int battlePoints = BattleScoreCalculation.calculateBattlePoints(battleScore);
        
        
        // Load the player data
        Player player = Game.readPlayerData(userId);
        
        
        // Add battle points to the player
        player.addBattlePoints(battlePoints);

        
        // Save the updated player data
        Game.savePlayerData(player);
        
        
        println("Battle finished! Points earned: " + battlePoints);
        println("Total Battle Points: " + player.getBattlePoints());
        
        
        pokemonUpgrade(battleScore, userPokemon1, userPokemon2);
        
        
        scoreCalculation.updateTopScores(userId, battleScore);
        scoreCalculation.displayTopScores();
        
        // rank display
        println("\nYou got rank " + scoreCalculation.determineRankMessage(battleScore));
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

        println("\nAvailable Pokémon:");
        for (int i = 0; i < availablePokemons.size(); i++) {
            Pokemon pokemon = availablePokemons.get(i);
            String typesString = String.join(", ", pokemon.getTypes()); //Join types with a comma
            System.out.println((i + 1) + ": '" + pokemon.getName() + "' | Type: " + typesString + " | Stars: " + pokemon.getStars());
        }
        
        while (userChoice < 0 || userChoice >= availablePokemons.size() || userChoice == previousChoice) {
            println("Choose your Pokémon (enter number): ");
            try {
                userChoice = scanner.nextInt() - 1;
                if (userChoice < 0 || userChoice >= availablePokemons.size() || userChoice == previousChoice) {
                    println("Invalid Pokémon choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        return userChoice;
    }

	private String capitalize(String type) {
	    if (type == null || type.isEmpty()) {
	        return type;
	    }
	    return type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
	}
	
	  public void printEffectivenessMessage(Pokemon attacker, Pokemon defender) {
	        double effectiveness = typeChart.getEffectiveness(capitalize(attacker.getTypes().get(0)), capitalize(defender.getTypes().get(0))); // get(0) is used to only get the first type of a pokemon
	        if (effectiveness == 2.0) {
	            print("It's super effective!");
	        } else if (effectiveness == 0.5) {
	            print("It's not very effective...");
	        } else if (effectiveness == 0.0) {
	            print("It has no effect...");
	        } else {
	            print("It's a normal hit");
	        }
	    }
	  
	private int calculateAttackStrength(Pokemon attacker, long reactionTime, Pokemon defender) {
	     // Calculate base attack strength based on reaction time
	     int baseAttack = calculateBaseAttack(attacker, reactionTime);           
	     // Get type effectiveness from TypeChart
	     double effectiveness = typeChart.getEffectiveness(capitalize(attacker.getTypes().get(0)), capitalize(defender.getTypes().get(0)));  
	     // Apply type effectiveness to attack strength
	     return (int) (baseAttack * effectiveness);
	    }
		
	
    private int calculateBaseAttack(Pokemon pokemon, long reactionTime) {
        // Example implementation of calculating base attack
    	if (reactionTime < 5000) {
            return pokemon.getAttack() * 2;
        } else if (reactionTime < 10000) {
            return pokemon.getAttack();
        } else {
            return pokemon.getAttack() / 2;
        }
    }
    
    private int calculateSpecialAttackStrength(Pokemon attacker, long reactionTime, Pokemon defender) {
        // Calculate base special attack strength based on reaction time
        int baseSpecialAttack = calculateBaseSpecialAttack(attacker, reactionTime);
        // Get type effectiveness from TypeChart
        double effectiveness = typeChart.getEffectiveness(capitalize(attacker.getTypes().get(0)), capitalize(defender.getTypes().get(0)));
        // Apply type effectiveness to special attack strength
        return (int) (baseSpecialAttack * effectiveness);	     
    }

    private int calculateBaseSpecialAttack(Pokemon pokemon, long reactionTime) {
        // Example implementation of calculating base special attack
        if (reactionTime < 5000) {
            return pokemon.getSpecialAttack() * 2;
        } else if (reactionTime < 10000) {
            return pokemon.getSpecialAttack();
        } else {
            return pokemon.getSpecialAttack() / 2;
        }
    }
    
    private int calculateDefenseStrength(Pokemon defender, long reactionTime, Pokemon attacker) {
        // Calculate base defense strength based on reaction time
        int baseDefense = calculateBaseDefense(defender, reactionTime);
        // Get type effectiveness from TypeChart
        double effectiveness = typeChart.getEffectiveness(capitalize(attacker.getTypes().get(0)), capitalize(defender.getTypes().get(0)));
        // Apply type effectiveness to defense strength
        return (int) (baseDefense * effectiveness);
    }

    private int calculateBaseDefense(Pokemon pokemon, long reactionTime) {
        // Example implementation of calculating base defense
        if (reactionTime < 5000) {
            return pokemon.getDefense() * 2;
        } else if (reactionTime < 10000) {
            return pokemon.getDefense();
        } else {
            return pokemon.getDefense() / 2;
        }
    }

    private int calculateSpecialDefenseStrength(Pokemon defender, long reactionTime, Pokemon attacker) {
        // Calculate base special defense strength based on reaction time
        int baseSpecialDefense = calculateBaseSpecialDefense(defender, reactionTime);
        // Get type effectiveness from TypeChart
        double effectiveness = typeChart.getEffectiveness(capitalize(attacker.getTypes().get(0)), capitalize(defender.getTypes().get(0)));
        // Apply type effectiveness to special defense strength
        return (int) (baseSpecialDefense * effectiveness);
    }

    private int calculateBaseSpecialDefense(Pokemon pokemon, long reactionTime) {
        // Example implementation of calculating base special defense
        if (reactionTime < 5000) {
            return pokemon.getSpecialDefense() * 2;
        } else if (reactionTime < 10000) {
            return pokemon.getSpecialDefense();
        } else {
            return pokemon.getSpecialDefense() / 2;
        }
    }
    
    private void displayRemainingHP(Pokemon... pokemons) {
        for (Pokemon pokemon : pokemons) {
            println(pokemon.getName() + ": " + pokemon.getHealth() + " HP");
        }
    }
    
    public void pokemonUpgrade(int battleScore, Pokemon userPokemon1, Pokemon userPokemon2) {
        if (battleWins) {
            println("\nIncreasing attack and defense stats for your Pokémon...");

            if (userPokemon1 != null) {
                userPokemon1.increaseAttack(5);
                userPokemon1.increaseDefense(5);
            }

            if (userPokemon2 != null) {
                userPokemon2.increaseAttack(5);
                userPokemon2.increaseDefense(5);
            }

            if (battleScore > 2000) {
                println("\nIncreasing special attack and defense stats for your Pokémon...");

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
 
