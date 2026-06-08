package com.shrimptopia.model;

/** Globale Arbeiter-Politik. Beeinflusst die gesamte Farm (Effizienz, Reputation, Kosten). */
public enum WorkerPolicy {
    REGULAR("Regulärer Betrieb", "Geregelte Schichten. Alles im grünen Bereich.", null),
    OVERTIME("Überstunden-Kult", "Mehr Arbeitskraft (x1.25) und Output, aber -Reputation und teurer.", null),
    UNION("Gewerkschafts-Liebling", "Zufriedene Belegschaft (+Reputation), dafür etwas weniger Arbeitskraft.", null),
    BOOTCAMP("Krillkill-Bootcamp", "Shrimps UND Arbeiter im Drill: +30% Becken-Output, aber die Reputation leidet.", "worker.bootcamp");

    public final String displayName, desc, requiresFlag;

    WorkerPolicy(String displayName, String desc, String requiresFlag) {
        this.displayName = displayName; this.desc = desc; this.requiresFlag = requiresFlag;
    }

    /** Wendet die Politik auf die Farm-Modifikatoren an. */
    public void apply(FarmModifiers fm) {
        switch (this) {
            case OVERTIME -> { fm.workerMult *= 1.25; fm.upkeepMult *= 1.2; fm.repPerTick -= 0.08; }
            case UNION    -> { fm.workerMult *= 0.9;  fm.repPerTick += 0.08; }
            case BOOTCAMP -> { fm.tankShrimpMult *= 1.3; fm.repPerTick -= 0.06; fm.workerMult *= 1.1; }
            default -> { }
        }
    }
}
