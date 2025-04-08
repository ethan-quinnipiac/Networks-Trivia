/*
 * Ethan Lanier
 * This is the Game class to manage the players and flow of the game
 */

import java.util.*;
import java.util.concurrent.*;

public class Game {
    // Constants
    private static final int TOTAL_QUESTIONS = 20;
    private static final int POLL_TIMER_SECONDS = 15;
    private static final int ANSWER_TIMER_SECONDS = 10;
    private static final int CORRECT_ANSWER_POINTS = 10;
    private static final int INCORRECT_ANSWER_POINTS = -10;
    private static final int TIMEOUT_PENALTY = -20;

    // Game State
    private List<Player> players; // List of active players
    private Queue<Question> questions; // Queue of questions
    private int currentQuestionIndex;
    private boolean isPollingActive;
    private boolean isAnsweringActive;

    // Timer Executors
    private ScheduledExecutorService timerExecutor;

    // Constructor
    public Game(List<Question> questionPool) {
        this.players = new ArrayList<>();
        this.questions = new LinkedList<>(questionPool);
        this.currentQuestionIndex = 0;
        this.isPollingActive = false;
        this.isAnsweringActive = false;
        this.timerExecutor = Executors.newScheduledThreadPool(1);
    }

    // Add a player to the game
    public void addPlayer(Player player) {
        players.add(player);
    }

    // Remove a player from the game
    public void removePlayer(Player player) {
        players.remove(player);
    }

    // Start the game
    public void startGame() {
        if (questions.isEmpty()) {
            System.out.println("No questions available to start the game.");
            return;
        }
        nextQuestion();
    }

    // Move to the next question
    private void nextQuestion() {
        if (currentQuestionIndex >= TOTAL_QUESTIONS) {
            endGame();
            return;
        }

        Question currentQuestion = questions.poll();
        currentQuestionIndex++;
        isPollingActive = true;
        isAnsweringActive = false;

        // Broadcast the question to all players
        broadcastQuestion(currentQuestion);

        // Start the polling timer
        startPollingTimer();
    }

    // Broadcast a question to all players
    private void broadcastQuestion(Question question) {
        for (Player player : players) {
            player.receiveQuestion(question);
        }
    }

    // Start the polling timer
    private void startPollingTimer() {
        timerExecutor.schedule(() -> {
            isPollingActive = false;
            handlePollingTimeout();
        }, POLL_TIMER_SECONDS, TimeUnit.SECONDS);
    }

    // Handle polling timeout
    private void handlePollingTimeout() {
        System.out.println("Polling phase ended.");
        // Notify players and move to the next question if no one polled
        if (!isAnsweringActive) {
            broadcastMessage("No responses received. Moving to the next question.");
            nextQuestion();
        }
    }

    // Start the answering timer
    private void startAnsweringTimer(Player player) {
        timerExecutor.schedule(() -> {
            if (isAnsweringActive) {
                isAnsweringActive = false;
                penalizePlayerForTimeout(player);
                nextQuestion();
            }
        }, ANSWER_TIMER_SECONDS, TimeUnit.SECONDS);
    }

    // Penalize a player for not answering in time
    private void penalizePlayerForTimeout(Player player) {
        player.updateScore(TIMEOUT_PENALTY);
        System.out.println("Player " + player.getId() + " timed out. Penalized " + TIMEOUT_PENALTY + " points.");
    }

    // End the game
    private void endGame() {
        System.out.println("Game over! Final scores:");
        for (Player player : players) {
            System.out.println("Player " + player.getId() + ": " + player.getScore() + " points");
        }
    }

    // Broadcast a message to all players
    private void broadcastMessage(String message) {
        for (Player player : players) {
            player.receiveMessage(message);
        }
    }
}