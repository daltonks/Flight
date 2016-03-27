//Contains the raw buffers for vertex and vertex indices information
//Handles the lower-level OpenGL drawing

package com.github.daltonks.engine.world.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.github.daltonks.engine.EngineShaderProgram;
import com.github.daltonks.engine.datacompressor.models.ModelInfo;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Model {
    /*for use with a TexturedModel. Will be implemented separately
      private static final int VERTEX_SIZE_IN_BYTES = 11 * (Float.SIZE / 8);
    */

    public static final int NUM_OF_FLOATS_PER_VERTEX = 9; //9 floats: x, y, z, r, g, b, nx, ny, nz
    private static Mesh lastBoundMesh;
    private static Color lastColorMult = new Color();

    private boolean bufferedToGL = false;
    private short id;
    private int drawingMode, currentVerticesIndex;
    private String name;
    private ModelInfo modelInfo;
    private CollisionShape defaultScaleCollisionShape;
    private NonDuplicatedVertices nonDuplicatedVertices;

    private short[] indicesTemp;

    private float[] verticesTemp;

    private Mesh mesh;

    public Model(String name) {
        this.name = name;
        this.modelInfo = new ModelInfo(id);
    }

    private static Matrix4 mvpMatrix = new Matrix4();
    public void draw(
            Vec3d loc,
            Matrix4 rotationMatrix,
            float scaleX, float scaleY, float scaleZ,
            Color vertexColorMultiplication,
            Camera camera) {

        draw(loc, rotationMatrix, scaleX, scaleY, scaleZ, vertexColorMultiplication, camera, drawingMode);
    }

    public void draw(Vec3d loc,
                     Matrix4 rotationMatrix,
                     float scaleX, float scaleY, float scaleZ,
                     Color color,
                     Camera camera,
                     int drawingMode) {

        if(!bufferedToGL) {
            bufferToGL();
        }

        Vec3d minusCam = loc.clone().sub(camera.getViewMatrix().getLocation());
        mvpMatrix.idt();
        mvpMatrix.translate(minusCam.xf(), minusCam.yf(), minusCam.zf());
        mvpMatrix.scale(scaleX, scaleY, scaleZ);
        mvpMatrix.mul(rotationMatrix);
        mvpMatrix.mulLeft(camera.getViewMatrix().getMatrix());
        Pools.recycle(minusCam);

        EngineShaderProgram.setMVMatrix(mvpMatrix.val);
        mvpMatrix.mulLeft(camera.getProjectionMatrix().getMatrix());
        EngineShaderProgram.setMVPMatrix(mvpMatrix.val);

        if(!lastColorMult.equals(color)) {
            EngineShaderProgram.setColorMult(color.getRed(), color.getGreen(), color.getBlue());
            lastColorMult.set(color);
        }

        if(lastBoundMesh != mesh) {
            mesh.bind(EngineShaderProgram.getShaderProgram());

            if(modelInfo.renderBothSides) {
                Gdx.gl.glDisable(GL20.GL_CULL_FACE);
            } else {
                Gdx.gl.glEnable(GL20.GL_CULL_FACE);
            }
            lastBoundMesh = mesh;
        }

        mesh.render(EngineShaderProgram.getShaderProgram(), drawingMode);
    }

    public void bufferToGL() {
        mesh = new Mesh(
                Mesh.VertexDataType.VertexBufferObject,
                true,
                verticesTemp.length / NUM_OF_FLOATS_PER_VERTEX,
                indicesTemp.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 3, "colorIn"),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, "normal")
        );
        mesh.setAutoBind(false);
        mesh.setVertices(verticesTemp);
        mesh.setIndices(indicesTemp);

        this.indicesTemp = null;
        this.verticesTemp = null;
        bufferedToGL = true;
    }

    public void setNumOfVertices(int numOfVertices) {
        verticesTemp = new float[numOfVertices * NUM_OF_FLOATS_PER_VERTEX];
        currentVerticesIndex = 0;
    }

    public void updateIndicesAndDrawingMode(short[] indices, int drawingMode) {
        this.drawingMode = drawingMode;
        this.indicesTemp = indices;
    }

    public Model addVertex(float x, float y, float z, float r, float g, float b, float nx, float ny, float nz) {
        verticesTemp[currentVerticesIndex] = x;
        verticesTemp[currentVerticesIndex + 1] = y;
        verticesTemp[currentVerticesIndex + 2] = z;
        verticesTemp[currentVerticesIndex + 3] = r;
        verticesTemp[currentVerticesIndex + 4] = g;
        verticesTemp[currentVerticesIndex + 5] = b;
        verticesTemp[currentVerticesIndex + 6] = nx;
        verticesTemp[currentVerticesIndex + 7] = ny;
        verticesTemp[currentVerticesIndex + 8] = nz;
        currentVerticesIndex += NUM_OF_FLOATS_PER_VERTEX;
        return this;
    }

    public void addVertices(float[] vertexData) {
        System.arraycopy(vertexData, 0, verticesTemp, currentVerticesIndex, vertexData.length);
        currentVerticesIndex += vertexData.length;
    }

    private void generateNonDuplicatedVertices() {
        ArrayList<float[]> nonDuplicatedVertexLocs = new ArrayList<float[]>(mesh.getNumVertices());
        short[] nonDuplicatedVertexIndices = new short[mesh.getNumIndices()];
        mesh.getIndices(nonDuplicatedVertexIndices);

        for(int i = 0; i < mesh.getNumVertices(); i++) {
            nonDuplicatedVertexLocs.add(getVertexLocation(i).clone());
        }

        for(short v = 0; v < nonDuplicatedVertexLocs.size() - 1; v++) {
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
        mesh.getVertices(index * NUM_OF_FLOATS_PER_VERTEX, 3, temp);
        return temp;
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
        return mesh.getNumIndices() / 3;
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