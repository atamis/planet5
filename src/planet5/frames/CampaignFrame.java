package planet5.frames;

import java.awt.Rectangle;

import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.game.Map;

public class CampaignFrame extends Frame {
	Map map = new Map();
	int lastDrawTime;
	
	public CampaignFrame(Applet parent) {
		super(parent);
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
