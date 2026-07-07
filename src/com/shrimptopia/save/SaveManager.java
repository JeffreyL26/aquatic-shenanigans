package com.shrimptopia.save;

import com.shrimptopia.model.GameState;
import com.shrimptopia.quest.QuestSystem;
import com.shrimptopia.tutorial.Tutorial;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spielstand-Verwaltung: feste Slots als Textdateien (key=value, UTF-8).
 * Ablage unter ~/.shrimptopia/saves (überschreibbar per System-Property
 * "shrimptopia.saves" - so testet der Selbsttest gegen ein Temp-Verzeichnis).
 */
public final class SaveManager {

    private SaveManager() {}

    public static final int SLOTS = 5;
    private static final String FORMAT_HEADER = "shrimptopia-save 1";

    public static Path dir() {
        String override = System.getProperty("shrimptopia.saves");
        if (override != null && !override.isEmpty()) return Paths.get(override);
        return Paths.get(System.getProperty("user.home"), ".shrimptopia", "saves");
    }

    private static Path file(int slot) { return dir().resolve("slot" + slot + ".sav"); }

    /** Kopfdaten eines Slots für die Menü-Anzeige. */
    public record SlotInfo(int slot, int day, double money, long savedAt) {}

    /** Ein vollständig geladener Spielstand. */
    public record Loaded(GameState game, QuestSystem quests, Tutorial tutorial) {}

    /** Slot-Info oder null, wenn der Slot leer/unlesbar ist. */
    public static SlotInfo info(int slot) {
        Path f = file(slot);
        if (!Files.exists(f)) return null;
        try {
            Map<String, String> m = readFile(f);
            if (m == null) return null;
            return new SlotInfo(slot,
                parseInt(m.get("meta.day"), 0),
                parseDouble(m.get("meta.money"), 0),
                parseLong(m.get("meta.savedAt"), 0));
        } catch (IOException e) {
            return null;
        }
    }

    /** Speichert den kompletten Spielstand in den Slot. Liefert false bei I/O-Fehler. */
    public static boolean save(int slot, GameState gs, QuestSystem qs, Tutorial tut) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("meta.day", String.valueOf(gs.getDay()));
        m.put("meta.money", String.valueOf(Math.round(gs.getMoney())));
        m.put("meta.savedAt", String.valueOf(System.currentTimeMillis()));
        gs.writeSave(m);
        qs.writeSave(m);
        m.put("tut.index", String.valueOf(tut.index()));
        m.put("tut.active", String.valueOf(tut.isActive()));
        StringBuilder sb = new StringBuilder(FORMAT_HEADER).append('\n');
        for (Map.Entry<String, String> e : m.entrySet())
            sb.append(e.getKey()).append('=').append(esc(e.getValue())).append('\n');
        try {
            Files.createDirectories(dir());
            Files.write(file(slot), sb.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Lädt den Slot oder liefert null (leer/unlesbar/falsches Format). */
    public static Loaded load(int slot) {
        try {
            Map<String, String> m = readFile(file(slot));
            if (m == null) return null;
            GameState gs = GameState.readSave(m);
            QuestSystem qs = new QuestSystem();
            qs.readSave(m);
            Tutorial tut = new Tutorial();
            tut.restore(parseInt(m.get("tut.index"), 0), Boolean.parseBoolean(m.getOrDefault("tut.active", "false")));
            return new Loaded(gs, qs, tut);
        } catch (IOException | RuntimeException e) {
            return null;   // korrupte Datei: lieber "leer" melden als crashen
        }
    }

    /** Löscht den Slot. Liefert true, wenn eine Datei entfernt wurde. */
    public static boolean delete(int slot) {
        try { return Files.deleteIfExists(file(slot)); }
        catch (IOException e) { return false; }
    }

    // ---------------- Datei-Format ----------------

    private static Map<String, String> readFile(Path f) throws IOException {
        if (!Files.exists(f)) return null;
        java.util.List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).startsWith("shrimptopia-save")) return null;
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            m.put(line.substring(0, eq), unesc(line.substring(eq + 1)));
        }
        return m;
    }

    /** Escaped Zeilenumbrüche & Backslashes, damit jedes value eine Zeile bleibt. */
    private static String esc(String v) {
        if (v == null) return "";
        return v.replace("\\", "\\\\").replace("\r", "").replace("\n", "\\n");
    }

    private static String unesc(String v) {
        StringBuilder sb = new StringBuilder(v.length());
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c == '\\' && i + 1 < v.length()) {
                char n = v.charAt(++i);
                sb.append(n == 'n' ? '\n' : n);
            } else sb.append(c);
        }
        return sb.toString();
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private static long parseLong(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }
    private static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
}
