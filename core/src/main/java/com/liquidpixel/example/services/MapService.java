package com.liquidpixel.example.services;

import com.badlogic.gdx.math.GridPoint2;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.core.engine.GameState;
import com.liquidpixel.example.orchestrators.ChunkOrchestrator;
import com.liquidpixel.pool.api.ITaskManager;
import com.liquidpixel.procgen.ProcGen;
import com.liquidpixel.procgen.api.*;
import com.liquidpixel.procgen.builders.RendererConfig;
import com.liquidpixel.procgen.modules.TerrainModule;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.List;

/**
 * Your application's map service using the procgen module.
 */
public class MapService {

    private final IWorld world;
    private final IWorldRenderer worldRenderer;
    private final ChunkOrchestrator orchestrator;
    private final IMapConfiguration mapConfiguration;

    public MapService(
        ITaskManager taskManager,
        ISpriteFactory spriteFactory,
        IMapConfiguration mapConfiguration
    ) {
        this.mapConfiguration = mapConfiguration;

        // Create world with procgen module
        this.world = ProcGen.createWorld()
            .seed(GameState.getSeed())
            .worldSize(mapConfiguration.getWorldWidth(), mapConfiguration.getWorldHeight())
            .chunkSize(mapConfiguration.getChunkWidth(), mapConfiguration.getChunkHeight())
            .configPath("models/terrain.json")
            .noiseModule(new TerrainModule())
            .build();

        // Create renderer
        this.worldRenderer = world.createRenderer(
            new RendererConfig.Builder()
                .batch(GameResources.get().getBatch())
                .spriteFactory(spriteFactory)
                .camera(GameResources.get().getCamera())
                .cullingRadius(8)
                .build()
        );

        // Create orchestrator
        this.orchestrator = new ChunkOrchestrator(
            world.getChunkGenerator(),
            worldRenderer,
            taskManager
        );

        // Hook up listeners
//        world.addGenerationListener(new PathfindingGenerationListener(
//            mapGraph,
//            mapConfiguration.getWorldWidth()
//        ));
    }

    public void init() {
        // Generate all chunks
        orchestrator.generateChunksInsideOut(
            mapConfiguration.getChunkCountX(),
            mapConfiguration.getChunkCountY()
        );
    }

    public void init(IChunkGenerationListener listener) {
        orchestrator.setProgressListener(listener);
        init();
    }

    public IWorldRenderer getRenderer() {
        return worldRenderer;
    }

    public IWorld getWorld() {
        return world;
    }

    public void applyRiver(List<GridPoint2> riverPath) {
        for (GridPoint2 point : riverPath) {
            world.updateTerrain(point, "shallow-water");
        }
    }
}
