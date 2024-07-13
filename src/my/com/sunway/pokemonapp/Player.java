package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
    private final String userId;
    private final String password;
    private final List<Pokemon> userPokemons;
    private int battlePoints;

    public Player(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.userPokemons = new ArrayList<>();
        this.battlePoints = 0; // Initialize battle points to 0
    }

    public String getUserId() {
        return userId;
    }

    public int getBattlePoints() {
        return battlePoints;
    }

    public void setBattlePoints(int battlePoints) {
        this.battlePoints = battlePoints;
    }

    public List<Pokemon> getUserPokemons() {
        return userPokemons;
    }

    public Pokeball chooseRandomPokeball() {
        double randomValue = Math.random();
        double cumulativeProbability = 0.0;
        for (Pokeball pokeball : Pokeball.values()) {
            cumulativeProbability += pokeball.getAppearanceRate();
            if (randomValue <= cumulativeProbability) {
                return pokeball;
            }
        }
        return Pokeball.POKEBALL; // Default case, should never occur if appearance rates sum to 1
    }

    public boolean attemptCatch(Pokeball pokeball) {
        double catchChance = Math.random();
        return catchChance <= pokeball.getCatchRate();
    }

    public void saveChosenPokemon(Pokemon pokemon) {
        String fileName = "user_pokemon_list_" + getUserId() + ".txt";
        try {
            String pokemonDetails = pokemon.toString();
            Files.write(Paths.get(fileName), (pokemonDetails + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Saved " + pokemon.getName() + " to " + fileName);

            // Add the newly caught pokemon to the userPokemons list
            userPokemons.add(pokemon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUserPokemons() {
        String fileName = "user_pokemon_list_" + getUserId() + ".txt";
        Path filePath = Paths.get(fileName);

        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                userPokemons.clear(); // Clear existing list before reloading
                for (String line : lines) {
                    Pokemon pokemon = parsePokemonFromString(line);
                    if (pokemon != null) {
                        userPokemons.add(pokemon);
                    }
                }
            } else {
                System.out.println("No Pokémon list found for user. Creating new list...");
                Files.createFile(filePath); // Create a new file if it doesn't exist
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Pokemon parsePokemonFromString(String pokemonDetails) {
        // Example format: "Pokemon{name='Charizard', health=100, ...}"
        // Implement parsing logic to create a Pokemon object from string representation
        // Split by comma and then parse each attribute

        // Remove unnecessary characters and split by commas
        String[] parts = pokemonDetails.replace("Pokemon{", "").replace("}", "").split(", ");

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

        // Iterate through parts to parse each attribute
        for (String part : parts) {
            // Split each part into key and value
            String[] keyValue = part.split("=", 2);
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
                    if (value.startsWith("[") && value.endsWith("]")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    types = Arrays.asList(value.split(", "));
                    break;
                case "speed":
                    speed = Integer.parseInt(value);
                    break;
                case "special attack":
                    specialAttack = Integer.parseInt(value);
                    break;
                case "special defense":
                    specialDefense = Integer.parseInt(value);
                    break;
                default:
                    // Handle unrecognized keys or ignore
                    break;
            }
        }

        // Create and return Pokemon object
        return new Pokemon(name, health, attack, defense, stars, types, speed, specialAttack, specialDefense);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append("'s Pokémon: \n");
        for (Pokemon pokemon : userPokemons) {
            sb.append(pokemon.toString()).append("\n");
        }
        sb.append("Battle Points: ").append(battlePoints).append("\n");
        return sb.toString();
    }
}
