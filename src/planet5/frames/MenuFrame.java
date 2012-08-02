package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.loaders.Fonts;
import planet5.loaders.SpriteMaster;

public class MenuFrame extends Frame {
	// menu buttons
	private final String[] buttonTexts = { "Play Campaign", "Play Survival", "Settings", "About" };
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
		p.millis();
	}

	long l = 0, prev = 0;
	@Override
	public void draw() {
		l += (p.millis() - prev);
		prev=p.millis();
		
		p.background(0);
		
		// draw background image
		p.fill(255, 255, 224);
		p.textFont(Fonts.consolas96);
		p.textAlign(p.CENTER);
		p.text("Planet 5", 0, 120, p.width, 400);
				
		p.pushS();
		p.noSmooth();
		p.translate(p.width, p.height);
		p.imageMode(p.CENTER);
		p.rotate((float) (l * 0.0001));
		p.scale(25);
		p.image(SpriteMaster.instance(p).planet, 0, 0);
		p.smooth();
		p.popS();
	}

	@Override
	public void buttonClicked(String command) {
		if (command.equals("Play Campaign")) {
			p.transitionFrame(Main.levelSelectFrame, Main.fullFadeTransition);
		}
		else if (command.equals("Play Survival")) {
			Main.survivalFrame.game.restartGame();
			Main.survivalFrame.update();
			p.transitionFrame(Main.survivalFrame, Main.fullFadeTransition);
		}
		else if (command.equals("Settings")) {
			Main.settingsFrame.update();
			p.transitionFrame(Main.settingsFrame, Main.fullFadeTransition);
		}
		else if (command.equals("About")) {
			p.transitionFrame(Main.aboutFrame, Main.fullFadeTransition);
		}
	}
}
