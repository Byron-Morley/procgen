package com.liquidpixel.main.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.liquidpixel.core.api.ICameraManager;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.main.api.managers.IGameManager;
import com.liquidpixel.main.managers.CameraManager;
import com.liquidpixel.main.managers.GameManager;
import com.liquidpixel.main.services.MapService;
import com.liquidpixel.pool.api.ITaskManager;
import com.liquidpixel.pool.managers.TaskManager;
import com.liquidpixel.procgen.config.MapConfiguration;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.factory.SpriteFactory;

public class ProcGenScreen implements Screen {

    // Core components
    private GameResources resources;
    private Engine engine;
    private Stage stage;


    ITaskManager taskManager;
    ISpriteFactory spriteFactory;
    MapService mapService;
    // Managers
    private ICameraManager cameraManager;
    private IGameManager gameManager;

    private boolean initialized = false;

    public ProcGenScreen(GameResources resources) {
        this.resources = resources;
        this.engine = resources.getEngine();
        initialize();
        start();
    }

    private void initialize() {
        initializeStage();
        initializeModules();
        initializeFactories();
        initializeManagers();
        initializeSystems();
    }

    public void initializeStage() {
        ScreenViewport viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
        GameResources.get().setStage(stage);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeModules() {

        taskManager = new TaskManager();
        spriteFactory = new SpriteFactory();

        MapConfiguration mapConfiguration = new MapConfiguration(
            128, 128, 16, 16, 8, 8
        );

        mapService = new MapService(
            taskManager,
            spriteFactory,
            mapConfiguration
        );

        mapService.init();

    }

    public void initializeFactories() {
    }

    public void initializeManagers() {
        cameraManager = new CameraManager();
//        playerInputManager = new PlayerInputManager();
        gameManager = new GameManager();
    }

    public void initializeServices() {

    }

    public void initializeListeners() {

    }

    public void initializeSystems() {
        Engine engine = resources.getEngine();
    }

    public void start() {
        if (!initialized) {
            initialized = true;
        }
    }

    @Override
    public void render(float delta) {
        cameraManager.render(delta, resources.getBatch());
        resources.getBatch().begin();
//        mapService.getRenderer().render(delta);
        engine.update(delta);
        resources.getBatch().end();

        taskManager.tick(delta);
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
