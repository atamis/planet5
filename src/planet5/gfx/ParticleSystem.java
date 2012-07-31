package planet5.gfx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class ParticleSystem {
	public ArrayList<Particle> particles;

	public ParticleSystem() {
		particles = new ArrayList<Particle>();
	}

	public void update(int millis) {
		Iterator<Particle> i = particles.iterator();
		while (i.hasNext()) {
			Particle p = i.next();
			p.update(millis);
			if (p.dead())
				i.remove();
		}
	}

	public void draw(PApplet p) {
		for (Iterator<Particle> i = particles.iterator(); i.hasNext();) {
			Particle particle = i.next();
			particle.draw(p);
		}
	}

	public void explosion(float x, float y) {
		Random r = new Random();
		float rad = 0;
		float parts = 30f;
		for (float i = 0; i < parts; i++) {
			PVector tmp = new PVector((float) Math.cos(rad),
					(float) Math.sin(rad));
			tmp.mult(0.5f);
			particles.add(new Particle(new PVector(x, y), tmp, 500,
					new Color(0), 2));

			rad += PConstants.TWO_PI / parts;
		}
		particles.add(new Particle(new PVector(x, y), new PVector(
				r.nextFloat() * 0.25f - 0.125f, r.nextFloat() * 0.25f - 0.125f), 200,
				new Color(0xd0d0d0c8), 30));
	}
	
	public void bloodBang(float x, float y) {
		Random r = new Random();
		float rad = 0;
		float parts = 10f;
		for (float i = 0; i < parts; i++) {
			PVector tmp = new PVector((float) Math.cos(rad),
					(float) Math.sin(rad));
			tmp.mult(0.2f);
			particles.add(new Particle(new PVector(x, y), tmp, 500,
					new Color(0xc80000), 4));

			rad = r.nextFloat() * PConstants.TWO_PI;
		}

	}

	public void add(float x, float y) {
		Random r = new Random();
		particles.add(new Particle(new PVector(x, y), new PVector(
				r.nextFloat() - 0.5f, r.nextFloat() - 0.5f), 1000, new Color(
				0xff0000), 5));
	}
}