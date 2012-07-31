package planet5.game;

import java.awt.event.KeyEvent;

import planet5.config.SpriteMaster;
import planet5.framework.Applet;
import processing.core.PApplet;

public class Hero {
	// copied constants
	public static final int TILE_SIZE = Game.TILE_SIZE;
	
	// constants
	public static final int SIZE = 32;
	public static final int SPEED = 100;
	public static final int HP_BAR_HEIGHT = 8;
	public static final int BASE_HP = 100;

	
	
	// hero variables
	public int x, y;
	public int kiloX, kiloY;
	public int curHp, maxHp;
	
	// reference variables
	public Applet p;
	public Game map;
	
	// store most recent key pressed
	public int mostRecentWs = 0;
	public int mostRecentAd = 0;

	private float rad = 0;
	
	// constructor
	public Hero(Applet p, Game map, int x, int y) {
		this.x = x;
		this.y = y;
		this.p = p;
		this.map = map;
		maxHp = BASE_HP;
		curHp = maxHp;
	}
	
	// moves the hero, checking for wall and building collision
	public void update(int elapsedMillis) {
		if (curHp <= 0)
			return;
		
		int speed = elapsedMillis * SPEED;
		int xMove = 0;
		int yMove = 0;
		
		// calculate total pixels to move
		if (p.pressedKeys[KeyEvent.VK_W] && p.pressedKeys[KeyEvent.VK_S])
			yMove = elapsedMillis * mostRecentWs;
		else if (p.pressedKeys[KeyEvent.VK_W])
			yMove = -speed;
		else if (p.pressedKeys[KeyEvent.VK_S])
			yMove = speed;
		
		if (p.pressedKeys[KeyEvent.VK_A] && p.pressedKeys[KeyEvent.VK_D])
			xMove = elapsedMillis * mostRecentAd;
		else if (p.pressedKeys[KeyEvent.VK_A])
			xMove = -speed;
		else if (p.pressedKeys[KeyEvent.VK_D])
			xMove = speed;
		
		// find the sign of move
		int move = SPEED;
		int xSign = move * sign(xMove);
		int ySign = move * sign(yMove);
		
		// take absolute value of move
		xMove = Math.abs(xMove);
		yMove = Math.abs(yMove);
		
		// move pixel by pixel
		boolean moved;
		do {
			moved = false;
			
			if (xMove > 0) {
				kiloX += xSign;
				xMove -= move;
				x = kiloX / 1000;
				if (checkCollision()) {
					kiloX -= xSign;
					x = kiloX / 1000;
				} else {
					moved = true;
				}
			}

			if (yMove > 0) {
				kiloY += ySign;
				yMove -= move;
				y = kiloY / 1000;
				if (checkCollision()) {
					kiloY -= ySign;
					y = kiloY / 1000;
				} else {
					moved = true;
				}
			}
		} while (moved);
		
		// Rotate.
		rad = PApplet.atan2((x - map.mapX) - p.mouseX, (y - map.mapY) - p.mouseY);
				
	}

	private int sign(int num) {
		if (num > 0)
			return 1;
		else if (num == 0)
			return 0;
		else
			return -1;
	}

	private boolean checkCollision() {
		int left = x / TILE_SIZE;
		int up = y / TILE_SIZE;
		int right = (x + SIZE - 1) / TILE_SIZE;
		int down = (y + SIZE - 1) / TILE_SIZE;
		
		// TODO: can optimize more b/c max 9 squares have to be checked, not 16
		int loopTop = Math.max(0, up - 1);
		int loopLeft = Math.max(0, left - 1);
		for (int i = loopLeft; i <= right; i++)
			for (int j = loopTop; j <= down; j++)
				for (Enemy enemy : map.enemyArrayCorner[j][i])
					if (enemy.bounds.intersects(x, y, SIZE, SIZE))
						return true;
		
		if (x < 0 || y < 0 || right >= map.tileWidth || down >= map.tileHeight) {
			return true;
		}
		
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
	
	// draws the hero
	public void draw() {
		if (curHp <= 0)
			return;
		
		p.noStroke();
		
		// draw the hero
		p.fill(0xFF204080);
		p.pushMatrix();
		p.translate(x + TILE_SIZE/2, y + TILE_SIZE/2);
		p.rotate(p.TWO_PI - rad);
		p.image(SpriteMaster.instance(p).hero, -TILE_SIZE/2, -TILE_SIZE/2);
		p.popMatrix();
		
		// draw health bar outline
		int hpBarFill = (int) ((SIZE - 2) * curHp / maxHp);
		p.fill(0);
		p.rect(x, y, 1, HP_BAR_HEIGHT);
		p.rect(x + 1, y, hpBarFill, 1);
		p.rect(x + 1, y + HP_BAR_HEIGHT - 1, hpBarFill, 1);
		
		// draw health bar blackness
		p.rect(x + 1 + hpBarFill, y, SIZE - hpBarFill - 1, HP_BAR_HEIGHT);
		
		// draw health bar fill
		p.fill(0xFFC00000);
		p.rect(x + 1, y + 1, hpBarFill, HP_BAR_HEIGHT - 2);
	}

	// if the user presses keys that go in opposite directions, the most recent
	// key pressed takes priority
	public void keyPressed() {
		if (p.keyCode == KeyEvent.VK_W)
			mostRecentWs = -SPEED;
		else if (p.keyCode == KeyEvent.VK_A)
			mostRecentAd = -SPEED;
		else if (p.keyCode == KeyEvent.VK_S)
			mostRecentWs = SPEED;
		else if (p.keyCode == KeyEvent.VK_D)
			mostRecentAd = SPEED;
	}
}
