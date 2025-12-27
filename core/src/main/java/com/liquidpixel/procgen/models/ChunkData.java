package com.liquidpixel.procgen.models;

import com.badlogic.gdx.math.GridPoint2;

public class ChunkData {
    private final GridPoint2 location;
    private final int width;
    private final int height;
    private final TerrainTile[][] tiles;

    public ChunkData(GridPoint2 location, int width, int height, TerrainTile[][] tiles) {
        this.location = new GridPoint2(location);
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public GridPoint2 getLocation() {
        return new GridPoint2(location);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TerrainTile getTile(int x, int y) {
        return tiles[x][y];
    }
}
