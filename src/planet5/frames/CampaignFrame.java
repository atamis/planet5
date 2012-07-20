package planet5.frames;

import java.awt.Rectangle;

import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.game.Enemy;
import planet5.game.Map;
import processing.core.PVector;

public class CampaignFrame extends Frame {
	Map map = Map.noiseRandomLevel(p, 50, 50);
	int lastDrawTime;
	
	public CampaignFrame(Applet parent) {
		super(parent);
		map.enemies.add(new Enemy(new PVector(20, 20)));
		lastDrawTime = p.millis();
		
		// add buttons
		addButton(new MenuButton(new Rectangle(p.width-120-1, p.height-45-1, 120, 45), "Pause"));
	}
	
	@Override
	protected void draw() {
		if (p.focused) {
			map.update(p.millis() - lastDrawTime);
		}
		lastDrawTime = p.millis();
		map.draw(p);
	}
}
