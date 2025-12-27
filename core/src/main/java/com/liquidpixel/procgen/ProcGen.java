package com.liquidpixel.procgen;

import com.liquidpixel.procgen.api.IWorld;
import com.liquidpixel.procgen.builders.World;

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
}
