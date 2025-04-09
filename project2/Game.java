package project2;
/*
 * Ethan Lanier
 * This is the Game class to manage the players and flow of the game
 */

import java.util.*;
import java.util.concurrent.*;

import project2.ClientWindow.TimerCode;

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
    public boolean isPollingActive;
    public boolean isAnsweringActive;
    public static boolean isOver = false;

    // Timer Executors
    private ScheduledExecutorService timerExecutor;
    public static ScheduledFuture<?> pollingTimerFuture;
    private ClientWindow clientWindow; // Reference to the ClientWindow

    // Constructor
    public Game(List<Question> questionPool, ClientWindow clientWindow) {
        this.players = new ArrayList<>();
        this.questions = new LinkedList<>(questionPool);
        this.currentQuestionIndex = 0;
        this.isPollingActive = false;
        this.isAnsweringActive = false;
        this.timerExecutor = Executors.newScheduledThreadPool(1);
        this.clientWindow = clientWindow; // Initialize the ClientWindow
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
    public void nextQuestion() {
        if (currentQuestionIndex >= TOTAL_QUESTIONS) {
            endGame();
            return;
        }

        Question currentQuestion = questions.poll();
        currentQuestionIndex++;
        isPollingActive = true;
        isAnsweringActive = false;
        ((TimerCode) clientWindow.clock).setTime(15);

        // Broadcast the question to all players
        broadcastQuestion(currentQuestion);
        if(pollingTimerFuture != null && pollingTimerFuture.isDone() == false){
            pollingTimerFuture.cancel(true);
        }
       
        startPollingTimer();
    }

    // Broadcast a question to the ClientWindow
    private void broadcastQuestion(Question question) {
        clientWindow.displayQuestion(question); // Update the GUI with the question
    }

    // Start the polling timer
    private void startPollingTimer() {
        pollingTimerFuture = timerExecutor.schedule(() -> {
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
            players.get(0).updateScore(TIMEOUT_PENALTY);
            nextQuestion();
        }
    }

    // Start the answering timer
    public void startAnsweringTimer(Player player) {
        pollingTimerFuture.cancel(true);
        isAnsweringActive = true;
        ((TimerCode) clientWindow.clock).setTime(ANSWER_TIMER_SECONDS);
        pollingTimerFuture = timerExecutor.schedule(() -> {
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
        isOver = true;
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