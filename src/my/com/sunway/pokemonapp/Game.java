package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Game {
    private Player player;
    private List<List<Pokemon>> stages; // Pokémon in the stages
    private List<String> stageNames;
    private PokemonService pokemonService;
    private Battle battle; // Add Battle instance

    public Game() {
        this.player = new Player();
        this.stages = new ArrayList<>();
        this.stageNames = new ArrayList<>();
        this.pokemonService = new PokemonService();
        this.battle = new Battle(); // Initialize the Battle instance
        setupStages();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Game loading...Please be patient...");
        Game game = new Game();

        boolean loggedIn = game.login();

        if (loggedIn) {
            game.player.loadUserPokemons();
            List<Pokemon> userPokemons = game.player.getUserPokemons();
            String userId = game.player.getUserId();
            System.out.println("Login successful for user ID: " + userId);

            game.displayStages();

            // Choose stage and get wild Pokémon for battle
            List<Pokemon> stageWildPokemons = game.chooseStageAndPokemon();

            // Pass user ID to Battle class when starting battle
            game.battle.startBattle(userPokemons, stageWildPokemons, userId);

        } else {
            System.out.println("Login failed!"); //login fail
        }
    }


    public boolean login() {
        System.out.println("Game succesfully loaded."); //pretty pokemon word icon here
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;

        while (!isAuthenticated) {
            System.out.println("Enter your user ID:");
            String userId = scanner.nextLine().trim();

            // Check if user ID exists in stored credentials
            String[] storedCredentials = findStoredCredentials(userId);

            if (storedCredentials == null) {
                System.out.println("User ID not found. Would you like to create a new account? (yes/no)");
                String createAccountChoice = scanner.nextLine().trim().toLowerCase();

                if (createAccountChoice.equals("yes")) {
                    // Prompt for password creation
                    System.out.println("Enter your new password:");
                    String newPassword = scanner.nextLine().trim();

                    // Save new user credentials
                    saveUserCredentials(userId, newPassword);

                    // Authenticate with newly created credentials
                    Player newPlayer = new Player();
                    newPlayer.setUserId(userId);
                    newPlayer.setPassword( newPassword );
                    this.player = newPlayer;
                    isAuthenticated = true;

                } else {
                    System.out.println("Returning to login...");
                }
            } else {
                String storedUserId = storedCredentials[0];
                String storedPassword = storedCredentials[1];

                System.out.println("Enter your password:");
                String password = scanner.nextLine().trim();

                if (storedPassword.equals(password)) {

                    Player player = new Player();
                    player.setUserId(userId);

                    this.player = player;
                    System.out.println("Login successful!");
                    isAuthenticated = true;
                } else {
                    System.out.println("Incorrect password. Login failed. Retry? (yes/no)");
                    String retryChoice = scanner.nextLine().trim().toLowerCase();
                    if (!retryChoice.equals("yes")) {
                        break; // Exit the loop if user chooses not to retry
                    }
                }
            }
        }

        return isAuthenticated;
    }

    private String[] findStoredCredentials(String userId) {
        String fileName = "user_credentials.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].trim().equals(userId)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void displayStages() {
        if (stages.isEmpty()) {
            System.out.println("No stages available.");
        } else {
            for (int i = 0; i < stages.size(); i++) {
                System.out.println("Stage " + (i + 1) + ": " + stageNames.get(i));
                System.out.println("Pokemons that might appear in this stage: ");
                List<Pokemon> stagePokemons = stages.get(i);
                if (stagePokemons.isEmpty()) {
                    System.out.println("No Pokémon in this stage.");
                } else {
                    // Shuffle the list of Pokémon to randomize the order
                    Collections.shuffle(stagePokemons);

                    // Print up to 3 randomly selected Pokémon
                    int printCount = Math.min(3, stagePokemons.size());
                    for (int j = 0; j < printCount; j++) {
                        Pokemon pokemon = stagePokemons.get(j);
                        String typesString = String.join(", ", pokemon.getTypes()); // Join types with a comma
                        System.out.println(pokemon.getName() + " | Type: " + typesString + " | Stars: " + pokemon.getStars());
                    }
                }
                System.out.println();
            }
        }
    }

    public List<Pokemon> chooseStageAndPokemon() throws IOException {
        Scanner scanner = new Scanner(System.in);
        int stageChoice = -1;

        while (stageChoice < 0 || stageChoice >= stages.size()) {
            System.out.println("Choose a stage number: ");
            for (int i = 0; i < stageNames.size(); i++) {
                System.out.println((i + 1) + ": " + stageNames.get(i));
            }

            try {
                stageChoice = scanner.nextInt() - 1;
                if (stageChoice < 0 || stageChoice >= stages.size()) {
                    System.out.println("Invalid stage choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }

        }

        List<Pokemon> chosenStagePokemons = stages.get(stageChoice);
        Collections.shuffle(chosenStagePokemons);
        int printCount = Math.min(3, chosenStagePokemons.size());

        Set<Pokemon> chosenPokemons = new HashSet<>(); // Track chosen Pokémon

// List of availablePokemons directly from chosenStagePokemons
        List<Pokemon> availablePokemons = new ArrayList<>(chosenStagePokemons.subList(0, printCount)); // Copy the first 3 Pokémon from the shuffled list

        while (true) {
            // Display the list of available Pokémon
            System.out.println("Choose a Pokémon from the following list:");
            for (int i = 0; i < availablePokemons.size(); i++) {
                Pokemon pokemon = availablePokemons.get(i);
                String typesString = String.join(", ", pokemon.getTypes()); // Join types with a comma
                System.out.println((i + 1) + ": " + pokemon.getName() + " | Type: " + typesString + " | Stars: " + pokemon.getStars());
            }

            int pokemonChoice = -1;
            while (pokemonChoice < 0 || pokemonChoice >= availablePokemons.size()) {
                try {
                    pokemonChoice = scanner.nextInt() - 1;
                    scanner.nextLine(); // Consume the newline character left by nextInt()
                    if (pokemonChoice < 0 || pokemonChoice >= availablePokemons.size()) {
                        System.out.println("Invalid Pokémon choice. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Clear the invalid input
                    scanner.nextLine(); // Clear the newline character left by next()
                }
            }

            Pokemon chosenPokemon = availablePokemons.get(pokemonChoice);
            System.out.println("You chose: " + chosenPokemon.getName());

            PokeballType chosenPokeball = player.chooseRandomPokeball();
            System.out.println("A " + chosenPokeball + " appeared!");

            System.out.println("Press Enter to continue...");
            scanner.nextLine(); // Wait for the user to press Enter

            boolean isCaught = player.attemptCatch(chosenPokeball);
            if (isCaught) {
                System.out.println("You caught " + chosenPokemon.getName() + "!");
                player.saveChosenPokemon(chosenPokemon);
                chosenPokemons.add(chosenPokemon); // Add to chosenPokemons
            } else {
                System.out.println(chosenPokemon.getName() + " escaped!");
            }

            // Remove the chosen Pokémon from availablePokemons
            availablePokemons.remove(pokemonChoice);

            // Display current battle points and ask if they want to catch another Pokémon if they have enough battle points
            System.out.println("Current battle points: " + player.getBattlePoints());
            if (player.getBattlePoints() >= 200) {
                System.out.println("Do you want to catch another Pokémon? (yes/no)");
                // Exit the main loop if no more Pokémon are available to choose
                if (availablePokemons.isEmpty()) {
                    System.out.println("No more Pokémon available to catch.");
                    break;
                }
                String choice = scanner.nextLine().trim().toLowerCase();

                if (!choice.equals("yes")) {
                    break; // Exit loop if they don't want to catch another Pokémon
                } else {
                    // Deduct points, do not refresh the list of availablePokemons
                    player.deductPoints(200);
                    System.out.println("Points deducted. Current battle points: " + player.getBattlePoints());
                }
            } else {
                System.out.println("Not enough points to catch another Pokémon.");
                break; // Exit loop if there are not enough points to catch another Pokémon
            }
        }



        // Randomly generate wild Pokémon for battle
        List<Pokemon> wildPokemons = battle.chooseWildPokemons(chosenStagePokemons);

        return wildPokemons;
    }

    private void saveUserCredentials(String userId, String password) {
        String fileName = "user_credentials.txt";
        try {
            String credentials = userId + "," + password;
            Files.write(Paths.get(fileName), (credentials + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("User credentials saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupStages() {
        try {
            // Grasslands and Forest (IDs 2, 3)
            List<Integer> grasslandIds = List.of(2, 3);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(grasslandIds));
            stageNames.add("Grasslands and Forest");

            // Mountains and Caves (IDs 1, 4)
            List<Integer> mountainIds = List.of(1, 4);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(mountainIds));
            stageNames.add("Mountains and Caves");

            // Water Bodies (IDs 7, 9)
            List<Integer> waterIds = List.of(7, 9);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(waterIds));
            stageNames.add("Water Bodies");

            // Urban Areas (IDs 8)
            List<Integer> urbanIds = List.of(8);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(urbanIds));
            stageNames.add("Urban Areas");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}