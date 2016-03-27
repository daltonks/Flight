//generates a singular model for a random starscape

package com.github.daltonks.game.World.models;

import com.badlogic.gdx.graphics.GL20;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;

public class StarGenerator {
    private static final int TOTAL_STARS = 300;

    public static void initStars() {
        Model starModel = new Model("stars");

        short[] indices = new short[TOTAL_STARS * 3];
        for(short i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        starModel.updateIndicesAndDrawingMode(indices, GL20.GL_TRIANGLES);

        starModel.setNumOfVertices(TOTAL_STARS * 3);

        for(int i = 0; i < TOTAL_STARS; i++) {
            Vec3d loc = EngineMath.getRandomPointOnSphereSurfaceNew(800);
            float x = loc.xf();
            float y = loc.yf();
            float z = loc.zf();
            Pools.recycle(loc);

            addRandVertex(starModel, x, y, z);
            addRandVertex(starModel, x, y, z);
            addRandVertex(starModel, x, y, z);
        }

        Models.addModel(starModel);
    }

    private static void addRandVertex(Model model, float x, float y, float z) {
        Vec3d normal = Pools.getVec3d();
        normal.set(-x, -y, -z);
        normal.normalize();
        model.addVertex(
                x + randomOffset(), y + randomOffset(), z + randomOffset(),
                randomColor(), randomColor(), randomColor(),
                normal.xf(), normal.yf(), normal.zf()
        );
        Pools.recycle(normal);
    }

    private static float randomColor() {
        return (float) ((Math.random() + 1) / 2);
    }

    private static float randomOffset() {
        return (float) ((Math.random() - .5) * 10);
    }
}