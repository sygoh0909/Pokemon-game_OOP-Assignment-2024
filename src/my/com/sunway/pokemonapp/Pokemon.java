package my.com.sunway.pokemonapp;

import java.util.List;
import java.util.Random;

public class Pokemon {
    private String name;
    private int health;
    private int attack;
    private int defense;
    private String type;
    private List<String> powers;
    private int stars;

    //constructors
    public Pokemon(String name, int health, int attack, int defense, String type, List<String> powers, int stars) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.type = type;
        this.powers = powers;
        this.stars = stars;
    }

    //setters and getters
    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPowers() {
        return powers;
    }

    public void setPowers(List<String> powers) {
        this.powers = powers;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    //methods
    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public boolean isFainted() {
        return this.health <= 0;
    }

    public int attack() {
        return this.attack;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getStars() {
        return stars;
    }

    @Override
    public String toString() {
        return name + " (Type: " + type + ", HP: " + health + ", Stars: " + stars + ")";
    }

    public static void main (String[] args){

    }
}


