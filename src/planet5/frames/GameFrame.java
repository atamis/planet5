package planet5.frames;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import planet5.Game;
import planet5.config.Fonts;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.game.Map;

public class GameFrame extends Frame {
	public Map map;
	public int lastDrawTime;

	// what the mouse is doing
	public int placingBuilding = -1;

	public GameFrame(Applet parent) {
		super(parent);
		lastDrawTime = p.millis();
		map = Map.noiseRandomLevel(p, this, 50, 50);

		// add buttons
		addButton(new ConfirmButton(new Rectangle(0, 0, 120, 45), "Quit"));
	}

	// updating methods
	@Override
	protected void draw() {
		updateGameTime();
		p.translate(0, 45);
		map.draw();
		p.translate(0, -45);
		
		drawGui();
	}

	void updateGameTime() {
		if (p.focused) {
			map.update(p.millis() - lastDrawTime);
		}
		lastDrawTime = p.millis();
	}
	
	void drawGui() {
		// gui background
		p.fill(32);
		p.noStroke();
		p.rect(0, 0, p.width, 45);
		
		// background shadow
		p.strokeWeight(1);
		int alpha = 255;
		for (int i = 0; alpha >= 2; i++) {
			p.stroke(0, 0, 0, alpha);
			p.line(0, 45 + i, p.width, 45 + i);
			alpha /= 1.5;
		}
		
		// time
		p.noStroke();
		// p.fill(p.color()); TODO: custom
		p.fill(p.color(32, 64, 128));
		p.textFont(Fonts.consolas32);
		p.textAlign(p.RIGHT, p.CENTER);
		//p.text("12:40pm", p.width - 240, 0 - p.textDescent() / 2, 130, 45);
		
		// energy
		p.noStroke();
		p.fill(p.color(32, 64, 128));
		p.textAlign(p.CENTER, p.CENTER);
		p.text("32", p.width - 100, 0 - p.textDescent() / 2, 100, 45);
		
		// separators
		p.stroke(32);
		p.strokeWeight(1);
		//p.line(p.width - 244, 4, p.width - 244, 45-4);
		//p.line(p.width - 100, 4, p.width - 100, 45-4);
	}

	// key event handlers
	@Override
	public void keyPressed() {
		// TODO Hero.keyPressed(int keyCode)
		if (p.key >= '0' && p.key <= '9') {
			placingBuilding = p.key - '0';
		} else if (p.keyCode == KeyEvent.VK_ESCAPE) {
			// TODO reset everything
			placingBuilding = -1;
		}
	}

	// mouse event handlers
	@Override
	public void mousePressed() {
		if (placingBuilding != -1) {
			// TODO place down a building
			map.placeBuilding();
		}
	}

	@Override
	public void mouseReleased() {

	}

	// button event handlers
	@Override
	public void buttonClicked(String command) {
		if (command.equals("Quit")) {
			p.transitionFrame(Game.menuFrame, Game.fullFadeTransition);
		}
	}
}
