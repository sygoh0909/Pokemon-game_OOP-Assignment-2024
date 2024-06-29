package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonService {
    private static final String API_URL = "https://pokeapi.co/api/v2/";

    public List<Pokemon> fetchPokemonsByMultipleHabitats(List<Integer> habitatIds) throws IOException, InterruptedException {
        List<Pokemon> pokemonList = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();

        for (int habitatId : habitatIds) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "pokemon-habitat/" + habitatId))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            String regex = "\"pokemon_species\"\\s*:\\s*\\[(.*?)\\]";
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                String speciesJson = matcher.group(1);
                Pattern speciesItemPattern = Pattern.compile("\"name\":\\s*\"(\\w+)\",\\s*\"url\":\\s*\"(.*?)\"");
                Matcher speciesItemMatcher = speciesItemPattern.matcher(speciesJson);
                while (speciesItemMatcher.find()) {
                    String name = speciesItemMatcher.group(1);
                    String speciesUrl = speciesItemMatcher.group(2);

                    HttpRequest speciesRequest = HttpRequest.newBuilder()
                            .uri(URI.create(speciesUrl))
                            .build();

                    HttpResponse<String> speciesResponse = client.send(speciesRequest, HttpResponse.BodyHandlers.ofString());
                    String speciesJsonResponse = speciesResponse.body();

                    String pokemonUrlRegex = "\"url\":\\s*\"(https://pokeapi.co/api/v2/pokemon/\\d+/)\"";
                    Pattern pokemonUrlPattern = Pattern.compile(pokemonUrlRegex);
                    Matcher pokemonUrlMatcher = pokemonUrlPattern.matcher(speciesJsonResponse);

                    if (pokemonUrlMatcher.find()) {
                        String pokemonUrl = pokemonUrlMatcher.group(1);

                        HttpRequest pokemonRequest = HttpRequest.newBuilder()
                                .uri(URI.create(pokemonUrl))
                                .build();

                        HttpResponse<String> pokemonResponse = client.send(pokemonRequest, HttpResponse.BodyHandlers.ofString());
                        String pokemonJson = pokemonResponse.body();

                        List<String> types = extractPokemonTypes(pokemonJson); // Extract types
                        int[] stats = extractPokemonStats(pokemonJson); // Extract stats

                        int health = stats[0];
                        int attack = stats[1];
                        int defense = stats[2];
                        int speed = stats[3];
                        int accuracy = stats[4];

                        int stars = calculateStars(attack, defense, health);
                        pokemonList.add(new Pokemon(name, health, attack, defense, stars, types, speed, accuracy));
                    }
                }
            }
        }
        return pokemonList;
    }

    private List<String> extractPokemonTypes(String pokemonJson) {
        List<String> types = new ArrayList<>();
        try {
            // Regex to find all occurrences of type names in the JSON response
            String typeRegex = "\"type\":\\s*\\{\\s*\"name\":\\s*\"(\\w+)\"";
            Pattern typePattern = Pattern.compile(typeRegex);
            Matcher typeMatcher = typePattern.matcher(pokemonJson);

            while (typeMatcher.find()) {
                String typeName = typeMatcher.group(1);
                types.add(typeName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }

    private int[] extractPokemonStats(String pokemonJson) {
        int[] stats = new int[5]; // Array to store hp, attack, defense, speed, accuracy
        try {
            String statsRegex = "\"stats\":\\[(.*?)\\]";
            Pattern statsPattern = Pattern.compile(statsRegex, Pattern.DOTALL);
            Matcher statsMatcher = statsPattern.matcher(pokemonJson);

            if (statsMatcher.find()) {
                String statsJson = statsMatcher.group(1);

                stats[0] = extractStatValue(statsJson, "hp");
                stats[1] = extractStatValue(statsJson, "attack");
                stats[2] = extractStatValue(statsJson, "defense");
                stats[3] = extractStatValue(statsJson, "speed");
                stats[4] = extractStatValue(statsJson, "accuracy"); // Extract accuracy from stats
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

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

    private int calculateStars(int attack, int defense, int health) {
        int sumStats = attack + defense + health;
        if (sumStats > 220) {
            return 5;
        } else if (sumStats > 190) {
            return 4;
        } else if (sumStats > 170) {
            return 3;
        } else if (sumStats > 150) {
            return 2;
        } else {
            return 1;
        }
    }

    public static void main(String[] args) {
        PokemonService pokemonService = new PokemonService();
        List<Integer> habitatIds = Arrays.asList(1, 2); // Example habitat IDs

        try {
            List<Pokemon> pokemonList = pokemonService.fetchPokemonsByMultipleHabitats(habitatIds);
            for (Pokemon pokemon : pokemonList) {
                System.out.println(pokemon);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
