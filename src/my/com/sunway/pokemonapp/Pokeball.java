package my.com.sunway.pokemonapp;

public abstract class Pokeball {
    private final double catchRate;
    private final double appearanceRate;

    public Pokeball(double catchRate, double appearanceRate) {
        this.catchRate = catchRate;
        this.appearanceRate = appearanceRate;
    }

    public double getCatchRate() {
        return catchRate;
    }

    public double getAppearanceRate() {
        return appearanceRate;
    }

    @Override
    public abstract String toString(); // Abstract method to be implemented by subclasses
}
