package com.liquidpixel.teavm;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildReflectionListener;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.liquidpixel.main.models.Item;
import com.liquidpixel.main.models.ItemList;
import com.liquidpixel.main.models.Slot;
import com.liquidpixel.main.models.SlotList;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

/**
 * Builds the TeaVM/HTML application.
 */
public class TeaVMBuilder {
    /**
     * A single point to configure most debug vs. release settings.
     * This defaults to false in new projects; set this to false when you want to release.
     * If this is true, the output will not be obfuscated, and debug information will usually be produced.
     * You can still set obfuscation to false in a release if you want the source to be at least a little legible.
     * This works well when the targetType is set to JAVASCRIPT, but you can still set the targetType to WEBASSEMBLY_GC
     * while this is true in order to test that higher-performance target before releasing.
     */
    private static final boolean DEBUG = true;

    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new AssetFileHandle("../assets"));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();

        // Register any extra classpath assets here:
        teaBuildConfiguration.assetsClasspath.add("com/kotcrab/vis/ui/skin");
        teaBuildConfiguration.assetsClasspath.add("assets/ui/skin/golden-gate");


        teaBuildConfiguration.reflectionListener = new TeaBuildReflectionListener() {
            @Override
            public boolean shouldEnableReflection(String fullClassName) {
                if (fullClassName.startsWith("com.kotcrab.vis.ui") && fullClassName.endsWith("Style")) {
                    return true;
                }
                if (fullClassName.equals("com.kotcrab.vis.ui.Sizes")) return true;
                if (fullClassName.equals("com.liquidpixel.sprite.model.loader.ColorRGB")) return true;
                if (fullClassName.equals("com.liquidpixel.sprite.model.loader.AnimationModel")) return true;
                if (fullClassName.equals("com.liquidpixel.sprite.model.AnimationDefinition")) return true;
                if (fullClassName.equals("com.liquidpixel.sprite.model.AnimationFrame")) return true;
                if (fullClassName.equals("com.liquidpixel.sprite.model.AnimationSequence")) return true;
                if (fullClassName.equals(ObjectMap.class.getName())) return true;
                if (fullClassName.equals(HashMap.class.getName())) return true;
                if (fullClassName.equals(SlotList.class.getName())) return true;
                if (fullClassName.equals(Slot.class.getName())) return true;
                if (fullClassName.equals(ItemList.class.getName())) return true;
                if (fullClassName.equals(Item.class.getName())) return true;
                return false;
            }
        };

        // Register any classes or packages that require reflection here:
        // TeaReflectionSupplier.addReflectionClass("com.liquidpixel.reflect");
//        TeaReflectionSupplier.addReflectionClass(Person.class.getName());

        // Used by older TeaVM versions.
//        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);


        // The next two lines are used by gdx-teavm 1.3.1 and newer (and libGDX 1.14.0 and newer).
        TeaBuilder.config(teaBuildConfiguration);
        TeaVMTool tool = new TeaVMTool();

        // JavaScript is the default target type for TeaVM, and it works better during debugging.
        tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
//        tool.setTargetType(TeaVMTargetType.WEBASSEMBLY_GC);
        // You can choose to use the WebAssembly (WASM) GC target instead, which tends to perform better, but isn't
        // as easy to debug. It might be a good idea to alternate target types during development if you plan on using
        // WASM at release time.
//        tool.setTargetType(TeaVMTargetType.WEBASSEMBLY_GC);

        tool.setMainClass(TeaVMLauncher.class.getName());
        // For many (or most) applications, using a high optimization won't add much to build time.
        // If your builds take too long, and runtime performance doesn't matter, you can change ADVANCED to SIMPLE .
        tool.setOptimizationLevel(TeaVMOptimizationLevel.ADVANCED);
        // The line below should use tool.setObfuscated(false) if you want clear debugging info.
        // You can change it to tool.setObfuscated(true) when you are preparing to release, to try to hide your original code.
        tool.setObfuscated(!DEBUG);

        // If targetType is set to JAVASCRIPT, you can use the following lines to debug JVM languages from the browser,
        // setting breakpoints in Java code and stopping in the appropriate place in generated JavaScript code.
        // These settings don't quite work currently if generating WebAssembly. They may in a future release.
        if (DEBUG && tool.getTargetType() == TeaVMTargetType.JAVASCRIPT) {
            tool.setDebugInformationGenerated(true);
            tool.setSourceMapsFileGenerated(true);
            tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);
            tool.addSourceFileProvider(new DirectorySourceFileProvider(new File("../core/src/main/java/")));
        }

        TeaBuilder.build(tool);
    }
}
