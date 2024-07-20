package my.com.sunway.pokemonapp;

import java.io.*;
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
            String userId =  game.player.getUserId();
            println("Login successful for user ID: " + userId);
           
            
            Player loadedPlayer = readPlayerData(userId);
            game.player = loadedPlayer; // Update the player object with loaded data
            println("User ID: " + game.player.getUserId());
            println("Battle Points: " + game.player.getBattlePoints());
           
            
            game.displayStages();

            
            // Choose stage and get wild Pokémon for battle
            List<Pokemon> stageWildPokemons = game.chooseStageAndPokemon();

            
            // Pass user ID to Battle class when starting battle
            userPokemons = game.player.getUserPokemons();
            game.battle.startBattle(userPokemons, stageWildPokemons, userId);

            
        } else {
            println("Login failed!"); //login fail
        }
    }

    
    public static void print(String text) {  
    	final int DELAY = 100; //millisecond
    	
    	
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
    
    
    public static void println(String text) {  
    	final int DELAYS = 10; //millisecond
    	
    	// convert the String text into char
        for (char ch : text.toCharArray()) {
            System.out.print(ch);
            try {
            	// pause execution (insert delay time)
                Thread.sleep(DELAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(); // Print a new line at the end
    }
    
    
    public boolean login() {
    	println("Game succesfully loaded\n");
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;
        
        
        System.out.println("                                  ,'\\\r\n"
				+ "    _.----.        ____         ,'  _\\   ___    ___     ____\r\n"
				+ "_,-'       `.     |    |  /`.   \\,-'    |   \\  /   |   |    \\  |`.\r\n"
				+ "\\      __    \\    '-.  | /   `.  ___    |    \\/    |   '-.   \\ |  |\r\n"
				+ " \\.    \\ \\   |  __  |  |/    ,','_  `.  |          | __  |    \\|  |\r\n"
				+ "   \\    \\/   /,' _`.|      ,' / / / /   |          ,' _`.|     |  |\r\n"
				+ "    \\     ,-'/  /   \\    ,'   | \\/ / ,`.|         /  /   \\  |     |\r\n"
				+ "     \\    \\ |   \\_/  |   `-.  \\    `'  /|  |    ||   \\_/  | |\\    |\r\n"
				+ "      \\    \\ \\      /       `-.`.___,-' |  |\\  /| \\      /  | |   |\r\n"
				+ "       \\    \\ `.__,'|  |`-._    `|      |__| \\/ |  `.__,'|  | |   |\r\n"
				+ "        \\_.-'       |__|    `-._ |              '-.|     '-.| |   |\r\n"
				+ "                                `'                            '-._|");

        
        while (!isAuthenticated) {
            print("Enter your user ID:");
            String userId = scanner.nextLine().trim();

            
            // Check if user ID exists in stored credentials
            String[] storedCredentials = findStoredCredentials(userId);

            
            if (storedCredentials == null) {
                println("User ID not found. Would you like to create a new account? (yes/no)");
                String createAccountChoice = scanner.nextLine().trim().toLowerCase();

                
                if (createAccountChoice.equals("yes")) {
                    // Prompt for password creation
                    print("Enter your new password:");
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
                    println("Returning to login...");
                }
            } else {
                String storedUserId = storedCredentials[0];
                String storedPassword = storedCredentials[1];

                
                print("Enter your password:");
                String password = scanner.nextLine().trim();

                
                if (storedPassword.equals(password)) {
                    Player player = new Player();
                    player.setUserId(userId);

                    this.player = player;
                    isAuthenticated = true;
                } else {
                    println("Incorrect password. Login failed. Retry? (yes/no)");
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

    
    public static void savePlayerData(Player player) {
        String filename = "player_" + player.getUserId() + ".txt";
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            out.println(player.getUserId());
            out.println(player.getBattlePoints());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static Player readPlayerData(String userId) {
        String filename = "player_" + userId + ".txt";
        Player player = new Player();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            player.setUserId(userId); // Set the userId in the player object
            br.readLine(); // Skip the first line (userId)
            int battlePoints = Integer.parseInt(br.readLine());
            player.setBattlePoints(battlePoints);
        } catch (FileNotFoundException e) {
            // File does not exist, create a new one and initialize player with default values
            print("File not found. Creating new file for user: " + userId);
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
                out.println(userId); // Write userId to the file
                out.println("0"); // Initialize battle points to 0
                player.setUserId(userId); // Set the userId in the player object
                player.setBattlePoints(0); // Initialize battle points in the player object
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }

    public void displayStages() {
        if (stages.isEmpty()) {
            println("No stages available.");
        } else {
        	System.out.println();
            for (int i = 0; i < stages.size(); i++) {
                println("Stage " + (i + 1) + ": " + stageNames.get(i));
                println("Pokemons that might appear in this stage: ");
                List<Pokemon> stagePokemons = stages.get(i);
                if (stagePokemons.isEmpty()) {
                    println("No Pokémon in this stage.");
                } else {
                    // Shuffle the list of Pokémon to randomize the order
                    Collections.shuffle(stagePokemons);

                    // Print up to 3 randomly selected Pokémon
                    int printCount = Math.min(3, stagePokemons.size());
                    for (int j = 0; j < printCount; j++) {
                        Pokemon pokemon = stagePokemons.get(j);
                        String typesString = String.join(", ", pokemon.getTypes()); // Join types with a comma
                        println(pokemon.getName() + " | Type: " + typesString + " | Stars: " + pokemon.getStars());
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
            print("Choose a stage number: ");
            for (int i = 0; i < stageNames.size(); i++) {
                println((i + 1) + ": " + stageNames.get(i));
            }

            try {
                stageChoice = scanner.nextInt() - 1;
                if (stageChoice < 0 || stageChoice >= stages.size()) {
                    println("Invalid stage choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input
            }

        }

        List<Pokemon> chosenStagePokemons = stages.get(stageChoice);
        Collections.shuffle(chosenStagePokemons);
        int maxChoices = Math.min(3, chosenStagePokemons.size());

        Set<Pokemon> chosenPokemons = new HashSet<>(); // Track chosen Pokémon

        // List of availablePokemons directly from chosenStagePokemons
        List<Pokemon> availablePokemons = new ArrayList<>(chosenStagePokemons.subList(0, maxChoices)); // Copy the first 3 Pokémon from the shuffled list

        while (true) {
            // Display the list of available Pokémon
            println("Choose a Pokémon from the following list:");
            for (int i = 0; i < availablePokemons.size(); i++) {
                Pokemon pokemon = availablePokemons.get(i);
                String typesString = String.join(", ", pokemon.getTypes()); // Join types with a comma
                println((i + 1) + ": " + pokemon.getName() + " | Type: " + typesString + " | Stars: " + pokemon.getStars());
            }

            int pokemonChoice = -1;
            while (pokemonChoice < 0 || pokemonChoice >= availablePokemons.size()) {
                try {
                    pokemonChoice = scanner.nextInt() - 1;
                    scanner.nextLine(); // Consume the newline character left by nextInt()
                    if (pokemonChoice < 0 || pokemonChoice >= availablePokemons.size()) {
                        println("Invalid Pokémon choice. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    println("Invalid input. Please enter a number.");
                    scanner.next(); // Clear the invalid input
                    scanner.nextLine(); // Clear the newline character
                }
            }

            Pokemon chosenPokemon = availablePokemons.get(pokemonChoice);
            println("You chose: " + chosenPokemon.getName());

            // Randomly choose a Poké Ball
            PokeballType chosenPokeball = player.chooseRandomPokeball();
            println("A " + chosenPokeball + " appeared!");

            println("Press Enter to attempt to catch the " + chosenPokemon.getName() + "!");
            scanner.nextLine(); // Wait for user to press enter

            boolean isCaught = player.attemptCatch(chosenPokeball);
            if (isCaught) {
                println("You caught the " + chosenPokemon.getName() + "!");
                player.saveChosenPokemon(chosenPokemon);
                chosenPokemons.add(chosenPokemon); // Add to chosenPokemons
            } else {
                println(chosenPokemon.getName() + " escaped!");
            }

            // Remove the chosen Pokémon from availablePokemons
            availablePokemons.remove(chosenPokemon); // Use object reference

            player.loadUserPokemons();

            // Display current battle points and ask if they want to catch another Pokémon if they have enough battle points
            println("\nCurrent battle points: " + player.getBattlePoints());
            if (player.getBattlePoints() >= 200) {
                println("Do you want to catch another Pokémon? (yes/no)");
                // Exit the main loop if no more Pokémon are available to choose
                if (availablePokemons.isEmpty()) {
                    println("No more Pokémon available to catch.");
                    break;
                }
                String choice = scanner.nextLine().trim().toLowerCase();

                // Error handling
                if (!choice.equals("yes")) {
                    break; // Exit loop if they don't want to catch another Pokémon
                } else {
                    // Deduct points
                    try {
                        player.deductBattlePoints(200);
                        println("Points deducted. Current battle points: " + player.getBattlePoints());
                        Game.savePlayerData(player);
                    } catch (IllegalArgumentException e) {
                        println("Error: " + e.getMessage());
                        println("Unable to deduct points: " + e.getMessage());
                        break; // Exit the loop on error
                    }
                }
            } else {
                println("Not enough points to catch another Pokémon.");
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
            println("User credentials saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void setupStages() {
        try {
            // Read Pokémon data from file
            List<Pokemon> pokemonList = readPokemonsFromFile(POKEMONS_FILENAME);

            if (pokemonList.size()<=0) { //if no found any pokemon in the list, then call it from api
                PokemonService pokemonService = new PokemonService();
                List<Integer> habitatIds = new ArrayList<>();
                habitatIds.add(1);
                habitatIds.add(2);
                habitatIds.add(3);
                habitatIds.add(4);
                habitatIds.add(5);
                habitatIds.add(6);
                habitatIds.add(7);
                habitatIds.add(8);
                habitatIds.add(9);

                try {
                    pokemonService.fetchPokemonsByMultipleHabitats(habitatIds);
                    pokemonList = readPokemonsFromFile(POKEMONS_FILENAME);

                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            }

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

