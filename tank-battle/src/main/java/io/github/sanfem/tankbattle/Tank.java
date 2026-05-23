package io.github.sanfem.tankbattle;

import java.awt.Color;
import java.awt.Rectangle;

public class Tank {
    private int x;
    private int y;
    private final int size;
    private final int speed;
    private int health;
    private Direction direction;
    private final boolean player;
    private final Color bodyColor;
    private final Color trimColor;
    private int shotCooldown;
    private final int shotCooldownMax;

    public Tank(int x, int y, int size, int speed, int health, boolean player, Color bodyColor, Color trimColor,
                int shotCooldownMax) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.health = health;
        this.player = player;
        this.bodyColor = bodyColor;
        this.trimColor = trimColor;
        this.shotCooldownMax = shotCooldownMax;
        this.direction = Direction.UP;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public void damage() {
        if (health > 0) {
            health -= 1;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isPlayer() {
        return player;
    }

    public Color getBodyColor() {
        return bodyColor;
    }

    public Color getTrimColor() {
        return trimColor;
    }

    public void tickCooldown() {
        if (shotCooldown > 0) {
            shotCooldown -= 1;
        }
    }

    public boolean canShoot() {
        return shotCooldown == 0;
    }

    public void resetCooldown() {
        shotCooldown = shotCooldownMax;
    }
}
