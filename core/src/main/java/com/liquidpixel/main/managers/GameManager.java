package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Engine;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.main.api.managers.IGameManager;
import com.liquidpixel.main.api.services.IGameService;
import com.liquidpixel.main.services.GameService;

public class GameManager implements IGameManager {
    IGameService gameService;
    Engine engine;

    public GameManager() {
        gameService = new GameService(this);
        engine = GameResources.get().getEngine();
    }

    @Override
    public IGameService getGameService() {
        return gameService;
    }
}
