package com.liquidpixel.procgen.api;

import com.liquidpixel.procgen.models.ChunkData;

public interface IChunkCallback {
    void onSuccess(ChunkData chunk);
    void onError(Exception e);
}
