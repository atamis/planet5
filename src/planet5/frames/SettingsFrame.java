package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.config.Fonts;
import planet5.framework.Applet;
import planet5.framework.Button;
import planet5.framework.Frame;

public class SettingsFrame extends Frame {
	public MenuButton[] fpsButtons;
	public int phase;
	
	public SettingsFrame(Applet parent) {
		super(parent);
		
		// add back button
		addButton(new MenuButton(new Rectangle(32, 32, 110, 63), "Back", Fonts.consolas32, true));
		
		// add fps buttons
		fpsButtons = new MenuButton[6];
		for (int i = 1; i <= 6; i++) {
			fpsButtons[i - 1] = new MenuButton(new Rectangle(32+16+128, 32+16+64+64+64*i, 110, 63), "" + (60 / i), Fonts.consolas32, true);
			addButton(fpsButtons[i - 1]);
		}
		fpsButtons[0].pressed = true;
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
		
		p.fill(255);
		p.textFont(Fonts.consolas32);
		p.textAlign(p.CENTER, p.TOP);
		p.textLeading(p.textAscent() + p.textDescent());
		p.text("Choose Target FPS", 32+16, 64+96+16, p.width-32-32-32, 128);
		
		int width = 100;
		p.fill(0xFF204080);
		//p.fill(255);
		/*
		phase += 8 * Main.speed;
		for (int i = Main.speed; i <= 6; i++) {
			//32+16+128, 32+16+64+64+64*i, 110, 63)
			int stage = 448 * (phase % 4000) / 2000;
			stage /= i;
			stage *= i;
			if (stage > 448) {
				stage = 896 - stage;
			}
			// max 320?
			p.rect(32+16+128+128+stage, 32+16+64+64+64*i, 63, 63);
		}*/
	}

	@Override
	public void buttonClicked(String command) {
		if (command.equals("Back")) {
			p.transitionFrame(Main.menuFrame, Main.fullFadeTransition);
		}
	}
	
	@Override
	public void buttonPressed(String command) {
		int newSpeed = 1;
		
		if (command.equals("60")) {
			newSpeed = 1;
		} else if (command.equals("30")) {
			newSpeed = 2;
		} else if (command.equals("20")) {
			newSpeed = 3;
		} else if (command.equals("15")) {
			newSpeed = 4;
		} else if (command.equals("12")) {
			newSpeed = 5;
		} else if (command.equals("10")) {
			newSpeed = 6;
		} else {
			return;
		}

		//fpsButtons[Main.instance.speed - 1].pressed = false;
		//Main.instance.changeSpeed(newSpeed);
		//fpsButtons[Main.instance.speed - 1].pressed = true;
	}
}
