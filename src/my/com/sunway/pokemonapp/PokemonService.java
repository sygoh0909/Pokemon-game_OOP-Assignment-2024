package my.com.sunway.pokemonapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonService {
    private static final String API_URL = "https://pokeapi.co/api/v2/"; // Base URL for PokeAPI
    private static final String POKEMONS_FILENAME = "pokemons.txt";

    // Method to fetch Pokemons based on multiple habitat IDs
    public List<Pokemon> fetchPokemonsByMultipleHabitats(List<Integer> habitatIds) throws IOException, InterruptedException {
        List<Pokemon> pokemonList = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();

        for (int habitatId : habitatIds) {
            // Build request URL for habitat ID
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "pokemon-habitat/" + habitatId))
                    .build();

            // Send HTTP request and get JSON response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            // Regex pattern to extract Pokémon species data from JSON response
            String regex = "\"pokemon_species\"\\s*:\\s*\\[(.*?)\\]";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                String speciesJson = matcher.group(1); // Retrieves the substring captured by the first capturing group in the regex pattern
                Pattern speciesItemPattern = Pattern.compile("\"name\":\\s*\"(\\w+)\",\\s*\"url\":\\s*\"(.*?)\"");
                Matcher speciesItemMatcher = speciesItemPattern.matcher(speciesJson);

                while (speciesItemMatcher.find()) {
                    String name = speciesItemMatcher.group(1);
                    String speciesUrl = speciesItemMatcher.group(2);

                    // Request for detailed species data
                    HttpRequest speciesRequest = HttpRequest.newBuilder()
                            .uri(URI.create(speciesUrl))
                            .build();

                    // Send species request and get JSON response
                    HttpResponse<String> speciesResponse = client.send(speciesRequest, HttpResponse.BodyHandlers.ofString());
                    String speciesJsonResponse = speciesResponse.body();

                    // Regex pattern to extract Pokémon URL from species response
                    String pokemonUrlRegex = "\"url\":\\s*\"(https://pokeapi.co/api/v2/pokemon/\\d+/)\"";
                    Pattern pokemonUrlPattern = Pattern.compile(pokemonUrlRegex);
                    Matcher pokemonUrlMatcher = pokemonUrlPattern.matcher(speciesJsonResponse);

                    if (pokemonUrlMatcher.find()) {
                        String pokemonUrl = pokemonUrlMatcher.group(1);

                        // Request for detailed Pokémon data
                        HttpRequest pokemonRequest = HttpRequest.newBuilder()
                                .uri(URI.create(pokemonUrl))
                                .build();

                        // Send Pokémon request and get JSON response
                        HttpResponse<String> pokemonResponse = client.send(pokemonRequest, HttpResponse.BodyHandlers.ofString());
                        String pokemonJson = pokemonResponse.body();

                        // Extract Pokémon types and stats
                        List<String> types = extractPokemonTypes(pokemonJson); // Extract types
                        int[] stats = extractPokemonStats(pokemonJson); // Extract stats

                        // Extract individual stats
                        int health = stats[0];
                        int attack = stats[1];
                        int defense = stats[2];
                        int specialAttack = stats[3];
                        int specialDefense = stats[4];
                        int speed = stats[5];

                        // Calculate stars based on stats
                        int stars = calculateStars(attack, defense, health, speed, specialAttack, specialDefense);

                        // Create Pokémon object and add to list
                        Pokemon pokemon = new Pokemon(name, health, attack, defense, stars, types, speed, specialAttack, specialDefense, habitatId);
                        pokemonList.add(pokemon);
                    }
                }
            }
        }

        // Write all fetched Pokémon data to file
        writePokemonsToFile(pokemonList, POKEMONS_FILENAME);

        return pokemonList;
    }

    // Method to write Pokémon data to a text file
    private void writePokemonsToFile(List<Pokemon> pokemons, String POKEMONS_FILENAME) {
        try {
            Path path = Paths.get(POKEMONS_FILENAME);

            // Check if file exists; create it if it doesn't
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            // Check if file is empty before appending data
            if (Files.size(path) == 0) {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(POKEMONS_FILENAME, true)))) { //true means open in append mode
                    for (Pokemon pokemon : pokemons) {
                        out.println("Pokemon{name='" + pokemon.getName() + "', health=" + pokemon.getHealth() +
                                ", attack=" + pokemon.getAttack() + ", defense=" + pokemon.getDefense() +
                                ", stars=" + pokemon.getStars() + ", types=" + pokemon.getTypes() +
                                ", speed=" + pokemon.getSpeed() + ", specialAttack=" + pokemon.getSpecialAttack() +
                                ", specialDefense=" + pokemon.getSpecialDefense() + ", habitatId=" + pokemon.getHabitatId() + "}");
                    }
                }
            } else {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(POKEMONS_FILENAME, true)))) { //pverwrite the existing content of file
                    for (Pokemon pokemon : pokemons) {
                        out.println("Pokemon{name='" + pokemon.getName() + "', health=" + pokemon.getHealth() +
                                ", attack=" + pokemon.getAttack() + ", defense=" + pokemon.getDefense() +
                                ", stars=" + pokemon.getStars() + ", types=" + pokemon.getTypes() +
                                ", speed=" + pokemon.getSpeed() + ", specialAttack=" + pokemon.getSpecialAttack() +
                                ", specialDefense=" + pokemon.getSpecialDefense() + ", habitatId=" + pokemon.getHabitatId() + "}");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to extract Pokémon types from JSON response
    private List<String> extractPokemonTypes(String pokemonJson) {
        List<String> types = new ArrayList<>();
        try {
            // Regex pattern to find all occurrences of type names in the JSON response
            String typeRegex = "\"type\":\\s*\\{\\s*\"name\":\\s*\"(\\w+)\"";
            Pattern typePattern = Pattern.compile(typeRegex);
            Matcher typeMatcher = typePattern.matcher(pokemonJson);

            // Loop through matches and add type names to list
            while (typeMatcher.find()) {
                String typeName = typeMatcher.group(1);
                types.add(typeName);
            }

            // If there are three types, return only the last two
            if (types.size() == 3) {
                return types.subList(1, 3); // starting from index 1 and end before index 3
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }

    // Method to extract Pokémon stats from JSON response
    private int[] extractPokemonStats(String pokemonJson) {
        int[] stats = new int[6]; // Array to store hp, attack, defense, special attack, special defense, speed
        try {
            // Regex pattern to find stats array in JSON response
            String statsRegex = "\"stats\":\\s*\\[\\s*\\{(.*?)\\}\\s*\\]";
            Pattern statsPattern = Pattern.compile(statsRegex, Pattern.DOTALL);
            Matcher statsMatcher = statsPattern.matcher(pokemonJson);

            if (statsMatcher.find()) {
                String statsArrayJson = statsMatcher.group(1);

                // Regex pattern to extract individual stats
                String statRegex = "\"base_stat\":\\s*(\\d+)";
                Pattern statPattern = Pattern.compile(statRegex);
                Matcher statMatcher = statPattern.matcher(statsArrayJson);

                // Index for storing stats in the array
                int index = 0;
                while (statMatcher.find() && index < 6) {
                    stats[index] = Integer.parseInt(statMatcher.group(1));
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Helper method to extract specific stat value from stats JSON
    private int extractStatValue(String statsJson, String statName) {
        String statRegex = "\"stat\":\\{.*?\"name\":\"" + statName + "\".*?\"base_stat\":(\\d+)";
        Pattern statPattern = Pattern.compile(statRegex);
        Matcher statMatcher = statPattern.matcher(statsJson);

        if (statMatcher.find()) {
            return Integer.parseInt(statMatcher.group(1));
        } else {
            return 0; // Default value if not found
        }
    }

    // Method to calculate stars based on Pokémon stats
    private int calculateStars(int attack, int defense, int health, int speed, int specialAttack, int specialDefense) {
        int sumStats = attack + defense + health + speed + specialAttack + specialDefense;
        if (sumStats > 570) {
            return 5;
        } else if (sumStats > 500) {
            return 4;
        } else if (sumStats > 350) {
            return 3;
        } else if (sumStats > 250) {
            return 2;
        } else {
            return 1;
        }
    }
}
