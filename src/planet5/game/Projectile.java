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
	private static final int EXPLOSION_RADIUS_SQ = 32 * 32;
	private static final float SPEED = 0.2f;
	public static final int DAMAGE = 50;
	
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
		rotation = Math.atan2(dx, dy);
		
		// explode if target is close enough
		if (target.center.distanceSq(center) < DETONATE_RADIUS_SQ) {
			// kill nearby enemies
			for (Enemy e : map.enemies) {
				if (e.center.distanceSq(center) < EXPLOSION_RADIUS_SQ) {
					e.processFutureDamage(DAMAGE);
					if (e.isDead()) {
						
					}
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
		PImage sprite = SpriteMaster.instance(p).hero;
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
