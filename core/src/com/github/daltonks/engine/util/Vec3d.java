package com.github.daltonks.engine.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.github.daltonks.engine.util.interfaces.Positionable;

import javax.vecmath.Vector3f;

public class Vec3d implements Positionable {
    private double x, y, z;

    public Vec3d() {

    }

    public Vec3d(double x, double y, double z) {
        set(x, y, z);
    }

    public Vec3d(float[] floats) {
        set(floats);
    }

    public Vec3d(double[] doubles) {
        set(doubles);
    }

    public Vec3d(Vec3d other) {
        set(other);
    }

    public Vec3d clone() {
        return Pools.getVec3d().set(this);
    }

    //                                                 ** Setters **

    public Vec3d x(double x) {
        this.x = x;
        return this;
    }

    public Vec3d y(double y) {
        this.y = y;
        return this;
    }

    public Vec3d z(double z) {
        this.z = z;
        return this;
    }

    public Vec3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3d set(float[] floats) {
        return set(floats[0], floats[1], floats[2]);
    }

    public Vec3d set(double[] doubles) {
        return set(doubles[0], doubles[1], doubles[2]);
    }

    public Vec3d set(Vec3d other) {
        return set(other.x, other.y, other.z);
    }

    //                                                 ** Getters **

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float xf() {
        return (float) x;
    }

    public float yf() {
        return (float) y;
    }

    public float zf() {
        return (float) z;
    }

    public Vec3d get(float[] floats) {
        floats[0] = (float) x;
        floats[1] = (float) y;
        floats[2] = (float) z;
        return this;
    }

    public Vec3d get(double[] doubles) {
        doubles[0] = x;
        doubles[1] = y;
        doubles[2] = z;
        return this;
    }

    public Vec3d get(Vector3f vector3f) {
        vector3f.set((float) x, (float) y, (float) z);
        return this;
    }

    public Vec3d get(Vector3 vector3) {
        vector3.set((float) x, (float) y, (float) z);
        return this;
    }

    public Vec3d getLocation() {
        return this;
    }

    //                                                ** Addition **

    public Vec3d add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3d add(Vec3d other) {
        return add(other.x, other.y, other.z);
    }

    //                                              ** Subtraction **

    public Vec3d sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec3d sub(Vec3d other) {
        return sub(other.x, other.y, other.z);
    }

    //                                            ** Multiplication **

    public Vec3d mult(double scale) {
        return mult(scale, scale, scale);
    }

    public Vec3d mult(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vec3d mult(Vec3d other) {
        return mult(other.x, other.y, other.z);
    }

    //                                              ** Division **

    public Vec3d div(double scale) {
        return div(scale, scale, scale);
    }

    public Vec3d div(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Vec3d div(Vec3d other) {
        return div(other.x, other.y, other.z);
    }

    //                                        ** Matrix Multiplication **

    public Vec3d multMatrix(Matrix4 matrix) {
        float[] values = matrix.val;
        double x = this.x;
        double y = this.y;
        double z = this.z;
        this.x = values[0] * x + values[4] * y + values[8] * z;
        this.y = values[1] * x + values[5] * y + values[9] * z;
        this.z = values[2] * x + values[6] * y + values[10] * z;
        return this;
    }

    /**
        @return the 'w' value
     */
    public float multMatrix(Matrix4 matrix, double w) {
        float[] values = matrix.val;
        double x = this.x;
        double y = this.y;
        double z = this.z;
        this.x = values[0] * x + values[4] * y + values[8] * z + values[12] * w;
        this.y = values[1] * x + values[5] * y + values[9] * z + values[13] * w;
        this.z = values[2] * x + values[6] * y + values[10] * z + values[14] * w;
             w = values[3] * x + values[7] * y + values[11] * z + values[15] * w;

        return (float) w;
    }

    //                                             ** Distance **

    public double distanceSquaredTo(Vec3d other) {
        double x = this.x - other.x;
        double y = this.y - other.y;
        double z = this.z - other.z;
        return x * x + y * y + z * z;
    }

    public double distanceTo(Vec3d other) {
        return Math.sqrt(distanceSquaredTo(other));
    }

    //                                            ** Cross Product **

    public Vec3d cross(double cx, double cy, double cz) {
        double tx = this.x;
        double ty = this.y;
        double tz = this.z;
        this.x = ty * cz - tz * cy;
        this.y = tz * cx - tx * cz;
        this.z = tx * cy - ty * cx;
        return this;
    }

    public Vec3d cross(Vec3d other) {
        return cross(other.x, other.y, other.z);
    }

    //                                             ** Set Length **

    public Vec3d setLength(double l) {
        if(x == 0 && y == 0 && z == 0) {
            return this;
        }
        double mult = l / length();
        x *= mult;
        y *= mult;
        z *= mult;
        return this;
    }

    //                                              ** Normal **

    public Vec3d normal(Vec3d second, Vec3d third) {
        sub(second);
        Vec3d thirdMinusSecond = third.clone().sub(second);
        cross(thirdMinusSecond);
        normalize();
        Pools.recycle(thirdMinusSecond);
        return this;
    }

    //                                              ** Other **

    public double angleTo(Vec3d other) {
        return Math.acos(EngineMath.clamp(dot(other) / (length() * other.length()), -1, 1));
    }

    public double angleComplementTo(Vec3d other) {
        return Math.asin(EngineMath.clamp(dot(other) / (length() * other.length()), -1, 1));
    }

    public Vec3d lerp(Vec3d other, double position) {
        x = EngineMath.lerp(x, other.x, position);
        y = EngineMath.lerp(y, other.y, position);
        z = EngineMath.lerp(z, other.z, position);
        return this;
    }

    public Vec3d normalize() {
        return setLength(1);
    }

    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public double dot(Vec3d other) {
        return dot(other.x, other.y, other.z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public boolean equals(Vec3d other) {
        return x == other.x && y == other.y && z == other.z;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}