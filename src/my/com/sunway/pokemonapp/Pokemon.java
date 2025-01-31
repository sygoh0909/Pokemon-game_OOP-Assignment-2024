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
    private int maxHealth;
    private int habitatId;

    public Pokemon(String name, int health, int attack, int defense, int stars, List<String> types, int speed, int specialAttack, int specialDefense, int habitatId) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.stars = stars;
        this.types = types;
        this.speed = speed;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.maxHealth = health; // Initialize maxHealth to the initial health value
        this.habitatId = habitatId;
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

    public void increaseAttack(int amount){
        this.attack += amount;
    }

    public void increaseDefense(int amount){
        this.defense += amount;
    }

    public void increaseSpecialAttack(int amount){
        this.specialAttack += amount;
    }

    public void increaseSpecialDefense(int amount){
        this.specialDefense += amount;
    }

    public void setHabitatId(int habitatId) {
        this.habitatId = habitatId;
    }
    public int getHabitatId() {
        return habitatId;
    }

    // Method to reset health to maxHealth
    public void resetHealth() {
        this.health = this.maxHealth;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", types=" + types +
                ", stars=" + stars +
                ", health=" + health +
                ", attack=" + attack +
                ", defense=" + defense +
                ", speed=" + speed +
                ", specialAttack=" + specialAttack +
                ", specialDefense=" + specialDefense +
                ", habitatId=" + habitatId +
                '}';
    }
}
