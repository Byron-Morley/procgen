package com.liquidpixel.procgen.api;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.builders.RendererConfig;
import com.liquidpixel.procgen.models.TerrainData;

public interface IWorld {

    // Generation
    IChunkGenerator getChunkGenerator();

    // Rendering (optional convenience)
    IWorldRenderer createRenderer(RendererConfig config);

    boolean isChunkGenerated(GridPoint2 chunkLocation);

    // Modification
    void updateTerrain(GridPoint2 location, String terrainType);

    // Configuration access
    IWorldContext getContext();

    // Event system
    void addGenerationListener(IGenerationListener listener);

    void removeGenerationListener(IGenerationListener listener);


    // Queries
    TerrainData getTerrainAt(int x, int y);
//    boolean isInBounds(int x, int y);

    // Modification
//    void updateTerrain(int x, int y, String terrainType);

    // Listeners
//    void addChunkGenerationListener(IChunkGenerationListener listener);
//    void removeChunkGenerationListener(IChunkGenerationListener listener);
//    void addTerrainChangeListener(ITerrainChangeListener listener);
//    void removeTerrainChangeListener(ITerrainChangeListener listener);

}
