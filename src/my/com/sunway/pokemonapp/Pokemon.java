package my.com.sunway.pokemonapp;

import java.util.List;

public class Pokemon {
    private String name;
    private int health;
    private int attack;
    private int defense;
    private int stars;
    private List<String> types;
    private int speed;
    private int specialAttack;
    private int specialDefense;

    public Pokemon(String name, int health, int attack, int defense, int stars, List<String> types, int speed, int accuracy, int specialAttack, int specialDefense) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.stars = stars;
        this.types = types;
        this.speed = speed;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
    }

    public void setSpecialDefense(int specialDefense) {
        this.specialDefense = specialDefense;
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

    public int getStars() {
        return stars;
    }

    public List<String> getTypes() {
        return types;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
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
                ", stars=" + stars +
                ", types=" + types +
                ", speed=" + speed +
                ", special attack=" + specialAttack +
                ", specialDefense=" + specialDefense +
                '}';
    }
}
