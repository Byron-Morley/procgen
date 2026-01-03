package com.liquidpixel.example.factories;


import com.liquidpixel.example.components.PositionComponent;
import com.liquidpixel.example.components.RenderComponent;
import com.liquidpixel.example.components.StatusComponent;
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
