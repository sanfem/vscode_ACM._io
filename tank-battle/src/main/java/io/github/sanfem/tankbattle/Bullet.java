package io.github.sanfem.tankbattle;

import java.awt.Rectangle;

public class Bullet {
    private int x;
    private int y;
    private final int size;
    private final int speed;
    private final Direction direction;
    private final boolean fromPlayer;

    public Bullet(int x, int y, int size, int speed, Direction direction, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.direction = direction;
        this.fromPlayer = fromPlayer;
    }

    public void move() {
        switch (direction) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
        }
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

    public int getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }
}
