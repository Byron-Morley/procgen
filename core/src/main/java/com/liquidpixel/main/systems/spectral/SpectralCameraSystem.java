package com.liquidpixel.main.systems.spectral;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.core.api.services.ICameraService;
import com.liquidpixel.core.api.services.IPlayerInputService;
import com.liquidpixel.core.api.ui.IUIService;
import com.liquidpixel.core.components.core.PlayerControlComponent;
import com.liquidpixel.core.components.core.PositionComponent;
import com.liquidpixel.core.components.core.camera.CameraComponent;
import com.liquidpixel.core.core.Direction;
import com.liquidpixel.core.engine.GameResources;

import java.util.LinkedList;
import java.util.Stack;

public class SpectralCameraSystem extends IteratingSystem {

    private final IPlayerInputService playerInputService;
    private final ICameraService cameraService;
    private final IUIService uiService;

    // Zoom State
    float minimumZoom = 0.005f;
    float maximumZoom = 0.1f;
    float zoomAmount = 0.01f;

    // Camera drag state
    private boolean isDragging = false;
    private final Vector2 lastMousePosition = new Vector2();
    private float dragSpeed = 1.0f; // Adjust this for drag sensitivity

    // Camera WASD movement
    private float keyboardMoveSpeed = 500f;
    private final Vector2 movementVector;

    public SpectralCameraSystem(IPlayerInputService playerInputService, ICameraService cameraService, IUIService uiService) {
        super(Family.all(PlayerControlComponent.class, PositionComponent.class, CameraComponent.class).get());
        this.playerInputService = playerInputService;
        this.cameraService = cameraService;
        this.uiService = uiService;
        movementVector = new Vector2();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        handleCameraDrag(entity);
        handleCameraMovement(entity, deltaTime);
        processZoom();
    }

    private void handleCameraDrag(Entity cameraEntity) {
        if (uiService.isMouseOverUI()) {
            // Don't drag camera when mouse is over UI
            isDragging = false;
            return;
        }

        boolean middlePressed = playerInputService.isMiddleMousePressed();
        Vector2 currentMousePosition = playerInputService.getCurrentMousePosition();
        OrthographicCamera camera = GameResources.get().getCamera();
        PositionComponent cameraPosition = cameraEntity.getComponent(PositionComponent.class);

        if (middlePressed && !isDragging) {
            // Start dragging
            isDragging = true;
            lastMousePosition.set(currentMousePosition);
        } else if (!middlePressed && isDragging) {
            // Stop dragging
            isDragging = false;
        } else if (isDragging) {
            // Continue dragging
            Vector2 mouseDelta = new Vector2(
                currentMousePosition.x - lastMousePosition.x,
                currentMousePosition.y - lastMousePosition.y
            );

            // Convert screen delta to world delta (accounting for zoom)
            float worldDeltaX = -mouseDelta.x * camera.zoom * dragSpeed;
            float worldDeltaY = mouseDelta.y * camera.zoom * dragSpeed; // Flip Y axis

            // Update camera position
            Vector2 newCameraPosition = cameraPosition.getPosition().add(worldDeltaX, worldDeltaY);
            cameraPosition.setPosition(newCameraPosition);

            // Update the camera
            camera.position.set(newCameraPosition.x, newCameraPosition.y, camera.position.z);
            camera.update();

            lastMousePosition.set(currentMousePosition);
        }
    }

    private void handleCameraMovement(Entity cameraEntity, float deltaTime) {
        Stack<Direction> movementKeys = playerInputService.getMovementKeysPressed();
        if (movementKeys.isEmpty()) return;

        float moveX = 0f;
        float moveY = 0f;

        if (movementKeys.contains(Direction.UP)) moveY += 1;
        if (movementKeys.contains(Direction.DOWN)) moveY -= 1;
        if (movementKeys.contains(Direction.LEFT)) moveX -= 1;
        if (movementKeys.contains(Direction.RIGHT)) moveX += 1;

        if (moveX == 0 && moveY == 0) return;

        OrthographicCamera camera = GameResources.get().getCamera();
        PositionComponent cameraPosition = cameraEntity.getComponent(PositionComponent.class);

        movementVector.set(moveX, moveY).nor();
        float currentSpeed = keyboardMoveSpeed * camera.zoom * deltaTime;
        float worldDeltaX = movementVector.x * currentSpeed;
        float worldDeltaY = movementVector.y * currentSpeed;

        Vector2 newCameraPosition = cameraPosition.getPosition().add(worldDeltaX, worldDeltaY);
        cameraPosition.setPosition(newCameraPosition);

        camera.position.set(newCameraPosition.x, newCameraPosition.y, camera.position.z);
        camera.update();
    }

    private void processZoom() {
        LinkedList<Float> scrollQueue = playerInputService.getScrollQueue();

        if (!scrollQueue.isEmpty()) {
            float scroll = scrollQueue.poll();

            OrthographicCamera camera = GameResources.get().getCamera();

            if (scroll > 0 && camera.zoom < maximumZoom) {
                camera.zoom += zoomAmount;
            } else if (scroll < 0 && camera.zoom > minimumZoom) {
                camera.zoom -= zoomAmount;
            }

            camera.update();
            camera.zoom = MathUtils.clamp(camera.zoom, minimumZoom, maximumZoom);
        }
    }

    public void setDragSpeed(float speed) {
        this.dragSpeed = speed;
    }

    public void setKeyboardMoveSpeed(float keyboardMoveSpeed) {
        this.keyboardMoveSpeed = keyboardMoveSpeed;
    }
}
