package com.liquidpixel.procgen.api;

import com.liquidpixel.procgen.models.ChunkData;

public interface IChunkGenerationListener {
    void onChunkGenerated(ChunkData chunk);
    void onGenerationProgress(float progress);
    void onGenerationComplete();
}
