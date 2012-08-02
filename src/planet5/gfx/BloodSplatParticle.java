package planet5.gfx;

import java.awt.Color;
import java.util.Random;

import planet5.game.Game;
import planet5.game.GameRenderer;
import planet5.loaders.SpriteMaster;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class BloodSplatParticle extends Particle {
	private final static int max_age = 10000;
	private float rad;
	private Color c;

	public BloodSplatParticle(PVector loc) {
		super(loc, new PVector(0, 0), max_age, new Color(0xffffff), 0, false);
		Random r = new Random();
		rad = r.nextFloat() * PConstants.TWO_PI;
		c = new Color(r.nextInt(150) + 100, 0, 0, 0);
	}
	
	@Override
	public void draw(PApplet p, boolean above) {
		if (above == true)
			return;
		
		int top = (int) loc.y - GameRenderer.mapY;
		int left = (int) loc.x - GameRenderer.mapX;
		int bottom = top + SpriteMaster.instance(p).blood_splat.height;
		int right = left + SpriteMaster.instance(p).blood_splat.width;
		if (top > p.height || left > p.width || bottom < Game.BAR_HEIGHT || right < 0)
			return;
		
		p.pushStyle();
		p.pushMatrix();
		
		p.tint(c.getRed(), c.getGreen(), c.getBlue(), p.map(time_left, 0, max_age, 0, 255)); 

		
		p.translate(loc.x, loc.y);
		p.rotate(rad);
		p.imageMode(p.CENTER);
		p.image(SpriteMaster.instance(p).blood_splat, 0, 0);
		
		p.popMatrix();
		p.popStyle();
	}

}
