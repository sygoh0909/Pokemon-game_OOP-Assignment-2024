package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher; //regular expressions
import java.util.regex.Pattern; //regular expressions

public class PokemonService {
    private static final String API_URL = "https://pokeapi.co/api/v2/";

    /**
     * Fetches Pokémon by habitat from the PokeAPI.
     *
     * @param habitatId   The ID of the habitat.
     * @param habitatType The type of the habitat (e.g., grassland, cave).
     * @return A list of Pokémon found in the specified habitat.
     * @throws IOException          If an error occurs during HTTP request/response.
     * @throws InterruptedException If the HTTP request is interrupted.
     */
    public List<Pokemon> fetchPokemonByHabitat(int habitatId, String habitatType) throws IOException, InterruptedException {
        List<Pokemon> pokemonList = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient(); //new HTTP client
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "pokemon-habitat/" + habitatId))
                .build(); //Builds an HTTP GET request to fetch Pokémon by habitat ID

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body(); //Sends the request and captures the response as a string (jsonResponse)

        // Regular expression to find the key-value pair for "pokemon_species"
        String regex = "\"pokemon_species\"\\s*:\\s*\\[(.*?)\\]"; //Defines a regular expression to match the "pokemon_species" key and capture the corresponding array.
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL); //Compiles the regular expression (regex) into a Pattern object, enabling it to be used for matching.
        Matcher matcher = pattern.matcher(jsonResponse); //Creates a Matcher object that will perform matching operations on the jsonResponse.

        if (matcher.find()) {
            String speciesJson = matcher.group(1); //Extracts the first capturing group (the array of Pokémon species) from the matched subsequence.
            Pattern speciesItemPattern = Pattern.compile("\"name\":\\s*\"(\\w+)\",\\s*\"url\":\\s*\"(.*?)\"");
            Matcher speciesItemMatcher = speciesItemPattern.matcher(speciesJson);
            while (speciesItemMatcher.find()) {
                String name = speciesItemMatcher.group(1); //Extracts the Pokémon's name from the first capturing group.
                String url = speciesItemMatcher.group(2);

                int health = (int) (Math.random() * 50) + 50;
                int attack = (int) (Math.random() * 50) + 50;
                int defense = (int) (Math.random() * 50) + 50;
                List<String> powers = List.of("Power1", "Power2", "Power3");
                int stars = calculateStars(attack, defense, health);
                pokemonList.add(new Pokemon(name, health, attack, defense, habitatType, powers, stars));
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
        if (sumStats > 130) {
            return 5;
        } else if (sumStats > 110) {
            return 4;
        } else if (sumStats > 90) {
            return 3;
        } else if (sumStats > 70) {
            return 2;
        } else {
            return 1;
        }
    }
}