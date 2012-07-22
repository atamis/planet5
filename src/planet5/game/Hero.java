package planet5.game;

import java.awt.event.KeyEvent;

import planet5.Game;
import planet5.config.BuildingStats;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import processing.core.PApplet;

public class Hero {
	// copied constants
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	public static final int HERO_SIZE = 32;
	
	// constants
	public static final int SPEED = 2;
	
	// hero variables
	public int x;
	public int y;
	public int hp;
	public int maxHp;
	
	// reference variables
	public Applet p;
	public Map map;
	
	// store most recent key pressed
	public int mostRecentWs = 0;
	public int mostRecentAd = 0;
	
	public Hero(Applet p, Map map, int x, int y) {
		this.x = x;
		this.y = y;
		this.p = p;
		this.map = map;
		maxHp = 100;
		hp = 100;
	}
	
	public boolean checkCollision() {
		int left = x / TILE_SIZE;
		int up = y / TILE_SIZE;
		int right = (x + HERO_SIZE - 1) / TILE_SIZE;
		int down = (y + HERO_SIZE - 1) / TILE_SIZE;
		
		if (x < 0 || y < 0 || right >= map.tileWidth || down >= map.tileHeight) {
			return true;
		}
		
		// TODO: refactor
		if (map.tiles[up][left].wall || map.tiles[up][right].wall || 
				map.tiles[down][left].wall || map.tiles[down][right].wall) {
			return true;
		}

		if (map.tiles[up][left].building != null || map.tiles[up][right].building != null || 
				map.tiles[down][left].building != null || map.tiles[down][right].building != null) {
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
	
	public void update() {
		int speed = Game.speed * SPEED;
		int xMove = 0;
		int yMove = 0;
		
		// calculate total pixels to move
		if (p.pressedKeys[KeyEvent.VK_W] && p.pressedKeys[KeyEvent.VK_S]) {
			yMove = mostRecentWs;
		} else if (p.pressedKeys[KeyEvent.VK_W]) {
			yMove = -speed;
		} else if (p.pressedKeys[KeyEvent.VK_S]) {
			yMove = speed;
		}
		
		if (p.pressedKeys[KeyEvent.VK_A] && p.pressedKeys[KeyEvent.VK_D]) {
			xMove = mostRecentAd;
		} else if (p.pressedKeys[KeyEvent.VK_A]) {
			xMove = -speed;
		} else if (p.pressedKeys[KeyEvent.VK_D]) {
			xMove = speed;
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
	}
	
	public void draw(int dx, int dy) {
		p.noStroke();
		p.fill(0xFF204080);
		p.rect(x + dx, y + dy, HERO_SIZE, HERO_SIZE);
	}

	public void keyPressed() {
		int speed = Game.speed * SPEED;
		if (p.key == 'w') {
			mostRecentWs = -speed;
		} else if (p.key == 's') {
			mostRecentWs = speed;
		} else if (p.key == 'a') {
			mostRecentAd = -speed;
		} else if (p.key == 'd') {
			mostRecentAd = speed;
		}
	}
}
