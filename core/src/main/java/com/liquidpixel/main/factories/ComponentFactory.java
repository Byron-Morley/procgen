package com.liquidpixel.main.factories;


import com.liquidpixel.main.components.PositionComponent;
import com.liquidpixel.main.components.RenderComponent;
import com.liquidpixel.main.components.StatusComponent;
import com.liquidpixel.sprite.api.component.IPositionComponent;
import com.liquidpixel.sprite.api.component.IRenderComponent;
import com.liquidpixel.sprite.api.component.IStatusComponent;
import com.liquidpixel.sprite.api.factory.IComponentFactory;

public class ComponentFactory implements IComponentFactory {

    @Override
    public IPositionComponent createPositionComponent(float x, float y) {
        return new PositionComponent(x, y);
    }

    @Override
    public IRenderComponent createRenderComponent() {
        return new RenderComponent();
    }

    @Override
    public IStatusComponent createStatusComponent() {
        return new StatusComponent();
    }
}
