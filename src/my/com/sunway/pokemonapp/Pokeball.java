package my.com.sunway.pokemonapp;

public class Pokeball extends PokeballType {
    public Pokeball() {
        super(0.28, 0.6); // Default values for POKEBALL
    }

    @Override
    public String toString() {
        return "Pokeball";
    }
}
