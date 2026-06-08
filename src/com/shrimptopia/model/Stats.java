package com.shrimptopia.model;

/** Effektive Tick-Werte eines Gebäudes (Basis + Modus + Upgrades + Farm-Modifikatoren). */
public class Stats {
    public double powerProduce, powerUse;
    public double waterProduce, waterUse;
    public double feedProduce, feedUse;
    public double shrimpProduce;
    public double shrimpUse;
    public double shellProduce, shellUse;
    public double boostProduce, boostUse;
    public double robotProduce, robotUse;
    public double armyProduce;
    public double sellCap;       // verkaufbare Shrimps/Tag (Märkte)
    public double upkeep;
    public double repPerTick;
    public int workerProvide, workerNeed;
    public ShrimpTier tier = ShrimpTier.STANDARD;  // welches Tier ein Becken produziert
}
