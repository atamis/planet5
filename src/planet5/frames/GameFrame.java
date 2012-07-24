package planet5.frames;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import planet5.Game;
import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import planet5.framework.Applet;
import planet5.framework.Button;
import planet5.framework.Frame;
import planet5.game.Building;
import planet5.game.Map;
import planet5.game.gen.CaveGenerator;
import planet5.game.gen.VoronoiPerlinNoiseGenerator;

public class GameFrame extends Frame {
	public Map map;
	public static final int TILE_SIZE = Globals.TILE_SIZE;

	// bar variables
	public static final int BAR_HEIGHT = 45;
	public Button pauseButton;
	public boolean paused = false, help = false;
	public int lastFrameRate = 10, lastFrameRateUpdate = 0;
	public int energy = 0, maxEnergy = 1000;
	
	// game time
	public int gameTime;
	public int day, hour, minute;
	
	// building variables
	public int placingBuilding = -1;
	public Building selectedBuilding = null;

	// colors
	public static final int MONO_32 = 0xFF202020;

	public GameFrame(Applet parent) {
		super(parent);
		map = (new CaveGenerator()).gen(p, this, 200, 200);
		
		// TODO: REMOVE THESE 3 LINES LATER
		map.buildings.remove(0);
		map.base = map.buildings.get(0);
		map.recalculateField();

		// calculate path array
		map.calculatePathing();
		gameTime = 0 * 25 * 60; // start at 8 am TODO make sure no monsters spawn
		
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
			updateGameTime();
		}
		
		map.update();
		
		// remove building buy if not enough energy
		if (placingBuilding != -1 && energy < BuildingStats.costs[placingBuilding]) {
			placingBuilding = -1;
		}
	}
	public void updateGameTime() {
		gameTime += Game.speed;
		minute = gameTime / 25;
		day = minute / (24 * 60);
		minute %= 24 * 60;
		hour = minute / (60);
		minute %= 60;
	}

	void drawBar() {
		p.noStroke();
		// TODO: to greatly increase draw time, don't redraw everything
		// TODO: redrawing the bar
		//		buttons, turret select, time of day, fps counter, energy bar
		// TODO: redrawing the map
		//		movement: difficult (complete redraw if tiles are not one color rectangles)
		//		building placing: simple
		// 		enemy movement: 
		//		lighting: simple (redraw all affected tiles)
		// method takes ~335us
		drawBarBackground();	// ~45us
		drawBarBuildings();		// ~80us

		int energy_bar_start = 6 * (BAR_HEIGHT - 1) + 1;
		int energy_bar_width = p.width - 6 * (BAR_HEIGHT - 1) - 1;
		int energy_bar_fill = (int) p.map(energy, 0, maxEnergy, 0, energy_bar_width);
		
		// maximum energy bar
		// ~17us
		p.fill(64, 64, 0);
		p.rect(energy_bar_start + energy_bar_fill, BAR_HEIGHT / 2 + 2, energy_bar_width - energy_bar_fill, BAR_HEIGHT / 2 - 1);
		
		// current energy bar
		p.fill(128, 128, 0);
		p.rect(energy_bar_start, BAR_HEIGHT / 2 + 2, energy_bar_fill, BAR_HEIGHT / 2 - 1);

		// Draw the energy text.
		p.textAlign(p.CENTER, p.CENTER);
		p.fill(255);
		p.text(String.format("%d/%d", energy, maxEnergy), energy_bar_start
				, BAR_HEIGHT / 2 + 2 - p.textDescent() / 2, energy_bar_width, BAR_HEIGHT / 2 - 2);

		// Draw the timer
		p.fill(255);
		p.textAlign(p.LEFT, p.CENTER);
		String part = "AM";
		if (hour >= 12) {
			hour -= 12;
			part = "PM";
		}
		if (hour == 0) {
			hour = 12;
		}
		String time = String.format("Day %d, %d:%02d %s", day + 1, hour, minute, part);
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
	void drawBarBuildings() {
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
		// method takes ~450us
		// TODO: try using loadPixels() for faster drawing
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
		int intKey = p.key - '0';

		map.hero.keyPressed();

		// update building placement
		if (intKey >= 1 && intKey <= BuildingStats.rows.length - 1) {
			if (placingBuilding == intKey) {
				// TODO: UI
				//placingBuilding = -1;
			} else {
				placingBuilding = intKey;
			}
		} else if (p.keyCode == KeyEvent.VK_ESCAPE
				|| p.keyCode == KeyEvent.VK_SPACE) {
			placingBuilding = -1;
		} else if (p.keyCode == KeyEvent.VK_Q) {
			if (selectedBuilding != null) {
				map.sellBuilding(selectedBuilding);
			}
		}
	}

	// mouse event handlers
	@Override
	public void mousePressed() {
		for (int i = 0; i < 6; i++) {
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			if (p.mouseButton == p.LEFT && p.mouseX >= boxX && p.mouseY >= 1 &&
					energy >= BuildingStats.costs[i + 1] && energy >= BuildingStats.costs[i + 1] &&
					p.mouseX <= boxX + BAR_HEIGHT - 2 && p.mouseY <= BAR_HEIGHT) {
				if (placingBuilding == i + 1) {
					placingBuilding = -1;
				} else {
					placingBuilding = i + 1;
				}
			}
		}
		
		// do building related options
		if (p.mouseX >= BAR_HEIGHT) {
			int x = (p.mouseX + map.mapX) / TILE_SIZE;
			int y = (p.mouseY + map.mapY - BAR_HEIGHT) / TILE_SIZE; // TODO: refactor, update every tick?
			Building building = map.tiles[y][x].building;

			// check if a building can be sold
			if (building != null && selectedBuilding == building) {
				map.sellBuilding(building);
			}

			// check if a building can be selected
			if (building != null && building != map.base) {
				selectedBuilding = building;
				placingBuilding = -1;
			} else {
				selectedBuilding = null;
			}
		}

		// TODO check if an enemy can be selected (or hover?)

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
