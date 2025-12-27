package com.liquidpixel.procgen.generators;


import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.liquidpixel.procgen.api.*;
import com.liquidpixel.procgen.builders.World;
import com.liquidpixel.procgen.models.*;
import com.liquidpixel.procgen.utils.IndexCalculator;

import java.util.*;

public class ChunkGenerator implements IChunkGenerator {

    private final World world;
    private final INoiseModule module;
    private final IndexCalculator indexCalculator;

    public ChunkGenerator(World world, INoiseModule module) {
        this.world = world;
        this.module = module;
        this.indexCalculator = new IndexCalculator(module);
    }

    @Override
    public ChunkData generateChunk(GridPoint2 chunkLocation) {
        IWorldContext context = world.getContext();
        int chunkWidth = context.getChunkWidth();
        int chunkHeight = context.getChunkHeight();

        TerrainTile[][] tiles = new TerrainTile[chunkWidth][chunkHeight];

        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {
                int globalX = chunkLocation.x * chunkWidth + x;
                int globalY = chunkLocation.y * chunkHeight + y;

                // Generate terrain
                TerrainDefinition terrain = module.generateAt(globalX, globalY);

                // Calculate variant and texture index
                int variant = module.getVariant(terrain);
                int textureIndex = indexCalculator.calculateIndex(globalX, globalY, terrain);

                // Generate item (if any)
                TerrainItem item = module.getItem(terrain);

                // Create terrain data
                TerrainData terrainData = new TerrainData(
                    terrain.getName(),
                    terrain.getId(),
                    terrain.getTypeId(),
                    terrain.getBaseTile()
                );

                tiles[x][y] = new TerrainTile(terrainData, variant, textureIndex, item);
            }
        }

        ChunkData chunk = new ChunkData(chunkLocation, chunkWidth, chunkHeight, tiles);
        world.notifyChunkGenerated(chunk);

        return chunk;
    }

    @Override
    public List<ChunkData> generateChunks(Collection<GridPoint2> locations) {
        List<ChunkData> chunks = new ArrayList<>();
        for (GridPoint2 location : locations) {
            chunks.add(generateChunk(location));
        }
        return chunks;
    }

    @Override
    public void precomputeRegion(Rectangle worldBounds) {
        // Pre-compute terrain without creating full chunks
        // Useful for features like rivers that need terrain queries
        // Implementation depends on your caching strategy
    }
}
