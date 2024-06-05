package com.casino.Controllers;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import com.casino.Logic.Card;
import com.casino.Connection.ConnectionManager;
import com.casino.Connection.IncomingMessage;
import com.casino.Logic.BlackJackManager;
import com.casino.Logic.Player;
import com.casino.Logic.User;

public class BlackJackController implements IController {

    @FXML
    private Button orangeChip1, blueChip5, greenChip10,
    blackChip25, purpleChip100, redChip500;
    @FXML
    private Button betButton, clearButton, hitButton, stayButton,dDownButton, playAgainButton, insuYesButton, insuNoButton;
    @FXML
    private Label balanceLabel, betLabel, dealerLabel, cardsValLabel, dCardsValLabel, previousGameLabel;
    @FXML
    private Pane dealerCardPane;
    @FXML
    private Pane playerCardPane;
    private Card dealerFaceDownCard;
    private ImageView dealerFaceDownCardView = new ImageView();
    private ImageView dealerCardView = new ImageView();
    private int totalBetValue;
    private final String balanceLabelText = "Balance: ";
    private final String betLabelText = "Current bet: ";
    private final String cardValLabelText = "Cards value: ";
    private final String dcardValLabelText = "Dealer cards value: ";
    private final String prevGameLabelText = "Previous game: ";
    private final BlackJackManager bjm  = new BlackJackManager();
    private Player player = bjm.getPlayer();
    private final Player dealer = bjm.getDealer();
    private int dealerCardsPassed = 0;
    private int playerCardsPassed= 0;
    private int nextCardPos = 0;
    private int totalPlayerCardsValue = 0;
    private String cardPathForTheExposure;
    private int nextCardPixelDraft = 50;
    private int playerBalanceBeforeBetting = 0;

    private int currentGameId = -1;

    public BlackJackController()
    {
        this.totalBetValue = 0;
        dealerLabel = new Label();
//        playAgainButton.setVisible(false);
    }

    public void onActivate() {
        initbuttons();
        showBegginingLabel();

        ConnectionManager.getInstance().GetConnection().registerCallback((byte)0x04, (IncomingMessage msg) -> {
            onNewGameResultCallback(msg);
        });
    }
    
    public void requestStartGame() {
        ConnectionManager.getInstance().GetConnection().getMessageSender().sendGameStart(1, totalBetValue);
    }

    public void onNewGameResultCallback(IncomingMessage msg) {
        int success = msg.getInt();
        if (success == 1) {
            int gameId = msg.getInt();
            currentGameId = gameId;
            System.out.println("[casino-client] Received game start message: success (started game id "+gameId+")");
            Platform.runLater(() -> placeBet());
        } else {
            int errorCode = msg.getInt();
            String errorMessage = msg.getString();
            System.out.println("[casino-client] Received game start message: failed (error code "+errorCode+"): "+errorMessage);
        }
    }

    public void onBalanceUpdate(double newBalance) {
        balanceLabel.setText(balanceLabelText + newBalance);
    }

    private void endGame(int result, double betMultiplier)
    {
        ConnectionManager.getInstance().GetConnection().getMessageSender().sendGameEnd(currentGameId, result, betMultiplier);
    }

    @FXML
    protected void initbuttons()
    {

        player = bjm.getPlayer();

        onBalanceUpdate(User.getBalance());
        betLabel.setText(betLabelText + "0");
//        orangeChiplayer.setOnAction(this::setBetValue1);
        orangeChip1.setOnAction(event -> setBetValue(1));
        blueChip5.setOnAction(event -> setBetValue(5));
        greenChip10.setOnAction(event -> setBetValue(10));
        blackChip25.setOnAction(event -> setBetValue(25));
        purpleChip100.setOnAction(event -> setBetValue(100));
        redChip500.setOnAction(event -> setBetValue(500));

        clearButton.setOnAction(event -> clearBet());
        betButton.setOnAction(event -> requestStartGame());

        hitButton.setOnAction(event -> hitPlayerCard());
        stayButton.setOnAction(event -> stayButtonHandler());
        dDownButton.setOnAction(event -> dDownButtonHandler());

        insuYesButton.setDisable(true);
        insuYesButton.setVisible(false);
        insuNoButton.setDisable(true);
        insuNoButton.setVisible(false);
        insuNoButton.setOnAction(event -> noButtonHandler());
        insuYesButton.setOnAction(event -> yesButtonHandler());

        playAgainButton.setVisible(false);
        playAgainButton.setDisable(true);
        playAgainButton.setOnAction(event -> playAgain());


    }

    private void playAgain()
    {
        //start new game
        initbuttons();

        BlackJackManager bjm = new BlackJackManager();
        player.reset();
        dealer.reset();
        totalBetValue = 0;
        dealerCardsPassed = 0;
        playerCardsPassed= 0;
        nextCardPos = 0;
        totalPlayerCardsValue = 0;
        nextCardPixelDraft = 50;
        cardsValLabel.setText(cardValLabelText + "0");
        dCardsValLabel.setText(dcardValLabelText + "0");
        betButton.setDisable(false);
        hitButton.setDisable(false);
        playAgainButton.setVisible(false);
        playAgainButton.setDisable(true);
        dealerCardPane.getChildren().clear();
        playerCardPane.getChildren().clear();
        cardsValLabel.setText(cardValLabelText + "0");
        dealerLabel.setText(""); // Reset any game messages
        orangeChip1.setDisable(false);
        blueChip5.setDisable(false);
        greenChip10.setDisable(false);
        blackChip25.setDisable(false);
        purpleChip100.setDisable(false);
        redChip500.setDisable(false);

        showBegginingLabel();

    }

//    private void setBetValue1(ActionEvent event)
//    {
//        int betValue = 1;
//        totalBetValue+=1;
//        String currentBetText = "Current Bet: " + Integer.toString(totalBetValue);
//        betLabel.setText(currentBetText);
//
//    }

    private void setBetValue(int betValue)
    {
        if ((totalBetValue + betValue)  <= User.getBalance()) {
            totalBetValue += betValue;
            player.setBetValue(betValue);
            String currentBetText = betLabelText + totalBetValue;
            betLabel.setText(currentBetText);
        }
        else
        {
            totalBetValue = (int)User.getBalance();
            player.setBetValue(betValue);
            betLabel.setText(betLabelText + User.getBalance());
        }
        player.setTotalRoundBet(totalBetValue);
    }
    private void stayButtonHandler()
    {
        hitButton.setDisable(true);
        stayButton.setDisable(true);
        dDownButton.setDisable(true);

        while ((dealer.getHandVal() < 17 && player.getHandVal() >  dealer.getHandVal()) || (dealer.getHandVal() < player.getHandVal() && dealer.hasAce() && dealer.getHandVal() < 17))
        {
            dealDelaerCard();
        }

        if(player.getHandVal() == dealer.getHandVal())
        {
            dealerLabel.setText("It's a draw!");
            player.setBalance(playerBalanceBeforeBetting );
            previousGameLabel.setText(prevGameLabelText + "+" +player.getTotalRoundBet() );

            endGame(3, 1.0);

            playAgainButton.setDisable(false);
            playAgainButton.setVisible(true);

        } else if (player.getHandVal() > dealer.getHandVal() || dealer.getHandVal() > 21) {

            dealerLabel.setText("You won!");
            player.setBalance(playerBalanceBeforeBetting+  player.getTotalRoundBet() );
            previousGameLabel.setText(prevGameLabelText + "+" + (2 * player.getTotalRoundBet()) );
            playAgainButton.setDisable(false);
            playAgainButton.setVisible(true);
            endGame(1, 2.0);
        }
        else
        {
            dealerLabel.setText("You lost!");
            previousGameLabel.setText(prevGameLabelText + "-" +player.getTotalRoundBet() );
            player.setBalance(playerBalanceBeforeBetting - player.getTotalRoundBet());
            playAgainButton.setDisable(false);
            playAgainButton.setVisible(true);

            endGame(2, 0.0);
        }

        playDealerCardAnimation();

    }
    private void noButtonHandler()
    {
        insuNoButton.setVisible(false);
        insuNoButton.setDisable(true);
        insuYesButton.setVisible(false);
        insuYesButton.setDisable(true);
        if(dealer.getHandVal() == 21 && player.getHandVal() < 21)
        {
            dealerLabel.setText("You lost!");
            previousGameLabel.setText(prevGameLabelText + "-" + player.getTotalRoundBet() );
            // balanceLabel.setText(balanceLabelText+ (playerBalanceBeforeBetting - player.getTotalRoundBet()));
            playDealerCardAnimation();
            playAgainButton.setDisable(false);
            playAgainButton.setVisible(true);

            endGame(2, 0);

        }
        else
        {

            hitButton.setDisable(false);
            if( 2 * player.getTotalRoundBet() > player.getBalance())
            {
                dDownButton.setDisable(true);
            }
            else
            {
                dDownButton.setDisable(false);
            }
            stayButton.setDisable(false);
            dealerLabel.setText("...");
        }
    }
    private void yesButtonHandler()
    {
        insuNoButton.setVisible(false);
        insuNoButton.setDisable(true);
        insuYesButton.setVisible(false);
        insuYesButton.setDisable(true);
        int insuaranceValue;
        int playerTotalBet = player.getTotalRoundBet();
        if(playerTotalBet % 2 == 1)
        {
            insuaranceValue = playerTotalBet/2 + 1;
        }
        else {
            insuaranceValue = playerTotalBet/ 2;
        }

        if(dealer.getHandVal() == 21 && player.getHandVal() < 21)
        {
            dealerLabel.setText("You get the insurance!");
            player.setBalance(playerBalanceBeforeBetting - player.getTotalRoundBet() + insuaranceValue);
            previousGameLabel.setText(prevGameLabelText + "+" + insuaranceValue*2);
            // balanceLabel.setText(balanceLabelText+ player.getBalance());
            playDealerCardAnimation();

            endGame(1, 0.5);

            playAgainButton.setDisable(false);
            playAgainButton.setVisible(true);
        }
        else
        {
            hitButton.setDisable(false);
            stayButton.setDisable(false);
            if( 2 * player.getTotalRoundBet() > player.getBalance())
            {
                dDownButton.setDisable(true);
            }
            else
            {
                dDownButton.setDisable(false);
            }
            dealerLabel.setText("...");
            player.setBalance(playerBalanceBeforeBetting - insuaranceValue - player.getTotalRoundBet());
            // balanceLabel.setText(balanceLabelText+ player.getBalance());
            endGame(2, -0.5);
        }

    }
    private void dDownButtonHandler()
    {
        hitButton.setDisable(true);
        stayButton.setDisable(true);
        dDownButton.setDisable(true);

//        player.setBalance(player.getBalance() - player.getTotalRoundBet());
//        player.setBetValue( 2 * player.getTotalRoundBet() );
//        balanceLabel.setText(balanceLabelText + player.getBalance());
        player.doubleDown();
        dealCard();
        if(player.getHandVal() > 21)
        {
            dealerLabel.setText("You lost!");
            previousGameLabel.setText(prevGameLabelText + "-" +2*player.getTotalRoundBet() );
            player.setBalance(playerBalanceBeforeBetting - 2* player.getTotalRoundBet());
            // balanceLabel.setText(balanceLabelText+ player.getBalance());
            playAgainButton.setDisable(false);
            playAgainButton.setVisible(true);
            endGame(2, -1.0);
            return;
        }
        stayButtonHandler();
    }

    private void playDealerCardAnimation()
    {

        Thread waitForTransition = new Thread(()-> {
            try {
                TranslateTransition faceDownCardTransition = new TranslateTransition();
                faceDownCardTransition.setDuration(Duration.millis(700));
                faceDownCardTransition.setNode(dealerFaceDownCardView);
                faceDownCardTransition.setToY(-150);
                faceDownCardTransition.play();

                Thread.sleep(700);

                Image faceDownExposureImage = new Image(getClass().getResource(cardPathForTheExposure).toExternalForm());
                dealerFaceDownCardView.setImage(faceDownExposureImage);
                dealerFaceDownCardView.setFitWidth(120);
                dealerFaceDownCardView.setFitHeight(180);

                TranslateTransition revealedCardTransition = new TranslateTransition();
                revealedCardTransition.setDuration(Duration.millis(700));
                revealedCardTransition.setNode(dealerFaceDownCardView);
                revealedCardTransition.setToY(209);
                revealedCardTransition.play();

                Platform.runLater(() -> dCardsValLabel.setText(dcardValLabelText + dealer.getHandVal()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        waitForTransition.start();
    }
    private void clearBet()
    {
        totalBetValue = 0;
        betLabel.setText(betLabelText + "0");
    }

    private void placeBet()
    {
        if(player.getBetValue() == 0)
        {
            return;
        }
        betLabel.setText(betLabelText + "0");
        totalBetValue = 0;

        betButton.setDisable(true);
        orangeChip1.setDisable(true);
        blueChip5.setDisable(true);
        greenChip10.setDisable(true);
        blackChip25.setDisable(true);
        purpleChip100.setDisable(true);
        redChip500.setDisable(true);
        showShuffleLabel();
    }

    private void showShuffleLabel() {

        Thread shuffleThread = new Thread(() -> {
            try {

                for (int i = 0; i < 2; i++) {
                //Platform is used in order to interact with JavaFX UI applications Thread
                //The non fx application threads (regular threads) produce an error
                // method .runLater() puts the next Text I want to set in queue, and
                //it is handled (set) as soon at it is possible
                Platform.runLater(() -> dealerLabel.setText("Shuffling cards. " ));
                Thread.sleep(500);

                Platform.runLater(() -> dealerLabel.setText("Shuffling cards.. " ));
                Thread.sleep(500);

                Platform.runLater(() -> dealerLabel.setText("Shuffling cards... " ));
                Thread.sleep(500);
                }
                Platform.runLater(() -> dealerLabel.setText("Cards are shuffled!" ));
                Thread.sleep(500);

                dealCard();
                Thread.sleep(300);
                dealCard();
                Thread.sleep(300);

                dealDelaerCard();
                dealDelaerCard();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        shuffleThread.start();
    }

    private void dealCard()
    {

        bjm.dealCard();
        ImageView playerCardView= new ImageView();

        Card playerCard = player.getHand().get(playerCardsPassed);
        totalPlayerCardsValue = player.getHandVal();
        Image playerCardImage= new Image(getClass().getResource(playerCard.getImagePath()).toExternalForm());
        playerCardView.setImage(playerCardImage);
        playerCardView.setFitWidth(125);
        playerCardView.setFitHeight(185);
        playerCardView.setX(45);
        playerCardView.setY(500);

        Platform.runLater(() -> playerCardPane.getChildren().add(playerCardView));
        TranslateTransition playerCardTransition= new TranslateTransition();
        playerCardTransition.setDuration(Duration.seconds(1));
        playerCardTransition.setNode(playerCardView);
        playerCardTransition.setToY(-250 - nextCardPos);
        playerCardTransition.play();
        ++playerCardsPassed;
        nextCardPos += 50;


//        if(player.getHandVal() == dealer.getHandVal())
//        {
//            Platform.runLater(() ->  dealerLabel.setText("It's a draw!"));
//            System.out.println("player balance before update: " + player.getBalance());
//            System.out.println(player.getTotalRoundBet());
//            player.setBalance(player.getBalance() + player.getTotalRoundBet());
//            System.out.println("player balance after update: " + player.getBalance());
//            Platform.runLater(() -> balanceLabel.setText(balanceLabelText + player.getBalance()));
//            Platform.runLater(() -> previousGameLabel.setText(prevGameLabelText + "+" +player.getTotalRoundBet() ));
//
//            playDealerCardAnimation();
//            return;
//        }
        if (player.getHandVal() == 21) {
            Platform.runLater(() -> dealerLabel.setText("You won!"));
            //nowa zmiana
//            player.setBalance(player.getBalance() + player.getTotalRoundBet());
            player.setBalance(playerBalanceBeforeBetting + 2* player.getTotalRoundBet());
            // Platform.runLater(() -> balanceLabel.setText(balanceLabelText + player.getBalance()));
            Platform.runLater(() -> previousGameLabel.setText(prevGameLabelText + "+" + 2 * player.getTotalRoundBet()));
            Platform.runLater(() -> cardsValLabel.setText(cardValLabelText + totalPlayerCardsValue));
            playAgainButton.setVisible(true);
            playAgainButton.setDisable(false);

            endGame(1, 2.0);
            return;
        } else if (player.getHandVal() > 21 && player.hasAce()) {
            player.changeAceValues();
        }
        else if (player.getHandVal() > 21) {

            Platform.runLater(()-> dealerLabel.setText("You lost!"));
            Platform.runLater(() -> previousGameLabel.setText(prevGameLabelText + "-" +player.getTotalRoundBet() ));
            // Platform.runLater(() -> balanceLabel.setText(balanceLabelText+ (playerBalanceBeforeBetting - player.getTotalRoundBet())));
            //???
//            Platform.runLater(() -> balanceLabel.setText(balanceLabelText+ (playerBalanceBeforeBetting - player.getTotalRoundBet())));
            playAgainButton.setVisible(true);
            playAgainButton.setDisable(false);
            endGame(2, 0.0);
            return;
        } else
        {
            stayButton.setDisable(false);
            hitButton.setDisable(false);
            if( 2 * player.getTotalRoundBet() > player.getBalance())
            {
                dDownButton.setDisable(true);
            }
            else
            {
                dDownButton.setDisable(false);
            }
        }
        totalPlayerCardsValue = player.getHandVal();
        Platform.runLater(() -> cardsValLabel.setText(cardValLabelText + totalPlayerCardsValue));

    }

    private void dealDelaerCard()
    {
//        dealerFaceDownCardView = new ImageView();
        bjm.dealDealerCard();
        ImageView dealerCardView = new ImageView();
        int dealerCardsValue = dealer.getHandVal();
        if (dealerCardsValue > 21 && dealer.hasAce()) {
            dealer.changeAceValues();
        }
        dealerCardsValue = dealer.getHandVal();
        if(dealerCardsPassed == 0)
        {

            //initializing face_down_card
//            dealerFaceDownCard = dealer.getHand().get(dealerCardsPassed);
//            cardPathForTheExposure = dealerFaceDownCard.getImagePath();
//            dealerFaceDownCard.setImagePath("/com/example/kasyno/pngcards/facedown.png");
//            Image dealerFaceDownCardImage = new Image(getClass().getResource(dealerFaceDownCard.getImagePath()).toExternalForm());
//            dealerFaceDownCardView.setImage(dealerFaceDownCardImage);
//            dealerFaceDownCardView.setFitWidth(155);
//            dealerFaceDownCardView.setFitHeight(193);
//            dealerFaceDownCardView.setX(172);
//            dealerFaceDownCardView.setY(-150);
            dealerFaceDownCardView = getDealerFaceDownCardView();
            Platform.runLater(() -> dealerCardPane.getChildren().add(dealerFaceDownCardView));
            Platform.runLater(() -> dCardsValLabel.setText(dcardValLabelText + "?"));
        } else if (dealerCardsPassed == 1) {

            Card dealerRegularCard = dealer.getHand().get(dealerCardsPassed);
            if(dealerRegularCard.getValue() == 11 && player.getHandVal() != 21)
            {

                insuYesButton.setVisible(true);
                insuYesButton.setDisable(false);
                insuNoButton.setVisible(true);
                insuNoButton.setDisable(false);
                Platform.runLater(() -> dealerLabel.setText("Insuarance Bet? Pays 2:1"));
                hitButton.setDisable(true);
                dDownButton.setDisable(true);
                stayButton.setDisable(true);

            }
            Image dealerRegularCardImage = new Image(getClass().getResource(dealerRegularCard.getImagePath()).toExternalForm());
            dealerCardView.setImage(dealerRegularCardImage);
            dealerCardView.setFitWidth(120);
            dealerCardView.setFitHeight(180);
            dealerCardView.setX(152 + 160);
            dealerCardView.setY(-150);
            Platform.runLater(() -> dCardsValLabel.setText(dcardValLabelText + "?"));
        }
        else
        {

            Card dealerRegularCard = dealer.getHand().get(dealerCardsPassed);
            Image dealerRegularCardImage = new Image(getClass().getResource(dealerRegularCard.getImagePath()).toExternalForm());
            dealerCardView.setImage(dealerRegularCardImage);
            dealerCardView.setFitWidth(120);
            dealerCardView.setFitHeight(180);
            dealerCardView.setX(152 + 160 + nextCardPixelDraft);
            System.out.println(nextCardPixelDraft);
            dealerCardView.setY(-150);
            int finalDealerCardsValue = dealerCardsValue;
            Platform.runLater(() -> dCardsValLabel.setText(dcardValLabelText + finalDealerCardsValue));
            nextCardPixelDraft += 50;
        }

        Platform.runLater(() -> dealerCardPane.getChildren().add(dealerCardView));

        TranslateTransition faceDownTransition = new TranslateTransition();
        faceDownTransition.setDuration(Duration.seconds(1));
        faceDownTransition.setNode(dealerFaceDownCardView);
        faceDownTransition.setToY(210);
        faceDownTransition.play();

        Thread waitForDealingCards = new Thread(() -> {
            if (dealerCardsPassed > 2) {

                try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            TranslateTransition regularDealerCardTransition = new TranslateTransition();
            regularDealerCardTransition.setDuration(Duration.seconds(1));
            regularDealerCardTransition.setNode(dealerCardView);
            regularDealerCardTransition.setToY(210);
            regularDealerCardTransition.play();
        });
        waitForDealingCards.start();
        ++dealerCardsPassed;

    }

    private ImageView getDealerFaceDownCardView()
    {

        dealerFaceDownCardView = new ImageView();
        dealerFaceDownCard = dealer.getHand().get(dealerCardsPassed);
        cardPathForTheExposure = dealerFaceDownCard.getImagePath();
        dealerFaceDownCard.setImagePath("/Images/pngcards/facedown.png");
        Image dealerFaceDownCardImage = new Image(getClass().getResource(dealerFaceDownCard.getImagePath()).toExternalForm());
        dealerFaceDownCardView.setImage(dealerFaceDownCardImage);
        dealerFaceDownCardView.setFitWidth(155);
        dealerFaceDownCardView.setFitHeight(193);
        dealerFaceDownCardView.setX(172);
        dealerFaceDownCardView.setY(-150);
        return dealerFaceDownCardView;
    }
    private void scaleDealerCard(ImageView dealerCardView)
    {
        double dealerCardScaleAmount = 0.4;
        double currDealerCardWidth = dealerCardView.getFitWidth();
        double currDealerCardHeight = dealerCardView.getFitHeight();

        dealerCardView.setFitWidth(currDealerCardWidth*dealerCardScaleAmount);
        dealerCardView.setFitWidth(currDealerCardHeight*dealerCardScaleAmount);
        dealerCardView.setX(320);
        dealerCardView.setY(50);
    }

    public void showBegginingLabel()
    {

        Thread begginingGameThread= new Thread(() -> {
            try {

                betButton.setDisable(true);
                stayButton.setDisable(true);
                hitButton.setDisable(true);
                dDownButton.setDisable(true);
                Platform.runLater(() -> dealerLabel.setText("The game has begun..."));
                Thread.sleep(1000);
                Platform.runLater(() -> dealerLabel.setText("Place your bets!"));
                betButton.setDisable(false);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        begginingGameThread.start();
    }

    private void hitPlayerCard() {
        dealCard();

        if (player.getHandVal() == 21) {
            dealerLabel.setText("You won!");
            player.setBalance(playerBalanceBeforeBetting +  player.getTotalRoundBet());
            // balanceLabel.setText(balanceLabelText + player.getBalance());
            previousGameLabel.setText(prevGameLabelText + "+" + 2 * player.getTotalRoundBet() );
            playAgainButton.setVisible(true);
            playAgainButton.setDisable(false);
            endGame(1, 2.0);

        } else if (player.getHandVal() > 21 && player.hasAce()) {
            player.changeAceValues();
            totalPlayerCardsValue = player.getHandVal();
            cardsValLabel.setText(cardValLabelText + totalPlayerCardsValue);
            if(player.getHandVal() == 21)
            {
                dealerLabel.setText("BlackJack! You won!");
                playAgainButton.setVisible(true);
                playAgainButton.setDisable(false);
            }

        }
        else if(player.getHandVal() > 21)
        {
            player.setHandVal(0);
            player.getHand().clear();
            dealerLabel.setText("You are busted!");
            hitButton.setDisable(true);
            playAgainButton.setVisible(true);
            playAgainButton.setDisable(false);
        }
        dDownButton.setDisable(true);
    }

    public Label getDealerLabel() {
        return dealerLabel;
    }

}