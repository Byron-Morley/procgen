package com.liquidpixel.example.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LoadingUI extends Table {
    private final ProgressBar progressBar;
    private final Label statusLabel;
    private final Label percentLabel;

    public LoadingUI() {
        // Create a simple skin for the progress bar
        Skin skin = createSimpleSkin();

        // Create UI elements
        Label titleLabel = new Label("Generating World...", skin);
        titleLabel.setFontScale(2f);

        progressBar = new ProgressBar(0f, 1f, 0.01f, false, skin);
        progressBar.setAnimateDuration(0.1f);

        statusLabel = new Label("Initializing...", skin);
        percentLabel = new Label("0%", skin);
        percentLabel.setFontScale(1.5f);

        // Layout
        setFillParent(true);
        add(titleLabel).padBottom(30).row();
        add(percentLabel).padBottom(10).row();
        add(progressBar).width(400).height(30).padBottom(20).row();
        add(statusLabel).row();
    }

    public void updateProgress(float progress, int current, int total) {
        progressBar.setValue(progress);
        percentLabel.setText(String.format("%.0f%%", progress * 100));
        statusLabel.setText(String.format("Generated %d / %d chunks", current, total));
    }

    private Skin createSimpleSkin() {
        Skin skin = new Skin();

        // Create a simple font
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.2f);
        skin.add("default", font);

        // Create label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Create progress bar style
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = createDrawable(100, 10, new Color(0.3f, 0.3f, 0.3f, 1));
        progressBarStyle.knobBefore = createDrawable(100, 10, new Color(0.2f, 0.8f, 0.2f, 1));
        progressBarStyle.knob = createDrawable(0, 10, new Color(0.2f, 0.8f, 0.2f, 1));
        skin.add("default-horizontal", progressBarStyle);

        return skin;
    }

    private Drawable createDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(Math.max(1, width), Math.max(1, height), Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
