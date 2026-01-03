package com.liquidpixel.example.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class CameraRouteComponent implements Component {
    private float radius;
    private Vector2 center;
    private float speed; // degrees per second
    private float currentAngle; // current position on the circle in degrees
    private boolean enabled;

    /**
     * Creates a circular camera route component
     * @param centerX X coordinate of the circle center
     * @param centerY Y coordinate of the circle center
     * @param radius Radius of the circular path
     * @param speed Speed in degrees per second (360 = one full rotation per second)
     */
    public CameraRouteComponent(float centerX, float centerY, float radius, float speed) {
        this.center = new Vector2(centerX, centerY);
        this.radius = radius;
        this.speed = speed;
        this.currentAngle = 0;
        this.enabled = true;
    }

    public CameraRouteComponent() {
        // Default: center at origin, radius 1000, slow speed (full circle in ~2 minutes)
        this(0, 0, 1000, 3f);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(float x, float y) {
        this.center.set(x, y);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Calculate the current position on the circular path
     */
    public Vector2 getCurrentPosition() {
        float radians = (float) Math.toRadians(currentAngle);
        float x = center.x + radius * (float) Math.cos(radians);
        float y = center.y + radius * (float) Math.sin(radians);
        return new Vector2(x, y);
    }

    /**
     * Advance the angle based on time
     */
    public void update(float deltaTime) {
        if (enabled) {
            currentAngle += speed * deltaTime;
            // Wrap angle to 0-360 range
            if (currentAngle >= 360) {
                currentAngle -= 360;
            }
        }
    }
}
