package planet5.game;

import java.awt.Rectangle;

import planet5.config.Globals;
import planet5.frames.GameFrame;
import processing.core.PApplet;
import processing.core.PVector;

public class Enemy {
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	public static final int ENEMY_SIZE = 8;
	public Rectangle bounds;
	public Map map;
	public GameFrame game;
	
	public Enemy(int x, int y, Map map, GameFrame game) {
		bounds = new Rectangle(x + (TILE_SIZE - ENEMY_SIZE) / 2, y + (TILE_SIZE - ENEMY_SIZE) / 2, ENEMY_SIZE, ENEMY_SIZE);
		this.map = map;
	}
	
	public void draw() {
		int x = bounds.x - map.mapX;
		int y = bounds.y - map.mapY;

		// only draw if it actually shows up
		if (x + ENEMY_SIZE <= 0 || y + ENEMY_SIZE <= 0 ||
				x >= map.p.width || y >= map.p.height - game.BAR_HEIGHT) {
			return;
		}
		
		map.p.noStroke();
		int row = bounds.y / TILE_SIZE;
		int col = bounds.x / TILE_SIZE;
		int r = 0 * map.lighting[row][col] / 255;
		int g = 0 * map.lighting[row][col] / 255;
		int b = 255 * map.lighting[row][col] / 255;
		map.p.fill(map.p.color(r, g, b));
		map.p.rect(x, y, ENEMY_SIZE, ENEMY_SIZE);
	}
}
