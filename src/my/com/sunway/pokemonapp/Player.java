package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
