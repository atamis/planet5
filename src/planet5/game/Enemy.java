package planet5.game;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import planet5.Game;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import processing.core.PApplet;
import processing.core.PVector;

public class Enemy {
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	public static final int ENEMY_SIZE = 16;

	public static final int SPEED = 1;

	public boolean attacked = false;
	public int type;
	public Rectangle bounds;
	public Map map;
	public GameFrame game;
	public int hp;
	public int maxHp;

	public Enemy(int x, int y, int type, Map map, GameFrame game) {
		bounds = new Rectangle(x + (TILE_SIZE - ENEMY_SIZE) / 2, y
				+ (TILE_SIZE - ENEMY_SIZE) / 2, ENEMY_SIZE, ENEMY_SIZE);
		this.map = map;
		maxHp = 500; // TODO
		hp = maxHp;
	}

	public void move() {
		int speed = Game.speed * SPEED;
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
		int xSign = sign(xMove);
		int ySign = sign(yMove);

		// take absolute value of move
		if (xMove < 0) {
			xMove = -xMove;
		}
		if (yMove < 0) {
			yMove = -yMove;
		}

		// move pixel by pixel
		boolean moved;
		do {
			moved = false;

			if (xMove != 0) {
				bounds.x += xSign;
				--xMove;
				if (checkCollision()) {
					bounds.x -= xSign;
				} else {
					moved = true;
				}
			}

			if (yMove != 0) {
				bounds.y += ySign;
				--yMove;
				if (checkCollision()) {
					bounds.y -= ySign;
				} else {
					moved = true;
				}
			}
		} while (moved);
	}

	private int sign(int num) {
		if (num > 0) {
			return 1;
		} else if (num == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	private boolean checkCollision() {
		int left = bounds.x / TILE_SIZE;
		int up = bounds.y / TILE_SIZE;
		int right = (bounds.x + ENEMY_SIZE - 1) / TILE_SIZE;
		int down = (bounds.y + ENEMY_SIZE - 1) / TILE_SIZE;
		
		if (bounds.intersects(map.hero.x, map.hero.y, Hero.HERO_SIZE, Hero.HERO_SIZE)) {
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

	// TODO: 3 duplicate methods with hero.java

	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;

		// only draw if it actually shows up
		if (x + ENEMY_SIZE <= 0 || y + ENEMY_SIZE <= 0 || x >= map.p.width
				|| y >= map.p.height - game.BAR_HEIGHT) {
			return;
		}

		final int hpHeight = 4;
		PApplet p = map.p;

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
		int fill = (ENEMY_SIZE - 2) * hp / maxHp;
		p.fill(p.color(full, zero, zero));
		p.rect(x + 1, y + 1, fill, hpHeight - 2);
	}
}
