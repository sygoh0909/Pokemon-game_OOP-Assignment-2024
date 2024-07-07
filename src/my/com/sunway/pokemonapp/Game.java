package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Game {
    private Player player;
    private List<List<Pokemon>> stages; // Pokémon in the stages
    private List<String> stageNames;
    private PokemonService pokemonService;
    private List<Pokemon> userPokemons;
    private Battle battle; // Add Battle instance

    public Game() {
        this.stages = new ArrayList<>();
        this.stageNames = new ArrayList<>();
        this.pokemonService = new PokemonService();
        this.userPokemons = new ArrayList<>(); // Initialize the userPokemons list
        this.battle = new Battle(); // Initialize the Battle instance
        setupStages();
    }

    public boolean login() {
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
                    this.player = new Player(userId, newPassword);
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
                    this.player = new Player(storedUserId, storedPassword);
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

        System.out.println("Choose a Pokémon from the following list:");
        for (int i = 0; i < printCount; i++) {
            Pokemon pokemon = chosenStagePokemons.get(i);
            String typesString = String.join(", ", pokemon.getTypes()); // Join types with a comma
            System.out.println((i + 1) + ": " + pokemon.getName() + " | Type: " + typesString + " | Stars: " + pokemon.getStars());
        }

        int pokemonChoice = -1;
        while (pokemonChoice < 0 || pokemonChoice >= printCount) {
            try {
                pokemonChoice = scanner.nextInt() - 1;
                if (pokemonChoice < 0 || pokemonChoice >= printCount) {
                    System.out.println("Invalid Pokémon choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }
        }

        Pokemon chosenPokemon = chosenStagePokemons.get(pokemonChoice);
        System.out.println("You chose: " + chosenPokemon.getName());

        // Randomly choose a Poké Ball
        Pokeball chosenPokeball = chooseRandomPokeball();
        System.out.println("A " + chosenPokeball + " appeared!");

        // Ask user to press Enter to attempt to catch the Pokémon
        System.out.println("Press Enter to attempt to catch the " + chosenPokemon.getName() + "!");
        Scanner keyboard = new Scanner(System.in);
        keyboard.nextLine(); //wait for user to press enter

        // Attempt to catch the Pokémon
        boolean isCaught = attemptCatch(chosenPokeball);
        if (isCaught) {
            System.out.println("You caught the " + chosenPokemon.getName() + "!");
            saveChosenPokemon(chosenPokemon);
        } else {
            System.out.println("The " + chosenPokemon.getName() + " escaped!");
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

    private void loadUserPokemons() {
        String fileName = "user_pokemon_list_" + player.getUserId() + ".txt";
        Path filePath = Paths.get(fileName);

        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                for (String line : lines) {
                    Pokemon pokemon = parsePokemonFromString(line);
                    if (pokemon != null) {
                        userPokemons.add(pokemon);
                    }
                }
            } else {
                System.out.println("No Pokémon list found for user. Creating new list...");
                Files.createFile(filePath); // Create a new file if it doesn't exist
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Pokemon parsePokemonFromString(String pokemonDetails) {
        // Example format: "Pokemon{name='Charizard', health=100, ...}"
        // Implement parsing logic to create a Pokemon object from string representation
        // Split by comma and then parse each attribute

        // Remove unnecessary characters and split by commas
        String[] parts = pokemonDetails.replace("Pokemon{", "").replace("}", "").split(", ");

        // Initialize variables to store parsed values
        String name = null;
        int health = 0;
        int attack = 0;
        int defense = 0;
        int stars = 0;
        List<String> types = new ArrayList<>();
        int speed = 0;
        int accuracy = 0;
        int specialAttack = 0;
        int specialDefense = 0;

        // Iterate through parts to parse each attribute
        for (String part : parts) {
            // Split each part into key and value
            String[] keyValue = part.split("=");
            if (keyValue.length != 2) {
                // Handle unexpected format or skip if needed
                continue;
            }
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            // Switch case to assign values based on key
            switch (key) {
                case "name":
                    name = value;
                    break;
                case "health":
                    health = Integer.parseInt(value);
                    break;
                case "attack":
                    attack = Integer.parseInt(value);
                    break;
                case "defense":
                    defense = Integer.parseInt(value);
                    break;
                case "stars":
                    stars = Integer.parseInt(value);
                    break;
                case "types":
                    // Remove brackets and split by comma for types
                    types = Arrays.asList(value.substring(1, value.length() - 1).split(", "));
                    break;
                case "speed":
                    speed = Integer.parseInt(value);
                    break;
                case "accuracy":
                    accuracy = Integer.parseInt(value);
                    break;
                case "special attack":
                    specialAttack = Integer.parseInt(value);
                    break;
                case "special defense":
                    specialDefense = Integer.parseInt(value);
                    break;
                default:
                    // Handle unrecognized keys or ignore
                    break;
            }
        }

        // Create and return Pokemon object
        return new Pokemon(name, health, attack, defense, stars, types, speed, specialAttack, specialDefense);
    }

    private Pokeball chooseRandomPokeball() {
        double randomValue = Math.random();
        double cumulativeProbability = 0.0;
        for (Pokeball pokeball : Pokeball.values()) {
            cumulativeProbability += pokeball.getAppearanceRate();
            if (randomValue <= cumulativeProbability) {
                return pokeball;
            }
        }
        return Pokeball.POKEBALL; // Default case, should never occur if appearance rates sum to 1
    }

    private boolean attemptCatch(Pokeball pokeball) {
        double catchChance = Math.random();
        return catchChance <= pokeball.getCatchRate();
    }

    private void saveChosenPokemon(Pokemon pokemon) {
        String fileName = "user_pokemon_list_" + player.getUserId() + ".txt"; // Use player.getUserId() to get the user ID
        try {
            String pokemonDetails = pokemon.toString();
            Files.write(Paths.get(fileName), (pokemonDetails + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Saved " + pokemon.getName() + " to " + fileName);

            // Add the newly caught pokemon to the userPokemons list
            userPokemons.add(pokemon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Game game = new Game();
        boolean loggedIn = game.login();

        if (loggedIn) {
            System.out.println("Login successful for user ID: " + game.player.getUserId());

            // Load user-specific Pokémon data after successful login
            game.loadUserPokemons();

            game.displayStages();

            // Choose stage and get wild Pokémon for battle
            List<Pokemon> wildPokemons = game.chooseStageAndPokemon();

            // Start battle with wildPokemons
            game.battle.startBattle(game.userPokemons, wildPokemons);

        } else {
            System.out.println("Login failed!");
        }
    }

}