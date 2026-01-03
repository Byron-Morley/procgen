package com.liquidpixel.example.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.camera.CameraComponent;
import com.liquidpixel.core.engine.GameResources;
import com.liquidpixel.example.components.CameraRouteComponent;

public class CameraRouteSystem extends IteratingSystem {

    public CameraRouteSystem() {
        super(Family.all(
            CameraComponent.class,
            PositionComponent.class,
            CameraRouteComponent.class
        ).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraRouteComponent routeComponent = entity.getComponent(CameraRouteComponent.class);

        if (!routeComponent.isEnabled()) {
            return;
        }

        // Update the route angle
        routeComponent.update(deltaTime);

        // Get the new position on the circular path
        Vector2 newPosition = routeComponent.getCurrentPosition();

        // Update entity position
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        positionComponent.setPosition(newPosition);

        // Update camera position
        OrthographicCamera camera = GameResources.get().getCamera();
        camera.position.set(newPosition.x, newPosition.y, camera.position.z);
        camera.update();
    }
}
