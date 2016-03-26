package com.github.daltonks.game.datacompressor;

import serialization.ProguardKeeping;

public class LivingEntityInfo implements ProguardKeeping {
    public int lowerSpeedRange;
    public int maxHealth;

    public float rotVelocityMax;
    public float forwardForceMax;
    public float linearDamping;
    public float angularDamping;

    public transient int lowerSpeedRangeSquared;
}