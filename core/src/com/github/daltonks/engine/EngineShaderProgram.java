package com.github.daltonks.engine;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.world.WorldStaticEntityInfoImporter;
import com.github.daltonks.engine.world.models.ModelImporter;
import com.github.daltonks.engine.world.models.ModelInfoImporter;

public class EngineShaderProgram {
    private static int LIGHT_POSITION_IN_VIEW_3F_UNIFORM;
    private static int COLOR_MULT_3F_UNIFORM;
    private static int MVP_MATRIX_UNIFORM;
    private static int MV_MATRIX_UNIFORM;
    private static ShaderProgram program;

    public static void init() {
        program = new ShaderProgram(
                Gdx.files.getFileHandle("android/assets/data/vertexshader.txt", Files.FileType.Internal),
                Gdx.files.getFileHandle("android/assets/data/fragmentshader.txt", Files.FileType.Internal)
        );

        program.enableVertexAttribute("position");
        program.enableVertexAttribute("colorIn");
        program.enableVertexAttribute("normal");

        program.begin();

        LIGHT_POSITION_IN_VIEW_3F_UNIFORM = program.getUniformLocation("sunPositionInView");
        MVP_MATRIX_UNIFORM = program.getUniformLocation("MVPMatrix");
        MV_MATRIX_UNIFORM = program.getUniformLocation("MVMatrix");
        COLOR_MULT_3F_UNIFORM = program.getUniformLocation("colorMult");

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glDisable(GL20.GL_DITHER);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        //Textures.generateTextures();

        Util.startTimer();
        ModelImporter.importAllModels();
        System.out.println("Model loading: " + Util.endTimer());
        Util.startTimer();
        ModelInfoImporter.importModelInfo();
        System.out.println("Model info loading: " + Util.endTimer());
        Engine.INSTANCE.beforeModelGeneration();

        Util.startTimer();
        WorldStaticEntityInfoImporter.importInfos();
        System.out.println("World static entity import: " + Util.endTimer());
        Engine.INSTANCE.onSurfaceCreated();

        System.out.println("Total boot time: " + (System.currentTimeMillis() - Engine.creationTime) / 1000.0);
    }

    public static void setLightPositionInView(float x, float y, float z) {
        program.setUniformf(LIGHT_POSITION_IN_VIEW_3F_UNIFORM, x, y, z);
    }

    public static void setColorMult(float x, float y, float z) {
        program.setUniformf(COLOR_MULT_3F_UNIFORM, x, y, z);
    }

    public static void setMVPMatrix(float[] matrix) {
        program.setUniformMatrix4fv(MVP_MATRIX_UNIFORM, matrix, 0, 16);
    }

    public static void setMVMatrix(float[] matrix) {
        program.setUniformMatrix4fv(MV_MATRIX_UNIFORM, matrix, 0, 16);
    }

    public static ShaderProgram getShaderProgram() {
        return program;
    }
}