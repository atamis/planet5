package planet5.game;

import java.awt.Rectangle;

import processing.core.PApplet;

public class Projectile {
	private static final int TILE_SIZE = Game.TILE_SIZE;
	private static final int PROJECTILE_SIZE = 16;
	
	public Rectangle bounds;
	public Enemy target;
	public Game map;
	private PApplet p;
	
	public Projectile(Game map, PApplet p) {
		this.map = map;
		this.p = p;
	}
	
	public void update() {
		// TODO: calculate rotation here
		// TODO: explode?
		
	}
	
	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;

		// only draw if it actually shows up
		if (x + PROJECTILE_SIZE <= 0 || y + PROJECTILE_SIZE <= 0 || x >= p.width
				|| y >= p.height - map.BAR_HEIGHT) {
			return;
		}

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
