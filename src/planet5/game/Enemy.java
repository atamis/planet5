package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import planet5.Main;
import planet5.config.EnemyStats;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import processing.core.PApplet;
import processing.core.PVector;

public class Enemy {
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	public static final int ENEMY_SIZE = 16;

	private int kiloX, kiloY;
	public Point center;
	public boolean attacked = false;
	public int type;
	public Rectangle bounds;
	public Game map;
	private PApplet p;

	private int curHp = 0, maxHp = 0, computedHp = 0;
	private int speed;
	
	public int getComputedHp() {
		return computedHp;
	}
	public boolean isDead() {
		return curHp <= 0;
	}
	public boolean willBeDead() {
		return computedHp <= 0;
	}
	
	public void takeFutureDamage(int damage) {
		computedHp -= damage;
	}
	public void processFutureDamage(int damage) {
		curHp -= damage;
	}
	public void takeDamage(int damage) {
		curHp -= damage;
		computedHp -= damage;
	}

	public Enemy(int x, int y, int type, Game map, PApplet p) {
		bounds = new Rectangle(x + (TILE_SIZE - ENEMY_SIZE) / 2, y
				+ (TILE_SIZE - ENEMY_SIZE) / 2, ENEMY_SIZE, ENEMY_SIZE);
		this.kiloX = x * 1000;
		this.kiloY = y * 1000;
		center = new Point(x + ENEMY_SIZE / 2, y + ENEMY_SIZE / 2);
		this.map = map;
		this.p = p;
		maxHp = EnemyStats.getHP(type);
		speed = EnemyStats.getSpeed(type);
		computedHp = curHp = maxHp;
	}
	
	private int sign(int num) {
		if (num > 0)
			return 1;
		else if (num == 0)
			return 0;
		else
			return -1;
	}

	public void move(int elapsedMillis) {
		int speed = elapsedMillis * this.speed;
		int xMove = 0;
		int yMove = 0;

		// calculate total pixels to move
		int x = bounds.x / TILE_SIZE;
		int y = bounds.y / TILE_SIZE;
		int right = (bounds.x + ENEMY_SIZE) / TILE_SIZE;
		int bot = (bounds.y + ENEMY_SIZE) / TILE_SIZE;
		int targetX = x;
		int targetY = y;

		int currentDistance = map.path[y][x];
		if (x != 0 && map.path[y][x - 1] < currentDistance) {
			targetX = x - 1;
		} else if (x != map.tileWidth - 1
				&& map.path[y][x + 1] < currentDistance) {
			targetX=x+1;
		}

		if (y != 0 && map.path[y - 1][x] < currentDistance) {
			targetY=y-1;
		} else if (y != map.tileHeight - 1 && map.path[y + 1][x] < currentDistance) {
			targetY = y+1;
		}
		
		if (x != right) {
			if (targetX == right) {
				xMove = speed;
			} else {
				xMove = -speed;
			}
		} else {
			if (targetX == x + 1) {
				xMove = speed;
			} else if (targetX == x - 1) {
				xMove = -speed;
			}
		}
		
		if (y != bot) {
			if (targetY == bot) {
				yMove = speed;
			} else {
				yMove = -speed;
			}
		} else {
			if (targetY == y + 1) {
				yMove = speed;
			} else if (targetY == y - 1) {
				yMove = -speed;
			}
		}

		// find the sign of move
		int move = this.speed;
		int xSign = move * sign(xMove);
		int ySign = move * sign(yMove);
		
		// take absolute value of move
		xMove = Math.abs(xMove);
		yMove = Math.abs(yMove);

		// move pixel by pixel
		boolean moved;
		do {
			moved = false;

			if (xMove > 0) {
				xMove -= move;
				this.kiloX += xSign;
				this.bounds.x = this.kiloX / 1000;
				
				if (checkCollision()) {
					this.kiloX -= xSign;
					this.bounds.x = this.kiloX / 1000;
				} else {
					moved = true;
				}
			}

			if (yMove > 0) {
				this.kiloY += ySign;
				yMove -= move;
				this.bounds.y = this.kiloY / 1000;
				
				if (checkCollision()) {
					this.kiloY -= ySign;
					this.bounds.y = this.kiloY / 1000;
				} else {
					moved = true;
				}
			}
		} while (moved);
		
		center.setLocation(this.bounds.x + ENEMY_SIZE / 2, this.bounds.y + ENEMY_SIZE / 2);
	}

	private boolean checkCollision() {
		int left = bounds.x / TILE_SIZE;
		int up = bounds.y / TILE_SIZE;
		int right = (bounds.x + ENEMY_SIZE - 1) / TILE_SIZE;
		int down = (bounds.y + ENEMY_SIZE - 1) / TILE_SIZE;
		
		if (bounds.intersects(map.hero.x, map.hero.y, Hero.SIZE, Hero.SIZE)) {
			return true;
		}

		if (bounds.x < 0 || bounds.y < 0 || right >= map.tileWidth
				|| down >= map.tileHeight) {
			return true;
		}

		if (map.tiles[up][left].wall || map.tiles[up][right].wall
				|| map.tiles[down][left].wall || map.tiles[down][right].wall) {
			return true;
		}

		if (map.tiles[up][left].building != null
				|| map.tiles[up][right].building != null
				|| map.tiles[down][left].building != null
				|| map.tiles[down][right].building != null) {
			return true;
		}

		return false;
	}
	
	public PVector screenLoc() {
		return new PVector(bounds.x - map.mapX, bounds.y - map.mapY);
	}

	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;

		// only draw if it actually shows up
		if (x + ENEMY_SIZE <= 0 || y + ENEMY_SIZE <= 0 || x >= p.width
				|| y >= p.height - map.BAR_HEIGHT) {
			return;
		}

		final int hpHeight = 4;

		// fill
		p.noStroke();
		int row = bounds.y / TILE_SIZE;
		int col = bounds.x / TILE_SIZE;
		int zero = 0 * map.lighting[row][col] / 255;
		int full = 255 * map.lighting[row][col] / 255;
		if (attacked) {
			p.fill(p.color(full, zero, zero));
		} else {
			p.fill(p.color(zero, zero, full));
		}
		p.rect(x, y + hpHeight, ENEMY_SIZE, ENEMY_SIZE - hpHeight);

		// hp bar background
		p.fill(0);
		p.rect(x, y, ENEMY_SIZE, hpHeight);

		// hp bar
		int fill = (int) ((ENEMY_SIZE - 2) * curHp / maxHp);
		p.fill(p.color(full, zero, zero));
		p.rect(x + 1, y + 1, fill, hpHeight - 2);
	}
}
