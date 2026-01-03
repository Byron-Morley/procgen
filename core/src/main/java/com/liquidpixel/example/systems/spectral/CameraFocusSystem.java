package com.liquidpixel.example.systems.spectral;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.liquidpixel.core.api.services.ICameraService;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.camera.CameraFocusComponent;

public class CameraFocusSystem extends IteratingSystem {
    ICameraService cameraService;

    public CameraFocusSystem(ICameraService cameraService) {
        super(Family.all(CameraFocusComponent.class, PositionComponent.class).get());
        this.cameraService = cameraService;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = entity.getComponent(PositionComponent.class);
        cameraService.setCameraPosition(position.getX(), position.getY());
    }
}
