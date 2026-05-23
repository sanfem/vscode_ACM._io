package io.github.sanfem.tankbattle;

import java.awt.Color;

public enum TileType {
    EMPTY(false, false, null),
    BRICK(true, true, new Color(156, 86, 60)),
    STEEL(true, false, new Color(150, 150, 150)),
    WATER(true, false, new Color(50, 90, 160));

    private final boolean solid;
    private final boolean destructible;
    private final Color color;

    TileType(boolean solid, boolean destructible, Color color) {
        this.solid = solid;
        this.destructible = destructible;
        this.color = color;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isDestructible() {
        return destructible;
    }

    public Color getColor() {
        return color;
    }

    public static TileType fromChar(char value) {
        if (value == '#') {
            return BRICK;
        }
        if (value == 'S') {
            return STEEL;
        }
        if (value == 'W') {
            return WATER;
        }
        return EMPTY;
    }
}
