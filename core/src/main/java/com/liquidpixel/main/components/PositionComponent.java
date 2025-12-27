
package com.liquidpixel.main.components;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.liquidpixel.sprite.api.component.IPositionComponent;

import static com.liquidpixel.core.utils.PositionUtils.reduceToCell;

public class PositionComponent implements IPositionComponent {
    private GridPoint2 previousPosition;
    private float x;
    private float y;

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PositionComponent(GridPoint2 previousPosition, float x, float y) {
        this.previousPosition = previousPosition;
        this.x = x;
        this.y = y;
    }

    public PositionComponent(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public PositionComponent(GridPoint2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setPosition(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    @Override
    public void setPosition(GridPoint2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    @Override
    public GridPoint2 getGridPosition() {
        return reduceToCell(new Vector2(x, y));
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public GridPoint2 getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(GridPoint2 previousPosition) {
        this.previousPosition = previousPosition;
    }
}
