package planet5.game;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PVector;

public class Enemy {
	public static final int ENEMY_SIZE = 4;
	public Rectangle bounds;
	
	public Enemy(int x, int y) {
		bounds = new Rectangle(x, y, ENEMY_SIZE, ENEMY_SIZE);
	}
	
	public void draw(PApplet p) {
		p.noStroke();
		p.fill(0x0000ff);
		p.rect(bounds.x, bounds.y, ENEMY_SIZE, ENEMY_SIZE);
	}
}
