package io.github.sanfem.tankbattle;

public class Level {
    private final String name;
    private final String[] rows;

    public Level(String name, String[] rows) {
        this.name = name;
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public String[] getRows() {
        return rows;
    }
}
