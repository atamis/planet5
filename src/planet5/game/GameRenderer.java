package planet5.game;

import java.awt.Color;
import java.util.ArrayList;

import planet5.config.BuildingStats;
import planet5.config.EnemyStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import planet5.config.UpgradeStats;
import processing.core.PApplet;

public final class GameRenderer {
	private static PApplet p;
	private static Game game;
	
	private static final int BAR_HEIGHT = Game.BAR_HEIGHT;
	private static final int TILE_SIZE = Game.TILE_SIZE;
	
	private GameRenderer() {
	}
	
	public static void init(PApplet parent) {
		p = parent;
	}

	public static void draw(Game game) {
		p.translate(0, BAR_HEIGHT);
		GameRenderer.game = game;

		p.translate(-game.mapX, -game.mapY);
		drawTiles();
		p.translate(game.mapX, game.mapY);
		
		drawBuildings();
		
		p.translate(-game.mapX, -game.mapY);
		game.hero.draw();
		p.translate(game.mapX, game.mapY);
		
		drawEnemies();
		
		drawProjectiles();
		
		game.ps.draw(p);

		p.translate(-game.mapX, -game.mapY);
		drawBuildingPlaceover();
		p.translate(game.mapX, game.mapY);
			
		drawLoseWin();

		p.translate(0, -BAR_HEIGHT);
		
		// draw bar
		drawBarBackground();
		
		drawBarBuildings();

		drawBarUi();
		
		if(Globals.DEBUG)
			drawDebug();
	}

	private static void drawDebug() {

		
		p.pushMatrix();
		p.pushStyle();
		
		
		p.textFont(Fonts.consolas16);
		p.textAlign(p.LEFT, p.TOP);
		p.textSize(12);

		String[] strings = {"MortarH: " + BuildingStats.getHealth(6),
				"Gen: " + game.getTotalGen(),
				"MHealth: " + EnemyStats.getHP(0),
				"MDamage: " + EnemyStats.getDamage(0),
				"MSpeed: " + EnemyStats.getSpeed(0),
				"#Monsters: " + game.enemies.size(),
				"Particles: " + game.ps.particles.size()};

		
		int width = 100, height = (int) ((strings.length + 3) * (p.textAscent() + 4));

		
		p.translate(0, p.height-height);
		p.fill(200, 200, 200, 100);
		
		p.rect(0, 0, width, height);
		
		p.fill(0);
		p.stroke(0, 255, 0);
		
		
		for(int i = 0; i < height/p.textAscent() + 5; i++) {
			p.pushMatrix();
			p.translate(0, i * (p.textAscent() + 5));
			p.text(strings[i%strings.length], 0, 0);
			//p.rect(0, 0, 10, 10);
			p.popMatrix();
		}
		
		p.popStyle();
		p.popMatrix();
	}

	// drawing methods
	static void drawBarBackground() {
		p.noStroke();
		p.fill(0xFF202020);
		p.rect(0, 0, p.width, BAR_HEIGHT + 1);
		p.fill(0);
		p.rect(p.width - 2 * 64, 0, 2 * 64, 23);
		//p.rect(p.width - 3 * 64 - 5 * 32, 0, 3 * 64 + 5 * 32, 23);
	}
	static void drawBarBuildings() {
		p.noStroke();
		p.textFont(Fonts.consolas16);
		p.textAlign(p.CENTER, p.CENTER);
		
		for (int i = 0; i < 6; i++) {
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			boolean buyable = (game.curEnergy >= BuildingStats.costs[i + 1]);
			
			if (buyable || (game.selectedBuilding != null && game.selectedBuilding.type == 4))
				p.fill(128);
			else
				p.fill(64);
			
			if (game.help) {
				
			} else if (game.selectedBuilding != null && game.selectedBuilding.type == 4 && i == game.selectedBuilding.current_upgrade) {
				int alpha = (p.millis() / 2) % 511;
				if (alpha > 255)
					alpha = 511 - alpha;
				p.fill(p.color(32, 128, 0, alpha / 2 + 128));
			} else if (i == game.placingBuilding - 1) {
				int alpha = (p.millis() / 2) % 511;
				if (alpha > 255)
					alpha = 511 - alpha;
				p.fill(p.color(32, 128, 0, alpha / 2 + 128));
			} else if (p.mouseX >= boxX && p.mouseY >= 1 &&
					p.mouseX <= boxX + BAR_HEIGHT - 2 && p.mouseY <= BAR_HEIGHT) {
				if ((buyable || (game.selectedBuilding != null && game.selectedBuilding.type == 4)))
					p.fill(0xFFC0C080); // yellow
				p.pushStyle();
				
				// draw upgrade tool-tip or building tool-tip
				if (game.selectedBuilding != null && game.selectedBuilding.type == 4) {
					
				} else {
					
				}
				
				p.popStyle();
			}
			p.rect(boxX, 1, BAR_HEIGHT - 2, BAR_HEIGHT - 1);
			p.fill(0);
			
			String text = "" + (i + 1);
			if (game.selectedBuilding != null && game.selectedBuilding.type == 4) {
				String[] vals = { "HP", "GEN", "CAP", "DMG", "EFF", "X" };
				/*
				if (i == 5)
					text = "X";
				else
					text = "[" + text + "]";
				//*/
				text = vals[i];
			}
			p.text(text, boxX, 1 - p.textDescent() / 2, BAR_HEIGHT - 2,
					BAR_HEIGHT - 1);
		}
	}
	static void drawBarUi() {
		p.noStroke();
		int energy_bar_start = 6 * (BAR_HEIGHT - 1) + 1;
		int energy_bar_width = p.width - 6 * (BAR_HEIGHT - 1) - 1;
		int energy_bar_fill = (int) p.map(game.curEnergy, 0, game.maxEnergy, 0, energy_bar_width);
		
		// maximum energy bar
		p.fill(64, 64, 0);
		p.rect(energy_bar_start + energy_bar_fill, BAR_HEIGHT / 2 + 2, energy_bar_width - energy_bar_fill, BAR_HEIGHT / 2 - 1);
		
		// current energy bar
		p.fill(128, 128, 0);
		p.rect(energy_bar_start, BAR_HEIGHT / 2 + 2, energy_bar_fill, BAR_HEIGHT / 2 - 1);

		// Draw the energy text.
		p.textAlign(p.CENTER, p.CENTER);
		p.fill(255);
		p.text(String.format("%d/%d", game.curEnergy, game.maxEnergy), energy_bar_start
				, BAR_HEIGHT / 2 + 2 - p.textDescent() / 2, energy_bar_width, BAR_HEIGHT / 2 - 2);

		// Draw the timer
		p.fill(255);
		p.textAlign(p.LEFT, p.CENTER);
		String part = "AM";
		int hour = game.hour;
		if (hour >= 12) {
			hour -= 12;
			part = "PM";
		}
		
		if (hour == 0)
			hour = 12;
		
		String time = String.format("Day %d, %d:%02d %s", game.day + 1, hour, game.minute, part);
		p.text(time, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64 - 16, BAR_HEIGHT / 2 - 1);
		
		/*
		p.textAlign(p.RIGHT, p.CENTER);
		String text = "fps: " + game.lastFrameRate;
			text = Game.DOUBLE_ASDF + "x! " + text;
		p.text(text, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64 - 16, BAR_HEIGHT / 2 - 1);
		//*/
		if (p.millis() - game.lastFrameRateUpdate >= 1000) {
			game.lastFrameRate = (int) p.frameRate;
			game.lastFrameRateUpdate = p.millis(); // DOESNT BELONG HERE.
		}
	}
	
	static void drawTiles() {
		boolean drawField = (game.placingBuilding != -1 && game.isPlaying());
		p.noStroke();

		// calculate bounding x and y values
		int xMin = game.mapX / TILE_SIZE;
		int yMin = game.mapY / TILE_SIZE;
		int xMax = (p.width + game.mapX - 1) / TILE_SIZE + 1;
		int yMax = (p.height + game.mapY - BAR_HEIGHT - 1) / TILE_SIZE + 1;

		// draw the tiles
		for (int x = xMin; x < xMax; x++) {
			for (int y = yMin; y < yMax; y++) {
				// get original tile color
				Color c = new Color(game.tiles[y][x].color);
				int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
				
				// apply field
				if (drawField && game.field[y][x]) {
					r = (192 * r + 64 * 64) / 256;
					g = (192 * g + 64 * 128) / 256;
					b = (192 * b + 64 * 255) / 256;
				}
				
				// apply lighting
				int alpha = game.lighting[y][x];
				r = r * alpha / 255;
				g = g * alpha / 255;
				b = b * alpha / 255;

				// draw the tile
				p.fill(p.color(r, g, b));
				p.rect(x * TILE_SIZE , y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}
	}
	static void drawBuildings() {
		// TODO: use this?
		Building hoveredBuilding = null;
		if (p.mouseY >= BAR_HEIGHT) {
			int x = (p.mouseX + game.mapX) / TILE_SIZE;
			int y = (p.mouseY + game.mapY - BAR_HEIGHT) / TILE_SIZE; // TODO: refactor, update every tick?
			hoveredBuilding = game.tiles[y][x].building;
		}
		
		// draw all the buildings
		for (Building building : game.buildings) {
			// calculate variables
			int x = building.col * TILE_SIZE - game.mapX;
			int y = building.row * TILE_SIZE - game.mapY;
			int w = building.width * TILE_SIZE;
			int h = building.height * TILE_SIZE;

			// only draw building if it will show up
			if (x + w >= 0 && y + h >= 0 && x <= p.width
					&& y <= p.height - BAR_HEIGHT) {
				if (building == game.selectedBuilding) {
					building.draw(p, x, y, p.millis());
				} else {
					building.draw(p, x, y, -1);
				}
			}
		}
	}
	static void drawEnemies() {
		for (Enemy enemy : game.enemies) {
			enemy.draw();
		}
	}
	static void drawProjectiles() {
		p.stroke(0xFFFF0000);
		p.strokeWeight(2);
		for (Building building : game.buildings) {
			int x = building.col * TILE_SIZE - game.mapX;
			int y = building.row * TILE_SIZE - game.mapY;
			int w = building.width * TILE_SIZE;
			int h = building.height * TILE_SIZE;

			Enemy e = building.target;
			if (x + w >= 0 && y + h >= 0 && x <= p.width
					&& y <= p.height - BAR_HEIGHT) {
				if (e != null) {
					//p.line(x + (building.width) * TILE_SIZE / 2,
					//		y + (building.height) * TILE_SIZE / 2,
					//		e.bounds.x + e.ENEMY_SIZE / 2 - game.mapX,
					//		e.bounds.y + e.ENEMY_SIZE / 2 - game.mapY);
				}
			}
			
			if (e != null && e.isDead()) {
				game.enemies.remove(e); // shouldn't be here
				game.enemyArrayCenter[e.center.y / TILE_SIZE][e.center.x / TILE_SIZE].remove(e);
				game.enemyArrayCorner[e.bounds.y / TILE_SIZE][e.bounds.x / TILE_SIZE].remove(e);
				building.target = null;
			}
		}
		p.noStroke();
		
		for (Projectile pr : game.projectiles)
			pr.draw();
	}
	static void drawBuildingPlaceover() {
		if (!game.isPlacingBuilding() || !game.isPlaying())
			return;
		
		int x = (p.mouseX + game.mapX) / 32;
		int y = (p.mouseY + game.mapY - BAR_HEIGHT) / 32;
		int w = BuildingStats.cols[game.placingBuilding];
		int h = BuildingStats.rows[game.placingBuilding];

		// draw a transparency showing where the building will be placed
		if (game.canPlaceBuilding(x, y, w, h))
			p.fill(0x80208000);
		else
			p.fill(0x80FF0000);
		
		p.noStroke();
		p.rect(x * TILE_SIZE, y * TILE_SIZE, w * TILE_SIZE, h * TILE_SIZE);
	}
	static void drawLoseWin() {
		if (game.win == -1 && game.lose == -1)
			return;
		
		p.fill(0xFF202020);
		p.rect((p.width - 300) / 2, 290, 300, 89);
		p.textFont(Fonts.consolas32);
		p.textSize(64);
		p.textAlign(p.CENTER, p.CENTER);
		if (game.win != -1) {
			p.fill(0xFF204080);
			p.text("You Win", 0, 280, p.width, 100);
		} else if (game.lose != -1) {
			p.fill(0xFFC00000);
			p.text("You Lose", 0, 280, p.width, 100);
		}
	}
}
