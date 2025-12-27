package com.liquidpixel.procgen.models;

public class TerrainTile {
    private final TerrainData terrain;
    private final int variant;
    private final int textureIndex;
    private final TerrainItem item;

    public TerrainTile(TerrainData terrain, int variant, int textureIndex, TerrainItem item) {
        this.terrain = terrain;
        this.variant = variant;
        this.textureIndex = textureIndex;
        this.item = item;
    }

    public TerrainData getTerrain() {
        return terrain;
    }

    public int getVariant() {
        return variant;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public TerrainItem getItem() {
        return item;
    }

    public int getTypeId() {
        return terrain.getTypeId();
    }
}
