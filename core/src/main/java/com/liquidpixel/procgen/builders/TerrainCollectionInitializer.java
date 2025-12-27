package com.liquidpixel.procgen.builders;

import com.liquidpixel.core.utils.RandomCollection;
import com.liquidpixel.procgen.models.TerrainDefinition;
import com.liquidpixel.procgen.models.TerrainItem;
import com.liquidpixel.procgen.models.TerrainVariant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TerrainCollectionInitializer {

    public static class TerrainCollections {
        private final Map<Integer, RandomCollection<Integer>> variantCollections;
        private final Map<Integer, RandomCollection<Integer>> itemCollections;

        public TerrainCollections(
            Map<Integer, RandomCollection<Integer>> variantCollections,
            Map<Integer, RandomCollection<Integer>> itemCollections) {
            this.variantCollections = variantCollections;
            this.itemCollections = itemCollections;
        }

        public Map<Integer, RandomCollection<Integer>> getVariantCollections() {
            return variantCollections;
        }

        public Map<Integer, RandomCollection<Integer>> getItemCollections() {
            return itemCollections;
        }
    }

    public static TerrainCollections initialize(
        List<TerrainDefinition> terrainDefinitions,
        Random variantRandom,
        Random terrainRandom) {

        Map<Integer, RandomCollection<Integer>> variantCollections = new HashMap<>();
        Map<Integer, RandomCollection<Integer>> itemCollections = new HashMap<>();

        for (TerrainDefinition terrain : terrainDefinitions) {
            variantCollections.put(terrain.getId(), createVariantCollection(terrain, variantRandom));
            itemCollections.put(terrain.getId(), createItemCollection(terrain, terrainRandom));
        }

        return new TerrainCollections(variantCollections, itemCollections);
    }

    private static RandomCollection<Integer> createVariantCollection(
        TerrainDefinition terrainDef,
        Random random) {

        RandomCollection<Integer> collection = new RandomCollection<>(random);
        for (TerrainVariant variant : terrainDef.getTerrainVariants()) {
            collection.add(variant.getProbability(), variant.getIndex());
        }
        return collection;
    }

    private static RandomCollection<Integer> createItemCollection(
        TerrainDefinition terrainDef,
        Random random) {

        RandomCollection<Integer> collection = new RandomCollection<>(random);

        List<TerrainItem> items = terrainDef.getTerrainItems();
        for (int i = 0; i < items.size(); i++) {
            TerrainItem item = items.get(i);
            if (item.getProbability() > 0) {
                collection.add(item.getProbability(), i);
            }
        }

        double totalProb = items.stream()
            .mapToDouble(TerrainItem::getProbability)
            .sum();

        if (totalProb < 1.0) {
            collection.add(1.0 - totalProb, -1);
        }

        return collection;
    }
}
