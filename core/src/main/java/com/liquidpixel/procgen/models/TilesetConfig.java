package com.liquidpixel.procgen.models;

import java.util.List;

public class TilesetConfig {

    private int tileWidth;
    private int tileHeight;
    private List<TerrainDefinition> terrainDefinitions;

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public List<TerrainDefinition> getTerrainDefinitions() {
        return terrainDefinitions;
    }

    public void setTerrainDefinitions(List<TerrainDefinition> terrainDefinitions) {
        this.terrainDefinitions = terrainDefinitions;
    }

    public TerrainDefinition getTerrainType(double value) {
        for (TerrainDefinition terrain : terrainDefinitions) {
            if (value > terrain.getMin() && value < terrain.getMax()) {
                return terrain;
            }
        }
        return terrainDefinitions.get(0);
    }

    public TerrainDefinition getTerrainTypeByName(String name) {
        for (TerrainDefinition terrain : terrainDefinitions) {
            if (name.equals(terrain.getName())) {
                return terrain;
            }
        }
        return null;
    }

    public TerrainDefinition getTerrainTypeById(int id) {
        for (TerrainDefinition terrain : terrainDefinitions) {
            if (terrain.getId() == id) {
                return terrain;
            }
        }
        return null;
    }
}
