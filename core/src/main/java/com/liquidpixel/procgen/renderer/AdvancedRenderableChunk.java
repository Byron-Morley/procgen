package com.liquidpixel.procgen.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.liquidpixel.procgen.api.IWorldContext;
import com.liquidpixel.procgen.models.ChunkData;
import com.liquidpixel.procgen.models.TerrainTile;
import com.liquidpixel.sprite.api.factory.ISpriteFactory;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

/**
 * Advanced renderable chunk with support for tile transformations and dynamic updates.
 */
class AdvancedRenderableChunk {

    private static final float PX_PER_METER = 16f;

    private final ChunkData chunkData;
    private final GridPoint2 worldPosition;
    private final int tileWidth;
    private final int tileHeight;
    private final ISpriteFactory spriteFactory;

    // Tile transformation data (rotation, flip)
    private final byte[][] tileRotations;   // 0, 1, 2, 3 for 0°, 90°, 180°, 270°
    private final boolean[][] horizontalFlips;
    private final boolean[][] verticalFlips;

    // Pre-computed vertices
    private final Map<Texture, float[]> batchedVertices;
    private boolean isDirty = true;
    private float opacity = 1.0f;

    // For dynamic updates - track which tiles have changed
    private final boolean[][] dirtyTiles;
    private boolean hasAnyDirtyTiles = false;

    AdvancedRenderableChunk(ChunkData chunkData, IWorldContext context, ISpriteFactory spriteFactory) {
        this.chunkData = chunkData;
        this.spriteFactory = spriteFactory;
        this.tileWidth = (int) PX_PER_METER;
        this.tileHeight = (int) PX_PER_METER;

        int width = chunkData.getWidth();
        int height = chunkData.getHeight();

        this.worldPosition = new GridPoint2(
            chunkData.getLocation().x * width,
            chunkData.getLocation().y * height
        );

        // Initialize transformation arrays
        this.tileRotations = new byte[width][height];
        this.horizontalFlips = new boolean[width][height];
        this.verticalFlips = new boolean[width][height];
        this.dirtyTiles = new boolean[width][height];

        this.batchedVertices = new HashMap<>();

        // Add some variety by randomly rotating/flipping tiles
        randomizeTransformations();

        prebuildVertices();
    }

    /**
     * Randomly rotates/flips tiles for visual variety.
     * Remove this if you don't want random transformations.
     */
    private void randomizeTransformations() {
        java.util.Random random = new java.util.Random(
            chunkData.getLocation().x * 31L + chunkData.getLocation().y
        );

        for (int x = 0; x < chunkData.getWidth(); x++) {
            for (int y = 0; y < chunkData.getHeight(); y++) {
                // 25% chance to flip horizontally
                if (random.nextFloat() < 0.25f) {
                    horizontalFlips[x][y] = true;
                }
                // 25% chance to flip vertically
                if (random.nextFloat() < 0.25f) {
                    verticalFlips[x][y] = true;
                }
                // Random rotation (0-3)
                tileRotations[x][y] = (byte) random.nextInt(4);
            }
        }
    }

    private void prebuildVertices() {
        Map<Texture, Array<float[]>> tempVerticesByTexture = new HashMap<>();

        final float layerTileWidth = tileWidth / PX_PER_METER;
        final float layerTileHeight = tileHeight / PX_PER_METER;
        final int width = chunkData.getWidth();
        final int height = chunkData.getHeight();

        final int col1 = worldPosition.y;
        final int col2 = height + worldPosition.y;
        final int row1 = worldPosition.x;
        final int row2 = width + worldPosition.x;

        float y = row2 * layerTileHeight;
        float xStart = col1 * layerTileWidth;

        for (int row = row2; row >= row1; row--) {
            float x = xStart;
            int localX = row - worldPosition.x;

            for (int col = col1; col < col2; col++) {
                int localY = col - worldPosition.y;

                // Only rebuild if tile is dirty (or doing initial build)
                if (localX >= 0 && localX < width && localY >= 0 && localY < height) {
                    if (isDirty || (hasAnyDirtyTiles && dirtyTiles[localX][localY])) {
                        TerrainTile tile = chunkData.getTile(localX, localY);

                        if (tile != null) {
                            TextureRegion region = spriteFactory.getTextureWithFallback(
                                tile.getTerrain().getBaseTile(),
                                tile.getTextureIndex()
                            );

                            if (region != null) {
                                float[] vertices = buildTileVerticesWithTransform(
                                    region, x, y, layerTileWidth, layerTileHeight,
                                    tileRotations[localX][localY],
                                    horizontalFlips[localX][localY],
                                    verticalFlips[localX][localY]
                                );

                                Texture texture = region.getTexture();
                                tempVerticesByTexture.computeIfAbsent(texture, k -> new Array<>()).add(vertices);
                            }
                        }

                        // Mark as clean
                        dirtyTiles[localX][localY] = false;
                    }
                }

                x += layerTileWidth;
            }

            y -= layerTileHeight;
        }

        // Consolidate vertices
        batchedVertices.clear();
        for (Map.Entry<Texture, Array<float[]>> entry : tempVerticesByTexture.entrySet()) {
            Texture texture = entry.getKey();
            Array<float[]> tileVertices = entry.getValue();

            int totalVertices = tileVertices.size * 20;
            float[] combinedVertices = new float[totalVertices];

            int offset = 0;
            for (float[] vertices : tileVertices) {
                System.arraycopy(vertices, 0, combinedVertices, offset, 20);
                offset += 20;
            }

            batchedVertices.put(texture, combinedVertices);
        }

        isDirty = false;
        hasAnyDirtyTiles = false;
    }

    /**
     * Builds vertices with rotation and flip transformations applied.
     */
    private float[] buildTileVerticesWithTransform(
        TextureRegion region, float x, float y, float width, float height,
        byte rotation, boolean flipH, boolean flipV
    ) {
        float x1 = y;
        float y1 = x;
        float x2 = x1 + region.getRegionWidth() / PX_PER_METER;
        float y2 = y1 + region.getRegionHeight() / PX_PER_METER;

        // Get base UVs
        float u1 = region.getU();
        float v1 = region.getV2();
        float u2 = region.getU2();
        float v2 = region.getV();

        // Apply horizontal flip
        if (flipH) {
            float temp = u1;
            u1 = u2;
            u2 = temp;
        }

        // Apply vertical flip
        if (flipV) {
            float temp = v1;
            v1 = v2;
            v2 = temp;
        }

        // Apply rotation
        if (rotation != 0) {
            float tempU, tempV;
            switch (rotation) {
                case 1: // 90° clockwise
                    tempU = u1; u1 = u2; u2 = tempU;
                    tempV = v1; v1 = v2; v2 = tempV;
                    break;
                case 2: // 180°
                    float temp = u1; u1 = u2; u2 = temp;
                    temp = v1; v1 = v2; v2 = temp;
                    break;
                case 3: // 270° clockwise
                    tempU = u1; u1 = u2; u2 = tempU;
                    tempV = v1; v1 = v2; v2 = tempV;
                    break;
            }
        }

        float color = Color.WHITE_FLOAT_BITS;

        float[] vertices = new float[20];
        vertices[X1] = x1; vertices[Y1] = y1; vertices[C1] = color; vertices[U1] = u1; vertices[V1] = v1;
        vertices[X2] = x1; vertices[Y2] = y2; vertices[C2] = color; vertices[U2] = u1; vertices[V2] = v2;
        vertices[X3] = x2; vertices[Y3] = y2; vertices[C3] = color; vertices[U3] = u2; vertices[V3] = v2;
        vertices[X4] = x2; vertices[Y4] = y1; vertices[C4] = color; vertices[U4] = u2; vertices[V4] = v1;

        return vertices;
    }

    public void render(SpriteBatch batch, float delta) {
        if (isDirty || hasAnyDirtyTiles) {
            prebuildVertices();
        }

        final Color batchColor = batch.getColor();
        final float originalAlpha = batchColor.a;

        if (opacity < 1.0f) {
            batchColor.a *= opacity;
            batch.setColor(batchColor);
        }

        for (Map.Entry<Texture, float[]> entry : batchedVertices.entrySet()) {
            batch.draw(entry.getKey(), entry.getValue(), 0, entry.getValue().length);
        }

        if (opacity < 1.0f) {
            batchColor.a = originalAlpha;
            batch.setColor(batchColor);
        }
    }

    /**
     * Efficiently updates a single tile without rebuilding entire chunk.
     */
    public void updateTile(int localX, int localY, String baseTile, int textureIndex) {
        if (localX >= 0 && localX < chunkData.getWidth() &&
            localY >= 0 && localY < chunkData.getHeight()) {
            dirtyTiles[localX][localY] = true;
            hasAnyDirtyTiles = true;
        }
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0, Math.min(1, opacity));
    }

    public float getOpacity() {
        return opacity;
    }

    public GridPoint2 getLocation() {
        return chunkData.getLocation();
    }

    public void dispose() {
        batchedVertices.clear();
    }
}
