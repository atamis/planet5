package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;

import planet5.config.BuildingStats;
import planet5.config.SpriteMaster;
import processing.core.PApplet;
import processing.core.PImage;

public class Projectile {
	private static final int TILE_SIZE = Game.TILE_SIZE;
	private static final int PROJECTILE_SIZE = 16;
	private static final int DETONATE_RADIUS_SQ = 4 * 4;
	private static final int EXPLOSION_RADIUS = 32;
	private static final int EXPLOSION_RADIUS_SQ = EXPLOSION_RADIUS * EXPLOSION_RADIUS;
	private static final float SPEED = 0.8f;
	
	private float floatX, floatY;
	public Rectangle bounds;
	public Enemy target;
	public Game map;
	public boolean remove = false;
	
	private PApplet p;
	private double rotation;
	
	public Projectile(Game map, PApplet p, int x, int y) {
		this.bounds = new Rectangle(x - PROJECTILE_SIZE / 2, y - PROJECTILE_SIZE / 2, PROJECTILE_SIZE, PROJECTILE_SIZE);
		this.map = map;
		this.p = p;
		floatX = (int) bounds.x;
		floatY = (int) bounds.y;
	}
	
	public void update(int elapsedMillis) {
		Point center = new Point(bounds.x + PROJECTILE_SIZE / 2, bounds.y + PROJECTILE_SIZE / 2);
		int dx = target.center.x - center.x;
		int dy = target.center.y - center.y;
		
		
		// move towards target
		float move = elapsedMillis * SPEED;
		double mag = dx * dx + dy * dy;
		double mult = move / Math.sqrt(mag);
		if (mult < 1) {
			dx *= mult;
			dy *= mult;
		}
		floatX += dx;
		floatY += dy;
		bounds.x = (int) floatX;
		bounds.y = (int) floatY;
		
		// turn towards target
		dx = target.center.x - center.x;
		dy = target.center.y - center.y;
		rotation = Math.PI / 2 + Math.atan2(dx, dy);
		
		// explode if target is close enough
		int damage = (int) BuildingStats.getDamage(6);
		if (target.center.distanceSq(center) < DETONATE_RADIUS_SQ) {
			int loopTop = Math.max(0, target.center.y / TILE_SIZE - EXPLOSION_RADIUS);
			int loopLeft = Math.max(0, target.center.x / TILE_SIZE - EXPLOSION_RADIUS);
			int loopBottom = Math.min(target.center.y / TILE_SIZE + 1 + EXPLOSION_RADIUS, map.tileHeight - 1);
			int loopRight = Math.min(target.center.x / TILE_SIZE + 1 + EXPLOSION_RADIUS, map.tileWidth - 1);
			
			for (int i = loopLeft; i <= loopRight; i++)
				for (int j = loopTop; j <= loopBottom; j++)
					for (Enemy enemy : map.enemyArrayCenter[j][i])
						if (enemy.center.distanceSq(center) < EXPLOSION_RADIUS_SQ) {
							enemy.processFutureDamage(damage);
							enemy.takeDamage(damage);
							if (enemy.isDead()) {
								map.ps.bloodBang(enemy.center.x - map.mapX, enemy.center.y - map.mapY);
							}
						}
			
			// TODO: explosion effect?
			remove = true;
		}
		
		if (target.isDead())
			remove = true;
	}
	
	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;
		
		map.ps.projectileTrail(x, y);

		// only draw if it actually shows up
		if (x + PROJECTILE_SIZE <= 0 || y + PROJECTILE_SIZE <= 0 || x >= p.width
				|| y >= p.height - map.BAR_HEIGHT) {
			return;
		}

		p.pushMatrix();
		p.translate(x, y);
		p.rotate((float) rotation);
		p.pushStyle();
		p.imageMode(p.CENTER);
		PImage sprite = SpriteMaster.instance(p).mortar_bullet;
		p.image(sprite, 0, 0);
		p.popMatrix();
		p.popStyle();

		// fill
		/*
		p.noStroke();
		int row = bounds.y / TILE_SIZE;
		int col = bounds.x / TILE_SIZE;
		int zero = 0 * map.lighting[row][col] / 255;
		int full = 255 * map.lighting[row][col] / 255;
		p.fill(p.color(full, zero, zero));
		p.rect(x, y, PROJECTILE_SIZE, PROJECTILE_SIZE);*/
	}
}
