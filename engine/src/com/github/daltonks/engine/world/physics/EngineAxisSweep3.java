//efficient custom implementation of AxisSweep3 that doesn't create new int arrays every updateHandle(...)

package com.github.daltonks.engine.world.physics;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.Dispatcher;

import javax.vecmath.Vector3f;

public class EngineAxisSweep3 extends AxisSweep3 {
    public EngineAxisSweep3(Vector3f worldAabbMin, Vector3f worldAabbMax) {
        super(worldAabbMin, worldAabbMax);
    }

    private static int[] min = new int[3];
    private static int[] max = new int[3];
    public void updateHandle(int handle, Vector3f aabbMin, Vector3f aabbMax, Dispatcher dispatcher) {
        Handle pHandle = this.getHandle(handle);
        this.quantize(min, aabbMin, 0);
        this.quantize(max, aabbMax, 1);

        for(int axis = 0; axis < 3; ++axis) {
            int emin = pHandle.getMinEdges(axis);
            int emax = pHandle.getMaxEdges(axis);
            int dmin = min[axis] - this.pEdges[axis].getPos(emin);
            int dmax = max[axis] - this.pEdges[axis].getPos(emax);
            this.pEdges[axis].setPos(emin, min[axis]);
            this.pEdges[axis].setPos(emax, max[axis]);
            if(dmin < 0) {
                this.sortMinDown(axis, emin, dispatcher, true);
            }

            if(dmax > 0) {
                this.sortMaxUp(axis, emax, dispatcher, true);
            }

            if(dmin > 0) {
                this.sortMinUp(axis, emin, dispatcher, true);
            }

            if(dmax < 0) {
                this.sortMaxDown(axis, emax, dispatcher, true);
            }
        }
    }
}
