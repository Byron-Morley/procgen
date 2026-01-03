package com.liquidpixel.example.managers;

import com.badlogic.ashley.core.Engine;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.example.api.managers.IGameManager;
import com.liquidpixel.example.api.services.IGameService;
import com.liquidpixel.example.services.GameService;

public class GameManager implements IGameManager {
    IGameService gameService;
    Engine engine;

    public GameManager() {
        gameService = new GameService(this);
        engine = GameResources.get().getEngine();
        init();
    }

    private void init() {




    }

    @Override
    public IGameService getGameService() {
        return gameService;
    }
}
