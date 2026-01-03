
package com.liquidpixel.example.orchestrators;


import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.example.tasks.ChunkGenerationTask;
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

    // Progress tracking
    private int totalChunks = 0;
    private int generatedChunks = 0;
    private IChunkGenerationListener progressListener;

    public ChunkOrchestrator(IChunkGenerator generator, IWorldRenderer renderer, ITaskManager taskManager) {
        this.generator = generator;
        this.renderer = renderer;
        this.taskManager = taskManager;
    }

    public void setProgressListener(IChunkGenerationListener listener) {
        this.progressListener = listener;
    }

    /**
     * Strategy 1: Generate all chunks synchronously (blocking).
     */
    public void generateAllChunksSync(int chunkCountX, int chunkCountY) {
        totalChunks = chunkCountX * chunkCountY;
        generatedChunks = 0;

        for (int x = 0; x < chunkCountX; x++) {
            for (int y = 0; y < chunkCountY; y++) {
                GridPoint2 location = new GridPoint2(x, y);
                ChunkData chunk = generator.generateChunk(location);
                renderer.addChunk(chunk);

                generatedChunks++;
                notifyProgress();
            }
        }

        notifyComplete();
    }

    /**
     * Strategy 2: Generate chunks asynchronously using task system.
     */
    public void generateAllChunksAsync(int chunkCountX, int chunkCountY) {
        totalChunks = chunkCountX * chunkCountY;
        generatedChunks = 0;

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
        totalChunks = chunkCountX * chunkCountY;
        generatedChunks = 0;

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
            chunk -> {
                renderer.addChunk(chunk);
                generatedChunks++;
                notifyProgress();

                if (generatedChunks >= totalChunks) {
                    notifyComplete();
                }
            }
        );
        taskManager.getTaskRunner().addToQueue(task);
    }

    private void notifyProgress() {
        if (progressListener != null) {
            float progress = totalChunks > 0 ? (float) generatedChunks / totalChunks : 0f;
            progressListener.onGenerationProgress(progress);
        }
    }

    private void notifyComplete() {
        if (progressListener != null) {
            progressListener.onGenerationComplete();
        }
    }

    public float getProgress() {
        return totalChunks > 0 ? (float) generatedChunks / totalChunks : 0f;
    }

    public boolean isComplete() {
        return generatedChunks >= totalChunks && totalChunks > 0;
    }
}
