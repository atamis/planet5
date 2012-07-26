package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;

import processing.core.PApplet;

public class Projectile {
	private static final int TILE_SIZE = Game.TILE_SIZE;
	private static final int PROJECTILE_SIZE = 16;
	private static final int EXPLOSION_RADIUS_SQ = 4 * 4;
	private static final int SPEED = 2;
	private static final int DAMAGE = 50;
	
	public Rectangle bounds;
	public Enemy target;
	public Game map;
	public boolean remove = false;
	
	private PApplet p;
	private double rotation;
	
	public Projectile(Game map, PApplet p) {
		this.map = map;
		this.p = p;
	}
	
	public void update(int elapsedMillis) {
		Point center = new Point(bounds.x + PROJECTILE_SIZE / 2, bounds.y + PROJECTILE_SIZE / 2);
		int dx = target.center.x - center.x;
		int dy = target.center.y - center.y;
		
		// move towards target
		int move = elapsedMillis * SPEED;
		int moveX = 0, moveY = 0;
		if (dx < 0)
			moveX = Math.max(dx, -move);
		else if (dx > 0)
			moveX = Math.min(dx, move);
		if (dy < 0)
			moveY = Math.max(dy, -move);
		else if (dy > 0)
			moveY = Math.min(dy, move);
		
		// turn towards target
		rotation = Math.atan2(dx, dy);
		
		// explode if target is close enough
		if (target.center.distanceSq(center) < EXPLOSION_RADIUS_SQ) {
			// kill nearby enemies
			for (Enemy e : map.enemies) {
				if (e.center.distanceSq(center) < EXPLOSION_RADIUS_SQ) {
					e.curHp -= DAMAGE;
					if (e.curHp <= 0) {
						
					}
				}
			}
			
			// TODO: explosion effect?
			remove = true;
		}
	}
	
	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;

		// only draw if it actually shows up
		if (x + PROJECTILE_SIZE <= 0 || y + PROJECTILE_SIZE <= 0 || x >= p.width
				|| y >= p.height - map.BAR_HEIGHT) {
			return;
		}
		
		// TODO: draw with rotation

		// fill
		p.noStroke();
		int row = bounds.y / TILE_SIZE;
		int col = bounds.x / TILE_SIZE;
		int zero = 0 * map.lighting[row][col] / 255;
		int full = 255 * map.lighting[row][col] / 255;
		p.fill(p.color(full, zero, zero));
		p.rect(x, y, PROJECTILE_SIZE, PROJECTILE_SIZE);
	}
}
