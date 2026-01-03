package com.liquidpixel.procgen;

import com.liquidpixel.procgen.api.IWorld;
import com.liquidpixel.procgen.builders.World;
import com.liquidpixel.procgen.generators.ImageGenerator;

public class ProcGen {
    public static World.Builder createWorld() {
        return new World.Builder();
    }

    // Convenience methods
    public static IWorld createDefaultWorld(long seed) {
        return new World.Builder()
            .seed(seed)
            .build();
    }

    public static void main(String[] args) {
        generate(256, 256, 32, 32, System.currentTimeMillis(), "output");
    }



    public static void generate(int width, int height, int ChunkWidth, int ChunkHeight, long seed, String outputDir) {
       new ImageGenerator();
    }
}
