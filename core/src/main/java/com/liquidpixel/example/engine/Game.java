package com.liquidpixel.example.engine;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.liquidpixel.core.core.ScreenManager;
import com.kotcrab.vis.ui.VisUI;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.core.engine.GameState;
import com.liquidpixel.example.screens.ProcGenScreen;

public class Game extends ApplicationAdapter {
    private Engine engine;
    private SpriteBatch batch;
    private ScreenManager screenManager;
    GLProfiler glProfiler;
    ShapeRenderer shapeRenderer;
    static AssetManager assetManager;

    @Override
    public void create() {
        assetManager = new AssetManager();
        engine = new Engine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        loadUISkin();
        initializeScreen();
        glProfiler = new GLProfiler(Gdx.graphics);
        glProfiler.enable();

    }

    private static void loadUISkin() {

        String skinPath = "ui/skin/golden-gate/skin";

        assetManager.load(skinPath + ".atlas", TextureAtlas.class);
        assetManager.finishLoading();


        assetManager.load(skinPath + ".json", Skin.class, new SkinLoader.SkinParameter(skinPath + ".atlas"));
        assetManager.finishLoading();


//        assetManager.load("sprites/test/green_fish.png", Texture.class);
//        assetManager.finishLoading();


        Skin skin = assetManager.get(skinPath + ".json", Skin.class);

        VisUI.load(skin);
    }


    private void initializeScreen() {
        screenManager = new ScreenManager();
        screenManager.setCurrentScreen(new ProcGenScreen(new GameResources(engine, batch, shapeRenderer)));
    }

    @Override
    public void render() {
        if (!GameState.isPaused()) clearScreen();

        screenManager.render(Gdx.graphics.getDeltaTime());



        if (Gdx.input.isKeyPressed(Input.Keys.F5)) {
            this.dispose();
            create();
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        glProfiler.reset();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().resize(width, height);
    }

    @Override
    public void dispose() {
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().dispose();
        VisUI.dispose();
//        batch.dispose();
//        shapeRenderer.dispose();
        super.dispose();
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }
}
