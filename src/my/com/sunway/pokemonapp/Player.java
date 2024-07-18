package my.com.sunway.pokemonapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {
    private String userId;
    private String password;
    private List<Pokemon> userPokemons;
    private int battlePoints;

    public Player() {
        this.userPokemons = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public List<Pokemon> getUserPokemons() {
        return userPokemons;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBattlePoints(){
        return battlePoints;
    }

    public void setBattlePoints(int battlePoints) {
        this.battlePoints = battlePoints;
    }

    public void addBattlePoints(int pointsToAdd) {
        this.battlePoints += pointsToAdd;
    }

    // Method to deduct points
    public void deductBattlePoints(int pointsToDeduct) {
        battlePoints -= pointsToDeduct;
    }

    public PokeballType chooseRandomPokeball() {
        double randomValue = Math.random();
        double cumulativeProbability = 0.0;

        List<PokeballType> pokeballs = new ArrayList<>();
        pokeballs.add(new Pokeball());
        pokeballs.add(new GreatBall());
        pokeballs.add(new UltraBall());
        pokeballs.add(new MasterBall());

        for (PokeballType pokeball : pokeballs) {
            cumulativeProbability += pokeball.getAppearanceRate();
            if (randomValue <= cumulativeProbability) {
                return pokeball;
            }
        }
        return new Pokeball(); // Default case, should never occur if appearance rates sum to 1
    }

    public boolean attemptCatch(PokeballType pokeball) {
        double catchChance = Math.random();
        return catchChance <= pokeball.getCatchRate();
    }

    public void saveChosenPokemon(Pokemon pokemon) {
        String fileName = "user_pokemon_list_" + userId + ".txt";
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
        String fileName = "user_pokemon_list_" + userId + ".txt";
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

    public void updatePokemonDetailsToFile(Pokemon pokemonToUpdate) {
        String fileName = "user_pokemon_list_" + userId + ".txt"; // Use userId from Player instance
        Path filePath = Paths.get(fileName);

        try {
            List<String> updatedLines = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath);
            boolean found = false;

            for (String line : lines) {
                if (line.contains("name='" + pokemonToUpdate.getName() + "'")) {
                    // Update the line for the specific Pokémon
                    updatedLines.add(pokemonToUpdate.toString());
                    found = true;
                    System.out.println("Updated line: " + pokemonToUpdate.toString());
                } else {
                    updatedLines.add(line);
                }
            }

            if (!found) {
                System.out.println("Pokémon not found in file. Adding as new entry.");
                // If the Pokémon was not found in the file, add it as a new entry
                updatedLines.add(pokemonToUpdate.toString());
                System.out.println("New line: " + pokemonToUpdate.toString());
            }

            // Write the updated lines back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String updatedLine : updatedLines) {
                    writer.write(updatedLine);
                    writer.newLine(); // Use newLine() for cross-platform compatibility
                }
            }

            System.out.println("Updated Pokémon details saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error updating Pokémon details to file: " + e.getMessage());
        }
    }

    public static String[] parsePokemon(String input) {
        ArrayList<String> attributesList = new ArrayList<>();

        // Define regex pattern to match key-value pairs
        Pattern pattern = Pattern.compile("(\\w+)='([^']+)'|\\w+=\\d+|\\w+=\\[([^\\]]+)\\]|\\w+=\\d+");
        Matcher matcher = pattern.matcher(input);

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
        String[] attributesArray = new String[attributesList.size()];
        attributesArray = attributesList.toArray(attributesArray);

        return attributesArray;
    }


    public Pokemon parsePokemonFromString(String pokemonDetails) {

        String[] parts = parsePokemon(pokemonDetails );

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
                    types.addAll(Arrays.asList(value.replaceAll("[\\[\\]]", "").split(", ")));
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
                default:
                    // Handle unrecognized keys or ignore
                    break;
            }
        }

        // Create and return Pokemon object
        return new Pokemon(name, health, attack, defense, stars, types, speed, specialAttack, specialDefense, habitatId);
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
