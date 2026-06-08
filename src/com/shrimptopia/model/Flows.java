package com.shrimptopia.model;

/** Zusatz-Durchsatz der v3-Ressourcen (Schalen, SHRIMPBOOST, Roboter, Armee) pro Gebaeudetyp. */
public class Flows {
    public double shrimpUse;     // verbraucht Shrimps (niedrigste Tiers zuerst)
    public double shellProduce, shellUse;
    public double boostProduce, boostUse;
    public double robotProduce, robotUse;
    public double armyProduce;

    public Flows shrimp(double v) { shrimpUse = v; return this; }
    public Flows shellsOut(double v) { shellProduce = v; return this; }
    public Flows shellsIn(double v) { shellUse = v; return this; }
    public Flows boostOut(double v) { boostProduce = v; return this; }
    public Flows boostIn(double v) { boostUse = v; return this; }
    public Flows robotsOut(double v) { robotProduce = v; return this; }
    public Flows army(double v) { armyProduce = v; return this; }
}
