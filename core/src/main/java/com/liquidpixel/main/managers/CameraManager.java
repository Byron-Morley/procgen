package com.liquidpixel.main.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.liquidpixel.core.components.core.PlayerControlComponent;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.VelocityComponent;
import com.liquidpixel.core.components.core.camera.CameraComponent;
import com.liquidpixel.core.components.core.camera.CameraFocusComponent;
import com.liquidpixel.core.core.CoreCameraManager;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.main.components.StatusComponent;

public class CameraManager extends CoreCameraManager {

    public CameraManager() {
        super();
        createCamera();
    }

    private void createCamera() {
        Engine engine = GameResources.get().getEngine();
        Entity camera = engine.createEntity();
        camera.add(new PlayerControlComponent())
            .add(new PositionComponent(0, 0))
            .add(new CameraComponent())
            .add(new StatusComponent())
            .add(new CameraFocusComponent())
            .add(new VelocityComponent(10));

        engine.addEntity(camera);
    }
}
