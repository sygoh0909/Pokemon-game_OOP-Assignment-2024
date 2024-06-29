package my.com.sunway.pokemonapp;

public enum Pokeball {
    POKEBALL(0.28, 0.6),
    GREATBALL(0.41, 0.4),
    ULTRABALL(0.624, 0.2),
    MASTERBALL(1.0, 0.1);

    private final double catchRate;
    private final double appearanceRate;

    Pokeball(double catchRate, double appearanceRate) {
        this.catchRate = catchRate;
        this.appearanceRate = appearanceRate;
    }

    public double getCatchRate() {
        return catchRate;
    }

    public double getAppearanceRate() {
        return appearanceRate;
    }
}
