package com.liquidpixel.procgen.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.liquidpixel.procgen.api.IConfigLoader;
import com.liquidpixel.procgen.models.TilesetConfig;

public class DefaultConfigLoader implements IConfigLoader {

    @Override
    public TilesetConfig load(String configPath) {
        Json json = new Json();
        String jsonContent = Gdx.files.internal(configPath).readString();
        return json.fromJson(TilesetConfig.class, jsonContent);
    }
}
