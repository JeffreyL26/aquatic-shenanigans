package com.shrimptopia.model;

/** Ein umschaltbarer Betriebsmodus eines Gebäudes (lokale Wirkung). */
public class Mode {
    public final String id, name, desc;
    public double shrimpMult = 1, powerMult = 1, waterMult = 1, feedMult = 1, upkeepMult = 1, capMult = 1;
    public double prodMult = 1;   // skaliert die Hauptproduktion (Strom/Wasser/Futter)
    public double repAdd = 0;
    public ShrimpTier tierOverride = null;  // legt das produzierte Tier fest
    public String requiresFlag = null;      // Modus nur wählbar, wenn Flag freigeschaltet

    public Mode(String id, String name, String desc) {
        this.id = id; this.name = name; this.desc = desc;
    }

    public Mode shrimp(double m) { shrimpMult = m; return this; }
    public Mode power(double m)  { powerMult = m;  return this; }
    public Mode water(double m)  { waterMult = m;  return this; }
    public Mode feed(double m)   { feedMult = m;   return this; }
    public Mode upkeep(double m) { upkeepMult = m; return this; }
    public Mode prod(double m)   { prodMult = m;   return this; }
    public Mode cap(double m)    { capMult = m;    return this; }
    public Mode rep(double d)    { repAdd = d;     return this; }
    public Mode tier(ShrimpTier t) { tierOverride = t; return this; }
    public Mode needs(String flag) { requiresFlag = flag; return this; }
}
