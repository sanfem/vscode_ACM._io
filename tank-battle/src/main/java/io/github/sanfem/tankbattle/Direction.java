package io.github.sanfem.tankbattle;

import java.util.Random;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Direction random(Random random) {
        return values()[random.nextInt(values().length)];
    }
}
