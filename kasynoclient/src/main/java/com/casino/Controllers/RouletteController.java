package com.casino.Controllers;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;
import com.casino.Logic.*;


public class RouletteController implements IController {

    public void onActivate() {
        ConnectionManager.getInstance().GetConnection().registerCallback((byte)0x04, (IncomingMessage msg) -> {
            onNewGameResultCallback(msg);
        });
    }

    @FXML
    private ImageView wheelImView = new ImageView();
    @FXML
    private Button betB0, betB1, betB2, betB3, betB4, betB5, betB6, betB7, betB8, betB9,
            betB10, betB11, betB12, betB13, betB14, betB15, betB16, betB17, betB18,
            betB19, betB20, betB21, betB22, betB23, betB24, betB25, betB26, betB27,
            betB28, betB29, betB30, betB31, betB32, betB33, betB34, betB35, betB36;
    @FXML
    private Button twoToOneBBot, twoToOneBMid, twoToOneBTop, firstTwelveB, secondTwelveB,
            thirdTwelveB,oneToEighteenB, evenNumbersB,  allRedB, allBlackB, oddNumbersB,
            nineteenToThirtySixB;
    @FXML
    private Button rollButton, clearButton, clearTableButton;
    @FXML
    private Button orangeChip1, blueChip5, greenChip10,
            blackChip25, purpleChip100, redChip500;

    @FXML
    private Label balanceLabel, betLabel, dealerLabel, previousGameLabel;
    @FXML
    private Button exitButton;

    private final String balanceLabelText = "Balance: ";
    private final String betLabelText = "Current bet: ";

    private final String prevGameLabelText = "Previous game: ";
    private final RouletteLogic rouletteLogic = new RouletteLogic();
    private int totalBetValue = 0;
    private int playerBalanceBeforeBetting = 0;
    private int playerBalanceAfterBetting = 0;
    private int playerWholeRoundBet=0;
    private final RoulettePlayer roulettePlayer = rouletteLogic.getRoulettePlayer();
    private double newAngle = 0;
    private List<RouletteNumber> rouletteNumbers;

    public void onBalanceUpdate(double newBalance) {
        balanceLabel.setText(balanceLabelText + newBalance);
        roulettePlayer.setBalance((int)newBalance);
    }

    public void requestStartGame() {
        ConnectionManager.getInstance().GetConnection().getMessageSender().sendGameStart(2, totalBetValue);
    }

    public void onNewGameResultCallback(IncomingMessage msg) {
        int success = msg.getInt();
        if (success == 1) {
            int gameId = msg.getInt();
            roulettePlayer.currentGameId = gameId;
            System.out.println("[casino-client] Received game start message: success (started game id "+gameId+")");
            Platform.runLater(() -> spinTheWheel());
        } else {
            int errorCode = msg.getInt();
            String errorMessage = msg.getString();
            System.out.println("[casino-client] Received game start message: failed (error code "+errorCode+"): "+errorMessage);
        }
    }

    @FXML
    public void initialize() {
        rouletteNumbers = rouletteLogic.getRouletteNumbers();
        initializeButtons();
    }

    private void spinTheWheel()
    {
        playerBalanceAfterBetting -= totalBetValue;
        betLabel.setText(betLabelText + "0");
        totalBetValue = 0;
        roulettePlayer.setTotalRoundBet(totalBetValue);

        orangeChip1.setDisable(true);
        blueChip5.setDisable(true);
        greenChip10.setDisable(true);
        blackChip25.setDisable(true);
        purpleChip100.setDisable(true);
        redChip500.setDisable(true);
        clearTableButton.setDisable(true);


        int spins = randomSpin(3, 7);
        int rollDuration = randomSpin(4, 8);
        int randomRoll = rouletteLogic.rollNumber();

        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(wheelImView);
        rotateTransition.setDuration(Duration.seconds(rollDuration));
//        int randomRoll = 31;
        double angleRotation = rouletteNumbers.get(randomRoll).getAngle();
        System.out.println(rouletteNumbers.get(randomRoll).getNumber());
        rotateTransition.setByAngle(360* spins + angleRotation - newAngle);
        newAngle = angleRotation;
        rotateTransition.play();
        rotateTransition.setOnFinished(event -> handleGameOutcome(randomRoll));
        rollButton.setDisable(true);

    }
    public int randomSpin(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
    private void handleGameOutcome(int rolledNumber)
    {
        Button[] numericButtons = {betB0, betB1, betB2, betB3, betB4, betB5, betB6, betB7, betB8, betB9,
            betB10, betB11, betB12, betB13, betB14, betB15, betB16, betB17, betB18,
            betB19, betB20, betB21, betB22, betB23, betB24, betB25, betB26, betB27,
            betB28, betB29, betB30, betB31, betB32, betB33, betB34, betB35, betB36};

        dealerLabel.setText(rolledNumber + " rolled!");
        resetButtonStyles(numericButtons);
        orangeChip1.setDisable(false);
        blueChip5.setDisable(false);
        greenChip10.setDisable(false);
        blackChip25.setDisable(false);
        purpleChip100.setDisable(false);
        redChip500.setDisable(false);
        clearTableButton.setDisable(true);

        rouletteLogic.checkIfPlayerWins(rolledNumber);
        balanceLabel.setText(balanceLabelText + roulettePlayer.getBalance());
        PauseTransition waitForBalanceTransition = new PauseTransition();
        waitForBalanceTransition.setDuration(Duration.millis(500));
        waitForBalanceTransition.setOnFinished(event -> {
        if(playerBalanceBeforeBetting > roulettePlayer.getBalance())
        {
            previousGameLabel.setText(prevGameLabelText + "-" + (playerWholeRoundBet));
        }
        else
        {
            previousGameLabel.setText(prevGameLabelText + "+" + roulettePlayer.getLatestWinnings());
        }});
        waitForBalanceTransition.play();
//        playerBalanceAfterBetting = roulettePlayer.getBalance();
        roulettePlayer.getBetRouletteNumbers().clear();
        playerWholeRoundBet = 0;
        playerBalanceBeforeBetting = 0;
        playerBalanceAfterBetting = roulettePlayer.getBalance();

    }

    @FXML
    private void initializeButtons() {

        Button[] numericButtons = {betB0, betB1, betB2, betB3, betB4, betB5, betB6, betB7, betB8, betB9,
                betB10, betB11, betB12, betB13, betB14, betB15, betB16, betB17, betB18,
                betB19, betB20, betB21, betB22, betB23, betB24, betB25, betB26, betB27,
                betB28, betB29, betB30, betB31, betB32, betB33, betB34, betB35, betB36};


        orangeChip1.setOnAction(event -> setBetValue(1));
        blueChip5.setOnAction(event -> setBetValue(5));
        greenChip10.setOnAction(event -> setBetValue(10));
        blackChip25.setOnAction(event -> setBetValue(25));
        purpleChip100.setOnAction(event -> setBetValue(100));
        redChip500.setOnAction(event -> setBetValue(500));

        clearButton.setOnAction(event -> clearBet());
        clearTableButton.setOnAction(event -> clearTable());
        rollButton.setOnAction(event -> requestStartGame());
        rollButton.setDisable(true);
        clearTableButton.setDisable(true);


        for (int i = 0; i <  numericButtons.length; i++) {
            setupNumericButtonAction(numericButtons[i], i);
        }

        setTwoToOneButtons(twoToOneBTop, new int[]{3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36}, numericButtons);
        setTwoToOneButtons(twoToOneBMid, new int[]{2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35}, numericButtons);
        setTwoToOneButtons(twoToOneBBot, new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34}, numericButtons);

        setupGroupBetButton(firstTwelveB, 1, 12, numericButtons);
        setupGroupBetButton(secondTwelveB, 13, 24, numericButtons);
        setupGroupBetButton(thirdTwelveB, 25, 36, numericButtons);
        setupGroupBetButton(oneToEighteenB, 1, 18, numericButtons);
        setupGroupBetButton(nineteenToThirtySixB, 19, 36, numericButtons);


        setEvenOddButtons(evenNumbersB, true, numericButtons);
        setEvenOddButtons(oddNumbersB, false, numericButtons);

        setColorButtons(allRedB, "red", numericButtons);
        setColorButtons(allBlackB, "black", numericButtons);

    }
    private void changeButtonStyle(Button button) {
        button.setStyle("-fx-background-color: red; -fx-opacity: 0.65;");
    }
    private void resetButtonStyles(Button[] buttons) {
        for (Button button : buttons) {
            button.setStyle("");
        }
    }
    private void clearTable() {
        rollButton.setDisable(true);

        Button[] numericButtons = {betB0, betB1, betB2, betB3, betB4, betB5, betB6, betB7, betB8, betB9,
                betB10, betB11, betB12, betB13, betB14, betB15, betB16, betB17, betB18,
                betB19, betB20, betB21, betB22, betB23, betB24, betB25, betB26, betB27,
                betB28, betB29, betB30, betB31, betB32, betB33, betB34, betB35, betB36};

        // reset button coloring
        resetButtonStyles(numericButtons);

        // clear selected roulette numbers
        roulettePlayer.getBetRouletteNumbers().clear();
        //ZMIANA BALANSU - OBSTAWIANIE - ZWROT PO WYCZYSZCZENIU OBSTAWIONYCH NUMEROW
        playerWholeRoundBet = 0;
        playerBalanceBeforeBetting = 0;
        clearTableButton.setDisable(true);
    }


    private void setBetValue(int betValue)
    {
        if ((totalBetValue + betValue)  <= roulettePlayer.getBalance()) {
            totalBetValue += betValue;
            roulettePlayer.setBetValue(betValue);
            String currentBetText = betLabelText + totalBetValue;
            betLabel.setText(currentBetText);
        }
        else
        {
            totalBetValue = roulettePlayer.getBalance();
            roulettePlayer.setBetValue(betValue);
            betLabel.setText(betLabelText + roulettePlayer.getBalance());
        }
        roulettePlayer.setTotalRoundBet(totalBetValue);
    }
    private void clearBet()
    {
        totalBetValue = 0;
        betLabel.setText(betLabelText + "0");
    }
    private void placeBet()
    {
        playerBalanceBeforeBetting = roulettePlayer.getBalance();
        totalBetValue = roulettePlayer.getTotalRoundBet();
        playerWholeRoundBet+= totalBetValue;
        //ZMIANA BALANSU - ODJECIE PO OBSTAWIENIU
        

        rollButton.setDisable(false);
        clearTableButton.setDisable(false);

    }

    private void setupNumericButtonAction(Button button, int number) {
        button.setOnAction(event -> {
            if(roulettePlayer.getTotalRoundBet() > 0 ) {
                System.out.println("Bet button " + number + "click ");
                Bet bet = new Bet(totalBetValue,List.of(number));
                roulettePlayer.getBetRouletteNumbers().add(bet);
                button.setStyle("-fx-background-color: red ; -fx-opacity: 0.6;");
                placeBet();
            }
        });
    }

    private void setTwoToOneButtons(Button button, int[] numbers, Button[] numericButtons) {
        button.setOnAction(event -> {
            if(roulettePlayer.getTotalRoundBet() > 0 ) {
                List<Integer> bettedNumbers = new ArrayList<>();
                for (int number : numbers) {
                    bettedNumbers.add(number);
                    changeButtonStyle(numericButtons[number]);
                }
                Bet bet = new Bet(totalBetValue,bettedNumbers);
                roulettePlayer.getBetRouletteNumbers().add(bet);
                System.out.println("2 to 1 clicked");
                placeBet();
            }
        });
    }


    private void setupGroupBetButton(Button button, int start, int end, Button[] numericButtons) {
        button.setOnAction(event -> {
            if(roulettePlayer.getTotalRoundBet() > 0 ) {
                List<Integer> bettedNumbers = new ArrayList<>();
                for (int i = start; i <= end; i++) {
                    bettedNumbers.add(i);
//                    RouletteNumber newNumber = new RouletteNumber(i, "");
                    changeButtonStyle(numericButtons[i]);
                }
                Bet bet = new Bet(totalBetValue,bettedNumbers);
                roulettePlayer.getBetRouletteNumbers().add(bet);
                System.out.println("12 group clicked");
                placeBet();
            }
        });
    }


    private void setEvenOddButtons(Button button, boolean even, Button[] numericButtons) {
        button.setOnAction(event -> {
            if(roulettePlayer.getTotalRoundBet() > 0 ) {
                List<Integer> bettedNumbers = new ArrayList<>();
                for (int i = 1; i <= 36; i++) {
                    if ((even && i % 2 == 0) || (!even && i % 2 != 0)) {
//                        RouletteNumber newNumber = new RouletteNumber(i, "");
                        bettedNumbers.add(i);
                        changeButtonStyle(numericButtons[i]);
                    }
                }
                Bet bet = new Bet(totalBetValue,bettedNumbers);
                roulettePlayer.getBetRouletteNumbers().add(bet);
                System.out.println("even/odd clicked");

                placeBet();
            }
        });
    }


    private void setColorButtons(Button button, String color, Button[] numericButtons) {
        button.setOnAction(event -> {
            if(roulettePlayer.getTotalRoundBet() > 0 ) {
                List<Integer> bettedNumbers = new ArrayList<>();
                for (RouletteNumber number : rouletteNumbers) {
                    if (number.getColorOfNumber().equals(color)) {
                        bettedNumbers.add(number.getNumber());
                        changeButtonStyle(numericButtons[number.getNumber()]);
                    }
                }
                Bet bet = new Bet(totalBetValue,bettedNumbers);
                roulettePlayer.getBetRouletteNumbers().add(bet);
                System.out.println("color clicked");
                placeBet();
            }
        });

    }

}
