package my.com.sunway.pokemonapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class BattleScoreCalculation {
    private static final int BASE_SCORE = 1000;
    private static final double WIN_MULTIPLIER = 2.0;
    private static final double LOSS_MULTIPLIER = 0.5;
    private static final double CATCH_BONUS = 500;
    private static final double HIGHEST_DAMAGE_MULTIPLIER = 1.5;
    private static final double EFFECTIVE_TYPE_MULTIPLIER = 2.0;
    private static final double INEFFECTIVE_TYPE_MULTIPLIER = 0.5;
    private static final int HIGHEST_SPECIAL_DAMAGE_MULTIPLIER = 10;
    private static final int DEFENSE_MULTIPLIER = 5;
    private static final int SPECIAL_DEFENSE_MULTIPLIER = 5;
    private Player player = new Player();

    private static final String SCORES_DIRECTORY = "user_scores/";
    private static final int MAX_TOP_SCORES = 5;

    public int calculateBattleScore(long startTime, long battleTimeLimit,
                                    Pokemon userPokemon1, Pokemon userPokemon2,
                                    Pokemon wildPokemon1, Pokemon wildPokemon2,
                                    TypeChart typeChart, int attackStrength1, int attackStrength2,
                                    int defenseStrength1, int defenseStrength2,
                                    int specialAttackStrength1, int specialAttackStrength2,
                                    int specialDefenseStrength1, int specialDefenseStrength2) {
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

        // Add score based on highest attack and special attack strength in one move
        int highestAttackStrength = Math.max(attackStrength1, attackStrength2);
        int highestSpecialAttackStrength = Math.max(specialAttackStrength1, specialAttackStrength2);
        battleScore += highestAttackStrength * HIGHEST_DAMAGE_MULTIPLIER;
        battleScore += highestSpecialAttackStrength * HIGHEST_SPECIAL_DAMAGE_MULTIPLIER;

        // Modify score based on type effectiveness for physical attacks
        if (wildPokemon1.getHealth() > 0) {
            double effectiveness1 = typeChart.getEffectiveness(userPokemon1.getTypes().get(0), wildPokemon1.getTypes().get(0));
            battleScore *= effectiveness1 == 2.0 ? EFFECTIVE_TYPE_MULTIPLIER : effectiveness1 == 0.5 ? INEFFECTIVE_TYPE_MULTIPLIER : 1.0;
        }

        if (wildPokemon2.getHealth() > 0) {
            double effectiveness2 = typeChart.getEffectiveness(userPokemon2.getTypes().get(0), wildPokemon2.getTypes().get(0));
            battleScore *= effectiveness2 == 2.0 ? EFFECTIVE_TYPE_MULTIPLIER : effectiveness2 == 0.5 ? INEFFECTIVE_TYPE_MULTIPLIER : 1.0;
        }

        // Modify score based on defense and special defense
        int totalDefense = defenseStrength1 + defenseStrength2;
        int totalSpecialDefense = specialDefenseStrength1 + specialDefenseStrength2;
        battleScore += totalDefense * DEFENSE_MULTIPLIER;
        battleScore += totalSpecialDefense * SPECIAL_DEFENSE_MULTIPLIER;

        return battleScore;
    }

    public void calculateBattlePoints(Player player, int battleScore) {
        // Example logic: 1 point for every 1000 score
        int points = battleScore / 100; // Adjust this logic based on your specific requirements
        player.setBattlePoints(points); // Set the calculated points to player's battle points
    }

    public String determineRankMessage(int score) {
        if (score >= 5000) {
            return "SSS";
        } else if (score >= 4000) {
            return "SS";
        } else if (score >= 3000) {
            return "S";
        } else if (score >= 2000) {
            return "A";
        } else if (score >= 1000) {
            return "B";
        } else if (score >= 500) {
            return "C";
        } else {
            return "F";
        }
    }

    void updateTopScores(String userId, int newScore) {
        List<Score> topScores = readTopScores();
        topScores.add(new Score(userId, newScore));
        Collections.sort(topScores, Collections.reverseOrder());
        if (topScores.size() > MAX_TOP_SCORES) {
            topScores = topScores.subList(0, MAX_TOP_SCORES);
        }
        writeTopScores(topScores);
    }

    private List<Score> readTopScores() {
        List<Score> topScores = new ArrayList<>();
        File file = new File(SCORES_DIRECTORY + "top_scores.txt");

        try {
            if (!file.exists()) {
                file.createNewFile(); // Create a new file if it doesn't exist
            }

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String userId = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    topScores.add(new Score(userId, score));
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Failed to read top scores: " + e.getMessage());
        }

        return topScores;
    }

    private void writeTopScores(List<Score> topScores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_DIRECTORY + "top_scores.txt"))) {
            for (Score score : topScores) {
                writer.println(score.getUserId() + "," + score.getScore());
            }
        } catch (IOException e) {
            System.out.println("Failed to write top scores: " + e.getMessage());
        }
    }

    void displayTopScores() {
        List<Score> topScores = readTopScores();
        System.out.println("\nTop Scores:");
        for (Score score : topScores) {
            System.out.println(score.getUserId() + ": " + score.getScore());
        }
    }

    public void saveUserScore(String userId, int score) {
        // Create user's score directory if it doesn't exist
        File directory = new File(SCORES_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Check if user's score file exists
        String userScoreFile = SCORES_DIRECTORY + userId + "_scores.txt";
        File file = new File(userScoreFile);

        try {
            // Append score to existing file or create new file
            FileWriter fw = new FileWriter(file, true); // true for append mode
            PrintWriter pw = new PrintWriter(fw);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());
            pw.println("Score: " + score + "," + dateTime);
            pw.close();
        } catch (IOException e) {
            System.out.println("Failed to save user score: " + e.getMessage());
        }
    }

    static class Score implements Comparable<Score> {
        private final String userId;
        private final int score;

        public Score(String userId, int score) {
            this.userId = userId;
            this.score = score;
        }

        public String getUserId() {
            return userId;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(Score other) {
            return Integer.compare(this.score, other.score);
        }
    }
}
