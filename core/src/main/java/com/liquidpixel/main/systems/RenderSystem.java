package com.liquidpixel.main.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.core.utils.Dimensions;
import com.liquidpixel.main.components.PositionComponent;
import com.liquidpixel.main.components.RenderComponent;
import com.liquidpixel.sprite.model.GameSprite;

import java.util.List;

public class RenderSystem extends IteratingSystem {
    private SpriteBatch batch;

    public RenderSystem() {
        super(Family.all(RenderComponent.class, PositionComponent.class).get());
        this.batch = GameResources.get().getBatch();
    }

       @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        RenderComponent renderComponent = entity.getComponent(RenderComponent.class);
        List<GameSprite> sprites = renderComponent.getSprites();
        OrthographicCamera camera = GameResources.get().getCamera();

        if (sprites.isEmpty()) {
            Gdx.app.debug("BatchedRenderSystem", "No sprites found for entity!");
            return;
        }

        for (GameSprite sprite : sprites) {
            Vector2 pos = Vector2.Zero;

            float x = pos.x - Dimensions.toMeters(sprite.getOriginX());
            float y = pos.y - Dimensions.toMeters(sprite.getOriginY());
            float width = Dimensions.toMeters(sprite.getRegionWidth());
            float height = Dimensions.toMeters(sprite.getRegionHeight());

            // Check if sprite has valid texture
            if (sprite.getTexture() == null) {
                Gdx.app.error("BatchedRenderSystem", "Sprite has NULL texture!");
                continue;
            }

            // Frustum culling check
            float margin = 2.5f;
            boolean inFrustum = camera.frustum.boundsInFrustum(
                x - width * margin, y - height * margin, 0,
                width * (margin * 2), height * (margin * 2), 0);

            batch.draw(sprite, x, y,
                sprite.getOriginX(), sprite.getOriginY(),
                width, height,
                sprite.getScaleX(), sprite.getScaleY(),
                sprite.getRotation()
            );
        }
    }
}
