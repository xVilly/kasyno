package com.casino.Logic;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private int handValue;
    private int balance;
    private int betValue;
    private int cardsPassed;
    private int totalRoundBet;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.balance = 1000; //$
        this.handValue = 0;
    }
    public void bet(int betVal)
    {
        betValue = betVal;
        balance -= betValue;
        totalRoundBet+= betValue;
    }
    public void doubleDown()
    {
        totalRoundBet = totalRoundBet*2;
    }
    public void insuaranceBet()
    {
       balance-= 2*betValue;
       totalRoundBet += betValue;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getHandVal() {
        return handValue;
    }

    public void setHandVal(int newHandVal) {
        handValue = newHandVal;
    }
    public void addCardSum(int howMuch) {
        handValue += howMuch;
    }

    public int getBetValue() {
        return betValue;
    }

    public void setBetValue(int betValue) {
        this.betValue = betValue;
    }

    public int getBalance() {
        return balance;
    }
    public int getHandSize()
    {
        return hand.size();
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCardsPassed() {
        return cardsPassed;
    }

    public void setCardsPassed(int cardsPassed) {
        this.cardsPassed = cardsPassed;
    }

    public int getTotalRoundBet() {
        return totalRoundBet;
    }

    public void setTotalRoundBet(int totalRoundBet) {
        this.totalRoundBet = totalRoundBet;
    }

    public boolean hasAce()
    {
        for (Card card: hand)
        {
            if( card.getValue() == 11)
            {
                return true;
            }
        }
        return false;
    }
    public void changeAceValues()
    {
//        int newCardVals = 0;
        for(Card card: hand)
        {
            if( card.getValue() == 11 && handValue > 21)
            {
                card.setValue(1);
                handValue-=10;
            }
        }
//        for(Card card : hand)
//        {
//            newCardVals += card.getValue();
//        }
//        handValue = newCardVals;
    }
    public void reset()
    {
        hand.clear();
        handValue = 0;
        cardsPassed = 0;
    }

    public void printHand()
    {
        System.out.println("Your current hand is: ");
        for(Card card : hand)
        {
            System.out.println(card.getName() );
        }
        System.out.println("Hand value: " + getHandVal());
    }

}
