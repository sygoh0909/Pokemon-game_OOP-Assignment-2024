package my.com.sunway.pokemonapp;

import java.util.HashMap;
import java.util.Map;

public class TypeChart {
    private static final Map<String, Map<String, Double>> typeEffectiveness = new HashMap<>();

    static {
        // Initialize the type effectiveness chart
        String[] types = {"Normal", "Fighting", "Poison", "Ground", "Flying", "Bug", "Rock", "Ghost", "Steel", "Fire", "Water", "Electric", "Grass", "Ice", "Psychic", "Dragon", "Dark", "Fairy"};
        for (String type : types) {
            typeEffectiveness.put(type, new HashMap<>());
        }

        // Populate the type effectiveness chart
        addEffectiveness("Normal", new String[]{"Rock", "Steel"}, new String[]{"Ghost"});
        addEffectiveness("Fighting", new String[]{"Normal", "Rock", "Steel", "Ice", "Dark"}, new String[]{"Flying", "Poison", "Bug", "Psychic", "Fairy"}, new String[]{"Ghost"});
        addEffectiveness("Poison", new String[]{"Grass", "Fairy"}, new String[]{"Poison", "Ground", "Rock", "Ghost"}, new String[]{"Steel"});
        addEffectiveness("Ground", new String[]{"Poison", "Rock", "Steel", "Fire", "Electric"}, new String[]{"Bug", "Grass"}, new String[]{"Flying"});
        addEffectiveness("Flying", new String[]{"Fighting", "Bug", "Grass"}, new String[]{"Rock", "Steel", "Electric"});
        addEffectiveness("Bug", new String[]{"Grass", "Psychic", "Dark"}, new String[]{"Fighting", "Flying", "Poison", "Ghost", "Steel", "Fire", "Fairy"});
        addEffectiveness("Rock", new String[]{"Flying", "Bug", "Fire", "Ice"}, new String[]{"Fighting", "Ground", "Steel"});
        addEffectiveness("Ghost", new String[]{"Ghost", "Psychic"}, new String[]{"Dark"}, new String[]{"Normal"});
        addEffectiveness("Steel", new String[]{"Rock", "Ice", "Fairy"}, new String[]{"Steel", "Fire", "Water", "Electric"});
        addEffectiveness("Fire", new String[]{"Bug", "Steel", "Grass", "Ice"}, new String[]{"Rock", "Fire", "Water", "Dragon"});
        addEffectiveness("Water", new String[]{"Ground", "Rock", "Fire"}, new String[]{"Water", "Grass", "Dragon"});
        addEffectiveness("Electric", new String[]{"Flying", "Water"}, new String[]{"Grass", "Electric", "Dragon"}, new String[]{"Ground"});
        addEffectiveness("Grass", new String[]{"Ground", "Rock", "Water"}, new String[]{"Flying", "Poison", "Bug", "Steel", "Fire", "Grass", "Dragon"});
        addEffectiveness("Ice", new String[]{"Flying", "Ground", "Grass", "Dragon"}, new String[]{"Steel", "Fire", "Water", "Ice"});
        addEffectiveness("Psychic", new String[]{"Fighting", "Poison"}, new String[]{"Steel", "Psychic"}, new String[]{"Dark"});
        addEffectiveness("Dragon", new String[]{"Dragon"}, new String[]{"Steel"}, new String[]{"Fairy"});
        addEffectiveness("Dark", new String[]{"Ghost", "Psychic"}, new String[]{"Fighting", "Dark", "Fairy"});
        addEffectiveness("Fairy", new String[]{"Fighting", "Dragon", "Dark"}, new String[]{"Poison", "Steel", "Fire"});
    }

    private static void addEffectiveness(String type, String[] superEffective, String[] notVeryEffective) {
        addEffectiveness(type, superEffective, notVeryEffective, new String[]{});
    }

    private static void addEffectiveness(String type, String[] superEffective, String[] notVeryEffective, String[] noEffect) {
        for (String targetType : superEffective) {
            typeEffectiveness.get(type).put(targetType, 2.0);
        }
        for (String targetType : notVeryEffective) {
            typeEffectiveness.get(type).put(targetType, 0.5);
        }
        for (String targetType : noEffect) {
            typeEffectiveness.get(type).put(targetType, 0.0);
        }
    }

    public double getEffectiveness(String attackType, String defenderType) {
        return typeEffectiveness.getOrDefault(attackType, new HashMap<>()).getOrDefault(defenderType, 1.0);
    }
}
