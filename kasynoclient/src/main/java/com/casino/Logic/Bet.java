package com.casino.Logic;
import java.util.List;

public class Bet {
    private int amount;
    private List<Integer> betNumbers;

    public Bet(int amount, List<Integer> betNumbers) {
        this.amount = amount;
        this.betNumbers = betNumbers;
    }

    public int getAmount() {
        return amount;
    }

    public List<Integer> getNumbers() {
        return betNumbers;
    }
}