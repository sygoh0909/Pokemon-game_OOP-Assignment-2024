package my.com.sunway.pokemonapp;

public enum Pokeball {
    POKEBALL(0.5, 0.4),
    GREATBALL(0.7, 0.3),
    ULTRABALL(0.9, 0.2),
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
