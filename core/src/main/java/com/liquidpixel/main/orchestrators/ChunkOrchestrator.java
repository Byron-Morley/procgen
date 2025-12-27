package com.liquidpixel.main.orchestrators;


import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.main.tasks.ChunkGenerationTask;
import com.liquidpixel.pool.api.ITaskManager;
import com.liquidpixel.procgen.api.*;
import com.liquidpixel.procgen.models.ChunkData;


/**
 * Application-level chunk orchestration.
 * Decides WHEN and HOW to generate chunks.
 */
public class ChunkOrchestrator {
    private final IChunkGenerator generator;
    private final IWorldRenderer renderer;
    private final ITaskManager taskManager;

    public ChunkOrchestrator(IChunkGenerator generator, IWorldRenderer renderer, ITaskManager taskManager) {
        this.generator = generator;
        this.renderer = renderer;
        this.taskManager = taskManager;
    }

    /**
     * Strategy 1: Generate all chunks synchronously (blocking).
     */
    public void generateAllChunksSync(int chunkCountX, int chunkCountY) {
        for (int x = 0; x < chunkCountX; x++) {
            for (int y = 0; y < chunkCountY; y++) {
                GridPoint2 location = new GridPoint2(x, y);
                ChunkData chunk = generator.generateChunk(location);
                renderer.addChunk(chunk);
            }
        }
    }

    /**
     * Strategy 2: Generate chunks asynchronously using task system.
     */
    public void generateAllChunksAsync(int chunkCountX, int chunkCountY) {
        for (int x = 0; x < chunkCountX; x++) {
            for (int y = 0; y < chunkCountY; y++) {
                GridPoint2 location = new GridPoint2(x, y);
                enqueueChunkGeneration(location);
            }
        }
    }

    /**
     * Strategy 3: Generate chunks in spiral from center (inside-out).
     */
    public void generateChunksInsideOut(int chunkCountX, int chunkCountY) {
        int centerX = chunkCountX / 2;
        int centerY = chunkCountY / 2;

        // Use your existing LoopUtils.insideOut logic
        for (int radius = 0; radius <= Math.max(centerX, centerY); radius++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    if (x >= 0 && x < chunkCountX && y >= 0 && y < chunkCountY) {
                        enqueueChunkGeneration(new GridPoint2(x, y));
                    }
                }
            }
        }
    }

    /**
     * Strategy 4: Lazy generation - only generate what's needed.
     */
    public void generateChunkIfNeeded(GridPoint2 location) {
        if (!renderer.hasChunk(location)) {
            enqueueChunkGeneration(location);
        }
    }

    private void enqueueChunkGeneration(GridPoint2 location) {
        ChunkGenerationTask task = new ChunkGenerationTask(
            generator,
            location,
            chunk -> renderer.addChunk(chunk)
        );
        taskManager.getTaskRunner().addToQueue(task);
    }
}
