package planet5.frames;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import planet5.Game;
import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.framework.Applet;
import planet5.framework.Button;
import planet5.framework.Frame;
import planet5.game.Map;

public class GameFrame extends Frame {
	public Map map;

	// bar variables
	public static final int BAR_HEIGHT = 45;
	public Button pauseButton;
	public boolean paused = false;
	public boolean help = false;
	public int gameTime;
	public int lastFrameRateUpdate = 0;
	public int lastFrameRate = 10;
	public int maxEnergy = 1000, energy = 1000;
	
	// building variables
	public int placingBuilding = -1;

	// colors
	public static final int MONO_32 = 0xFF202020;

	
	public GameFrame(Applet parent) {
		super(parent);
		map = Map.noiseRandomLevel(p, this, 40, 50);
		gameTime = 0; // TODO: start at 6am?
		
		// add buttons
		// TODO: help => resume = unpaused???
		pauseButton = new MenuButton(new Rectangle(p.width - 63 - 64, 0, 63, 23), "Pause", Fonts.consolas16, true);
		addButton(pauseButton);
		addButton(new MenuButton(new Rectangle(p.width - 63 - 64 - 64, 0, 63, 23), "Help", Fonts.consolas16, true));
		addButton(new ConfirmButton(new Rectangle(p.width - 63, 0, 63, 23), "Quit"));
	}

	// updating methods
	@Override
	protected void draw() {
		updateGame();
		p.translate(0, BAR_HEIGHT);
		map.draw();
		p.translate(0, -BAR_HEIGHT);

		drawBar();
		drawShadows();
	}

	void updateGame() {
		if (!paused && !help) {
			gameTime += Game.speed;
		}
		
		if (p.focused) {
			map.update();
		}
		
		// remove building buy if not enough energy
		if (placingBuilding != -1 && energy < BuildingStats.costs[placingBuilding]) {
			placingBuilding = -1;
		}
	}

	void drawBar() {
		drawBarBackground();
		drawBarBuildings();

		int energy_bar_start = 6 * (BAR_HEIGHT - 1) + 1;
		int energy_bar_width = p.width - 6 * (BAR_HEIGHT - 1) - 1;
		
		// Draw the max energy bar.
		p.fill(64, 64, 0);
		p.rect(energy_bar_start, BAR_HEIGHT / 2 + 2, energy_bar_width, BAR_HEIGHT / 2 - 1);

		// Draw the current energy bar.
		p.fill(128, 128, 0);
		p.rect(energy_bar_start, BAR_HEIGHT / 2 + 2,
				p.map(energy, 0, maxEnergy, 0, energy_bar_width),
				BAR_HEIGHT / 2 - 1);

		// Draw the energy text.
		p.textAlign(p.CENTER, p.CENTER);
		p.fill(255);
		p.text(String.format("%d/%d", energy, maxEnergy), energy_bar_start
				, BAR_HEIGHT / 2 + 2 - p.textDescent() / 2, energy_bar_width, BAR_HEIGHT / 2 - 2);

		// Draw the timer
		p.fill(255);
		p.textAlign(p.LEFT, p.CENTER);
		int minutes = gameTime / 25;
		int days = minutes / (24 * 60);
		minutes %= 24 * 60;
		int hours = minutes / (60);
		minutes %= 60;
		String part = "AM";
		if (hours >= 12) {
			hours -= 12;
			part = "PM";
		}
		if (hours == 0) {
			hours = 12;
		}
		String time = String.format("Day %d, %d:%02d %s", days + 1, hours, minutes, part);
		p.text(time, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64 - 16, BAR_HEIGHT / 2 - 1);
		
		p.textAlign(p.RIGHT, p.CENTER);
		p.text("fps: " + lastFrameRate, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64 - 16, BAR_HEIGHT / 2 - 1);
		if (p.millis() - lastFrameRateUpdate >= 1000) {
			lastFrameRate = (int) p.frameRate;
			lastFrameRateUpdate = p.millis();
		}
	}

	void drawBarBackground() {
		// TODO: can optimize by drawing lines for left, rectangle for middle top, lines for right
		p.fill(MONO_32);
		p.rect(0, 0, p.width, BAR_HEIGHT);
		p.fill(0);
		p.rect(p.width - 3 * 64, 0, 3 * 64, 23);
	}

	private void drawBarBuildings() {
		p.noStroke();
		p.textFont(Fonts.consolas16);
		p.textAlign(p.CENTER, p.CENTER);
		
		for (int i = 0; i < 6; i++) {
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			boolean buyable = (energy >= BuildingStats.costs[i + 1]);
			
			if (buyable) {
				p.fill(128);
			} else {
				p.fill(64);
			}
			if (help) {
				
			} else if (i == placingBuilding - 1) {
				int alpha = (p.millis() / 2) % 511;
				if (alpha > 255)
					alpha = 511 - alpha;
				p.fill(p.color(32, 128, 0, alpha / 2 + 128));
			} else if (buyable && p.mouseX >= boxX && p.mouseY >= 1 &&
					p.mouseX <= boxX + BAR_HEIGHT - 2 && p.mouseY <= BAR_HEIGHT) {
				p.fill(0xFFC0C0C0); // yellow
			}
			p.rect(boxX, 1, BAR_HEIGHT - 2, BAR_HEIGHT - 1);
			p.fill(0);
			p.text("" + (i + 1), boxX, 1 - p.textDescent() / 2, BAR_HEIGHT - 2,
					BAR_HEIGHT - 1);
		}
	}
	
	void drawShadows() {
		// background shadow
		p.strokeWeight(1);
		int alpha = 255;
		if (help) {
			p.fill(0, 0, 0, 192);
			p.rect(0, BAR_HEIGHT, p.width, p.height - BAR_HEIGHT);
		}
		for (int i = 0; alpha >= 2; i++) {
			if (help) {
				p.stroke(0, 0, 0, alpha);
				p.line(0, BAR_HEIGHT + i, p.width, BAR_HEIGHT + i);
				p.line(i, BAR_HEIGHT, i, p.height);
				p.line(p.width - i - 1, BAR_HEIGHT, p.width - i - 1, p.height);
				p.line(0, p.height - i - 1, p.width, p.height - i - 1);
				alpha /= 1.5;
			} else {
				p.stroke(32, 32, 32, alpha);
				p.line(0, BAR_HEIGHT + i - 1, p.width, BAR_HEIGHT + i - 1);
				alpha /= 1.5;
			}
		}
	}

	// key event handlers
	@Override
	public void keyPressed() {
		int intKey = p.key - '0'; // the key as an integer

		map.hero.keyPressed();

		// update building placement
		if (intKey >= 1 && intKey <= BuildingStats.rows.length - 1) {
			if (placingBuilding == intKey) {
				placingBuilding = -1;
			} else {
				placingBuilding = intKey;
			}
		} else if (p.keyCode == KeyEvent.VK_ESCAPE
				|| p.keyCode == KeyEvent.VK_SPACE) {
			placingBuilding = -1;
		}
	}

	// mouse event handlers
	@Override
	public void mousePressed() {
		// TODO buy building
		for (int i = 0; i < 6; i++) { // TODO: optimize
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			boolean buyable = (energy >= BuildingStats.costs[i + 1]);
			if (buyable && p.mouseButton == p.LEFT && p.mouseX >= boxX && p.mouseY >= 1 &&
					energy >= BuildingStats.costs[i + 1] &&
					p.mouseX <= boxX + BAR_HEIGHT - 2 && p.mouseY <= BAR_HEIGHT) {
				if (placingBuilding == i + 1) {
					placingBuilding = -1;
				} else {
					placingBuilding = i + 1;
				}
			}
		}
		
		// TODO check if a building can be selected

		// TODO check if an enemy can be selected

		if (p.mouseButton == p.LEFT && placingBuilding != -1 && !help) {
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
		} else if (command.equals("Pause")) {
			pauseButton.text = pauseButton.command = "Resume";
			paused = true;
		} else if (command.equals("Resume")) {
			pauseButton.text = pauseButton.command = "Pause";
			paused = false;
		} else if (command.equals("Help")) {
			help = !help;
		}
	}
}
