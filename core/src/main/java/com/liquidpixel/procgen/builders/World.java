package com.liquidpixel.procgen.builders;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.procgen.api.*;
import com.liquidpixel.procgen.generators.ChunkGenerator;
import com.liquidpixel.procgen.loaders.DefaultConfigLoader;
import com.liquidpixel.procgen.models.*;
import com.liquidpixel.procgen.modules.TerrainModule;
import com.liquidpixel.procgen.renderer.WorldRenderer;
import com.liquidpixel.core.utils.RandomCollection;

import java.util.*;

public class World implements IWorld, IWorldContext {

    private final int worldWidth;
    private final int worldHeight;
    private final int chunkWidth;
    private final int chunkHeight;
    private final long seed;
    private final String configPath;
    private final INoiseModule module;
    private final TilesetConfig tilesetConfig;
    private final IConfigLoader configLoader;

    // Collections
    private final Map<Integer, RandomCollection<Integer>> variantCollections;
    private final Map<Integer, RandomCollection<Integer>> itemCollections;

    // State
    private final Map<GridPoint2, ChunkData> generatedChunks = new HashMap<>();

    // Listeners
    private final List<IGenerationListener> generationListeners = new ArrayList<>();

    // Random generators
    private final Random variantRandom;
    private final Random terrainRandom;

    // Lazy-initialized components
    private ChunkGenerator chunkGenerator;

    private World(Builder builder) {
        this.worldWidth = builder.worldWidth;
        this.worldHeight = builder.worldHeight;
        this.chunkWidth = builder.chunkWidth;
        this.chunkHeight = builder.chunkHeight;
        this.module = builder.module;
        this.seed = builder.seed;
        this.configPath = builder.configPath;
        this.configLoader = builder.configLoader;

        // Load configuration
        this.tilesetConfig = configLoader.load(configPath);

        // Initialize services
        this.variantRandom = new Random(seed);
        this.terrainRandom = new Random(seed);

        // Initialize module
        module.init(this);

        // Initialize collections
        TerrainCollectionInitializer.TerrainCollections collections =
            TerrainCollectionInitializer.initialize(
                tilesetConfig.getTerrainDefinitions(),
                variantRandom,
                terrainRandom
            );
        this.variantCollections = collections.getVariantCollections();
        this.itemCollections = collections.getItemCollections();
    }

    @Override
    public IChunkGenerator getChunkGenerator() {
        if (chunkGenerator == null) {
            chunkGenerator = new ChunkGenerator(this, module);
        }
        return chunkGenerator;
    }

    @Override
    public IWorldRenderer createRenderer(RendererConfig config) {
        return new WorldRenderer(this, config);
    }

    @Override
    public TerrainDefinition getTerrainType(double value) {
        return tilesetConfig.getTerrainType(value);
    }

    @Override
    public TerrainDefinition getTerrainTypeByName(String terrainType) {
        return tilesetConfig.getTerrainTypeByName(terrainType);
    }


    @Override
    public TerrainData getTerrainAt(int x, int y) {
        TerrainDefinition def = module.generateAt(x, y);
        return new TerrainData(def.getName(), def.getId(), def.getTypeId(), def.getBaseTile());
    }

    @Override
    public int getVariant(TerrainDefinition terrain) {
        return variantCollections.get(terrain.getId()).next();
    }

    @Override
    public boolean isChunkGenerated(GridPoint2 chunkLocation) {
        return generatedChunks.containsKey(chunkLocation);
    }

    @Override
    public void updateTerrain(GridPoint2 location, String terrainType) {
        // Implementation for terrain modification
        // This would update generated chunks and notify listeners
    }

    @Override
    public IWorldContext getContext() {
        return this;
    }

    @Override
    public void addGenerationListener(IGenerationListener listener) {
        generationListeners.add(listener);
    }

    @Override
    public void removeGenerationListener(IGenerationListener listener) {
        generationListeners.remove(listener);
    }

    // IWorldContext implementation
    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public int getWorldWidth() {
        return worldWidth;
    }

    @Override
    public int getWorldHeight() {
        return worldHeight;
    }

    @Override
    public int getChunkWidth() {
        return chunkWidth;
    }

    @Override
    public int getChunkHeight() {
        return chunkHeight;
    }

    @Override
    public TilesetConfig getTilesetConfig() {
        return tilesetConfig;
    }

    // Package-private methods for internal use
    public void notifyChunkGenerated(ChunkData chunk) {
        generatedChunks.put(new GridPoint2(chunk.getLocation()), chunk);
        for (IGenerationListener listener : generationListeners) {
            listener.onChunkGenerated(chunk);
        }
    }

    public TerrainItem getItem(TerrainDefinition terrain) {
        RandomCollection<Integer> itemCollection = itemCollections.get(terrain.getId());
        try {
            Integer index = itemCollection.next();
            return index >= 0 ? terrain.getTerrainItems().get(index) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static class Builder {
        private Random random = new Random();
        private String configPath = "models/terrain.json";
        private int worldWidth = 128;
        private int worldHeight = 128;
        private int chunkWidth = 16;
        private int chunkHeight = 16;
        private long seed = random.nextLong();
        private INoiseModule module = new TerrainModule();
        private IConfigLoader configLoader = new DefaultConfigLoader();

        public Builder configPath(String configPath) {
            this.configPath = configPath;
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder worldSize(int width, int height) {
            this.worldWidth = width;
            this.worldHeight = height;
            return this;
        }

        public Builder chunkSize(int width, int height) {
            this.chunkWidth = width;
            this.chunkHeight = height;
            return this;
        }

        public Builder noiseModule(INoiseModule module) {
            this.module = module;
            return this;
        }

        public Builder configLoader(IConfigLoader loader) {
            this.configLoader = loader;
            return this;
        }

        public World build() {
            validate();
            return new World(this);
        }

        private void validate() {
            if (worldWidth <= 0 || worldHeight <= 0) {
                throw new IllegalArgumentException("World dimensions must be positive");
            }
            if (chunkWidth <= 0 || chunkHeight <= 0) {
                throw new IllegalArgumentException("Chunk dimensions must be positive");
            }
            if (module == null) {
                throw new IllegalArgumentException("Noise Module cannot be null");
            }
        }
    }
}
