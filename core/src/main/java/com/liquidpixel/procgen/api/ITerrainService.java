package com.liquidpixel.procgen.api;


import com.liquidpixel.procgen.models.TerrainDefinition;
import com.liquidpixel.procgen.models.TerrainItem;

public interface ITerrainService {
    TerrainDefinition getTerrainType(double value);

    TerrainDefinition getTerrainTypeByName(String terrainType);

    int getVariant(TerrainDefinition terrain);

    TerrainItem getItem(TerrainDefinition terrain);
}
