package com.liquidpixel.procgen.modules;

import com.liquidpixel.core.engine.GameState;
import com.liquidpixel.procgen.api.INoiseModule;
import com.liquidpixel.procgen.api.IWorldContext;
import com.liquidpixel.procgen.models.TerrainDefinition;
import com.liquidpixel.procgen.models.TerrainItem;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleScaleDomain;


import static com.sudoplay.joise.module.ModuleBasisFunction.BasisType.GRADIENT;
import static com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType.CUBIC;
import static com.sudoplay.joise.module.ModuleFractal.FractalType.FBM;

public class TerrainModule implements INoiseModule {

    IWorldContext context;
    Module module;

    public TerrainModule() {
        module = getFractalModule(GRADIENT, CUBIC, FBM);
    }

    @Override
    public void init(IWorldContext context) {
        this.context = context;
    }

    private ModuleAutoCorrect getFractalModule(
        ModuleBasisFunction.BasisType basisType,
        ModuleBasisFunction.InterpolationType interpolationType,
        ModuleFractal.FractalType fractalType
    ) {
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(basisType);
        gen.setAllSourceInterpolationTypes(interpolationType);
        gen.setNumOctaves(5);
        gen.setFrequency(2.34);
        gen.setType(fractalType);
        gen.setSeed(GameState.getSeed());

        ModuleScaleDomain moduleScaleDomain = new ModuleScaleDomain();
        moduleScaleDomain.setSource(gen);
        moduleScaleDomain.setScaleX(0.5);
        moduleScaleDomain.setScaleY(0.5);

        ModuleAutoCorrect source = new ModuleAutoCorrect(0, 1);
        source.setSource(moduleScaleDomain);
        source.setSamples(10000);
        source.calculate2D();
        return source;
    }

    public TerrainDefinition generateAt(int x, int y) {
        float tileValue = calculateTileValue(x, y);
        return context.getTerrainType(tileValue);
    }

    @Override
    public int getVariant(TerrainDefinition terrain) {
        return context.getVariant(terrain);
    }

    @Override
    public TerrainItem getItem(TerrainDefinition terrain) {
        return context.getItem(terrain);
    }

    public float calculateTileValue(int x, int y) {
        float px = x / 128f * 1;
        float py = y / 128f * 1;
        float tileValue = (float) module.get(px, py);
        return Math.max(0, Math.min(1, tileValue));
    }
}
