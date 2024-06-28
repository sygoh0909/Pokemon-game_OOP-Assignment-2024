package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Game {
    private Player player;
    private List<List<Pokemon>> stages; // Pokémon in the stages
    private List<String> stageNames;
    private List<Integer> topScores;
    private PokemonService pokemonService;

    public Game() {
        this.stages = new ArrayList<>();
        this.stageNames = new ArrayList<>();
        this.topScores = new ArrayList<>();
        this.pokemonService = new PokemonService();
        setupStages();
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

    public boolean login(String userId, String password) {
        this.player = new Player("testUser", "password123");
        return this.player.authenticate(userId, password);
    }

    public void displayStages() { // Display stage name, Pokémon name, type, star
        if (stages.isEmpty()) {
            System.out.println("No stages available.");
        } else {
            for (int i = 0; i < stages.size(); i++) {
                System.out.println("Stage " + (i + 1) + ": " + stageNames.get(i));
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

    public void chooseStageAndPokemon() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose a stage number: ");
        for (int i = 0; i < stageNames.size(); i++) {
            System.out.println((i + 1) + ": " + stageNames.get(i));
        }

        int stageChoice = scanner.nextInt() - 1;
        if (stageChoice < 0 || stageChoice >= stages.size()) {
            System.out.println("Invalid stage choice.");
            return;
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

        int pokemonChoice = scanner.nextInt() - 1;
        if (pokemonChoice < 0 || pokemonChoice >= printCount) {
            System.out.println("Invalid Pokémon choice.");
            return;
        }

        Pokemon chosenPokemon = chosenStagePokemons.get(pokemonChoice);
        System.out.println("You chose: " + chosenPokemon.getName());

        // Randomly choose a Poké Ball
        Pokeball chosenPokeball = chooseRandomPokeball();
        System.out.println("A " + chosenPokeball + " appeared!");

        // Attempt to catch the Pokémon
        boolean isCaught = attemptCatch(chosenPokeball);
        if (isCaught) {
            System.out.println("You caught the " + chosenPokemon.getName() + "!");
            saveChosenPokemon(chosenPokemon);
        } else {
            System.out.println("The " + chosenPokemon.getName() + " escaped!");
        }
    }

    private Pokeball chooseRandomPokeball() {
        Pokeball[] pokeballs = Pokeball.values();
        int randomIndex = new Random().nextInt(pokeballs.length);
        return pokeballs[randomIndex];
    }

    private boolean attemptCatch(Pokeball pokeball) {
        double catchChance = Math.random();
        return catchChance <= pokeball.getCatchRate();
    }

    private void saveChosenPokemon(Pokemon pokemon) throws IOException {
        String fileName = "user_pokemon_list.txt";
        String pokemonDetails = pokemon.toString();
        Files.write(Paths.get(fileName), (pokemonDetails + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        System.out.println("Saved " + pokemon.getName() + " to " + fileName);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Game game = new Game();
        boolean loggedIn = game.login("testUser", "password123"); // For testing only

        if (loggedIn) {
            System.out.println("Login successful!");
            game.displayStages();
            game.chooseStageAndPokemon();
        } else {
            System.out.println("Login failed!");
        }
    }
}
