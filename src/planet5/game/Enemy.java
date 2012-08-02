package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;

import planet5.config.EnemyStats;
import planet5.config.Globals;
import planet5.config.SpriteMaster;
import planet5.frames.GameFrame;
import processing.core.PApplet;
import processing.core.PVector;

public class Enemy {
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	public static final int ENEMY_SIZE = 16;

	private int pXb, pYb, pXc, pYc;
	private int remainder, left;
	private int xDirection, yDirection;
	public Point center;
	public boolean attacked = false;
	public int type;
	public Rectangle bounds;
	public Game map;
	private PApplet p;
	public boolean drawHp = false;

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
		bounds = new Rectangle(x + 8, y + 8, ENEMY_SIZE, ENEMY_SIZE);
		pYb=pYc=y/32;
		pXb=pXc=x/32;
		this.remainder = 0;
		left = 0;
		this.xDirection = 0;
		this.yDirection=0;
		center = new Point(x + 16, y + 16);
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
		// calculate total pixels to move
		remainder += elapsedMillis * this.speed;
		int pixelsToMove = remainder / 1000;
		remainder -= pixelsToMove * 1000;
		
		if (left == 0) {
			left = 32;
			xDirection = map.pathX[bounds.y / 32][bounds.x / 32];
			yDirection = map.pathY[bounds.y / 32][bounds.x / 32];
		}
		
		if (pixelsToMove > left)
			pixelsToMove = left;
		
		// move that amount of pixels
		while (pixelsToMove > 0) {
			--pixelsToMove;
			--left;
			bounds.x += xDirection;
			bounds.y += yDirection;
		}

		// update center
		center.setLocation(bounds.x + 8, bounds.y + 8);

		// update array lists if needed
		int pXb2 = bounds.x / TILE_SIZE;
		int pYb2 = bounds.y / TILE_SIZE;
		if (pXb != pXb2 || pYb != pYb2) {
			map.enemyArrayCorner[pYb][pXb].remove(this);
			map.enemyArrayCorner[pYb2][pXb2].add(this);
			pXb=pXb2;
			pYb=pYb2;
		}
		
		int pXc2 = center.x / TILE_SIZE;
		int pYc2 = center.y / TILE_SIZE;
		if (pXc != pXc2 || pYc != pYc2) {
			map.enemyArrayCenter[pYc][pXc].remove(this);
			map.enemyArrayCenter[pYc2][pXc2].add(this);
			pXc=pXc2;
			pYc=pYc2;
		}
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

		int dx = map.pathX[bounds.y/32][bounds.x/32];
		int dy = map.pathY[bounds.y/32][bounds.x/32];
		float rotation = 0;
		if (dy == -1) {
			if (dx == -1) {
				rotation=-1;
			} else if (dx == 0) {
				rotation=0;
			} else {
				rotation=1;
			}
		} else if (dy == 0) {
			if (dx == -1) {
				rotation=-2;
			} else if (dx == 0) {
				rotation=0;// THIS IS CENTERED... SHOULD NEVER HAPPEN
			} else {
				rotation=2;
			}
		} else {
			if (dx == -1) {
				rotation=-3;
			} else if (dx == 0) {
				rotation=4;
			} else {
				rotation=3;
			}
		}
		rotation*=p.QUARTER_PI;
		
		p.pushMatrix();
		p.pushStyle();
		p.translate(x+8, y+8);
		p.rotate(rotation);
		p.imageMode(p.CENTER);
		int b=map.lighting[y/32][x/32];
		p.tint(p.color(b,b,b));
		p.image(SpriteMaster.instance(p).enemy, 0, 0);
		p.popStyle();
		p.popMatrix();
		if (Globals.DRAW_HP || drawHp) {
			drawHp = false;
			//p.rect(x, y + hpHeight, ENEMY_SIZE, ENEMY_SIZE - hpHeight);
			
			// draw health bar outline
			int hpBarFill = (int) ((ENEMY_SIZE - 2) * curHp / maxHp);
			p.fill(0);
			p.rect(x, y, 1, hpHeight);
			p.rect(x + 1, y, hpBarFill, 1);
			p.rect(x + 1, y + hpHeight - 1, hpBarFill, 1);
			
			// draw health bar blackness
			p.rect(x + 1 + hpBarFill, y, ENEMY_SIZE - hpBarFill - 1, hpHeight);
			
			// draw health bar fill
			p.fill(0xFFC00000);
			p.rect(x + 1, y + 1, hpBarFill, hpHeight - 2);
		} else {
		}
	}
}
