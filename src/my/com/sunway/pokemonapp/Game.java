package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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

    public void displayStages() {
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

    public static void main(String[] args) {
        Game game = new Game();
        boolean loggedIn = game.login("testUser", "password123"); //for testing only

        //login page and sign up page
        //save user id and password to a text file

        if (loggedIn) {
            System.out.println("Login successful!");
            game.displayStages();
        } else {
            System.out.println("Login failed!");
        }
    }
}
