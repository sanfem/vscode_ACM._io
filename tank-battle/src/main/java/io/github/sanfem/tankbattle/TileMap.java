package io.github.sanfem.tankbattle;

import java.awt.Rectangle;

public class TileMap {
    private final TileType[][] tiles;
    private final int tileSize;

    public TileMap(TileType[][] tiles, int tileSize) {
        this.tiles = tiles;
        this.tileSize = tileSize;
    }

    public int getWidth() {
        return tiles[0].length;
    }

    public int getHeight() {
        return tiles.length;
    }

    public int getPixelWidth() {
        return getWidth() * tileSize;
    }

    public int getPixelHeight() {
        return getHeight() * tileSize;
    }

    public TileType getTile(int col, int row) {
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[row].length) {
            return TileType.STEEL;
        }
        return tiles[row][col];
    }

    public void setTile(int col, int row, TileType type) {
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[row].length) {
            return;
        }
        tiles[row][col] = type;
    }

    public boolean collides(Rectangle bounds) {
        int left = bounds.x / tileSize;
        int right = (bounds.x + bounds.width - 1) / tileSize;
        int top = bounds.y / tileSize;
        int bottom = (bounds.y + bounds.height - 1) / tileSize;

        for (int row = top; row <= bottom; row++) {
            for (int col = left; col <= right; col++) {
                if (getTile(col, row).isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }
}
