package my.com.sunway.pokemonapp;

public class BattleScoreCalculation {
    private static final int BASE_SCORE = 1000;
    private static final double WIN_MULTIPLIER = 2.0;
    private static final double LOSS_MULTIPLIER = 0.5;
    private static final double CATCH_BONUS = 500;
    private static final double HIGHEST_DAMAGE_MULTIPLIER = 1.5;
    private static final double EFFECTIVE_TYPE_MULTIPLIER = 2.0;
    private static final double INEFFECTIVE_TYPE_MULTIPLIER = 0.5;

    public int calculateBattleScore(long startTime, long battleTimeLimit,
                                    Pokemon userPokemon1, Pokemon userPokemon2,
                                    Pokemon wildPokemon1, Pokemon wildPokemon2,
                                    TypeChart typeChart, int attackStrength1, int attackStrength2) {
        int battleScore = BASE_SCORE;

        // Calculate score based on battle outcome
        if (userPokemon1.getHealth() <= 0 || userPokemon2.getHealth() <= 0) {
            battleScore *= LOSS_MULTIPLIER; // Reduce score if user loses a Pokémon
        } else {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime <= battleTimeLimit / 3) {
                battleScore *= WIN_MULTIPLIER; // Double score if user wins within time limit
            }
        }

        // Add bonus score for catching Pokémon
        if ((wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() > 0 && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0)) ||
                (wildPokemon2.getHealth() <= 0 && wildPokemon1.getHealth() > 0 && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0)) ||
                (wildPokemon1.getHealth() <= 0 && wildPokemon2.getHealth() <= 0 && (System.currentTimeMillis() - startTime) < battleTimeLimit && (userPokemon1.getHealth() > 0 || userPokemon2.getHealth() > 0))) {
            battleScore += CATCH_BONUS;
        }

        // Add score based on highest attack strength in one move
        int highestAttackStrength = Math.max(attackStrength1, attackStrength2); // Example, replace with actual tracking
        battleScore += highestAttackStrength * HIGHEST_DAMAGE_MULTIPLIER;

        // Modify score based on type effectiveness
        if (wildPokemon1.getHealth() > 0) {
            double effectiveness1 = typeChart.getEffectiveness(userPokemon1.getTypes().get(0), wildPokemon1.getTypes().get(0));
            battleScore *= effectiveness1 == 2.0 ? EFFECTIVE_TYPE_MULTIPLIER : effectiveness1 == 0.5 ? INEFFECTIVE_TYPE_MULTIPLIER : 1.0;
        }

        if (wildPokemon2.getHealth() > 0) {
            double effectiveness2 = typeChart.getEffectiveness(userPokemon2.getTypes().get(0), wildPokemon2.getTypes().get(0));
            battleScore *= effectiveness2 == 2.0 ? EFFECTIVE_TYPE_MULTIPLIER : effectiveness2 == 0.5 ? INEFFECTIVE_TYPE_MULTIPLIER : 1.0;
        }

        return battleScore;
    }

    public String determineRankMessage(int score) {
        if (score >= 1000000000) {
            return "WOW YOU ARE A POKÉMON MASTER!!!! AMAZING!";
        } else if (score >= 1000) {
            return "Great job!";
        } else {
            return "WOW YOU SUCK, YOU NEED TO LEARN YOUR TYPES BRO, YOU ARE WORSE THAN A 10YO";
        }
    }
}
