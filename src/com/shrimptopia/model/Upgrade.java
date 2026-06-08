package com.shrimptopia.model;

/** Ein kaufbares Gebäude-Upgrade. Kann lokal wirken UND/ODER einen farmweiten Effekt haben. */
public class Upgrade {
    public final String id, name, desc;
    public final int cost;
    // lokale Wirkung (wie ein Modus)
    public double shrimpMult = 1, powerMult = 1, waterMult = 1, feedMult = 1, upkeepMult = 1, capMult = 1;
    public double prodMult = 1;
    public double repAdd = 0;
    public ShrimpTier tierOverride = null;
    // farmweite Wirkung (optional)
    public GlobalEffect global = null;
    public String requiresFlag = null;  // erst kaufbar, wenn freigeschaltet
    public String grantsFlag = null;     // schaltet beim Kauf etwas frei

    public Upgrade(String id, String name, int cost, String desc) {
        this.id = id; this.name = name; this.cost = cost; this.desc = desc;
    }

    public Upgrade shrimp(double m) { shrimpMult = m; return this; }
    public Upgrade power(double m)  { powerMult = m;  return this; }
    public Upgrade water(double m)  { waterMult = m;  return this; }
    public Upgrade feed(double m)   { feedMult = m;   return this; }
    public Upgrade upkeep(double m) { upkeepMult = m; return this; }
    public Upgrade prod(double m)   { prodMult = m;   return this; }
    public Upgrade cap(double m)    { capMult = m;    return this; }
    public Upgrade rep(double d)    { repAdd = d;     return this; }
    public Upgrade tier(ShrimpTier t) { tierOverride = t; return this; }
    public Upgrade global(GlobalEffect.Type t, double mag) { global = new GlobalEffect(t, mag); return this; }
    public Upgrade needs(String flag)  { requiresFlag = flag; return this; }
    public Upgrade grants(String flag) { grantsFlag = flag;  return this; }
}
