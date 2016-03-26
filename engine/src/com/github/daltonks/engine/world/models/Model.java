//Contains the raw buffers for vertex and vertex indices information
//Handles the lower-level OpenGL drawing

package com.github.daltonks.engine.world.models;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.github.daltonks.engine.datacompressor.models.ModelInfo;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Model {
    /*for use with a TexturedModel. Will be implemented separately
      private static final int VERTEX_SIZE_IN_BYTES = 11 * (Float.SIZE / 8);
    */

    public static final int NUM_OF_FLOATS_PER_VERTEX = 9; //9 floats: x, y, z, r, g, b, nx, ny, nz
    public static final int VERTEX_SIZE_IN_BYTES = NUM_OF_FLOATS_PER_VERTEX * (Float.SIZE / 8);
    private static int lastVertexBufferRef = -1;
    private static Color lastColorMult = new Color();

    private boolean bufferedToGL = false;
    private short id;
    private int vertexBufferRef, indicesBufferRef;
    private int drawingMode;
    private String name;
    private ModelInfo modelInfo;
    private CollisionShape defaultScaleCollisionShape;
    private NonDuplicatedVertices nonDuplicatedVertices;

    private IntBuffer indicesIntBuffer;
    private FloatBuffer vertexFloatBuffer;

    public Model(String name) {
        this.name = name;
        this.modelInfo = new ModelInfo(id);
    }

    private static float[] mvpMatrix = new float[16];
    private static float[] modelMatrix = new float[16];
    private static float[] modelViewMatrix = new float[16];
    private static float[] tempMatrix = new float[16];
    public void draw(
            Vec3d loc,
            float[] rotationMatrix,
            float scaleX, float scaleY, float scaleZ,
            Color vertexColorMultiplication,
            Camera camera) {

        draw(loc, rotationMatrix, scaleX, scaleY, scaleZ, vertexColorMultiplication, camera, drawingMode);
    }

    public void draw(Vec3d loc,
                     float[] rotationMatrix,
                     float scaleX, float scaleY, float scaleZ,
                     Color vertexColorMultiplication,
                     Camera camera,
                     int drawingMode) {

        if(!bufferedToGL) {
            bufferToGL();
        }

        getModelViewMatrix(loc, rotationMatrix, scaleX, scaleY, scaleZ, camera);
        Gdx.gl.glUniformMatrix4fv(EngineGLScene.MV_MATRIX_UNIFORM, 1, false, modelViewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, camera.getProjectionMatrix().getMatrix(), 0, modelViewMatrix, 0);
        Gdx.gl.glUniformMatrix4fv(EngineGLScene.MVP_MATRIX_UNIFORM, 1, false, mvpMatrix, 0);

        if(!lastColorMult.equals(vertexColorMultiplication)) {
            Gdx.gl.glUniform3f(
                    EngineGLScene.COLOR_MULT_3F_UNIFORM,
                    vertexColorMultiplication.getRed(), vertexColorMultiplication.getGreen(), vertexColorMultiplication.getBlue()
            );

            lastColorMult.set(vertexColorMultiplication);
        }

        if(lastVertexBufferRef != vertexBufferRef) {
            //Gdx.gl.glBindTexture(Gdx.gl.GL_TEXTURE_2D, Textures.getTexture(textureName));
            Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, vertexBufferRef);
            Gdx.gl.glVertexAttribPointer(0, 3, Gdx.gl.GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, 0);
            Gdx.gl.glVertexAttribPointer(1, 3, Gdx.gl.GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, 3 * (Float.SIZE / 8));
            Gdx.gl.glVertexAttribPointer(2, 3, Gdx.gl.GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, 6 * (Float.SIZE / 8));

            /*for use in TexturedModels, will be implemented separately
            Gdx.gl.glVertexAttribPointer(3, 2, Gdx.gl.GL_FLOAT, false, VERTEX_SIZE_IN_BYTES, 6 * 4);
            */
            Gdx.gl.glBindBuffer(Gdx.gl.GL_ELEMENT_ARRAY_BUFFER, indicesBufferRef);
            lastVertexBufferRef = vertexBufferRef;
            if(modelInfo.renderBothSides) {
                Gdx.gl.glDisable(Gdx.gl.GL_CULL_FACE);
            } else {
                Gdx.gl.glEnable(Gdx.gl.GL_CULL_FACE);
            }
        }

        Gdx.gl.glDrawElements(drawingMode, indicesIntBuffer.limit(), Gdx.gl.GL_UNSIGNED_INT, 0);
    }

    public void bufferToGL() {
        lastVertexBufferRef = -1;
        lastColorMult.setRed(-1);

        indicesIntBuffer.position(0);
        vertexFloatBuffer.position(0);

        //vertex buffer data
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, vertexBufferRef);
        Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, getVertexBufferSizeBytes(), vertexFloatBuffer, Gdx.gl.GL_STATIC_DRAW);
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, 0);

        //indices buffer data
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ELEMENT_ARRAY_BUFFER, indicesBufferRef);
        Gdx.gl.glBufferData(Gdx.gl.GL_ELEMENT_ARRAY_BUFFER, indicesIntBuffer.limit() * (Integer.SIZE / 8), indicesIntBuffer, Gdx.gl.GL_STATIC_DRAW);
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ELEMENT_ARRAY_BUFFER, 0);

        Gdx.gl.glEnableVertexAttribArray(0);
        Gdx.gl.glEnableVertexAttribArray(1);
        Gdx.gl.glEnableVertexAttribArray(2);

        /*for use in TexturedModels, will be implemented separately
        Gdx.gl.glEnableVertexAttribArray(3);
        */

        bufferedToGL = true;
    }

    private static float[] getModelViewMatrix(
            Vec3d loc,
            float[] rotationMatrix,
            float scaleX, float scaleY, float scaleZ,
            Camera camera) {

        Vec3d minusCam = loc.clone().sub(camera.getViewMatrix().getLocation());
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, minusCam.xf(), minusCam.yf(), minusCam.zf());
        Matrix.scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);
        System.arraycopy(modelMatrix, 0, tempMatrix, 0, 16);
        Matrix.multiplyMM(modelMatrix, 0, tempMatrix, 0, rotationMatrix, 0);
        Matrix.multiplyMM(modelViewMatrix, 0, camera.getViewMatrix().getMatrix(), 0, modelMatrix, 0);
        Pools.recycle(minusCam);
        return modelViewMatrix;
    }

    public void updateNumOfVertices(int numOfVertices) {
        int vertexBufferSizeInBytes = numOfVertices * VERTEX_SIZE_IN_BYTES;
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertexBufferSizeInBytes);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexFloatBuffer = vertexByteBuffer.asFloatBuffer();
    }

    public void updateIndicesAndDrawingMode(int[] indices, int drawingMode) {
        this.drawingMode = drawingMode;
        ByteBuffer indicesByteBuffer = ByteBuffer.allocateDirect(indices.length * Integer.SIZE / 8);
        indicesByteBuffer.order(ByteOrder.nativeOrder());
        indicesIntBuffer = indicesByteBuffer.asIntBuffer();
        indicesIntBuffer.put(indices);
        indicesIntBuffer.position(0);
    }

    private static float[] buf = new float[NUM_OF_FLOATS_PER_VERTEX];
    public Model addVertex(float x, float y, float z, float r, float g, float b, float nx, float ny, float nz) {
        buf[0] = x;
        buf[1] = y;
        buf[2] = z;
        buf[3] = r;
        buf[4] = g;
        buf[5] = b;
        buf[6] = nx;
        buf[7] = ny;
        buf[8] = nz;
        vertexFloatBuffer.put(buf);
        return this;
    }

    public void addVertices(float[] vertexData) {
        vertexFloatBuffer.put(vertexData);
    }

    private void generateNonDuplicatedVertices() {
        ArrayList<float[]> nonDuplicatedVertexLocs = new ArrayList<>(getNumOfVertices());
        int[] nonDuplicatedVertexIndices = new int[indicesIntBuffer.limit()];
        for(int i = 0; i < nonDuplicatedVertexIndices.length; i++) {
            nonDuplicatedVertexIndices[i] = indicesIntBuffer.get(i);
        }

        for(int i = 0; i < getNumOfVertices(); i++) {
            nonDuplicatedVertexLocs.add(getVertexLocation(i).clone());
        }

        for(int v = 0; v < nonDuplicatedVertexLocs.size() - 1; v++) {
            float[] vertex = nonDuplicatedVertexLocs.get(v);
            for(int o = v + 1; o < nonDuplicatedVertexLocs.size(); o++) {
                float[] otherVertex = nonDuplicatedVertexLocs.get(o);
                //if same location
                if(vertex[0] == otherVertex[0] && vertex[1] == otherVertex[1] && vertex[2] == otherVertex[2]) {
                    for(int i = 0; i < nonDuplicatedVertexIndices.length; i++) {
                        if(nonDuplicatedVertexIndices[i] == o) {
                            nonDuplicatedVertexIndices[i] = v;
                        } else if(nonDuplicatedVertexIndices[i] > o) {
                            nonDuplicatedVertexIndices[i] -= 1;
                        }
                    }
                    nonDuplicatedVertexLocs.remove(o);
                    o--;
                }
            }
        }

        nonDuplicatedVertices = new NonDuplicatedVertices();
        nonDuplicatedVertices.vertices = nonDuplicatedVertexLocs;
        ByteBuffer buffer = ByteBuffer.allocateDirect(nonDuplicatedVertexIndices.length * (Integer.SIZE / 8));
        buffer.order(ByteOrder.nativeOrder());
        for(int s : nonDuplicatedVertexIndices) {
            buffer.putInt(s);
        }
        buffer.clear();
        nonDuplicatedVertices.indicesByteBuffer = buffer;
    }

    private float[] temp = new float[3];
    public float[] getVertexLocation(int index) {
        for(int i = 0; i < 3; i++) {
            temp[i] = vertexFloatBuffer.get(index * NUM_OF_FLOATS_PER_VERTEX + i);
        }
        return temp;
    }

    public void setVertexData(int index, float[] data) {
        for(int i = 0; i < data.length; i++) {
            vertexFloatBuffer.put(index * NUM_OF_FLOATS_PER_VERTEX + i, data[i]);
        }
    }

    public float[] getVertexData(int index) {
        float[] loc = new float[NUM_OF_FLOATS_PER_VERTEX];
        for(int i = 0; i < NUM_OF_FLOATS_PER_VERTEX; i++) {
            loc[i] = vertexFloatBuffer.get(index * NUM_OF_FLOATS_PER_VERTEX + i);
        }
        return loc;
    }

    public void bufferVertexData() {
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, vertexBufferRef);
        Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, getVertexBufferSizeBytes(), vertexFloatBuffer, Gdx.gl.GL_STATIC_DRAW);
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, 0);
    }

    public void bufferVertexSubData(int offset, int sizeInBytes) {
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, vertexBufferRef);
        Gdx.gl.glBufferSubData(Gdx.gl.GL_ARRAY_BUFFER, offset, sizeInBytes, vertexFloatBuffer);
        Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, 0);
    }

    public void deleteBuffers() {
        int[] buffers = {vertexBufferRef, indicesBufferRef};
        Gdx.gl.glDeleteBuffers(2, buffers, 0);
    }

    public void setModelInfo(ModelInfo info) {
        this.modelInfo = info;

        Vec3d boundingBoxDiag = Pools.getVec3d();
        boundingBoxDiag.set(modelInfo.maxX, modelInfo.maxY, modelInfo.maxZ);
        boundingBoxDiag.sub(modelInfo.minX, modelInfo.minY, modelInfo.minZ);
        modelInfo.boundingBoxDiagonalLength = (float) boundingBoxDiag.length();
        Pools.recycle(boundingBoxDiag);
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public void setID(short id) {
        this.id = id;
    }

    public short getID() {
        return id;
    }

    public void setDefaultScaleCollisionShape(CollisionShape shape) {
        this.defaultScaleCollisionShape = shape;
    }

    public void setBufferReferences(int vertexBufferRef, int indicesBufferRef) {
        this.bufferedToGL = false;
        this.vertexBufferRef = vertexBufferRef;
        this.indicesBufferRef = indicesBufferRef;
        lastVertexBufferRef = -1;
        lastColorMult.set(0, 0, 0);
    }

    public CollisionShape getDefaultScaleCollisionShape() {
        return defaultScaleCollisionShape;
    }

    public NonDuplicatedVertices getNonDuplicatedVerticesAndIndices() {
        if(nonDuplicatedVertices == null) {
            generateNonDuplicatedVertices();
        }
        return nonDuplicatedVertices;
    }

    public float getXLength() {
        return modelInfo.maxX - modelInfo.minX;
    }

    public float getYLength() {
        return modelInfo.maxY - modelInfo.minY;
    }

    public float getZLength() {
        return modelInfo.maxZ - modelInfo.minZ;
    }

    public int getNumberOfTriangles() {
        return indicesIntBuffer.limit() / 3;
    }

    public int getNumOfVertices() {
        return vertexFloatBuffer.limit() / NUM_OF_FLOATS_PER_VERTEX;
    }

    public int getVertexBufferSizeBytes() {
        return vertexFloatBuffer.limit() * (Float.SIZE / 8);
    }

    public String getName() {
        return name;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object object) {
        return getName().equals(((Model) object).getName());
    }

    public static class NonDuplicatedVertices {
        public ArrayList<float[]> vertices;
        public ByteBuffer indicesByteBuffer;
    }
}