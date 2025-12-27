package com.liquidpixel.procgen.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.api.*;
import com.liquidpixel.procgen.builders.RendererConfig;
import com.liquidpixel.procgen.builders.World;
import com.liquidpixel.procgen.models.ChunkData;

import java.util.*;

public class WorldRenderer implements IWorldRenderer {

    private final World world;
    private final RendererConfig config;
    private final Map<GridPoint2, RenderableChunk> chunks = new HashMap<>();
    private GridPoint2 activeChunkLocation;
    private int cullingRadius;

    public WorldRenderer(World world, RendererConfig config) {
        this.world = world;
        this.config = config;
        this.cullingRadius = config.getCullingRadius();
    }

    @Override
    public void render(float delta) {
        if (activeChunkLocation == null) {
            updateActiveChunk();
        }

        if (activeChunkLocation != null) {
            renderVisibleChunks(delta);
        }
    }

    private void renderVisibleChunks(float delta) {
        int chunkX = activeChunkLocation.x;
        int chunkY = activeChunkLocation.y;

        for (int x = chunkX - cullingRadius; x <= chunkX + cullingRadius; x++) {
            for (int y = chunkY - cullingRadius; y <= chunkY + cullingRadius; y++) {
                GridPoint2 location = new GridPoint2(x, y);
                RenderableChunk chunk = chunks.get(location);
                if (chunk != null) {
                    chunk.render(config.getBatch(), delta);
                }
            }
        }
    }

    private void updateActiveChunk() {
        OrthographicCamera camera = config.getCamera();
        if (camera == null) return;

        IWorldContext context = world.getContext();
        int x = (int) Math.floor(camera.position.x / context.getChunkWidth());
        int y = (int) Math.floor(camera.position.y / context.getChunkHeight());

        GridPoint2 newLocation = new GridPoint2(x, y);
        if (activeChunkLocation == null || !activeChunkLocation.equals(newLocation)) {
            if (chunks.containsKey(newLocation)) {
                activeChunkLocation = newLocation;
            }
        }
    }

    @Override
    public void addChunk(ChunkData chunkData) {
        RenderableChunk renderChunk = new RenderableChunk(
            chunkData,
            world.getContext(),
            config.getSpriteFactory()
        );
        chunks.put(new GridPoint2(chunkData.getLocation()), renderChunk);
    }

    @Override
    public void setActiveChunk(GridPoint2 chunkLocation) {
        this.activeChunkLocation = chunkLocation;
    }

    @Override
    public void setCullingRadius(int radius) {
        this.cullingRadius = radius;
    }

    @Override
    public boolean hasChunk(GridPoint2 location) {
        return chunks.containsKey(location);
    }

    @Override
    public int getRenderedChunkCount() {
        return chunks.size();
    }

    @Override
    public GridPoint2 getActiveChunk() {
        return activeChunkLocation;
    }

    @Override
    public void dispose() {
        for (RenderableChunk chunk : chunks.values()) {
            chunk.dispose();
        }
        chunks.clear();
    }
}
