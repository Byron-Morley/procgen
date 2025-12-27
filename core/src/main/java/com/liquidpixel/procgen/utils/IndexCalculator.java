package com.liquidpixel.procgen.utils;

import com.liquidpixel.procgen.api.INoiseModule;
import com.liquidpixel.procgen.models.TerrainDefinition;

public class IndexCalculator {

    INoiseModule module;

    public IndexCalculator(INoiseModule module) {
        this.module = module;
    }

    public int calculateIndex(int dx, int dy, TerrainDefinition terrain) {
        int topright = calculateWeight(dx + 1, dy + 1, 0);
        int bottomright = calculateWeight(dx + 1, dy, 1);
        int bottomleft = calculateWeight(dx, dy, 2);
        int topleft = calculateWeight(dx, dy + 1, 3);

        int index = bottomleft + bottomright + topleft + topright;

        if (index == terrain.getBaseIndex()) {
            index = module.getVariant(terrain);
        }
        return index;
    }

    private int calculateWeight(int x, int y, int bit) {
        TerrainDefinition terrain = module.generateAt(x, y);
        //TODO could do with calculating a better tile weight
        if (terrain == null) {
            return 0;
        }
        int[] weights = terrain.getWeights();
        return weights[bit];
    }
}
