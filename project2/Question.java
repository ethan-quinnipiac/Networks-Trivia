package project2;

/*
 * Ethan Lanier
 * This is the Question class to manage question information and options
 */

import java.util.List;

public class Question {
    private String questionText; // The text of the question
    private List<String> options; // List of options for the question
    private int correctOptionIndex; // Index of the correct option

    // Constructor
    public Question(String questionText, List<String> options, int correctOptionIndex) {
        if (options == null || options.size() < 4) {
            throw new IllegalArgumentException("A question must have at least 4 options.");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("Correct option index is out of bounds.");
        }
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    // Getters
    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getCorrectOption() {
        return options.get(correctOptionIndex);
    }

    // Utility method to check if an answer is correct
    public boolean isCorrectAnswer(int selectedOptionIndex) {
        return selectedOptionIndex == correctOptionIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(questionText).append("\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1).append(". ").append(options.get(i)).append("\n");
        }
        return sb.toString();
    }
}
