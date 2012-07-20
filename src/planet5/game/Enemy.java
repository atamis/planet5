package planet5.game;

import processing.core.PApplet;
import processing.core.PVector;

public class Enemy {
	public PVector loc;
	
	public Enemy(PVector loc) {
		this.loc = loc;
	}
	
	public void draw(PApplet p) {
		p.fill(0x0000ff);
		p.noStroke();
		p.ellipse(loc.x, loc.y, 4, 4);
	}
}
