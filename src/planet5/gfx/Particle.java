package planet5.gfx;

import java.awt.Color;

import planet5.config.Globals;
import processing.core.PApplet;
import processing.core.PVector;

public class Particle {
	public Particle(PVector loc, PVector vec, int time_left, Color color, int size) {
		this.loc = loc;
		this.vec = vec;
		this.time_left = time_left;
		this.color = color;
		this.size = size;
	}
	private PVector loc;
	private PVector vec;
	private int time_left;
	private Color color;
	private int size;
	
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
	
	public void draw(PApplet p) {
		p.pushStyle();
		p.noStroke();
		p.fill(color.getRed(), color.getBlue(), color.getGreen(), color.getAlpha());
		p.ellipse(loc.x, loc.y, size, size);
		p.popStyle();
	}
}
