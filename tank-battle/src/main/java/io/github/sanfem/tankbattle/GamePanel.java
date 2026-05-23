package io.github.sanfem.tankbattle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int TILE_SIZE = 24;
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 15;
    private static final int HUD_HEIGHT = 28;
    private static final int PANEL_WIDTH = MAP_WIDTH * TILE_SIZE;
    private static final int PANEL_HEIGHT = MAP_HEIGHT * TILE_SIZE + HUD_HEIGHT;
    private static final int TANK_SIZE = TILE_SIZE - 4;
    private static final int PLAYER_SPEED = 2;
    private static final int ENEMY_SPEED = 1;
    private static final int PLAYER_HEALTH = 3;
    private static final int PLAYER_SHOT_COOLDOWN = 12;
    private static final int ENEMY_SHOT_COOLDOWN = 40;
    private static final int BULLET_SIZE = 4;
    private static final int BULLET_SPEED = 5;

    private final Random random = new Random();
    private final Timer timer;
    private final Level[] levels;

    private GameState state;
    private int currentLevelIndex;

    private TileMap tileMap;
    private Tank player;
    private List<EnemyTank> enemies;
    private List<Bullet> bullets;

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean firePressed;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        levels = buildLevels();
        loadLevel(0);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        drawHud(g2d);
        drawMap(g2d);
        drawTanks(g2d);
        drawBullets(g2d);
        drawStateOverlay(g2d);

        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (state == GameState.PLAYING) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        updatePlayer();
        updateEnemies();
        updateBullets();
        checkLevelClear();
    }

    private void updatePlayer() {
        player.tickCooldown();

        Direction moveDirection = null;
        if (upPressed) {
            moveDirection = Direction.UP;
        } else if (downPressed) {
            moveDirection = Direction.DOWN;
        } else if (leftPressed) {
            moveDirection = Direction.LEFT;
        } else if (rightPressed) {
            moveDirection = Direction.RIGHT;
        }

        if (moveDirection != null) {
            player.setDirection(moveDirection);
            attemptMove(player, moveDirection, player.getSpeed());
        }

        if (firePressed && player.canShoot()) {
            spawnBullet(player);
            player.resetCooldown();
        }
    }

    private void updateEnemies() {
        for (EnemyTank enemy : enemies) {
            enemy.tickCooldown();
            enemy.tickAi();

            if (enemy.shouldPickNewDirection()) {
                enemy.setDirection(Direction.random(random));
                enemy.resetAiTimer(random);
            }

            if (!attemptMove(enemy, enemy.getDirection(), enemy.getSpeed())) {
                enemy.setDirection(Direction.random(random));
                enemy.resetAiTimer(random);
            }

            if (enemy.canShoot() && random.nextInt(100) < 3) {
                spawnBullet(enemy);
                enemy.resetCooldown();
            }
        }
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();

            Rectangle bounds = bullet.getBounds();
            if (bounds.x < 0 || bounds.y < 0
                    || bounds.x + bounds.width > tileMap.getPixelWidth()
                    || bounds.y + bounds.height > tileMap.getPixelHeight()) {
                iterator.remove();
                continue;
            }

            if (handleBulletTileCollision(bounds)) {
                iterator.remove();
                continue;
            }

            if (bullet.isFromPlayer()) {
                Iterator<EnemyTank> enemyIterator = enemies.iterator();
                boolean removed = false;
                while (enemyIterator.hasNext()) {
                    EnemyTank enemy = enemyIterator.next();
                    if (bounds.intersects(enemy.getBounds())) {
                        enemyIterator.remove();
                        iterator.remove();
                        removed = true;
                        break;
                    }
                }
                if (removed) {
                    continue;
                }
            } else {
                if (bounds.intersects(player.getBounds())) {
                    player.damage();
                    iterator.remove();
                    if (player.getHealth() <= 0) {
                        state = GameState.GAME_OVER;
                    }
                }
            }
        }
    }

    private void checkLevelClear() {
        if (enemies.isEmpty()) {
            if (currentLevelIndex >= levels.length - 1) {
                state = GameState.VICTORY;
            } else {
                state = GameState.LEVEL_CLEAR;
            }
        }
    }

    private boolean attemptMove(Tank tank, Direction direction, int speed) {
        int dx = 0;
        int dy = 0;
        if (direction == Direction.UP) {
            dy = -speed;
        } else if (direction == Direction.DOWN) {
            dy = speed;
        } else if (direction == Direction.LEFT) {
            dx = -speed;
        } else if (direction == Direction.RIGHT) {
            dx = speed;
        }

        Rectangle next = new Rectangle(tank.getX() + dx, tank.getY() + dy, tank.getSize(), tank.getSize());
        if (next.x < 0 || next.y < 0
                || next.x + next.width > tileMap.getPixelWidth()
                || next.y + next.height > tileMap.getPixelHeight()) {
            return false;
        }

        if (tileMap.collides(next)) {
            return false;
        }

        if (collidesWithTank(tank, next)) {
            return false;
        }

        tank.setPosition(next.x, next.y);
        return true;
    }

    private boolean collidesWithTank(Tank moving, Rectangle bounds) {
        if (player != null && player != moving && bounds.intersects(player.getBounds())) {
            return true;
        }
        for (Tank enemy : enemies) {
            if (enemy != moving && bounds.intersects(enemy.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private boolean handleBulletTileCollision(Rectangle bounds) {
        int left = bounds.x / TILE_SIZE;
        int right = (bounds.x + bounds.width - 1) / TILE_SIZE;
        int top = bounds.y / TILE_SIZE;
        int bottom = (bounds.y + bounds.height - 1) / TILE_SIZE;

        for (int row = top; row <= bottom; row++) {
            for (int col = left; col <= right; col++) {
                TileType tile = tileMap.getTile(col, row);
                if (tile.isSolid()) {
                    if (tile.isDestructible()) {
                        tileMap.setTile(col, row, TileType.EMPTY);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void spawnBullet(Tank tank) {
        int centerX = tank.getX() + tank.getSize() / 2 - BULLET_SIZE / 2;
        int centerY = tank.getY() + tank.getSize() / 2 - BULLET_SIZE / 2;
        int offset = tank.getSize() / 2 + 2;

        if (tank.getDirection() == Direction.UP) {
            centerY -= offset;
        } else if (tank.getDirection() == Direction.DOWN) {
            centerY += offset;
        } else if (tank.getDirection() == Direction.LEFT) {
            centerX -= offset;
        } else if (tank.getDirection() == Direction.RIGHT) {
            centerX += offset;
        }

        bullets.add(new Bullet(centerX, centerY, BULLET_SIZE, BULLET_SPEED, tank.getDirection(), tank.isPlayer()));
    }

    private void drawHud(Graphics2D g2d) {
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(0, 0, PANEL_WIDTH, HUD_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        String label = "Level " + (currentLevelIndex + 1) + "/" + levels.length
                + "  HP: " + player.getHealth()
                + "  Enemies: " + enemies.size();
        g2d.drawString(label, 8, 18);
    }

    private void drawMap(Graphics2D g2d) {
        int offsetY = HUD_HEIGHT;
        for (int row = 0; row < tileMap.getHeight(); row++) {
            for (int col = 0; col < tileMap.getWidth(); col++) {
                TileType tile = tileMap.getTile(col, row);
                if (tile == TileType.EMPTY) {
                    continue;
                }
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE + offsetY;
                g2d.setColor(tile.getColor());
                g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                if (tile == TileType.WATER) {
                    g2d.setColor(new Color(70, 120, 200));
                    g2d.fillRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                }
            }
        }
    }

    private void drawTanks(Graphics2D g2d) {
        drawTank(g2d, player);
        for (EnemyTank enemy : enemies) {
            drawTank(g2d, enemy);
        }
    }

    private void drawTank(Graphics2D g2d, Tank tank) {
        int x = tank.getX();
        int y = tank.getY() + HUD_HEIGHT;
        int size = tank.getSize();
        int tread = size / 4;
        int barrelThickness = Math.max(2, size / 6);
        int barrelLength = size / 2;

        g2d.setColor(tank.getBodyColor());
        g2d.fillRect(x, y, size, size);

        g2d.setColor(tank.getTrimColor());
        g2d.fillRect(x, y, tread, size);
        g2d.fillRect(x + size - tread, y, tread, size);

        g2d.setColor(Color.DARK_GRAY);
        int centerX = x + size / 2 - barrelThickness / 2;
        int centerY = y + size / 2 - barrelThickness / 2;
        g2d.fillRect(centerX, centerY, barrelThickness, barrelThickness);

        if (tank.getDirection() == Direction.UP) {
            g2d.fillRect(centerX, y - barrelLength / 2, barrelThickness, barrelLength);
        } else if (tank.getDirection() == Direction.DOWN) {
            g2d.fillRect(centerX, y + size - barrelLength / 2, barrelThickness, barrelLength);
        } else if (tank.getDirection() == Direction.LEFT) {
            g2d.fillRect(x - barrelLength / 2, centerY, barrelLength, barrelThickness);
        } else if (tank.getDirection() == Direction.RIGHT) {
            g2d.fillRect(x + size - barrelLength / 2, centerY, barrelLength, barrelThickness);
        }
    }

    private void drawBullets(Graphics2D g2d) {
        int offsetY = HUD_HEIGHT;
        for (Bullet bullet : bullets) {
            if (bullet.isFromPlayer()) {
                g2d.setColor(new Color(120, 220, 120));
            } else {
                g2d.setColor(new Color(220, 120, 120));
            }
            g2d.fillRect(bullet.getX(), bullet.getY() + offsetY, bullet.getSize(), bullet.getSize());
        }
    }

    private void drawStateOverlay(Graphics2D g2d) {
        if (state == GameState.PLAYING) {
            return;
        }

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, HUD_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT - HUD_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));

        String message;
        if (state == GameState.LEVEL_CLEAR) {
            message = "Level Clear! Press Enter";
        } else if (state == GameState.GAME_OVER) {
            message = "Game Over! Press Enter";
        } else {
            message = "Victory! Press Enter";
        }

        int textWidth = g2d.getFontMetrics().stringWidth(message);
        int x = (PANEL_WIDTH - textWidth) / 2;
        int y = HUD_HEIGHT + (PANEL_HEIGHT - HUD_HEIGHT) / 2;
        g2d.drawString(message, x, y);
    }

    private void loadLevel(int index) {
        currentLevelIndex = index;
        Level level = levels[index];
        String[] rows = level.getRows();
        if (rows.length == 0 || rows[0].length() == 0) {
            tileMap = new TileMap(new TileType[0][0], TILE_SIZE);
            enemies = new ArrayList<EnemyTank>();
            bullets = new ArrayList<Bullet>();
            player = new Tank(0, 0, TANK_SIZE, PLAYER_SPEED, PLAYER_HEALTH, true,
                    new Color(70, 180, 90), new Color(40, 120, 60), PLAYER_SHOT_COOLDOWN);
            state = GameState.GAME_OVER;
            return;
        }

        TileType[][] tiles = new TileType[rows.length][rows[0].length()];
        List<Point> enemySpawns = new ArrayList<Point>();
        Point playerSpawn = null;

        for (int row = 0; row < rows.length; row++) {
            String line = rows[row];
            for (int col = 0; col < line.length(); col++) {
                char value = line.charAt(col);
                if (value == 'P') {
                    playerSpawn = new Point(col, row);
                    tiles[row][col] = TileType.EMPTY;
                } else if (value == 'E') {
                    enemySpawns.add(new Point(col, row));
                    tiles[row][col] = TileType.EMPTY;
                } else {
                    tiles[row][col] = TileType.fromChar(value);
                }
            }
        }

        tileMap = new TileMap(tiles, TILE_SIZE);
        enemies = new ArrayList<EnemyTank>();
        bullets = new ArrayList<Bullet>();

        if (playerSpawn == null) {
            playerSpawn = new Point(1, rows.length - 2);
        }

        int offset = (TILE_SIZE - TANK_SIZE) / 2;
        player = new Tank(playerSpawn.x * TILE_SIZE + offset, playerSpawn.y * TILE_SIZE + offset,
                TANK_SIZE, PLAYER_SPEED, PLAYER_HEALTH, true, new Color(70, 180, 90), new Color(40, 120, 60),
                PLAYER_SHOT_COOLDOWN);

        Color[] enemyBodies = new Color[] {
                new Color(200, 90, 90),
                new Color(200, 160, 80),
                new Color(170, 120, 210),
                new Color(80, 160, 200)
        };

        for (int i = 0; i < enemySpawns.size(); i++) {
            Point spawn = enemySpawns.get(i);
            Color body = enemyBodies[i % enemyBodies.length];
            Color trim = body.darker();
            EnemyTank enemy = new EnemyTank(spawn.x * TILE_SIZE + offset, spawn.y * TILE_SIZE + offset,
                    TANK_SIZE, ENEMY_SPEED, 1, body, trim, ENEMY_SHOT_COOLDOWN, random);
            enemy.setDirection(Direction.DOWN);
            enemies.add(enemy);
        }

        state = GameState.PLAYING;
    }

    private Level[] buildLevels() {
        return new Level[] {
                new Level("关卡 1", new String[] {
                        "####################",
                        "#..............E...#",
                        "#..####.......###..#",
                        "#..#..#........#...#",
                        "#..#..#....####....#",
                        "#..#..#....#..#....#",
                        "#..#..#....#..#....#",
                        "#..#..#....####....#",
                        "#..#..#........#...#",
                        "#..####.......###..#",
                        "#..............E...#",
                        "#....####....####..#",
                        "#....#..#..........#",
                        "#P.................#",
                        "####################"
                }),
                new Level("关卡 2", new String[] {
                        "SSSSSSSSSSSSSSSSSSSS",
                        "S....E......W......S",
                        "S..####..W..####...S",
                        "S..#..#..W..#..#...S",
                        "S..#..#......#..#..S",
                        "S..#..######..#..E.S",
                        "S..#..........#....S",
                        "S..######..######..S",
                        "S......W...........S",
                        "S..######..######..S",
                        "S..#..........#....S",
                        "S..#..######..#..E.S",
                        "S..#..#......#..#..S",
                        "S.P#..#..W..#..#...S",
                        "SSSSSSSSSSSSSSSSSSSS"
                }),
                new Level("关卡 3", new String[] {
                        "####################",
                        "#E......####......E#",
                        "#..####..#..####..##",
                        "#..#..#..#..#..#...#",
                        "#..#..#..####..#...#",
                        "#..#..#........#...#",
                        "#..#..######..###..#",
                        "#..#..........#....#",
                        "#E.######..######..#",
                        "#..#..........#....#",
                        "#..#..######..###..#",
                        "#..#..#........#...#",
                        "#..#..#..####..#...#",
                        "#P.#..#..#..#..#...#",
                        "####################"
                }),
                new Level("关卡 4", new String[] {
                        "SSSSSSSSSSSSSSSSSSSS",
                        "S....E......W......S",
                        "S..S..S..W..S..S...S",
                        "S..####..W..####..ES",
                        "S..#..#......#..#..S",
                        "S..#..######..#..#.S",
                        "S..#..........#....S",
                        "S..######..######..S",
                        "S......W........E..S",
                        "S..######..######..S",
                        "S..#..........#....S",
                        "S..#..######..#..#.S",
                        "S..#..#......#..#..S",
                        "S.P#..#..W..#..#...S",
                        "SSSSSSSSSSSSSSSSSSSS"
                }),
                new Level("关卡 5", new String[] {
                        "####################",
                        "#E..####......####E#",
                        "#..#..#..####..#..##",
                        "#..#..#........#...#",
                        "#..####..####..#####",
                        "#......E..#..E.....#",
                        "#..######..######..#",
                        "#..#..........#....#",
                        "#..#..######..###..#",
                        "#..#..........#....#",
                        "#..######..######..#",
                        "#....####....####..#",
                        "#....#..#..........#",
                        "#P..............E..#",
                        "####################"
                })
        };
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = true;
        } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = true;
        } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        } else if (event.getKeyCode() == KeyEvent.VK_SPACE) {
            firePressed = true;
        } else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            handleEnterKey();
        }
    }

    private void handleEnterKey() {
        if (state == GameState.LEVEL_CLEAR) {
            loadLevel(currentLevelIndex + 1);
        } else if (state == GameState.GAME_OVER || state == GameState.VICTORY) {
            loadLevel(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = false;
        } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        } else if (event.getKeyCode() == KeyEvent.VK_SPACE) {
            firePressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // Not used.
    }
}
