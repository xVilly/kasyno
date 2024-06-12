package com.casino.Logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoulettePlayer {
    private String name;
    private List<Bet> betRouletteNumbers;
    private int balance;
    private int betValue;
    private int totalRoundBet;



    private int latestWinnings;

    public RoulettePlayer(String name) {
        this.name = name;
        this.betRouletteNumbers= new ArrayList<>();
        //balance zmienic w zaleznosci od logiki co michal czmyknal
        this.balance = 1000; //$
    }

    public List<Bet> getBetRouletteNumbers() {
        return betRouletteNumbers;
    }

    public void setBetRouletteNumbers(List<Bet> betRouletteNumbers) {
        this.betRouletteNumbers = betRouletteNumbers;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBetValue() {
        return betValue;
    }

    public void setBetValue(int betValue) {
        this.betValue = betValue;
    }

    public int getTotalRoundBet() {
        return totalRoundBet;
    }

    public void setTotalRoundBet(int totalRoundBet) {
        this.totalRoundBet = totalRoundBet;
    }

    public int getLatestWinnings() {
        return latestWinnings;
    }

    public void setLatestWinnings(int latestWinnings) {
        this.latestWinnings = latestWinnings;
    }
    public void printBetsAndNumbers()
    {
        int i =0;
        for(Bet b : betRouletteNumbers)
        {
            System.out.println("List number " + i);
            for( int bettedNumbers : b.getNumbers())
            {
                System.out.println(bettedNumbers);
            }
            i++;
        }
    }
}
