package com.liquidpixel.main.systems.spectral;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.components.core.PlayerControlComponent;
import com.liquidpixel.core.components.core.VelocityComponent;
import com.liquidpixel.core.components.core.camera.CameraComponent;
import com.liquidpixel.main.components.PositionComponent;
import com.liquidpixel.main.components.StatusComponent;
import com.liquidpixel.sprite.model.Action;
import com.liquidpixel.sprite.model.Direction;


public class SpectralMovementSystem extends IteratingSystem {

    public SpectralMovementSystem() {
        super(Family.all(
            StatusComponent.class,
            PlayerControlComponent.class,
            PositionComponent.class,
            VelocityComponent.class,
            CameraComponent.class
        ).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StatusComponent statusComponent = entity.getComponent(StatusComponent.class);
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        VelocityComponent velocityComponent = entity.getComponent(VelocityComponent.class);

        int toggleX = 0;
        int toggleY = 0;

        Action action = statusComponent.getAction();
        Direction direction = statusComponent.getDirection();

        if (Action.WALKING.equals(action)) {
            switch (direction) {
                case UP:
                    toggleY = 1;
                    break;
                case DOWN:
                    toggleY = -1;
                    break;
                case LEFT:
                    toggleX = -1;
                    break;
                case RIGHT:
                    toggleX = 1;
                    break;
                case UP_LEFT:
                    toggleX = -1;
                    toggleY = 1;
                    break;
                case UP_RIGHT:
                    toggleX = 1;
                    toggleY = 1;
                    break;
                case DOWN_LEFT:
                    toggleX = -1;
                    toggleY = -1;
                    break;
                case DOWN_RIGHT:
                    toggleX = 1;
                    toggleY = -1;
                    break;
            }
        }

        float speed = velocityComponent.getVelocity();

        if (toggleX != 0 && toggleY != 0) {
            speed = speed / (float) Math.sqrt(2);
        }
        float x = speed * toggleX * deltaTime;
        float y = speed * toggleY * deltaTime;

        Vector2 newPosition = positionComponent.getPosition().add(new Vector2(x, y));
        positionComponent.setPosition(newPosition);
    }
}
