package com.liquidpixel.main.components;

import com.liquidpixel.sprite.api.component.IStatusComponent;
import com.liquidpixel.sprite.model.Action;
import com.liquidpixel.sprite.model.Direction;

public class StatusComponent implements IStatusComponent {

    private Action action;

    private Direction direction;

    private boolean changeInActionOrDirection;

    public StatusComponent(
        Action action,
        Direction direction) {
        this.action = action;
        this.direction = direction;
        this.changeInActionOrDirection = false;
    }

    public StatusComponent() {
        this.action = Action.STANDING;
        this.direction = Direction.DOWN;
        this.changeInActionOrDirection = false;
    }

    public void setAction(Action action) {
        changeInActionOrDirection = action != this.action;
        this.action = action;
    }

    public void setDirection(Direction direction) {
        changeInActionOrDirection = direction != this.direction;
        this.direction = direction;
    }

    public Action getAction() {
        return this.action;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isChangeInActionOrDirection() {
        return changeInActionOrDirection;
    }

    public void setChangeInActionOrDirection(boolean changeInActionOrDirection) {
        this.changeInActionOrDirection = changeInActionOrDirection;
    }
}
