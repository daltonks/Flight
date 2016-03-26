//main SubActivity that handles and interacts with the game

package com.github.daltonks.game.subactivities;

import android.opengl.GLES20;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.engine.world.ui.TextBoxEntity;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;
import com.github.daltonks.engine.world.ui.UIEntity;
import com.github.daltonks.game.Finals;
import com.github.daltonks.game.World.engineworlds.GameEngineWorld;
import com.github.daltonks.game.World.items.base.ItemSlot;
import com.github.daltonks.game.World.livingentities.LivingEntity;
import com.github.daltonks.game.World.livingentities.Player;
import com.github.daltonks.game.World.models.StarGenerator;
import com.github.daltonks.game.World.saveandload.SaveAndLoadHandler;
import com.github.daltonks.game.World.ui.JoystickBodyBounds;

import java.io.IOException;
import java.util.ArrayList;

public class GameEngineState extends EngineState {
    public static TextBoxEntity debugText;
    public static UIEntity joystick, joystickBody;
	private static ItemSlot[] activeInventorySlots = new ItemSlot[Finals.NUM_OF_ACTIVE_INVENTORY_SLOTS];

    @Override
    public void beforeModelGeneration() {
        StarGenerator.initStars();
    }

    @Override
    public void init() {
        CursorDrawing.init();

        UIEntity menuButton = new UIEntity(this, Models.get("ingamemenubutton"));
        menuButton.setUIBoundsComponent(new UIBoundsComponent(menuButton) {
            public boolean onCollidedUp(EngineState engineState, float openGLX, float openGLY) {
                Engine.INSTANCE.setCurrentSubActivity("map", new GameTransition());
                return true;
            }
        });
        menuButton.glueTo(UIEntity.Glue.UP, UIEntity.Glue.LEFT, 0, 0);
        addUIEntity(menuButton);

        joystickBody = new UIEntity(this, Models.get("joystickbody"));
        joystickBody.setUIBoundsComponent(new JoystickBodyBounds(joystickBody));
        joystickBody.glueTo(UIEntity.Glue.LEFT, UIEntity.Glue.DOWN, 0, 0);
        //joystickBody.glueTo(UIEntity.Glue.DOWN, (Finals.NUM_OF_ACTIVE_INVENTORY_SLOTS - sub) * slotWidth, 0);
        addUIEntity(joystickBody);

        joystick = new UIEntity(this, Models.get("joystick"));
        addUIEntity(joystick);

        debugText = new TextBoxEntity(this, 0, 0, 0, " ", 500, .2f, Color.PLAYER_BLUE);
        debugText.glueTo(UIEntity.Glue.UP, 0, 0);
        addUIEntity(debugText);
    }

    public void onEnterSubActivity() {
        super.onEnterSubActivity();
        resetThrottle();

        //inventory slots
        float sub = Finals.NUM_OF_ACTIVE_INVENTORY_SLOTS / 2;
        float slotWidth;
        for(int i = 0; i < Finals.NUM_OF_ACTIVE_INVENTORY_SLOTS; i++) {
            if(Finals.NUM_OF_ACTIVE_INVENTORY_SLOTS % 2 == 0) {
                sub += .5f;
            }

            ItemSlot current = activeInventorySlots[i];
            if(current != null) {
                removeUIEntity(current);
            }

            ItemSlot itemSlot = new ItemSlot(this, getEngineWorld().getPlayer().getInventory(), i);
            slotWidth = itemSlot.getModelComponent().getXLength();
            itemSlot.glueTo(UIEntity.Glue.DOWN, (i - sub) * slotWidth, 0);
            itemSlot.updateGlue(this);
            activeInventorySlots[i] = itemSlot;
            addUIEntity(itemSlot);
        }
    }

    public void onNewSurfaceDimensions(int width, int height) {
        super.onNewSurfaceDimensions(width, height);
        resetThrottle();
        CursorDrawing.updateCircleRadius(this);
    }

    protected void drawNear() {
        super.drawNear();
        drawCursors();
    }

    protected void drawFar() {
        super.drawFar();
        drawCursors();
    }

    private void drawCursors() {
        Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
        ArrayList<LivingEntity> livingEntities = getEngineWorld().getLivingEntities().getUnderlyingList();
        for(int i = 0; i < livingEntities.size(); i++) {
            LivingEntity livingEntity = livingEntities.get(i);
            if(!(livingEntity instanceof Player)) {
                CursorDrawing.drawCursor(this, livingEntity, Color.RED, getEngineWorld().getCamera(), 1, 1, true);
            }
        }
        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
    }

    public void resetEngineWorld() {
        setEngineWorld(new GameEngineWorld(this));
        getEngineWorld().construct();
    }

    public static void resetThrottle() {
        joystick.getTransformComponent().setLocation(joystickBody.getTransformComponent().getLocation());
    }

    public static Vec3d getThrottleNew() {
        Vec3d throttle = joystick.getTransformComponent().getLocation().clone();
        throttle.sub(joystickBody.getTransformComponent().getLocation());
        throttle.div(joystickBody.getModelComponent().getRadius());
        return throttle;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        try {
            SaveAndLoadHandler.save();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Vec3d getStartingUICameraOffset() {
        return new Vec3d(0, 0, 5);
    }

    public GameEngineWorld getEngineWorld() {
        return (GameEngineWorld) super.getEngineWorld();
    }
}