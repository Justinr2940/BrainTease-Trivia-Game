package com.example.braintease_final;
public class User {
    private String name;
    private int score;
    private int incorrectAnswers;

    public User(String name) {
        this.name = name;
        this.score = 0;
        this.incorrectAnswers = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void incrementIncorrectAnswers() {
        incorrectAnswers++;
    }
}
