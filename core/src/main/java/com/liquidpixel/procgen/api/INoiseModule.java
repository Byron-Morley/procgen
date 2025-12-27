package com.liquidpixel.procgen.api;


import com.liquidpixel.procgen.models.TerrainDefinition;
import com.liquidpixel.procgen.models.TerrainItem;

public interface INoiseModule {
    int getVariant(TerrainDefinition terrain);
    TerrainItem getItem(TerrainDefinition terrain);
    void init(IWorldContext context);
    TerrainDefinition generateAt(int x, int y);
}
