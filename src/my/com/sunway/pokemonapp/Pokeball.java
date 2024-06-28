package my.com.sunway.pokemonapp;

public enum Pokeball {
    POKEBALL(0.5),
    GREATBALL(0.7),
    ULTRABALL(0.9),
    MASTERBALL(1.0);

    private final double catchRate;

    Pokeball(double catchRate) {
        this.catchRate = catchRate;
    }

    public double getCatchRate() {
        return catchRate;
    }
}
