package com.liquidpixel.procgen.api;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.models.TerrainData;

public interface ITerrainChangeListener {
    void onTerrainChanged(GridPoint2 location, TerrainData oldTerrain, TerrainData newTerrain);
}
