package de.jefa.core.model;

public enum Caliber {
    N9MM("9mm"),
    N45ACP(".45ACP");

    public final String id;

    Caliber(String id) {
        this.id = id;
    }

    public static Caliber from(String s) {
        if (s == null) {
            return null;
        }
        for (var c : values()) {
            if (c.id.equalsIgnoreCase(s) || c.name().equalsIgnoreCase(s)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown caliber: " + s);
    }

    @Override
    public String toString() {
        return id;
    }
}
