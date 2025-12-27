package com.liquidpixel.procgen.api;

import com.liquidpixel.procgen.models.TerrainDefinition;
import com.liquidpixel.procgen.models.TerrainItem;
import com.liquidpixel.procgen.models.TilesetConfig;

public interface IWorldContext {
    long getSeed();

    int getWorldWidth();

    int getWorldHeight();

    int getChunkWidth();

    int getChunkHeight();

    TilesetConfig getTilesetConfig();

    TerrainItem getItem(TerrainDefinition terrain);

    int getVariant(TerrainDefinition terrain);

    TerrainDefinition getTerrainTypeByName(String terrainType);

    TerrainDefinition getTerrainType(double value);
}
