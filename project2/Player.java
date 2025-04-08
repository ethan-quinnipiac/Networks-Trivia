package project2;

/*
 * Ethan Lanier
 * This is the Player class to manage player information and actions
 */

public class Player {
    private String id; // Unique identifier for the player
    private int score; // Player's current score

    // Constructor
    public Player(String id) {
        this.id = id;
        this.score = 0; // Initialize score to 0
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    // Update the player's score
    public void updateScore(int points) {
        this.score += points;
    }

    // Receive a question (placeholder for actual implementation)
    public void receiveQuestion(Question question) {
        System.out.println("Player " + id + " received question: " + question.getQuestionText());
    }

    // Receive a message (placeholder for actual implementation)
    public void receiveMessage(String message) {
        System.out.println("Player " + id + " received message: " + message);
    }
}
