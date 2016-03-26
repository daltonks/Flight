package com.github.daltonks.engine.world.ui;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public class HoverColorTextBoxUIBoundsComponent extends UIBoundsComponent {
    private Color initialColor, hoverColor;

    public HoverColorTextBoxUIBoundsComponent(Entity entity, Color initialColor, Color hoverColor) {
        super(entity);
        init(initialColor, hoverColor);
    }

    public HoverColorTextBoxUIBoundsComponent(Entity entity, float xScale, float yScale, Color initialColor, Color hoverColor) {
        super(entity, xScale, yScale);
        init(initialColor, hoverColor);
    }

    public HoverColorTextBoxUIBoundsComponent(Entity entity, float width, float height, float xScale, float yScale, Color initialColor, Color hoverColor) {
        super(entity, width, height, xScale, yScale);
        init(initialColor, hoverColor);
    }

    private void init(Color initialColor, Color hoverColor) {
        this.initialColor = initialColor;
        this.hoverColor = hoverColor;
    }

    public void onHover(EngineState engineState, float openGLX, float openGLY) {
        ((TextBoxEntity.TextBoxDrawableComponent) getEntity().getDrawableComponent()).setTextColor(hoverColor);
    }

    public void onHoverStopped(EngineState engineState, float openGLX, float openGLY) {
        ((TextBoxEntity.TextBoxDrawableComponent) getEntity().getDrawableComponent()).setTextColor(initialColor);
    }
}