package com.liquidpixel.procgen.api;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.models.ChunkData;

public interface IWorldRenderer {
    void render(float delta);
    void setActiveChunk(GridPoint2 chunkLocation);
    void setCullingRadius(int radius);
    void dispose();

    // Optional inspection
    int getRenderedChunkCount();
    GridPoint2 getActiveChunk();
    boolean hasChunk(GridPoint2 location);
    void addChunk(ChunkData chunk);
}
