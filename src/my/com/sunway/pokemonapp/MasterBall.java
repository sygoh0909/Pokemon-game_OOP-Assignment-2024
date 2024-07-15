package my.com.sunway.pokemonapp;

public class MasterBall extends PokeballType {
    public MasterBall() {
        super(1.0, 0.1);
    }

    @Override
    public String toString() {
        return "Master Ball";
    }
}
