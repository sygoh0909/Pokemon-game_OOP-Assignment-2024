package my.com.sunway.pokemonapp;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String userId; //using final means cannot be reassigned after being initialized in the constructor.
    private final String password;
    private final List<Pokemon> pokemons;
    private int points;

    public Player(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.pokemons = new ArrayList<Pokemon>();
        this.points = 100; // Initial points
    }

    public void catchPokemon(Pokemon pokemon) {
        this.pokemons.add(pokemon);
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public boolean authenticate(String userId, String password) {
        return this.userId.equals(userId) && this.password.equals(password);
    }

    public void deductPoints(int amount) {
        this.points -= amount;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append("'s Pokémon: \n");
        for (Pokemon pokemon : pokemons) {
            sb.append(pokemon.toString()).append("\n");
        }
        sb.append("Points: ").append(points).append("\n");
        return sb.toString();
    }
}
