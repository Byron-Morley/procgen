package com.liquidpixel.main.services;

import com.liquidpixel.main.api.services.IGameService;
import com.liquidpixel.main.managers.GameManager;

public class GameService implements IGameService {

    private GameManager gameManager;

    public GameService(GameManager gameManager) {
        this.gameManager = gameManager;
    }

}
