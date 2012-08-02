package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.loaders.Fonts;

public class LevelSelectFrame extends Frame {
	MenuButton[] buttons = new MenuButton[5];
	String[] texts = { "Level 1: 40x40 (Easy)    ",
			"Level 2: 80x80 (Easy)    ",
			"Level 3: 120x120 (Medium)",
			"Level 4: 160x160 (Hard)  ",
			"Level 5: 200x200 (Hard)  " };
	public LevelSelectFrame(Applet parent) {
		super(parent);

		// add back button
		addButton(new MenuButton(new Rectangle(32, 32, 110, 63), "Back",
				Fonts.consolas32, true));
		
		
		for (int i = 0; i < 5; i++) {
			int x = 48+32;
			int y = 96+32+16+100 + i * 70;
			int w = 864;
			int h = 69;
			buttons[i] = new MenuButton(new Rectangle(x,y,w,h), texts[i], Fonts.consolas32, true);
			addButton(buttons[i]);
		}
	}

	@Override
	protected void draw() {
		p.background(0);

		p.noStroke();
		p.fill(32);
		p.rect(32, 96, p.width - 32 - 32, p.height - 64 - 32 - 32);

		p.rect(32 + 111, 32, p.width - 32 - 32 - 111, 63);

		p.fill(255);
		p.textAlign(p.CENTER);
		p.textFont(Fonts.consolas32);
		p.text("Select Level", 32, 32 + (63 - p.textDescent() - p.textAscent()) / 2,
				p.width - 32 - 32, 63);

		p.fill(255);
		
	}

	@Override
	public void buttonClicked(String command) {
		if (command.equals("Back")) {
			p.transitionFrame(Main.menuFrame, Main.fullFadeTransition);
		} else {
			for (int i = 0; i < 5; i++) {
				if (command.equals(texts[i])) {
					int size = (i+1)*40;
					Main.campaignFrame = null;
					Main.campaignFrame = new CampaignFrame(super.p, size, size);
					Main.campaignFrame.game.restartGame();
					Main.campaignFrame.update();
					p.transitionFrame(Main.campaignFrame, Main.fullFadeTransition);
				}
			}
		}
	}
}
