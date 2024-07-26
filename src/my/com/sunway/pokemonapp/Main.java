package my.com.sunway.pokemonapp;

import java.io.IOException;
import java.util.List;

import static my.com.sunway.pokemonapp.Game.println;
import static my.com.sunway.pokemonapp.Game.readPlayerData;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Game loading...Please be patient...");
        Game game = new Game();


        boolean loggedIn = game.login();


        if (loggedIn) {
            game.player.loadUserPokemons();
            List<Pokemon> userPokemons = game.player.getUserPokemons();
            String userId = game.player.getUserId();
            println("Login successful for user ID: " + userId);


            Player loadedPlayer = readPlayerData(userId);
            game.player = loadedPlayer; // Update the player object with loaded data
            println("User ID: " + game.player.getUserId());
            println("Battle Points: " + game.player.getBattlePoints());


            game.displayStages();


            // Choose stage and get wild Pokémon for battle
            List<Pokemon> stageWildPokemons = game.chooseStageAndPokemon();


            // Pass user ID to Battle class when starting battle
            userPokemons = game.player.getUserPokemons();
            game.battle.startBattle(userPokemons, stageWildPokemons, userId);


        } else {
            println("Login failed!"); //login fail
        }
    }
}