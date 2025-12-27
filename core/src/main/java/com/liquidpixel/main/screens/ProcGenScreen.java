package com.liquidpixel.main.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.liquidpixel.core.api.ICameraManager;
import com.liquidpixel.core.api.IPlayerInputManager;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.main.api.managers.IGameManager;
import com.liquidpixel.main.factories.ComponentFactory;
import com.liquidpixel.main.managers.CameraManager;
import com.liquidpixel.main.managers.GameManager;
import com.liquidpixel.main.managers.PlayerInputManager;
import com.liquidpixel.main.services.MapService;
import com.liquidpixel.main.services.SimpleUIService;
import com.liquidpixel.main.systems.RenderSystem;
import com.liquidpixel.main.systems.spectral.CameraFocusSystem;
import com.liquidpixel.main.systems.spectral.SpectralCameraSystem;
import com.liquidpixel.main.systems.spectral.SpectralMovementSystem;
import com.liquidpixel.pool.api.ITaskManager;
import com.liquidpixel.pool.managers.TaskManager;
import com.liquidpixel.procgen.config.MapConfiguration;
import com.liquidpixel.sprite.SpriteAnimationModule;
import com.liquidpixel.sprite.api.ISpriteAnimationModule;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.registry.SpriteItemRegistry;

public class ProcGenScreen implements Screen {

    // Core components
    private GameResources resources;
    private Engine engine;
    private Stage stage;

    //    MODULES
    private ISpriteAnimationModule spriteModule;

    //    FACTORIES
    private ISpriteFactory spriteFactory;
    private IPlayerInputManager playerInputManager;
    ITaskManager taskManager;
    MapService mapService;
    // Managers
    private ICameraManager cameraManager;
    private IGameManager gameManager;

    InputMultiplexer inputMultiplexer;

    private boolean initialized = false;

    // Mock services
    private SimpleUIService uiService;

    public ProcGenScreen(GameResources resources) {
        this.resources = resources;
        this.engine = resources.getEngine();
        initialize();
        start();
    }

    private void initialize() {
        initializeStage();
        initializeServices();
        initializeModules();
        initializeManagers();
        initializeFactories();
        initializeListeners();
        initializeSystems();
    }

    public void initializeStage() {
        ScreenViewport viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
        GameResources.get().setStage(stage);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeModules() {
        spriteModule = new SpriteAnimationModule(
            "models/sprites/atlas.json",
            null,
            "models/sprites/animations.json"
        );

        spriteFactory = spriteModule.getSpriteFactory();

        // Load sprite items for layer resolution
        SpriteItemRegistry registry = spriteModule.loadSpriteItems("models/item/items.json", "models/item/slots.json");
        spriteModule.setComponentFactory(new ComponentFactory());

        taskManager = new TaskManager();

        MapConfiguration mapConfiguration = new MapConfiguration(128, 128, 16, 16);

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
        gameManager = new GameManager();
        playerInputManager = new PlayerInputManager(uiService);
    }

    public void initializeServices() {
        uiService = new SimpleUIService();
    }

    private void initializeListeners() {
        this.stage.addListener((EventListener) playerInputManager);
    }

    public void initializeSystems() {
        engine.addSystem(new SpectralCameraSystem(playerInputManager.getPlayerInputService(), cameraManager.getCameraService(), uiService));
        engine.addSystem(new SpectralMovementSystem());
        engine.addSystem(new CameraFocusSystem(cameraManager.getCameraService()));
        engine.addSystem(new RenderSystem());
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
        engine.update(delta);
        mapService.getRenderer().render(delta);
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
