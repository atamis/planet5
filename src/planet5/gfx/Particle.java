package planet5.gfx;

import java.awt.Color;

import planet5.config.Globals;
import planet5.game.Game;
import planet5.game.GameRenderer;
import processing.core.PApplet;
import processing.core.PVector;

public class Particle {
	public Particle(PVector loc, PVector vec, int time_left, Color color, int size, boolean above) {
		this.loc = loc;
		this.vec = vec;
		this.time_left = time_left;
		this.color = color;
		this.size = size;
	}
	public PVector loc;
	private PVector vec;
	public int time_left;
	private Color color;
	private int size;
	private boolean above;
	
	public boolean dead() {
		return time_left < 0;
	}
	
	public void update(int millis) {
		PVector t = new PVector(0, 0);
		t.set(vec);
		t.mult(millis);
		loc.add(t);
		vec.mult(Globals.FRICTION);
		time_left -= millis;
	}
	
	public void draw(PApplet p, boolean a) {
		if (above != a)
			return;
		
		int top = (int) loc.y - GameRenderer.mapY;
		int left = (int) loc.x - GameRenderer.mapX;
		int bottom = top + size;
		int right = left + size;
		if (top > p.height - Game.BAR_HEIGHT || left > p.width || bottom < 0 || right < 0) {
			return;
		}
		
		p.pushStyle();
		p.noStroke();
		p.fill(color.getRed(), color.getBlue(), color.getGreen(), color.getAlpha());
		p.ellipse(loc.x, loc.y, size, size);
		p.popStyle();
	}
}
