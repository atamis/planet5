package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.config.Globals;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.loaders.Fonts;

public class SettingsFrame extends Frame {
	MenuButton debug=new MenuButton(new Rectangle(262, 289, 500, 63), "Show Debug", Fonts.consolas32, true);
	MenuButton part=new MenuButton(new Rectangle(262, 353, 500, 63), "Hide Particles", Fonts.consolas32, true);
	MenuButton conn=new MenuButton(new Rectangle(262, 414, 500, 63), "Hide Building Connections", Fonts.consolas32, true);
	public SettingsFrame(Applet parent) {
		super(parent);
		
		// add back button
		addButton(new MenuButton(new Rectangle(32, 32, 110, 63), "Back", Fonts.consolas32, true));
		addButton(debug);
		addButton(part);
		addButton(conn);
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
		} else if (command.equals("Show Particles")) {
			part.text="Hide Particles";
			part.command="Hide Particles";
			Globals.PARTICLES = true;
		} else if (command.equals("Hide Particles")) {
			part.text="Show Particles";
			part.command="Show Particles";
			Globals.PARTICLES = false;
		} else if (command.equals("Show Building Connections")) {
			conn.text="Hide Building Connections";
			conn.command="Hide Building Connections";
			Globals.CONNECTIONS = true;
		} else if (command.equals("Hide Building Connections")) {
			conn.text="Show Building Connections";
			conn.command="Show Building Connections";
			Globals.CONNECTIONS = false;
		}
	}
}
