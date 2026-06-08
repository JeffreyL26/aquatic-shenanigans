package com.shrimptopia.model;

/** Aggregierte farmweite Multiplikatoren für einen Tick (aus globalen Upgrades + Arbeiter-Politik). */
public class FarmModifiers {
    public double tankShrimpMult   = 1.0;
    public double powerUseMult     = 1.0;
    public double upkeepMult       = 1.0;
    public double priceMult        = 1.0;
    public double waterProduceMult = 1.0;
    public double feedProduceMult  = 1.0;
    public double workerMult       = 1.0;
    public double sellCapMult      = 1.0;
    public double repPerTick       = 0.0;

    public void apply(GlobalEffect g) {
        switch (g.type) {
            case TANK_SHRIMP_MULT   -> tankShrimpMult   *= g.magnitude;
            case POWERUSE_MULT      -> powerUseMult     *= g.magnitude;
            case UPKEEP_MULT        -> upkeepMult       *= g.magnitude;
            case PRICE_MULT         -> priceMult        *= g.magnitude;
            case WATER_PRODUCE_MULT -> waterProduceMult *= g.magnitude;
            case FEED_PRODUCE_MULT  -> feedProduceMult  *= g.magnitude;
            case WORKER_MULT        -> workerMult       *= g.magnitude;
            case SELLCAP_MULT       -> sellCapMult      *= g.magnitude;
            case REP_PER_TICK       -> repPerTick       += g.magnitude;
        }
    }
}
