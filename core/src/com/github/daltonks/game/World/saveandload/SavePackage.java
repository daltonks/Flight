package com.github.daltonks.game.World.saveandload;

import serialization.ProguardKeeping;

public abstract class SavePackage implements ProguardKeeping {
    public abstract void setVariablesForSaving();
    public abstract void onConvertedFromLowerVersion(SavePackage lowerPackage);
    public abstract void convertToHigherVersion(SavePackage newPackage);
    public abstract void onLoad();
}