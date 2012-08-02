package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import planet5.config.BuildingStats;
import planet5.loaders.SoundMaster;
import planet5.loaders.SpriteMaster;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

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
	public Point startpt;
	public Point endpt;
	float speed = 0;
	
	private PApplet p;
	private double rotation;
	
	public Projectile(Game map, PApplet p, int x, int y) {
		SoundMaster.mortar_fire.trigger();
		this.bounds = new Rectangle(x - PROJECTILE_SIZE / 2, y - PROJECTILE_SIZE / 2, PROJECTILE_SIZE, PROJECTILE_SIZE);
		this.map = map;
		this.p = p;
		floatX = (int) bounds.x;
		floatY = (int) bounds.y;
		
		
	}
	
	public void update(int elapsedMillis) {
		Point center = new Point(bounds.x + PROJECTILE_SIZE / 2, bounds.y + PROJECTILE_SIZE / 2);
		
		if (target == null) {
			speed+=1;
			double dsq=startpt.distanceSq(endpt);
			if (dsq < speed*speed){
				startpt.x = endpt.x;
				startpt.y = endpt.y;
			} else {
				int dx=endpt.x-startpt.x;
				int dy=endpt.y-startpt.y;
				
				PVector v=new PVector(dx, dy);
				v.normalize();
				v.mult(speed);
				
				startpt.x+=v.x;
				startpt.y+=v.y;
			}
			bounds.x = startpt.x;
			bounds.y = startpt.y;

			int damage = (int) BuildingStats.getDamage(6);
			
			if (endpt.equals(startpt)) {
				int loopTop = Math.max(0, (endpt.y - EXPLOSION_RADIUS) / TILE_SIZE);
				int loopLeft = Math.max(0, (endpt.x - EXPLOSION_RADIUS) / TILE_SIZE);
				int loopBottom = Math.min((endpt.y + EXPLOSION_RADIUS + TILE_SIZE - 1) / TILE_SIZE, map.tileHeight - 1);
				int loopRight = Math.min((endpt.x + EXPLOSION_RADIUS + TILE_SIZE - 1) / TILE_SIZE, map.tileWidth - 1);
						
				for (int k = loopLeft; k <= loopRight; k++)
					for (int m = loopTop; m <= loopBottom; m++) {
						Iterator<Enemy> iterator = map.enemyArrayCenter[m][k].iterator();
						while(iterator.hasNext()) {
							Enemy e = iterator.next();
								if (e.center.distanceSq(endpt) < EXPLOSION_RADIUS_SQ) {
									e.processFutureDamage(damage);
									e.takeDamage(damage);
									SoundMaster.mortar_explosion.trigger();

									if (e.isDead()) {
										//map.ps.bloodBang(e.center.x - map.mapX, e.center.y - map.mapY);
										//iterator.remove();
										//map.enemyArrayCenter[e.center.y / TILE_SIZE][e.center.x / TILE_SIZE].remove(e);
										//map.enemyArrayCorner[e.bounds.y / TILE_SIZE][e.bounds.x / TILE_SIZE].remove(e);
									}
								}
							}
						}
				
				remove = true;
			}
			
			return;
		}
		
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
			int loopTop = Math.max(0, (target.center.y - EXPLOSION_RADIUS) / TILE_SIZE);
			int loopLeft = Math.max(0, (target.center.x - EXPLOSION_RADIUS) / TILE_SIZE);
			int loopBottom = Math.min((target.center.y + EXPLOSION_RADIUS + TILE_SIZE - 1) / TILE_SIZE, map.tileHeight - 1);
			int loopRight = Math.min((target.center.x + EXPLOSION_RADIUS + TILE_SIZE - 1) / TILE_SIZE, map.tileWidth - 1);
			
			for (int i = loopLeft; i <= loopRight; i++)
				for (int j = loopTop; j <= loopBottom; j++) {
					Iterator<Enemy> iterator = map.enemyArrayCenter[j][i].iterator();
					while(iterator.hasNext()) {
						Enemy e = iterator.next();
						if (e.center.distanceSq(center) < EXPLOSION_RADIUS_SQ) {
							e.processFutureDamage(damage);
							e.takeDamage(damage);
							SoundMaster.mortar_explosion.trigger();
							if (e.isDead()) {
								//map.ps.bloodBang(e.center.x - map.mapX, e.center.y - map.mapY);
								//iterator.remove();
								//map.enemyArrayCenter[e.center.y / TILE_SIZE][e.center.x / TILE_SIZE].remove(e);
								//map.enemyArrayCorner[e.bounds.y / TILE_SIZE][e.bounds.x / TILE_SIZE].remove(e);
							}
						}
					}
				}
			
			remove = true;
		}
		
		if (target.isDead())
			remove = true;
	}
	
	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;
		
		map.ps.projectileTrail(bounds.x, bounds.y);

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
