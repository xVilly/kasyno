package com.casino.Logic;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

public class BlackJackManager {

    private final List<Card> deck = new ArrayList<>();
    private Player player = new Player("Player1");
    private Player dealer = new Player("Dealer");
    private static final int NUMBER_OF_DECKS = 6;

    public BlackJackManager()
    {
        clearCards();
        initCards();
        shuffleCards();
    }

    private void clearCards() {
        deck.clear();
    }

    private void initCards()
    {
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

        for (int i = 0; i < NUMBER_OF_DECKS ; i++) {
            for (String suit : suits) {
                for (String rank : ranks) {
                    int cardValue;
                    if (rank.equals("jack") || rank.equals("queen") || rank.equals("king")) {
                        cardValue = 10;
                    } else if (rank.equals("ace")) {
                        cardValue = 11;
                    } else {
                        cardValue = Integer.parseInt(rank);
                    }
                    String cardName = rank + "_of_" + suit;
                    Card card = new Card();
                    card.setName(cardName);
                    card.setValue(cardValue);
                    deck.add(card);
                }
            }
        }
    }

    public void printDeck()
    {
        for (Card card : deck) {
            System.out.println(card.getName() + " " + card.getValue());
        }
    }

    public void shuffleCards()
    {
        Collections.shuffle(deck);
    }

    public void dealCard()
    {


        //part for blackjack testing
//        int playerPassedCardsCount = player.getCardsPassed();
//        if(playerPassedCardsCount == 0) {
////        Card playerGivenCard = deck.get(0);
//            Card playerGivenCard = new Card();
//            playerGivenCard.setValue(11);
//
////        deck.remove(playerGivenCard);
//
////        String imagePath = "/com/example/kasyno/pngcards/" + playerGivenCard.getName() + ".png";
//            String imagePath = "/com/example/kasyno/pngcards/" + "ace_of_spades.png";
//            playerGivenCard.setImagePath(imagePath);
//            player.addToHand(playerGivenCard);
//            player.addCardSum(playerGivenCard.getValue());
//        } else if (playerPassedCardsCount == 1) {
//
////        Card playerGivenCard = deck.get(0);
//            Card playerGivenCard = new Card();
//            playerGivenCard.setValue(10);
//
////        deck.remove(playerGivenCard);
//
////        String imagePath = "/com/example/kasyno/pngcards/" + playerGivenCard.getName() + ".png";
//            String imagePath = "/com/example/kasyno/pngcards/" + "10_of_spades.png";
//            playerGivenCard.setImagePath(imagePath);
//            player.addToHand(playerGivenCard);
//            player.addCardSum(playerGivenCard.getValue());
//        }
        //ends here

        int playerPassedCardsCount = player.getCardsPassed();
        Card playerGivenCard = deck.get(0);
        deck.remove(playerGivenCard);
        String imagePath = "/Images/pngcards/" + playerGivenCard.getName() + ".png";
        playerGivenCard.setImagePath(imagePath);
        player.addToHand(playerGivenCard);
        player.addCardSum(playerGivenCard.getValue());

//        System.out.println(player.getHand().get(playerPassedCardsCount).getName());

        playerPassedCardsCount++;
        player.setCardsPassed(playerPassedCardsCount);

    }

    public void dealDealerCard()
    {
        int dealerPassedCardsCount = dealer.getCardsPassed();
        //uncomment from here
        Card dealerGivenCard = deck.get(0);
        deck.remove(dealerGivenCard);

        String dealerImagePath = "/Images/pngcards/" + dealerGivenCard.getName() + ".png";
        dealerGivenCard.setImagePath(dealerImagePath);
        dealer.addToHand(dealerGivenCard);
        dealer.addCardSum(dealerGivenCard.getValue());
        //to there
        // repair ace checking for dealer

//        if(dealerPassedCardsCount == 1) {
//            Card dealerGivenCard = new Card();
//            dealerGivenCard.setValue(11);
//
//            String imagePath = "/com/example/kasyno/pngcards/" + "ace_of_spades.png";
//            dealerGivenCard.setImagePath(imagePath);
//            dealer.addToHand(dealerGivenCard);
//            dealer.addCardSum(dealerGivenCard.getValue());
//
//        }
//        else if (dealerPassedCardsCount== 0) {
//
//
////            Card dealerGivenCard = deck.get(0);
////            deck.remove(dealerGivenCard);
////
////            dealerGivenCard.setValue(2);
////            String dealerImagePath = "/com/example/kasyno/pngcards/" +  "2_of_hearts.png";
////            dealerGivenCard.setImagePath(dealerImagePath);
////            dealer.addToHand(dealerGivenCard);
////            dealer.addCardSum(dealerGivenCard.getValue());
//
//            Card dealerGivenCard= new Card();
//            dealerGivenCard.setValue(10);
//
//            String imagePath = "/com/example/kasyno/pngcards/" + "10_of_spades.png";
//            dealerGivenCard.setImagePath(imagePath);
//            dealer.addToHand(dealerGivenCard);
//            dealer.addCardSum(dealerGivenCard.getValue());
//        }
//        else
//        {
//            Card dealerGivenCard = deck.get(0);
//            System.out.println("given card from deck " + dealerGivenCard.getName() + " " + dealerGivenCard.getValue() );
//            deck.remove(dealerGivenCard);
//
//            String dealerImagePath = "/com/example/kasyno/pngcards/" + dealerGivenCard.getName() + ".png";
//            dealerGivenCard.setImagePath(dealerImagePath);
//            dealer.addToHand(dealerGivenCard);
//            dealer.addCardSum(dealerGivenCard.getValue());
//
//        }
//

//        System.out.println(dealer.getHand().get(dealerPassedCardsCount).getName());
        System.out.println(dealerPassedCardsCount);
        dealerPassedCardsCount++;
        dealer.setCardsPassed(dealerPassedCardsCount);
    }


    public List<Card> getDeck() {
        return deck;
    }

    public Player getDealer() {
        return dealer;
    }

    public Player getPlayer() {
        return player;
    }
}
