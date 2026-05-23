package io.github.sanfem.tankbattle;

import java.awt.Color;
import java.util.Random;

public class EnemyTank extends Tank {
    private int aiTimer;

    public EnemyTank(int x, int y, int size, int speed, int health, Color bodyColor, Color trimColor,
                     int shotCooldownMax, Random random) {
        super(x, y, size, speed, health, false, bodyColor, trimColor, shotCooldownMax);
        resetAiTimer(random);
    }

    public void resetAiTimer(Random random) {
        aiTimer = 30 + random.nextInt(80);
    }

    public void tickAi() {
        aiTimer -= 1;
    }

    public boolean shouldPickNewDirection() {
        return aiTimer <= 0;
    }
}
