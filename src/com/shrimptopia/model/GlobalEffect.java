package com.shrimptopia.model;

/** Ein farmweiter Effekt (von globalen Upgrades oder der Arbeiter-Politik). */
public class GlobalEffect {

    public enum Type {
        TANK_SHRIMP_MULT,    // Becken-Output x
        POWERUSE_MULT,       // Stromverbrauch aller Gebäude x
        UPKEEP_MULT,         // Betriebskosten aller Gebäude x
        PRICE_MULT,          // Verkaufspreis x
        REP_PER_TICK,        // Reputation +/- pro Tag
        WATER_PRODUCE_MULT,  // Wasserproduktion x
        FEED_PRODUCE_MULT,   // Futterproduktion x
        WORKER_MULT,         // verfügbare Arbeiter x
        SELLCAP_MULT         // Verkaufskapazität x
    }

    public final Type type;
    public final double magnitude;

    public GlobalEffect(Type type, double magnitude) {
        this.type = type;
        this.magnitude = magnitude;
    }
}
