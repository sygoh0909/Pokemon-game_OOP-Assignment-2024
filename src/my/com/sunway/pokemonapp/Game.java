package my.com.sunway.pokemonapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Game {
    private static final String POKEMONS_FILENAME = "pokemons.txt";

    // Define HabitatData as a nested static class or separate top-level class
    public static class HabitatData {
        private final String stageName;
        private final List<Integer> habitatIds;

        public HabitatData(String stageName, List<Integer> habitatIds) {
            this.stageName = stageName;
            this.habitatIds = habitatIds;
        }

        public String getStageName() {
            return stageName;
        }

        public List<Integer> getHabitatIds() {
            return habitatIds;
        }
    }

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

    public void setupStages() {
        try {
            // Read Pokémon data from file
            List<Pokemon> pokemonList = readPokemonsFromFile(POKEMONS_FILENAME);

            // Define habitat combinations
            List<HabitatData> predefinedHabitats = new ArrayList<>();
            predefinedHabitats.add(new HabitatData("Grasslands and Forest", List.of(2, 3)));
            predefinedHabitats.add(new HabitatData("Mountains and Caves", List.of(1, 4)));
            predefinedHabitats.add(new HabitatData("Water Bodies", List.of(7, 9)));
            predefinedHabitats.add(new HabitatData("Urban Areas", List.of(8)));

            // Iterate through each predefined habitat and filter Pokémon accordingly
            for (HabitatData predefinedHabitat : predefinedHabitats) {
                List<Pokemon> stagePokemons = pokemonList.stream()
                        .filter(pokemon -> predefinedHabitat.getHabitatIds().contains(pokemon.getHabitatId()))
                        .collect(Collectors.toList());

                stages.add(stagePokemons);
                stageNames.add(predefinedHabitat.getStageName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<HabitatData> readHabitatsFromFile(String POKEMONS_FILENAME) throws IOException {
        List<HabitatData> habitatDataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(POKEMONS_FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String stageName = parts[0].trim();
                String[] idStrings = parts[1].trim().split(",");
                List<Integer> habitatIds = new ArrayList<>();
                for (String idString : idStrings) {
                    habitatIds.add(Integer.parseInt(idString.trim()));
                }
                habitatDataList.add(new HabitatData(stageName, habitatIds));
            }
        }
        return habitatDataList;
    }

    private List<Pokemon> readPokemonsFromFile(String POKEMONS_FILENAME) throws IOException {
        List<Pokemon> pokemonList = new ArrayList<>();

        // Check if file exists; create it if it doesn't
        File file = new File(POKEMONS_FILENAME);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(POKEMONS_FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pokemon pokemon = parsePokemonFromString(line);
                pokemonList.add(pokemon);
            }
        }
        return pokemonList;
    }

    // Method to parse Pokémon details from a string representation
    public Pokemon parsePokemonFromString(String pokemonDetails) {
        String[] parts = parsePokemon(pokemonDetails);

        // Initialize variables to store parsed values
        String name = null;
        int health = 0;
        int attack = 0;
        int defense = 0;
        int stars = 0;
        List<String> types = new ArrayList<>();
        int speed = 0;
        int specialAttack = 0;
        int specialDefense = 0;
        int habitatId = 0;

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
                    name = value.replace("'", "");
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
                    types.addAll(List.of(value.replaceAll("[\\[\\]]", "").split(", ")));
                    break;
                case "speed":
                    speed = Integer.parseInt(value);
                    break;
                case "specialAttack":
                    specialAttack = Integer.parseInt(value);
                    break;
                case "specialDefense":
                    specialDefense = Integer.parseInt(value);
                    break;
                case "habitatId":
                    habitatId = Integer.parseInt(value);
                    break;
                default:
                    // Handle unrecognized keys or ignore
                    break;
            }
        }

        // Create and return Pokemon object
        return new Pokemon(name, health, attack, defense, stars, types, speed, specialAttack, specialDefense, habitatId);
    }

    // Method to parse attributes from a string into an array of strings
    public static String[] parsePokemon(String input) {
        ArrayList<String> attributesList = new ArrayList<>();

        // Define regex pattern to match key-value pairs
        String regex = "(\\w+)='([^']+)'|\\w+=\\d+|\\w+=\\[([^\\]]+)\\]|\\w+=\\d+";
        Matcher matcher = Pattern.compile(regex).matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null && matcher.group(2) != null) {
                // For string values
                attributesList.add(matcher.group(1) + "=" + matcher.group(2));
            } else if (matcher.group(3) != null) {
                // For list values
                attributesList.add("types=" + matcher.group(3));
            } else {
                // For numeric values
                String[] parts = matcher.group(0).split("=");
                attributesList.add(parts[0] + "=" + parts[1]);
            }
        }

        // Convert ArrayList to String array
        return attributesList.toArray(new String[0]);
    }
}