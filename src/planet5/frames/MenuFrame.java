/*
 * MenuFrame.java
 * James Zhang
 * July 19 2012
 * The menu screen.
 */

package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.config.Fonts;
import planet5.framework.Applet;
import planet5.framework.Frame;

public class MenuFrame extends Frame {
	// menu buttons
	private final String[] buttonTexts = { "Play Campaign", "Play Survival", "Settings", "About" };
	private final int menuBorder = 1;
	private final int menuX = 64, menuY = 350;
	private final int buttonHeightOffset = 50;
	private final int buttonWidth = 300, buttonHeight = buttonHeightOffset - 1;

	public MenuFrame(Applet parent) {
		super(parent);

		// add menu buttons
		for (int i = 0; i < buttonTexts.length; i++) {
			int x = menuX;
			int y = menuY + i * buttonHeightOffset;
			int w = buttonWidth;
			int h = buttonHeight;
			addButton(new MenuButton(new Rectangle(x, y, w, h), buttonTexts[i], Fonts.consolas32, false));
		}
	}

	@Override
	protected void draw() {
		p.background(0);
		
		// draw background image
		p.fill(255, 255, 224);
		p.textFont(Fonts.consolas32);
		p.textSize(96);
		p.textAlign(p.CENTER);
		p.text("Planet 5", 0, 120, p.width, 400);
		
		// big planet
		p.fill(16, 32, 64);
		p.stroke(255, 255, 224);
		p.strokeWeight(2);
		p.ellipse(1200, 1100, 1800, 1600);
	}

	@Override
	public void buttonClicked(String command) {
		if (command.equals("Play Campaign")) {
			p.transitionFrame(Main.campaignFrame, Main.fullFadeTransition);
			// TODO: restart level? (for survival)
		}
		else if (command.equals("Play Survival")) {
			
		}
		else if (command.equals("Settings")) {
			Main.settingsFrame.phase = 0;
			p.transitionFrame(Main.settingsFrame, Main.fullFadeTransition);
		}
		else if (command.equals("About")) {
			p.transitionFrame(Main.aboutFrame, Main.fullFadeTransition);
		}
	}
}
