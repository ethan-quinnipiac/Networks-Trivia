package project2;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a list of questions
        List<Question> questionPool = Arrays.asList(
            new Question("What is the capital of France?", Arrays.asList("Berlin", "Madrid", "Paris", "Rome"), 2),
            new Question("What is 2 + 2?", Arrays.asList("3", "4", "5", "6"), 1)
        );

        // Create the ClientWindow
        ClientWindow clientWindow = new ClientWindow();

        // Create the Game instance
        Game game = new Game(questionPool, clientWindow);

        // Start the game
        game.startGame();
    }
}
