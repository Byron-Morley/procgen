package com.liquidpixel.example.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.pool.blackboards.TaskBlackboard;
import com.liquidpixel.pool.tasks.PoolableTask;
import com.liquidpixel.procgen.api.IChunkGenerator;
import com.liquidpixel.procgen.models.ChunkData;

import java.util.function.Consumer;

public class ChunkGenerationTask extends PoolableTask<TaskBlackboard> {

    private final IChunkGenerator generator;
    private final GridPoint2 location;
    private final Consumer<ChunkData> onComplete;

    public ChunkGenerationTask(IChunkGenerator generator, GridPoint2 location, Consumer<ChunkData> onComplete) {
        this.generator = generator;
        this.location = location;
        this.onComplete = onComplete;
    }

    @Override
    public void tick(float delta) {
        Gdx.app.debug("ChunkGen", "Generating chunk at " + location);

        long startTime = System.nanoTime();

        // Module does the generation
        ChunkData chunk = generator.generateChunk(location);

        // Application handles the result
        onComplete.accept(chunk);

        long duration = System.nanoTime() - startTime;
        double milliseconds = duration / 1_000_000.0;
        Gdx.app.log("ChunkGen", "Generated chunk " + location + " in " + milliseconds + "ms");

        success();
    }

    @Override
    protected Task copyTo(Task task) {
        return task;
    }
}
