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

                        int health = (int) (Math.random() * 50) + 50;
                        int attack = (int) (Math.random() * 50) + 50;
                        int defense = (int) (Math.random() * 50) + 50;
                        List<String> powers = List.of("Power1", "Power2", "Power3");
                        int stars = calculateStars(attack, defense, health);
                        pokemonList.add(new Pokemon(name, health, attack, defense, powers, stars, types));
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
}
