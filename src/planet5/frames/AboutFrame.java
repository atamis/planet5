/*
 * AboutFrame.java
 * James Zhang
 * July 19 2012
 * Shows information about the game.
 */

package planet5.frames;

import java.awt.Rectangle;

import planet5.Game;
import planet5.config.Fonts;
import planet5.framework.Applet;
import planet5.framework.Button;
import planet5.framework.Frame;

public class AboutFrame extends Frame {

	public AboutFrame(Applet parent) {
		super(parent);

		// add back button
		addButton(new MenuButton(new Rectangle(32, 32, 110, 63), "Back"));
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
		p.text("About", 32, 32 + (63 - p.textDescent() - p.textAscent()) / 2, p.width-32-32, 63);
		
		// TODO: text in the about frame
		p.fill(255);
		p.textFont(Fonts.consolas32);
		p.textAlign(p.LEFT, p.TOP);
		p.textLeading(p.textAscent() + p.textDescent());
		p.text("", 32+16, 96+16, p.width-32-32-32, p.height-32-32-32);
	}

	@Override
	protected void buttonClicked(String command) {
		if (command.equals("Back")) {
			p.transitionFrame(Game.menuFrame, Game.fullFadeTransition);
		}
	}
}
