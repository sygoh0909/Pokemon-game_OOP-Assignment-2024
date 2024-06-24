package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Player player;
    private List<List<Pokemon>> stages; //pokemon in the stages comes from the list of pokemon
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
            // Grasslands (IDs 2 and 3)
            List<Integer> grasslandIds = List.of(2, 3);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(grasslandIds, "Grasslands and Forest"));
            stageNames.add("Grasslands and Forest");

            // Mountains and Caves (ID 1 and 4)
            List<Integer> mountainIds = List.of(1, 4);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(mountainIds, "Mountains and Caves"));
            stageNames.add("Mountains and Caves");

            // Water Bodies (ID 7 and 9)
            List<Integer> waterIds = List.of(7, 9);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(waterIds, "Water Bodies"));
            stageNames.add("Water Bodies");

            // Urban Areas (ID 8)
            List<Integer> urbanIds = List.of(8);
            stages.add(pokemonService.fetchPokemonsByMultipleHabitats(urbanIds, "Urban Areas"));
            stageNames.add("Urban Areas");

            // Icy Regions (ID 10)
            //stages.add(pokemonService.fetchPokemonsByMultipleHabitats(10, "Icy Regions"));
            //stageNames.add("Icy Regions");

            // Desert Regions (ID 7)
            //stages.add(pokemonService.fetchPokemonsByMultipleHabitats(7, "Desert Regions"));
            //stageNames.add("Desert Regions");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String userId, String password) {
        this.player = new Player("testUser", "password123");
        return this.player.authenticate(userId, password);
    }

    public void displayStages() { //display stage name, pokemon name, type, star
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
                        System.out.println(stagePokemons.get(j));
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
            System.out.println((i + 1) + ": " + chosenStagePokemons.get(i));
        }

        int pokemonChoice = scanner.nextInt() - 1;
        if (pokemonChoice < 0 || pokemonChoice >= printCount) {
            System.out.println("Invalid Pokémon choice.");
            return;
        }

        Pokemon chosenPokemon = chosenStagePokemons.get(pokemonChoice);
        System.out.println("You chose: " + chosenPokemon);

        saveChosenPokemon(chosenPokemon);
    }

    private void saveChosenPokemon(Pokemon pokemon) throws IOException {
        String fileName = "user_pokemon_list.txt";
        String pokemonDetails = pokemon.toString();
        Files.write(Paths.get(fileName), (pokemonDetails + System.lineSeparator()).getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        System.out.println("Saved " + pokemon.getName() + " to " + fileName);
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game();
        boolean loggedIn = game.login("testUser", "password123"); //for testing only

        //login page //sign up page if we got time
        //save user id to a text file //password if we got time

        if (loggedIn) {
            System.out.println("Login successful!");
            game.displayStages();
            game.chooseStageAndPokemon();
        } else {
            System.out.println("Login failed!");
        }
    }
}
