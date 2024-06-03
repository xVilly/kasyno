package com.casino.Controllers;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class RouletteController implements IController {

    @FXML
    private ImageView wheelImView = new ImageView();


    @FXML
    public void initialize() {

        Circle clip = new Circle(250,250,250);
        wheelImView.setClip(clip);
//        wheelImView.setX(50);
//        wheelImView.setY(50);
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(wheelImView);
        rotateTransition.setDuration(Duration.seconds(2));
        rotateTransition.setByAngle(720);
        rotateTransition.play();
    }

    public void onActivate() {
    }
}
