package com.liquidpixel.main.components;

import com.liquidpixel.sprite.api.component.IRenderComponent;
import com.liquidpixel.sprite.model.GameSprite;

import java.util.ArrayList;
import java.util.List;

public class RenderComponent implements IRenderComponent {

    List<GameSprite> sprites;

    public RenderComponent() {
        this.sprites = new ArrayList<>();
//        Texture texture = new Texture(Gdx.files.internal("sprites/test/green_fish.png"));
//        GameSprite gameSprite = new GameSprite(new TextureRegion(texture), "fish");
//        sprites.add(gameSprite);
    }

    @Override
    public void setSprite(GameSprite sprite) {
        this.sprites.add(sprite);
    }

    @Override
    public void clear() {
        sprites.clear();
    }

    @Override
    public void add(GameSprite sprite) {
        this.sprites.add(sprite);
    }

    public List<GameSprite> getSprites() {
        return sprites;
    }
}
