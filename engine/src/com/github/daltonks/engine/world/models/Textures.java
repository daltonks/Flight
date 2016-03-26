//Access point to the texture references, their names, and utility functions

package com.github.daltonks.engine.world.models;

import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.github.daltonks.engine.Engine;
import wcdtq7.gl.goo.game.R;

public class Textures {
    private static final int NUMBER_OF_TEXTURES = 1;
    private static String[] textureNames = new String[NUMBER_OF_TEXTURES];
    private static int[] textureRefs = new int[NUMBER_OF_TEXTURES];
    private static int index = 0;

    public static void generateTextures() {
        Gdx.gl.glGenTextures(textureRefs.length, textureRefs, 0);
        setNextTextureName("textBox");
        generateTextTexture("", textureRefs[index - 1]);
    }

    private static void setNextTextureName(String name) {
        textureNames[index] = name;
        index++;
    }

    public static int getTexture(String name) {
        for(int i = 0; i < textureNames.length; i++) {
            if(textureNames[i].equals(name)) {
                return textureRefs[i];
            }
        }
        return 0;
    }

    public static void generateTexture(int bufferLoc, int resourceID) {
        Bitmap bitmap = createBitmap(resourceID);
        bufferBitmapToGL(bitmap, bufferLoc, true);
    }

    private static Bitmap bitmap;
    private static Canvas canvas;
    private static Paint textPaint;
    public static void generateTextTexture(String text, int bufferLoc) {
        if(bitmap == null) {
            bitmap = createBitmap(R.drawable.background);
            canvas = new Canvas(bitmap);
            textPaint = new Paint();
            textPaint.setTextSize(24);
            textPaint.setAntiAlias(true);
            textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
        }
        bitmap.eraseColor(Color.BLACK);
        canvas.drawText(text, 0, 24, textPaint);
        bufferBitmapToGL(bitmap, bufferLoc, false);
    }

    private static Bitmap createBitmap(int resourceID) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(Engine.INSTANCE.getResources(), resourceID, options);
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private static void bufferBitmapToGL(Bitmap bitmap, int bufferLoc, boolean recycleBitmap) {
        Gdx.gl.glBindTexture(Gdx.gl.GL_TEXTURE_2D, bufferLoc);
        Gdx.gl.glTexParameteri(Gdx.gl.GL_TEXTURE_2D, Gdx.gl.GL_TEXTURE_MIN_FILTER, Gdx.gl.GL_NEAREST);
        Gdx.gl.glTexParameteri(Gdx.gl.GL_TEXTURE_2D, Gdx.gl.GL_TEXTURE_MAG_FILTER, Gdx.gl.GL_NEAREST);
        GLUtils.texImage2D(Gdx.gl.GL_TEXTURE_2D, 0, bitmap, 0);
        Gdx.gl.glBindTexture(Gdx.gl.GL_TEXTURE_2D, 0);
        if(recycleBitmap) {
            bitmap.recycle();
        }
    }
}