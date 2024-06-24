package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonService {
    private static final String API_URL = "https://pokeapi.co/api/v2/";

    /**
     * Fetches Pokémon by habitat IDs from the PokeAPI.
     *
     * @param habitatIds  The IDs of the habitats.
     * @param habitatType The type of the habitats (e.g., grassland, cave).
     * @return A list of Pokémon found in the specified habitats.
     * @throws IOException          If an error occurs during HTTP request/response.
     * @throws InterruptedException If the HTTP request is interrupted.
     */
    public List<Pokemon> fetchPokemonsByMultipleHabitats(List<Integer> habitatIds, String habitatType) throws IOException, InterruptedException {
        List<Pokemon> pokemonList = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();

        for (int habitatId : habitatIds) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "pokemon-habitat/" + habitatId))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            // Regular expression to find the key-value pair for "pokemon_species"
            String regex = "\"pokemon_species\"\\s*:\\s*\\[(.*?)\\]";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                String speciesJson = matcher.group(1);
                Pattern speciesItemPattern = Pattern.compile("\"name\":\\s*\"(\\w+)\",\\s*\"url\":\\s*\"(.*?)\"");
                Matcher speciesItemMatcher = speciesItemPattern.matcher(speciesJson);
                while (speciesItemMatcher.find()) {
                    String name = speciesItemMatcher.group(1);
                    String url = speciesItemMatcher.group(2);

                    // Fetch details of the Pokémon by URL
                    HttpRequest pokemonRequest = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .build();

                    HttpResponse<String> pokemonResponse = client.send(pokemonRequest, HttpResponse.BodyHandlers.ofString());
                    String pokemonJson = pokemonResponse.body();

                    int health = (int) (Math.random() * 50) + 50;
                    int attack = (int) (Math.random() * 50) + 50;
                    int defense = (int) (Math.random() * 50) + 50;
                    List<String> powers = List.of("Power1", "Power2", "Power3");
                    int stars = calculateStars(attack, defense, health);
                    pokemonList.add(new Pokemon(name, health, attack, defense, habitatType, powers, stars));
                }
            }
        }

        return pokemonList;
    }

    /**
     * Calculates stars based on Pokémon stats.
     *
     * @param attack  Pokémon's attack stat.
     * @param defense Pokémon's defense stat.
     * @param health  Pokémon's health stat.
     * @return Number of stars based on total stats.
     */
    private int calculateStars(int attack, int defense, int health) {
        int sumStats = attack + defense + health;
        if (sumStats > 350) {
            return 5;
        } else if (sumStats > 280) {
            return 4;
        } else if (sumStats > 190) {
            return 3;
        } else if (sumStats > 100) {
            return 2;
        } else {
            return 1;
        }
    }
}
