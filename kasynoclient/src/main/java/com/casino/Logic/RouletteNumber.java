package com.casino.Logic;

public class RouletteNumber {
    private String  colorOfNumber;
    private int number;
    private double angle;

    public RouletteNumber(int number, String colorOfNumber) {
        this.number = number;
        this.colorOfNumber = colorOfNumber;
    }

    public String getColorOfNumber() {
        return colorOfNumber;
    }

    public void setColorOfNumber(String colorOfNumber) {
        this.colorOfNumber = colorOfNumber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }
}
