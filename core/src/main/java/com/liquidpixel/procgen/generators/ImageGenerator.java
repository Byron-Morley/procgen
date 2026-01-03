package com.liquidpixel.procgen.generators;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.core.engine.GameState;
import com.liquidpixel.example.orchestrators.ChunkOrchestrator;
import com.liquidpixel.procgen.ProcGen;
import com.liquidpixel.procgen.api.IChunkGenerator;
import com.liquidpixel.procgen.api.IWorld;
import com.liquidpixel.procgen.config.MapConfiguration;
import com.liquidpixel.procgen.models.ChunkData;
import com.liquidpixel.procgen.models.TerrainTile;
import com.liquidpixel.procgen.modules.TerrainModule;

import java.util.*;

public class ImageGenerator implements ApplicationListener {

    private IWorld world;
    private ChunkOrchestrator orchestrator;


    String tileset = "summer-default/terrain";
    int tileSize = 16;

    public ImageGenerator() {
    }

    @Override
    public void create() {

        MapConfiguration mapConfiguration = new MapConfiguration(256, 256, 64, 64);

        this.world = ProcGen.createWorld()
            .seed(GameState.getSeed())
            .worldSize(mapConfiguration.getWorldWidth(), mapConfiguration.getWorldHeight())
            .chunkSize(mapConfiguration.getChunkWidth(), mapConfiguration.getChunkHeight())
            .configPath("models/terrain.json")
            .noiseModule(new TerrainModule())
            .build();


        IChunkGenerator chunkGenerator = world.getChunkGenerator();
        List<ChunkData> chunks = new ArrayList<>();

        for (int x = 0; x < mapConfiguration.getChunkCountX(); x++) {
            for (int y = 0; y < mapConfiguration.getChunkCountY(); y++) {
                GridPoint2 location = new GridPoint2(x, y);
                ChunkData chunk = chunkGenerator.generateChunk(location);
                chunks.add(chunk);
            }
        }
        renderToImage(chunks, "raw/output/");
    }

    private void renderToImage(List<ChunkData> chunks, String outputDir) {

        TextureAtlas.TextureAtlasData atlas =
            new TextureAtlas.TextureAtlasData(
                Gdx.files.internal("sprites/terrain/16x16_terrain.atlas"),
                Gdx.files.internal("sprites/terrain"),
                false
            );

        Map<String, Array<TextureAtlas.TextureAtlasData.Region>> regionsByName = new HashMap<>();

        for (TextureAtlas.TextureAtlasData.Region region : atlas.getRegions()) {
            regionsByName
                .computeIfAbsent(region.name, k -> new Array<>())
                .add(region);
        }

        for (Array<TextureAtlas.TextureAtlasData.Region> list : regionsByName.values()) {
            list.sort(Comparator.comparingInt(r -> r.index));
        }

        Map<TextureAtlas.TextureAtlasData.Page, Pixmap> pages = new HashMap<>();

        for (TextureAtlas.TextureAtlasData.Page page : atlas.getPages()) {
            pages.put(page, new Pixmap(page.textureFile));
        }


        for (ChunkData chunk : chunks) {

            Pixmap chunkImage = new Pixmap(
                chunk.getWidth() * tileSize,
                chunk.getHeight() * tileSize,
                Pixmap.Format.RGBA8888
            );

            for (int x = 0; x < chunk.getWidth(); x++) {
                for (int y = 0; y < chunk.getHeight(); y++) {

                    TerrainTile tile = chunk.getTile(x, y);
                    Pixmap image = extractRegion(
                        findRegion(tileset, tile.getTextureIndex(), regionsByName),
                        pages
                    );

                    int dx = x * tileSize;

                    int dy = y * tileSize;

                    int flippedY =
                        chunkImage.getHeight()
                            - dy
                            - image.getHeight();

                    chunkImage.drawPixmap(image, dx, flippedY);
                }
            }

            PixmapIO.writePNG(
                Gdx.files.local(outputDir+"chunk-"+(chunk.getLocation().x)+"-"+(chunk.getLocation().y)+".png"),
                chunkImage
            );
            chunkImage.dispose();
        }
    }

    private TextureAtlas.TextureAtlasData.Region findRegion(
        String name,
        int index,
        Map<String, Array<TextureAtlas.TextureAtlasData.Region>> regionsByName
    ) {
        Array<TextureAtlas.TextureAtlasData.Region> list = regionsByName.get(name);
        if (list == null) return null;

        if (index < 0) {
            // match libGDX behavior: return first region
            return list.first();
        }

        for (TextureAtlas.TextureAtlasData.Region r : list) {
            if (r.index == index) return r;
        }
        return null;
    }

    private Pixmap extractRegion(TextureAtlas.TextureAtlasData.Region region,
                                 Map<TextureAtlas.TextureAtlasData.Page, Pixmap> pages) {

        Pixmap page = pages.get(region.page);

        Pixmap out = new Pixmap(region.width, region.height, page.getFormat());

        out.drawPixmap(
            page,
            region.left,
            region.top,
            region.width,
            region.height,
            0,
            0,
            region.width,
            region.height
        );

        return out;
    }


    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}


