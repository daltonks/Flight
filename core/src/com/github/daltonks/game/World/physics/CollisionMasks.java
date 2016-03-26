//collision physics bitmasks

package com.github.daltonks.game.World.physics;

public class CollisionMasks {
    public static final byte WORLD = 0b00000001;
    public static final byte ALLY = 0b00000010;
    public static final byte ALLY_ATTACK = 0b00000100;
    public static final byte ENEMY = 0b00001000;
    public static final byte ENEMY_ATTACK = 0b00010000;

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