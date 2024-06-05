package com.example.game.structures;

import com.example.game.GameContext;

public class StartGameResult {
    public boolean success;

    public int errorCode;
    public String errorMessage;

    public GameContext game;

    public StartGameResult(int errorCode, String errorMessage) {
        this.success = false;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.game = null;
    }

    public StartGameResult(GameContext game) {
        this.success = true;
        this.errorCode = 0;
        this.errorMessage = "Game started successfully";
        this.game = game;
    }
}
