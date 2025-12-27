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
 * Renderable chunk that efficiently batches tiles by texture.
 * Handles all the complexity of tile rendering with proper batching.
 */
class RenderableChunk {

    private static final float PX_PER_METER = 16f; // Or configure this

    private final ChunkData chunkData;
    private final GridPoint2 worldPosition; // Position in world coordinates
    private final int tileWidth;
    private final int tileHeight;
    private final ISpriteFactory spriteFactory;

    // Pre-computed rendering data grouped by texture for efficient batching
    private final Map<Texture, float[]> batchedVertices;
    private boolean isDirty = true;
    private float opacity = 1.0f;

    RenderableChunk(ChunkData chunkData, IWorldContext context, ISpriteFactory spriteFactory) {
        this.chunkData = chunkData;
        this.spriteFactory = spriteFactory;
        this.tileWidth = (int) PX_PER_METER;
        this.tileHeight = (int) PX_PER_METER;

        // Calculate world position
        this.worldPosition = new GridPoint2(
            chunkData.getLocation().x * chunkData.getWidth(),
            chunkData.getLocation().y * chunkData.getHeight()
        );

        this.batchedVertices = new HashMap<>();
        prebuildVertices();
    }

    /**
     * Pre-builds all vertices for this chunk, grouped by texture.
     * This is done once when the chunk is created, then reused for every render.
     */
    private void prebuildVertices() {
        // Temporary storage for vertices grouped by texture
        Map<Texture, Array<float[]>> tempVerticesByTexture = new HashMap<>();

        final float layerTileWidth = tileWidth / PX_PER_METER;
        final float layerTileHeight = tileHeight / PX_PER_METER;
        final int width = chunkData.getWidth();
        final int height = chunkData.getHeight();

        // Calculate rendering bounds
        final int col1 = worldPosition.y;
        final int col2 = height + worldPosition.y;
        final int row1 = worldPosition.x;
        final int row2 = width + worldPosition.x;

        // Iterate through tiles in rendering order
        float y = row2 * layerTileHeight;
        float xStart = col1 * layerTileWidth;

        for (int row = row2; row >= row1; row--) {
            float x = xStart;
            int localX = row - worldPosition.x;

            for (int col = col1; col < col2; col++) {
                int localY = col - worldPosition.y;

                // Get the tile data
                if (localX >= 0 && localX < width && localY >= 0 && localY < height) {
                    TerrainTile tile = chunkData.getTile(localX, localY);

                    if (tile != null) {
                        // Get texture region from sprite factory
                        TextureRegion region = spriteFactory.getTextureWithFallback(
                            tile.getTerrain().getBaseTile(),
                            tile.getTextureIndex()
                        );

                        if (region != null) {
                            // Build vertices for this tile
                            float[] vertices = buildTileVertices(region, x, y, layerTileWidth, layerTileHeight);

                            // Group by texture for batching
                            Texture texture = region.getTexture();
                            tempVerticesByTexture.computeIfAbsent(texture, k -> new Array<>()).add(vertices);
                        }
                    }
                }

                x += layerTileWidth;
            }

            y -= layerTileHeight;
        }

        // Consolidate vertices into single arrays per texture
        for (Map.Entry<Texture, Array<float[]>> entry : tempVerticesByTexture.entrySet()) {
            Texture texture = entry.getKey();
            Array<float[]> tileVertices = entry.getValue();

            // Calculate total size
            int totalVertices = tileVertices.size * 20; // 20 floats per tile (4 vertices * 5 components)
            float[] combinedVertices = new float[totalVertices];

            // Copy all tile vertices into single array
            int offset = 0;
            for (float[] vertices : tileVertices) {
                System.arraycopy(vertices, 0, combinedVertices, offset, 20);
                offset += 20;
            }

            batchedVertices.put(texture, combinedVertices);
        }

        isDirty = false;
    }

    /**
     * Builds the 20 vertex components for a single tile.
     * Format: [x, y, color, u, v] * 4 vertices (quad)
     */
    private float[] buildTileVertices(TextureRegion region, float x, float y, float width, float height) {
        // Calculate positions (can add tile offset here if needed)
        float x1 = y; // Note: swapped for isometric-style rendering
        float y1 = x;
        float x2 = x1 + region.getRegionWidth() / PX_PER_METER;
        float y2 = y1 + region.getRegionHeight() / PX_PER_METER;

        // Get UVs
        float u1 = region.getU();
        float v1 = region.getV2();
        float u2 = region.getU2();
        float v2 = region.getV();

        // Pack color (white by default, will be modulated in render)
        float color = Color.WHITE_FLOAT_BITS;

        // Build vertex array
        float[] vertices = new float[20];

        // Bottom-left vertex
        vertices[X1] = x1;
        vertices[Y1] = y1;
        vertices[C1] = color;
        vertices[U1] = u1;
        vertices[V1] = v1;

        // Top-left vertex
        vertices[X2] = x1;
        vertices[Y2] = y2;
        vertices[C2] = color;
        vertices[U2] = u1;
        vertices[V2] = v2;

        // Top-right vertex
        vertices[X3] = x2;
        vertices[Y3] = y2;
        vertices[C3] = color;
        vertices[U3] = u2;
        vertices[V3] = v2;

        // Bottom-right vertex
        vertices[X4] = x2;
        vertices[Y4] = y1;
        vertices[C4] = color;
        vertices[U4] = u2;
        vertices[V4] = v1;

        return vertices;
    }

    /**
     * Renders this chunk with the given batch.
     * Very efficient - just draws pre-computed vertices grouped by texture.
     */
    public void render(SpriteBatch batch, float delta) {
        if (isDirty) {
            prebuildVertices();
        }

        // Apply opacity if needed
        final Color batchColor = batch.getColor();
        final float originalAlpha = batchColor.a;

        if (opacity < 1.0f) {
            batchColor.a *= opacity;
            batch.setColor(batchColor);
        }

        // Draw all tiles grouped by texture (minimal texture switches)
        for (Map.Entry<Texture, float[]> entry : batchedVertices.entrySet()) {
            Texture texture = entry.getKey();
            float[] vertices = entry.getValue();

            // Single draw call per texture!
            batch.draw(texture, vertices, 0, vertices.length);
        }

        // Restore original alpha
        if (opacity < 1.0f) {
            batchColor.a = originalAlpha;
            batch.setColor(batchColor);
        }
    }

    /**
     * Updates a single tile's texture and marks the chunk as dirty.
     * Use this when terrain is modified after generation.
     */
    public void updateTile(int localX, int localY, String baseTile, int textureIndex) {
        // This would require rebuilding vertices for the affected tile
        // Mark as dirty to trigger full rebuild on next render
        isDirty = true;
    }

    /**
     * Sets the opacity of this chunk (for fade effects).
     */
    public void setOpacity(float opacity) {
        this.opacity = Math.max(0, Math.min(1, opacity));
    }

    public float getOpacity() {
        return opacity;
    }

    public GridPoint2 getLocation() {
        return chunkData.getLocation();
    }

    public GridPoint2 getWorldPosition() {
        return worldPosition;
    }

    public ChunkData getChunkData() {
        return chunkData;
    }

    /**
     * Call this when the chunk is no longer needed.
     */
    public void dispose() {
        batchedVertices.clear();
    }
}
