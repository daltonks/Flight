package com.github.daltonks.game.states;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.world.ui.HoverColorTextBoxUIBoundsComponent;
import com.github.daltonks.engine.world.ui.TextBoxEntity;
import com.github.daltonks.engine.world.ui.UIEntity;
import com.github.daltonks.game.World.items.base.Items;
import com.github.daltonks.game.World.entities.livingentities.LivingEntityInfos;
import com.github.daltonks.game.World.saveandload.SaveAndLoadHandler;

import java.io.IOException;

public class MainMenuEngineState extends MenuEngineState {

    @Override
    public void beforeModelGeneration() {
        GameTransition.init();
    }

    @Override
    public void init() {
        Items.init();
        LivingEntityInfos.init();

        super.init();

        TextBoxEntity startGameText = new TextBoxEntity(this, 0, 0, 0, "Start Game", 500, 1, Color.RED);
        startGameText.glueTo(UIEntity.Glue.UP, 0, 0);
        startGameText.setUIBoundsComponent(new HoverColorTextBoxUIBoundsComponent(
                startGameText, startGameText.getTextBoxDrawableComponent().getWidth(), startGameText.getTextBoxDrawableComponent().getHeight(),
                1, 1, startGameText.getTextBoxDrawableComponent().getTextColor(), Color.WHITE) {

            @Override
            public boolean onCollidedUp(EngineState engineState, float openGLX, float openGLY) {
                super.onCollidedUp(engineState, openGLX, openGLY);

                GameEngineState gameActivity = (GameEngineState) Engine.INSTANCE.getSubActivity("game");
                gameActivity.resetEngineWorld();
                Engine.INSTANCE.setCurrentSubActivity("game", new GameTransition());

                try {
                    if(!SaveAndLoadHandler.load()) {
                        gameActivity.getEngineWorld().initNewGame();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
                return true;
            }
        });
        addUIEntity(startGameText);
    }

    public void onNewSurfaceDimensions(int width, int height) {
        super.onNewSurfaceDimensions(width, height);
        GameTransition.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}