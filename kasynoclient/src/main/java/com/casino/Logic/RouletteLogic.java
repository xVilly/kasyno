package com.casino.Logic;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteLogic {

    private final List<RouletteNumber> rouletteNumbers = new ArrayList<>();
    private final RoulettePlayer roulettePlayer = new RoulettePlayer("Player1");
    public RouletteLogic()
    {
       initializeRouletteNumbers();
       calculateAngles();
    }

    private void initializeRouletteNumbers() {
        rouletteNumbers.add(new RouletteNumber(0, "green"));
        int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        int[] blackNumbers = {2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35};

        for (int num : redNumbers) {
            rouletteNumbers.add(new RouletteNumber(num, "red"));
        }

        for (int num : blackNumbers) {
            rouletteNumbers.add(new RouletteNumber(num, "black"));
        }
        rouletteNumbers.sort((a, b) -> Integer.compare(a.getNumber(), b.getNumber()));

    }
    public int rollNumber()
    {
        Random random = new Random();
        return random.nextInt(37);
    }
    private void calculateAngles()
    {
        int[] numsOnWheel = {0,32,15,19,4,21,2,25,17,34,6,27,13,36,11,30,8,23,10,5,24,16,33,1,20,14,31,9,22,18,29,7,28,12,35,3,26};
        double angle = 0;
        for (int i = 9; i < numsOnWheel.length; i++) {
            rouletteNumbers.get(numsOnWheel[i]).setAngle(angle);
            // 360 divided by 37 numbers on wheel equals 9.72...
            //starting loop from 9 because at default wheel position numsOnWheel[9]=34 is on 0 angle postion
            angle -= 9.72972972972973;
        }
        for (int i =0; i < 8; i++)
        {
            rouletteNumbers.get(numsOnWheel[i]).setAngle(angle);
            angle -= 9.72972972972973;
        }

    }
    public void checkIfPlayerWins(int rolledNumber)
    {
        int totalWinnings = 0;
        if(rolledNumber == 0 )
        {
            for(Bet bet : roulettePlayer.getBetRouletteNumbers())
            {
                if(bet.getNumbers().contains(0))
                {
                    totalWinnings += bet.getAmount() * 35;
                }
            }
        }
        else
        {
            roulettePlayer.printBetsAndNumbers();
            for(Bet bet : roulettePlayer.getBetRouletteNumbers()) {

                if (bet.getNumbers().contains(rolledNumber) && bet.getNumbers().size() == 1) {

                    totalWinnings += bet.getAmount() * 35;

                } else if (bet.getNumbers().contains(rolledNumber) && bet.getNumbers().size() == 12) {

                    totalWinnings += bet.getAmount() * 3;
                } else if (bet.getNumbers().contains(rolledNumber)) {

                    totalWinnings += bet.getAmount() * 2;
                }
            }
        }
        //ZMIANA BALANSU - dodanie ewentualnych wygranych
        roulettePlayer.setBalance(roulettePlayer.getBalance() + totalWinnings);
        roulettePlayer.setLatestWinnings(totalWinnings);
        System.out.println("NEW PLAYER BALANCE");
        System.out.println(roulettePlayer.getBalance());
    }

    public List<RouletteNumber> getRouletteNumbers() {
        return rouletteNumbers;
    }

    public RoulettePlayer getRoulettePlayer() {
        return roulettePlayer;
    }
}
