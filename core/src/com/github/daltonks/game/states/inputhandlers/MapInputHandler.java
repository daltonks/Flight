package com.github.daltonks.game.states.inputhandlers;

import com.github.daltonks.engine.states.inputevents.ClickTracker;
import com.github.daltonks.engine.states.inputevents.DragEvent;
import com.github.daltonks.engine.states.inputevents.EngineInputHandler;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.ViewMatrix;
import com.github.daltonks.game.World.engineworlds.MapEngineWorld;
import com.github.daltonks.game.states.MapEngineState;

import java.util.LinkedList;

public class MapInputHandler extends EngineInputHandler<MapEngineState> {

    public MapInputHandler(MapEngineState state) {
        super(state);
    }

    private static double mapMoveSpeed = .002;
    @Override
    public void onDrag(DragEvent event) {
        LinkedList<ClickTracker> clickTrackers = event.getClickTrackers();
        Vec3d cameraLocation = getEngineState().getEngineWorld().getCamera().getViewMatrix().getLocation();
        double speed = mapMoveSpeed * cameraLocation.z();
        ViewMatrix viewMatrix = getEngineState().getEngineWorld().getCamera().getViewMatrix();
        if(clickTrackers.size() == 1) {
            ClickTracker clickTracker = clickTrackers.get(0);
            Vec3d loc = cameraLocation.clone();
            loc.add(-clickTracker.getDeltaX() * speed, clickTracker.getDeltaY() * speed, 0);
            viewMatrix.setLocation(loc);
            Pools.recycle(loc);
        } else {
            double pinchDelta = event.getPinchDelta();
            double z = cameraLocation.z() - (pinchDelta * speed);
            if(z < MapEngineWorld.MIN_CAMERA_Z) {
                z = MapEngineWorld.MIN_CAMERA_Z;
            } else if(z > MapEngineWorld.MAX_CAMERA_Z) {
                z = MapEngineWorld.MAX_CAMERA_Z;
            }

            Vec3d loc = cameraLocation.clone();
            loc.z(z);
            viewMatrix.setLocation(loc);
            Pools.recycle(loc);
        }
    }
}