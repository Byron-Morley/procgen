package com.liquidpixel.procgen.models;

import java.util.ArrayList;
import java.util.List;

public class TerrainDefinition {
    int id;
    String name;
    int typeId;
    double min;
    double max;
    int[] weights;
    String baseTile;
    int baseIndex;
    List<TerrainVariant> terrainVariants = new ArrayList<>();
    List<TerrainItem> terrainItems = new ArrayList<>();

    public TerrainDefinition() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TerrainVariant> getTerrainVariants() {
        return terrainVariants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getBaseTile() {
        return baseTile;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String toString() {
        return "TerrainDefinition{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", typeId=" + typeId +
            ", min=" + min +
            ", max=" + max +
            ", baseTile='" + baseTile + '\'' +
            '}';
    }

    public int[] getWeights() {
        return weights;
    }

    public int getBaseIndex() {
        return baseIndex;
    }

    public List<TerrainItem> getTerrainItems() {
        return terrainItems;
    }
}
