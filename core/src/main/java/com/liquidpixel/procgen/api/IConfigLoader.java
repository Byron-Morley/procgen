package com.liquidpixel.procgen.api;

import com.liquidpixel.procgen.models.TilesetConfig;

public interface IConfigLoader {
    TilesetConfig load(String configPath);
}
