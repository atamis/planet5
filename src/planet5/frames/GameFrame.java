package planet5.frames;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import planet5.Game;
import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.game.Map;

public class GameFrame extends Frame {
	public Map map;
	public int lastDrawTime;

	// bar variables
	public static final int barHeight = 45;
	
	// building variables
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
		p.translate(0, barHeight);
		map.draw();
		p.translate(0, -barHeight);
		
		drawBar();
	}

	void updateGameTime() {
		if (p.focused) {
			map.update(p.millis() - lastDrawTime);
		}
		lastDrawTime = p.millis();
	}
	
	void drawBar() {
		// bar background
		// TODO: replace this
		p.fill(32);
		p.noStroke();
		p.rect(0, 0, p.width, barHeight);
		
		// background shadow
		p.strokeWeight(1);
		int alpha = 255;
		for (int i = 0; alpha >= 2; i++) {
			p.stroke(0, 0, 0, alpha);
			p.line(0, barHeight + i, p.width, barHeight + i);
			alpha /= 1.5;
		}
	}

	// key event handlers
	@Override
	public void keyPressed() {
		int intKey = p.key - '0';	// the key as an integer
		
		//hero.keyPressed(keyCode)
		
		// update building placement
		if (intKey >= 1 && intKey <= BuildingStats.rows.length - 1) {
			placingBuilding = intKey;
		} else if (p.keyCode == KeyEvent.VK_ESCAPE) {
			placingBuilding = -1;
		}
	}

	// mouse event handlers
	@Override
	public void mousePressed() {
		// TODO check if a building can be selected
		
		
		// TODO check if an enemy can be selected
		
		if (placingBuilding != -1) {
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
