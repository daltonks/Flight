//collision physics bitmasks

package com.github.daltonks.game.World.physics;

public class CollisionMasks {
    public static final byte WORLD = 1;
    public static final byte ALLY = 2;
    public static final byte ALLY_ATTACK = 4;
    public static final byte ENEMY = 8;
    public static final byte ENEMY_ATTACK = 16;

    //Doesn't collide with team attacks
    public static byte getEntityCollidesWithMask(byte team) {
        return (byte) ~getAttackMask(team);
    }

    public static byte getAttackMask(byte team) {
        return (byte) (team << 1);
    }

    //Doesn't collide with team or attacks of team
    public static byte getAttackCollidesWithMask(byte team) {
        return (byte) ~(team | getAttackMask(team));
    }
}