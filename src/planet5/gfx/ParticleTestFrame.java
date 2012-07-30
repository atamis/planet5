package planet5.gfx;

import planet5.framework.Applet;
import planet5.framework.Frame;

public class ParticleTestFrame extends Frame {
	ParticleSystem ps;
	private long last_updated;

	public ParticleTestFrame(Applet p) {
		super(p);
		ps = new ParticleSystem();
		last_updated = 0;
		// TODO Auto-generated constructor stub
	}

	public void update() {
		int millis = (int) (p.millis() - last_updated);

		//if (p.frameCount % 4 == 0)
			ps.bloodBang(p.random(p.width), p.random(p.height));

		ps.update(millis);
		last_updated = p.millis();
	}

	@Override
	protected void draw() {
		p.background(100);
		ps.draw(p);
	}

	public void keyPressed() {
		ps.explosion(p.mouseX, p.mouseY);
	}
}
