package my.com.sunway.pokemonapp;

import java.util.List;

public class Pokemon {
    private String name;
    private int health;
    private int attack;
    private int defense;
    private List<String> powers;
    private int stars;
    private List<String> types;

    public Pokemon(String name, int health, int attack, int defense, List<String> powers, int stars, List<String> types) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.powers = powers;
        this.stars = stars;
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public List<String> getPowers() {
        return powers;
    }

    public int getStars() {
        return stars;
    }

    public List<String> getTypes() {
        return types;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public int attack() {
        // Simple attack calculation, can be expanded
        return (int) (Math.random() * attack) + 1;
    }

    public boolean isFainted() {
        return this.health <= 0;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", attack=" + attack +
                ", defense=" + defense +
                ", powers=" + powers +
                ", stars=" + stars +
                ", types=" + types +
                '}';
    }
}
