package com.liquidpixel.example.services;

import com.liquidpixel.example.api.services.IGameService;
import com.liquidpixel.example.managers.GameManager;

public class GameService implements IGameService {

    private GameManager gameManager;

    public GameService(GameManager gameManager) {
        this.gameManager = gameManager;
    }

}
