package com.liquidpixel.procgen.api;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.models.ChunkData;
import com.liquidpixel.procgen.models.TerrainData;

public interface IGenerationListener {
    void onChunkGenerated(ChunkData chunk);
    void onTerrainModified(GridPoint2 location, TerrainData newTerrain);
}
