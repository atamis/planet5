package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.config.Globals;
import planet5.framework.Applet;
import planet5.framework.Button;
import planet5.framework.Frame;
import planet5.loaders.Fonts;

public class SettingsFrame extends Frame {
	MenuButton debug=new MenuButton(new Rectangle(362, 352, 300, 63), "Show Debug", Fonts.consolas32, true);
	public SettingsFrame(Applet parent) {
		super(parent);
		
		// add back button
		addButton(new MenuButton(new Rectangle(32, 32, 110, 63), "Back", Fonts.consolas32, true));
		addButton(debug);
	}

	@Override
	protected void draw() {
		p.background(0);
		
		p.noStroke();
		p.fill(32);
		p.rect(32, 96, p.width-32-32, p.height-64-32-32);
		
		p.rect(32+111, 32, p.width-32-32-111, 63);

		p.fill(255);
		p.textAlign(p.CENTER);
		p.textFont(Fonts.consolas32);
		p.text("Settings", 32, 32 + (63 - p.textDescent() - p.textAscent()) / 2, p.width-32-32, 63);
	}

	@Override
	public void buttonClicked(String command) {
		if (command.equals("Back")) {
			p.transitionFrame(Main.menuFrame, Main.fullFadeTransition);
		}
	}
	
	@Override
	public void buttonPressed(String command) {
		if (command.equals("Show Debug")) {
			debug.text="Hide Debug";
			debug.command="Hide Debug";
			Globals.DEBUG = true;
		} else if (command.equals("Hide Debug")) {
			debug.text="Show Debug";
			debug.command="Show Debug";
			Globals.DEBUG = false;
		}
	}
}
