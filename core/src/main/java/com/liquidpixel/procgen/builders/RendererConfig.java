package com.liquidpixel.procgen.builders;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.liquidpixel.procgen.models.TilesetConfig;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

/**
 * Configuration for renderer creation.
 */
public class RendererConfig {
    private final SpriteBatch batch;
    private final ISpriteFactory spriteFactory;
    private final int cullingRadius;
    private final OrthographicCamera camera;

    private RendererConfig(Builder builder) {
        this.batch = builder.batch;
        this.spriteFactory = builder.spriteFactory;
        this.cullingRadius = builder.cullingRadius;
        this.camera = builder.camera;
    }

    public SpriteBatch getBatch() { return batch; }
    public ISpriteFactory getSpriteFactory() { return spriteFactory; }
    public int getCullingRadius() { return cullingRadius; }
    public OrthographicCamera getCamera() { return camera; }

    public static class Builder {
        private SpriteBatch batch;
        private ISpriteFactory spriteFactory;
        private int cullingRadius = 8;
        private OrthographicCamera camera;

        public Builder batch(SpriteBatch batch) {
            this.batch = batch;
            return this;
        }

        public Builder spriteFactory(ISpriteFactory spriteFactory) {
            this.spriteFactory = spriteFactory;
            return this;
        }

        public Builder cullingRadius(int radius) {
            this.cullingRadius = radius;
            return this;
        }

        public Builder camera(OrthographicCamera camera) {
            this.camera = camera;
            return this;
        }

        public RendererConfig build() {
            if (batch == null) throw new IllegalStateException("Batch is required");
            if (spriteFactory == null) throw new IllegalStateException("SpriteFactory is required");
            return new RendererConfig(this);
        }
    }
}
