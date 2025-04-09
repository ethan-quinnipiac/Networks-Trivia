package project2;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a list of questions
        List<Question> questionPool = Arrays.asList(
            new Question("What is the capital of France?", Arrays.asList("Berlin", "Madrid", "Paris", "Rome"), 2),
            new Question("What is 2 + 2?", Arrays.asList("3", "4", "5", "6"), 1),
            new Question("Which planet is known as the Red Planet?", Arrays.asList("Earth", "Mars", "Jupiter", "Venus"), 1),
            new Question("Who wrote 'Romeo and Juliet'?", Arrays.asList("Charles Dickens", "William Shakespeare", "Mark Twain", "Jane Austen"), 1),
            new Question("What is the largest ocean on Earth?", Arrays.asList("Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"), 3),
            new Question("What is the square root of 64?", Arrays.asList("6", "7", "8", "9"), 2),
            new Question("Which country is known as the Land of the Rising Sun?", Arrays.asList("China", "Japan", "South Korea", "Thailand"), 1),
            new Question("What is the chemical symbol for water?", Arrays.asList("H2O", "O2", "CO2", "NaCl"), 0),
            new Question("Who painted the Mona Lisa?", Arrays.asList("Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Claude Monet"), 1),
            new Question("What is the capital of Italy?", Arrays.asList("Rome", "Paris", "Berlin", "Madrid"), 0),
            new Question("Which element has the atomic number 1?", Arrays.asList("Oxygen", "Hydrogen", "Helium", "Carbon"), 1),
            new Question("What is the largest mammal in the world?", Arrays.asList("Elephant", "Blue Whale", "Giraffe", "Great White Shark"), 1),
            new Question("Who discovered gravity?", Arrays.asList("Albert Einstein", "Isaac Newton", "Galileo Galilei", "Nikola Tesla"), 1),
            new Question("What is the smallest prime number?", Arrays.asList("0", "1", "2", "3"), 2),
            new Question("Which country hosted the 2016 Summer Olympics?", Arrays.asList("China", "Brazil", "Japan", "USA"), 1),
            new Question("What is the freezing point of water in Celsius?", Arrays.asList("0", "32", "100", "-1"), 0),
            new Question("What is the capital of Canada?", Arrays.asList("Toronto", "Vancouver", "Ottawa", "Montreal"), 2),
            new Question("Which gas do plants primarily use for photosynthesis?", Arrays.asList("Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen"), 1),
            new Question("What is the longest river in the world?", Arrays.asList("Amazon River", "Nile River", "Yangtze River", "Mississippi River"), 1),
            new Question("Who was the first President of the United States?", Arrays.asList("Abraham Lincoln", "George Washington", "Thomas Jefferson", "John Adams"), 1)
        );

        // Create the ClientWindow
        ClientWindow clientWindow = new ClientWindow();

        // Create the Game instance
        Game game = new Game(questionPool, clientWindow);

        // Start the game
        game.startGame();
    }
}
