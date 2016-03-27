package com.github.daltonks.engine.util;

import javax.vecmath.Quat4f;

public class EngineMath {
    public static void setRotateEulerM(float[] rm, int rmOffset, float x, float y, float z) {
        x = x * 0.01745329f;
        y = y * 0.01745329f;
        z = z * 0.01745329f;
        float sx = (float) Math.sin(x);
        float sy = (float) Math.sin(y);
        float sz = (float) Math.sin(z);
        float cx = (float) Math.cos(x);
        float cy = (float) Math.cos(y);
        float cz = (float) Math.cos(z);
        float cxsy = cx * sy;
        float sxsy = sx * sy;

        rm[rmOffset + 0] = cy * cz;
        rm[rmOffset + 1] = -cy * sz;
        rm[rmOffset + 2] = sy;
        rm[rmOffset + 3] = 0.0f;

        rm[rmOffset + 4] = sxsy * cz + cx * sz;
        rm[rmOffset + 5] = -sxsy * sz + cx * cz;
        rm[rmOffset + 6] = -sx * cy;
        rm[rmOffset + 7] = 0.0f;

        rm[rmOffset + 8] = -cxsy * cz + sx * sz;
        rm[rmOffset + 9] = cxsy * sz + sx * cz;
        rm[rmOffset + 10] = cx * cy;
        rm[rmOffset + 11] = 0.0f;

        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
    }

    public static void quaternionToEulerXYZ(Quat4f quat, float[] euler) {
        float w = quat.w;
        float x = quat.x;
        float y = quat.y;
        float z = quat.z;
        double sqw = w * w;
        double sqx = x * x;
        double sqy = y * y;
        double sqz = z * z;
        euler[0] = (float) Math.toDegrees(Math.atan2(2.0 * (y * z + x * w), (-sqx - sqy + sqz + sqw)));
        euler[1] = (float) Math.toDegrees(Math.asin(-2.0 * (x * z - y * w)));
        euler[2] = (float) Math.toDegrees(Math.atan2(2.0 * (x * y + z * w), (sqx - sqy - sqz + sqw)));
    }

    public static Vec3d getRandomPointOnSphereSurfaceNew(double radius) {
        double theta = Math.PI * 2 * Math.random(); //between 0 and 2PI
        double u = (Math.random() * 2) - 1; //between -1 and 1
        double s = Math.sqrt(1 - u * u);
        double rs = radius * s;
        Vec3d point = Pools.getVec3d();
        point.set(rs * Math.cos(theta), rs * Math.sin(theta), radius * u);
        return point;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double lerp(double first, double second, double position) {
        return first * (1 - position) + second * position;
    }

    public static double lerpClamp(double first, double second, double position) {
        return clamp(lerp(first, second, position), first, second);
    }

    public static double clamp(double value, double left, double right) {
        double low = Math.min(left, right);
        double high = Math.max(left, right);
        if(value < low) {
            return low;
        } else if(value > high) {
            return high;
        } else {
            return value;
        }
    }
}