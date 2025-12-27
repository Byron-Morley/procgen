package com.liquidpixel.procgen.models;

public class TerrainData {
    private final String name;
    private final int id;
    private final int typeId;
    private final String baseTile;

    public TerrainData(String name, int id, int typeId, String baseTile) {
        this.name = name;
        this.id = id;
        this.typeId = typeId;
        this.baseTile = baseTile;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getBaseTile() {
        return baseTile;
    }
}
