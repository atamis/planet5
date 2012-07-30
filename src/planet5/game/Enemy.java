package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;

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
		map.enemyArray[center.y / TILE_SIZE][center.x / TILE_SIZE].remove(this);

		// calculate total pixels to move
		int left = bounds.x / TILE_SIZE;
		int up = bounds.y / TILE_SIZE;
		int right = (bounds.x + ENEMY_SIZE) / TILE_SIZE;
		int down = (bounds.y + ENEMY_SIZE) / TILE_SIZE;
		int targetX = left;
		int targetY = up;

		int currentDistance = map.path[up][left];
		if (left != 0 && map.path[up][left - 1] < currentDistance) {
			targetX = left - 1;
		} else if (left != map.tileWidth - 1
				&& map.path[up][left + 1] < currentDistance) {
			targetX = left + 1;
		}

		if (up != 0 && map.path[up - 1][left] < currentDistance) {
			targetY = up - 1;
		} else if (up != map.tileHeight - 1
				&& map.path[up + 1][left] < currentDistance) {
			targetY = up + 1;
		}

		if (left != right)
			if (targetX == right)
				xMove = speed;
			else
				xMove = -speed;
		else if (targetX == left + 1)
			xMove = speed;
		else if (targetX == left - 1)
			xMove = -speed;

		if (up != down)
			if (targetY == down)
				yMove = speed;
			else
				yMove = -speed;
		else if (targetY == up + 1)
			yMove = speed;
		else if (targetY == up - 1)
			yMove = -speed;

		// find the sign of move
		int move = this.speed;
		int xSign = move * sign(xMove);
		int ySign = move * sign(yMove);

		// take absolute value of move
		xMove = Math.abs(xMove);
		yMove = Math.abs(yMove);

		// move pixel by pixel
		boolean moved;
		int newLeft, newRight, newUp, newDown, oldX, oldY;
		while (true) {
			moved = false;

			oldX = bounds.x;
			oldY = bounds.y;
			newLeft = left;
			newRight = right;
			newUp = up;
			newDown = down;

			if (xMove > 0) {
				xMove -= move;
				kiloX += xSign;
				bounds.x = kiloX / 1000;

				newLeft = bounds.x / TILE_SIZE;
				newRight = (bounds.x + ENEMY_SIZE - 1) / TILE_SIZE;

				if ((left != newLeft || right != newRight)
						&& checkTileCollision(newLeft, up, newRight, down)) {
					kiloX -= xSign;
					bounds.x = oldX;
					newLeft = left;
					newRight = right;
				} else if (oldX != bounds.x && checkHeroCollision()) {
					kiloX -= xSign;
					bounds.x = oldX;
					newLeft = left;
					newRight = right;
				} else {
					moved = true;
				}
			}

			if (yMove > 0) {
				kiloY += ySign;
				yMove -= move;
				bounds.y = kiloY / 1000;

				newUp = bounds.y / TILE_SIZE;
				newDown = (bounds.y + ENEMY_SIZE - 1) / TILE_SIZE;

				if ((up != newUp || down != newDown)
						&& checkTileCollision(newLeft, newUp, newRight, newDown)) {
					kiloY -= ySign;
					bounds.y = oldY;
				} else if (oldY != bounds.y && checkHeroCollision()) {
					kiloY -= ySign;
					bounds.y = oldY;
				} else {
					moved = true;
				}
			}

			if (!moved)
				break;

			left = bounds.x / TILE_SIZE;
			right = (bounds.x + ENEMY_SIZE - 1) / TILE_SIZE;
			up = bounds.y / TILE_SIZE;
			down = (bounds.y + ENEMY_SIZE - 1) / TILE_SIZE;
		}

		center.setLocation(this.bounds.x + ENEMY_SIZE / 2, this.bounds.y
				+ ENEMY_SIZE / 2);
		map.enemyArray[center.y / TILE_SIZE][center.x / TILE_SIZE].add(this);
	}

	private boolean checkTileCollision(int left, int up, int right, int down) {
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

	private boolean checkHeroCollision() {
		return bounds.intersects(map.hero.x, map.hero.y, Hero.SIZE, Hero.SIZE);
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

		if (Globals.DRAW_HP) {
			// hp bar background
			p.fill(0);
			p.rect(x, y, ENEMY_SIZE, hpHeight);

			// hp bar
			int fill = (int) ((ENEMY_SIZE - 2) * curHp / maxHp);
			p.fill(p.color(full, zero, zero));
			p.rect(x + 1, y + 1, fill, hpHeight - 2);
		}
	}
}
