package com.example.braintease_final;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Import for ArrayList
import java.util.List;

public class BrainTeaseTriviaGame extends Application {
    private Stage primaryStage;
    private User player;
    private int currentRound;
    private int currentQuestionIndex;
    private List<Question> questionDatabase;
    private Random random = new Random();
    private static final String[] tableNames = {"Anime", "Sports", "Kpop", "General"};

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BrainTease Trivia Challenge");

        player = new User("Player1");
        currentRound = 0;
        currentQuestionIndex = 0;

        // Get a random table name for the questions
        String randomTableName = getRandomTableName();
        System.out.println("Selected Table Name: " + randomTableName); // Debugging

        // Load questions from the random table
        questionDatabase = QuestionDatabaseLoader.loadQuestionsFromDatabase(randomTableName);

        showIntroduction();
        primaryStage.show();
    }

    private String getRandomTableName() {
        int randomIndex = random.nextInt(tableNames.length);
        return tableNames[randomIndex];
    }

    private void showIntroduction() {
        currentRound = 0;
        String introductionText = "Welcome to BrainTease Trivia Challenge!\n\n"
                + "For our group project, we have created a trivia game that combines elements of both 'Jeopardy!' and 'Who wants to be a Millionaire?'.\n\n"
                + "This game will have different rounds and increasing difficulty levels.\n\n"
                + "Are you ready to start? Click the 'Start Game' button to begin.";

        VBox introductionLayout = new VBox(10);
        Scene introductionScene = new Scene(introductionLayout, 400, 300);

        Label introductionLabel = new Label(introductionText);
        introductionLabel.setWrapText(true);
        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> {
            System.out.println("Start Game button clicked");
            startRoundOne();
        });

        introductionLayout.getChildren().addAll(introductionLabel, startButton);
        primaryStage.setScene(introductionScene);
    }

    private void startRoundOne() {
        currentRound = 1;
        displayQuestion(questionDatabase.get(currentQuestionIndex)); // Display the first question
        currentQuestionIndex++;  // Increment after displaying the question
    }

    private void startNextRound() {
        switch (currentRound) {
            case 1:
                startRoundTwo();
                break;
            case 2:
                startRoundThree();
                break;
            case 3:
                startRoundFour();
                break;
            default:
                // Handle an unexpected case
                break;
        }
    }

    private void displayQuestion(Question question) {
        Label questionLabel = new Label(question.getQuestion());
        questionLabel.setWrapText(true);
        Button choiceAButton = new Button(question.getChoiceA());
        Button choiceBButton = new Button(question.getChoiceB());
        Button choiceCButton = new Button(question.getChoiceC());
        Button choiceDButton = new Button(question.getChoiceD());

        VBox questionLayout = new VBox(10);
        questionLayout.getChildren().addAll(questionLabel, choiceAButton, choiceBButton, choiceCButton, choiceDButton);

        Scene questionScene = new Scene(questionLayout, 400, 300);
        primaryStage.setScene(questionScene);

        choiceAButton.setOnAction(event -> handleAnswer(question, "A"));
        choiceBButton.setOnAction(event -> handleAnswer(question, "B"));
        choiceCButton.setOnAction(event -> handleAnswer(question, "C"));
        choiceDButton.setOnAction(event -> handleAnswer(question, "D"));
    }

    private void handleAnswer(Question question, String selectedChoice) {
        int roundScoreIncrement = 0;  // Initialize round score increment

        if (selectedChoice.equals(question.getCorrectAnswer())) {
            int scoreIncrement = currentRound * 10;
            player.setScore(player.getScore() + scoreIncrement);
            roundScoreIncrement = scoreIncrement;  // Update round score increment
            displayMessage("Correct! You earned " + scoreIncrement + " points.");
        } else {
            player.incrementIncorrectAnswers();
            displayMessage("Incorrect. The correct answer was " + question.getCorrectAnswer());
        }

        currentQuestionIndex++;

        int questionBankSize = questionDatabase.size();

        if (player.getIncorrectAnswers() <= 4) {
            if (currentQuestionIndex < questionBankSize) {
                displayQuestion(questionDatabase.get(currentQuestionIndex));
            } else {
                if (currentRound < 4) {
                    startNextRound();
                } else {
                    displayFinalResults();
                }
            }
        } else {
            displayRoundEndSummary(roundScoreIncrement, player.getScore(), false);
        }
    }

    private void displayRoundEndSummary(int roundScore, int totalScore, boolean qualifiedForNextRound) {
        String summaryMessage = "Round Over!\n\n" +
                "Round Score: " + roundScore + "\n" +
                "Total Score: " + totalScore + "\n\n";

        if (qualifiedForNextRound) {
            summaryMessage += "Congratulations! You qualified for the next round.";
        } else {
            summaryMessage += "Sorry, you didn't qualify for the next round. Game Over!";
        }

        Label summaryLabel = new Label(summaryMessage);
        summaryLabel.setWrapText(true);

        VBox summaryLayout = new VBox(10);
        summaryLayout.getChildren().addAll(summaryLabel);

        Button nextRoundButton = new Button("Next Round");
        nextRoundButton.setDisable(!qualifiedForNextRound);
        nextRoundButton.setOnAction(event -> {
            if (qualifiedForNextRound) {
                startNextRound();
            } else {
                displayFinalResults();
            }
        });

        summaryLayout.getChildren().add(nextRoundButton);

        Scene summaryScene = new Scene(summaryLayout, 400, 300);
        primaryStage.setScene(summaryScene);
    }

    private void displayFinalResults() {
        String finalResult = "Game Over!\n\n" + player.getName() + "'s Total Score: " + player.getScore();
        Label resultLabel = new Label(finalResult);
        resultLabel.setWrapText(true);

        VBox resultLayout = new VBox(10);
        resultLayout.getChildren().addAll(resultLabel);

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setOnAction(event -> startNewGame());

        resultLayout.getChildren().add(playAgainButton);

        Scene resultScene = new Scene(resultLayout, 400, 300);
        primaryStage.setScene(resultScene);
    }

    private void displayMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);

        VBox messageLayout = new VBox(10);
        messageLayout.getChildren().addAll(messageLabel);

        Scene messageScene = new Scene(messageLayout, 400, 300);
        primaryStage.setScene(messageScene);
    }

    private void startNewGame() {
        // Reset game state to its initial values
        currentRound = 0;
        currentQuestionIndex = 0;
        player.setScore(0);

        // Show the introduction screen to start a new game
        showIntroduction();
    }

    private void startRoundTwo() {
        currentRound = 2;
        currentQuestionIndex = 0;
        displayQuestion(questionDatabase.get(currentQuestionIndex)); // Display the first question of round two
    }

    private void startRoundThree() {
        currentRound = 3;
        currentQuestionIndex = 0;
        displayQuestion(questionDatabase.get(currentQuestionIndex)); // Display the first question of round three
    }

    private void startRoundFour() {
        currentRound = 4;
        currentQuestionIndex = 0;
        displayQuestion(questionDatabase.get(currentQuestionIndex)); // Display the first question of round four
    }

    public class QuestionDatabaseLoader {
        public static List<Question> loadQuestionsFromDatabase(String tableName) {
            List<Question> questions = new ArrayList<>();

            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/bigju/OneDrive/Desktop/BrainTeaseTriviaQuestions.db");

                // Print for debugging
                System.out.println("Connected to the database.");

                // Modify the SQL query to select from the appropriate table
                String query = "SELECT * FROM \"" + tableName + "\"";
                System.out.println(query);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String question = resultSet.getString("Questions");
                    String choiceA = resultSet.getString("Choice_A");
                    String choiceB = resultSet.getString("Choice_B");
                    String choiceC = resultSet.getString("Choice_C");
                    String choiceD = resultSet.getString("Choice_D");
                    String correctAnswer = resultSet.getString("Correct_Answer");

                    Question q = new Question(question, choiceA, choiceB, choiceC, choiceD, correctAnswer);
                    questions.add(q);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error loading questions from the database.");
            }

            return questions;
        }
    }
}
