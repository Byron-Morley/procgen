package com.liquidpixel.procgen.api;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.models.ChunkData;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collection;
import java.util.List;

public interface IChunkGenerator {
    /**
     * Synchronously generates a chunk at the specified location.
     */
    ChunkData generateChunk(GridPoint2 chunkLocation);

    /**
     * Generates multiple chunks. Useful for batch operations.
     */
    List<ChunkData> generateChunks(Collection<GridPoint2> locations);

    /**
     * Pre-generates terrain data for a region without creating full chunks.
     * Useful for features like rivers that need to query terrain.
     */
    void precomputeRegion(Rectangle worldBounds);
}
