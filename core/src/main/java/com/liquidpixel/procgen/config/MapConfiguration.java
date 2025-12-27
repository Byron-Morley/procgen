package com.liquidpixel.procgen.config;

import com.liquidpixel.procgen.api.IMapConfiguration;

public class MapConfiguration implements IMapConfiguration {

    private final int worldWidth;
    private final int worldHeight;
    private final int chunkWidth;
    private final int chunkHeight;
    private final int chunkCountX;
    private final int chunkCountY;

    public MapConfiguration(int worldWidth, int worldHeight, int chunkWidth, int chunkHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        this.chunkCountX = (int) Math.ceil(worldWidth / chunkWidth);
        this.chunkCountY = (int) Math.ceil(worldHeight / chunkHeight);
    }

    @Override
    public int getWorldWidth() {
        return worldWidth;
    }

    @Override
    public int getWorldHeight() {
        return worldHeight;
    }

    @Override
    public int getChunkWidth() {
        return chunkWidth;
    }

    @Override
    public int getChunkHeight() {
        return chunkHeight;
    }

    @Override
    public int getChunkCountX() {
        return chunkCountX;
    }

    @Override
    public int getChunkCountY() {
        return chunkCountY;
    }
}
