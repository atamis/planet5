package planet5.game;

import java.awt.event.KeyEvent;

import planet5.config.BuildingStats;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import processing.core.PApplet;

public class Hero {
	// copied constants
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	public static final int RADIUS = 12;
	public static final int HERO_SIZE = 2 * RADIUS + 1;
	
	public int x;
	public int y;
	public Applet p;
	public Map map;
	
	public Hero(Applet p, Map map, int x, int y) {
		this.x = x;
		this.y = y;
		this.p = p;
		this.map = map;
	}
	
	public boolean checkCollision() {
		int left = (x - RADIUS) / TILE_SIZE;
		int up = (y - RADIUS) / TILE_SIZE;
		int right = (x + RADIUS) / TILE_SIZE;
		int down = (y + RADIUS) / TILE_SIZE;
		
		if (x - RADIUS < 0 || y - RADIUS < 0 || right >= map.tileWidth || down >= map.tileHeight) {
			return true;
		}
		
		// TODO: refactor
		if (map.tiles[left][up].wall || map.tiles[right][up].wall || 
				map.tiles[left][down].wall || map.tiles[right][down].wall) {
			return true;
		}

		if (map.tiles[left][up].building != null || map.tiles[right][up].building != null || 
				map.tiles[left][down].building != null || map.tiles[right][down].building != null) {
			return true;
		}
		
		
		return false;
	}
	public int sign(int num) {
		if (num > 0) {
			return 1;
		} else if (num == 0) {
			return 0;
		} else {
			return -1;
		}
	}
	public void update(int timePassed) {
		// TODO: use timePassed value
		int move = 2;
		int xMove = 0;
		int yMove = 0;
		if (p.pressedKeys[KeyEvent.VK_W]) {
			yMove -= move;
		}
		if (p.pressedKeys[KeyEvent.VK_A]) {
			xMove -= move;
		}
		if (p.pressedKeys[KeyEvent.VK_S]) {
			yMove += move;
		}
		if (p.pressedKeys[KeyEvent.VK_D]) {
			xMove += move;
		}//TODO: refactor
		
		int xSign = sign(xMove);
		int ySign = sign(yMove);
		xMove = p.abs(xMove);
		yMove = p.abs(yMove);
		
		// TODO
		boolean moved;
		do {
			moved = false;
			
			if (xMove != 0) {
				x += xSign;
				--xMove;
				if (checkCollision()) {
					x -= xSign;
				} else {
					moved = true;
				}
			}

			if (yMove != 0) {
				y += ySign;
				--yMove;
				if (checkCollision()) {
					y -= ySign;
				} else {
					moved = true;
				}
			}
		} while (moved);
		
		// constrain hero position
		//x = p.constrain(x, RADIUS + 1, map.tileWidth * TILE_SIZE - RADIUS);
		//y = p.constrain(y, RADIUS + 1, map.tileHeight * TILE_SIZE - RADIUS);
	}
	
	public void draw(int dx, int dy) {
		p.noStroke();
		p.fill(0xFF204080);
		//p.ellipse(x + dx, y + dy, 2 * RADIUS, 2 * RADIUS);
		p.rect(x + dx - RADIUS, y + dy - RADIUS, HERO_SIZE, HERO_SIZE);
	}

	public void keyPressed() {
		
	}
}
