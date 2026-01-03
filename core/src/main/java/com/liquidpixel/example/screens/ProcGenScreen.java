
package com.liquidpixel.example.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.liquidpixel.core.api.ICameraManager;
import com.liquidpixel.core.api.IPlayerInputManager;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.example.api.managers.IGameManager;
import com.liquidpixel.example.components.PositionComponent;
import com.liquidpixel.example.components.RenderComponent;
import com.liquidpixel.example.components.StatusComponent;
import com.liquidpixel.example.factories.ComponentFactory;
import com.liquidpixel.example.managers.CameraManager;
import com.liquidpixel.example.managers.GameManager;
import com.liquidpixel.example.managers.PlayerInputManager;
import com.liquidpixel.example.services.MapService;
import com.liquidpixel.example.services.SimpleUIService;
import com.liquidpixel.example.systems.CameraRouteSystem;
import com.liquidpixel.example.systems.RenderSystem;
import com.liquidpixel.example.systems.spectral.CameraFocusSystem;
import com.liquidpixel.example.systems.spectral.SpectralCameraSystem;
import com.liquidpixel.example.systems.spectral.SpectralMovementSystem;
import com.liquidpixel.example.ui.LoadingUI;
import com.liquidpixel.pool.api.ITaskManager;
import com.liquidpixel.procgen.api.IChunkGenerationListener;
import com.liquidpixel.procgen.config.MapConfiguration;
import com.liquidpixel.procgen.models.ChunkData;
import com.liquidpixel.sprite.SpriteAnimationModule;
import com.liquidpixel.sprite.api.ISpriteAnimationModule;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;
import com.liquidpixel.sprite.model.GameSprite;
import com.liquidpixel.sprite.registry.SpriteItemRegistry;

public class ProcGenScreen implements Screen, IChunkGenerationListener {

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
    private boolean loading = true;
    private LoadingUI loadingUI;

    private MapConfiguration config;

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
        initializeLoadingUI();
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

    private void initializeLoadingUI() {
//        loadingUI = new LoadingUI();
//        stage.addActor(loadingUI);
    }

    private void initializeModules() {
        spriteModule = new SpriteAnimationModule(
            "models/sprites/atlas.json",
            null,
            "models/sprites/animations.json"
        );

        spriteFactory = spriteModule.getSpriteFactory();
        // Load sprite items for layer resolution
//        SpriteItemRegistry registry = spriteModule.loadSpriteItems("models/item/items.json", "models/item/slots.json");
//        spriteModule.setComponentFactory(new ComponentFactory());


        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                createBackground(x, y);
            }
        }

        loading = false;
//        taskManager = new TaskManager();

//        config = new MapConfiguration(256, 256, 32, 32);
//
//        mapService = new MapService(
//            taskManager,
//            spriteFactory,
//            config
//        );
//
//        mapService.init(this);

    }

    private void createBackground(int x, int y) {

        GameSprite sprite = spriteFactory.getSprite("chunk-" + x + "-" + y);
        if (sprite != null) {
            RenderComponent renderComponent = new RenderComponent();
            renderComponent.setSprite(sprite);
            Entity chunk = new Entity();
            chunk.add(new PositionComponent(64 * x, 64 * y));
            chunk.add(renderComponent);
            chunk.add(new StatusComponent());
            engine.addEntity(chunk);

        }
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
        engine.addSystem(new CameraRouteSystem());
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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (loading) {
            // Only render loading screen
            stage.act(delta);
            stage.draw();
//            taskManager.tick(delta);
        } else {
            // Render the actual game
            cameraManager.render(delta, resources.getBatch());
            resources.getBatch().begin();
            engine.update(delta);
//            mapService.getRenderer().render(delta);
            resources.getBatch().end();
//            taskManager.tick(delta);
        }
    }

    @Override
    public void onChunkGenerated(ChunkData chunk) {
        // Optional: could update UI with chunk count here
    }

    @Override
    public void onGenerationProgress(float progress) {
        int total = config.getChunkCountX() * config.getChunkCountY();
        int current = (int) (progress * total);
        loadingUI.updateProgress(progress, current, total);
    }

    @Override
    public void onGenerationComplete() {
        Gdx.app.log("ProcGenScreen", "World generation complete!");
        loading = false;
        loadingUI.remove(); // Remove loading UI from stage
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
