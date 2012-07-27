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

	int t = 0;
	static long[] avg = new long[13];
	static long[] min = new long[13];
	static long[] max = new long[13];
	static void stat(int idx, long num) {
		
	}
	
	public static void draw(Game game) {
		long start = System.nanoTime();
		long t = System.nanoTime();
		
		p.pushMatrix();
		p.translate(0, BAR_HEIGHT);
		GameRenderer.game = game;
		
		drawTiles();
		
		stat(0, (System.nanoTime()-t));t=System.nanoTime();
		
		drawBuildings(); t=System.nanoTime();
		
		p.translate(-game.mapX, -game.mapY);
		game.hero.draw(); t=System.nanoTime();
		
		p.translate(game.mapX, game.mapY);
		drawEnemies(); t=System.nanoTime();
		
		drawProjectiles();t=System.nanoTime();
		
		if (!game.help) {
			drawBuildingPlaceover(); t=System.nanoTime();
			
			drawField();t=System.nanoTime();
			
		}
		drawLoseWin();t=System.nanoTime();
		
		
		p.popMatrix();
		
		// draw bar
		drawBarBackground();	// ~45ust=System.nanoTime();
		
		drawBarBuildings();		// ~80ust=System.nanoTime();
		
		drawBarUi(); t=System.nanoTime();
		
		drawShadows();t=System.nanoTime();
		

		
		if(++t % 10 == 0 && t % 2 == 1 && t % 2 == 0){
			p.println();
			p.println("drawTiles: " + (System.nanoTime() - t) + "ns"); 
			p.println("drawBuildings: " + (System.nanoTime() - t) + "ns");
			p.println("hero.draw: " + (System.nanoTime() - t) + "ns");
			p.println("drawEnemies: " + (System.nanoTime() - t) + "ns");
			p.println("drawProjectiles: " + (System.nanoTime() - t) + "ns"); 
			p.println("drawBuildingPlaceover: " + (System.nanoTime() - t) + "ns");
			p.println("drawField: " + (System.nanoTime() - t) + "ns"); 
			p.println("drawLoseWin: " + (System.nanoTime() - t) + "ns"); 
			p.println("drawBarBackground: " + (System.nanoTime() - t) + "ns"); 
			p.println("drawBarBuildings: " + (System.nanoTime() - t) + "ns"); 
			p.println("drawBarUi: " + (System.nanoTime() - t) + "ns");
			p.println("drawShadows: " + (System.nanoTime() - t) + "ns"); 
			p.println("total: " + (System.nanoTime() - start) + "ns");
		}
		
		if(Globals.DEBUG)
			drawDebug();
	}

	private static void drawDebug() {
		int width = 100, height = 200;
		
		p.pushMatrix();
		p.pushStyle();
		p.translate(0, p.height-height);
		p.fill(200, 200, 200, 100);
		
		p.rect(0, 0, width, height);
		
		p.fill(0);
		p.stroke(0, 255, 0);
		p.textFont(Fonts.consolas16);
		p.textAlign(p.LEFT, p.TOP);
		p.textSize(12);
		
		String[] strings = {"Health: " + UpgradeStats.level[0],
				"Gen: " + UpgradeStats.level[1],
				"MHealth: " + EnemyStats.getHP(0),
				"MDamage: " + EnemyStats.getDamage(0),
				"MSpeed: " + EnemyStats.getSpeed(0)};
		
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
		// TODO: can optimize by drawing lines for left, rectangle for middle top, lines for right
		p.noStroke();
		p.fill(0xFF202020);
		p.rect(0, 0, p.width, BAR_HEIGHT);
		p.fill(0);
		p.rect(p.width - 3 * 64, 0, 3 * 64, 23);
	}
	static void drawBarBuildings() {
		p.noStroke();
		p.textFont(Fonts.consolas16);
		p.textAlign(p.CENTER, p.CENTER);
		
		for (int i = 0; i < 6; i++) {
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			boolean buyable = (game.curEnergy >= BuildingStats.costs[i + 1]);
			
			if (buyable) {
				p.fill(128);
			} else {
				p.fill(64);
			}
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
			} else if (buyable && p.mouseX >= boxX && p.mouseY >= 1 &&
					p.mouseX <= boxX + BAR_HEIGHT - 2 && p.mouseY <= BAR_HEIGHT) {
				p.fill(0xFFC0C0C0); // yellow
			}
			p.rect(boxX, 1, BAR_HEIGHT - 2, BAR_HEIGHT - 1);
			p.fill(0);
			
			String text = "" + (i + 1);
			if (game.selectedBuilding != null && game.selectedBuilding.type == 4) {
				if (i == 5)
					text = "X";
				else
					text = "[" + text + "]";
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
		// ~17us
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
		if (hour == 0) {
			hour = 12;
		}
		String time = String.format("Day %d, %d:%02d %s", game.day + 1, hour, game.minute, part);
		p.text(time, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64 - 16, BAR_HEIGHT / 2 - 1);
		
		p.textAlign(p.RIGHT, p.CENTER);
		p.text("fps: " + game.lastFrameRate, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64 - 16, BAR_HEIGHT / 2 - 1);
		if (p.millis() - game.lastFrameRateUpdate >= 1000) {
			game.lastFrameRate = (int) p.frameRate;
			game.lastFrameRateUpdate = p.millis(); // DOESNT BELONG HERE.
		}
	}
	static void drawShadows() {
		// method takes ~450us
		// TODO: try using loadPixels() for faster drawing
		// background shadow
		p.fill(32);
		p.noStroke();
		p.rect(0, BAR_HEIGHT, p.width, 1);
		/*
		p.strokeWeight(1);
		int alpha = 255;
		if (game.help) {
			p.fill(0, 0, 0, 192);
			p.rect(0, BAR_HEIGHT, p.width, p.height - BAR_HEIGHT);
		}
		for (int i = 0; alpha >= 2; i++) {
			if (game.help) {
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
		//*/
	}
	
	static void drawTiles() {
		// method takes ~2.1ms
		p.translate(-game.mapX, -game.mapY);
		p.noStroke();

		// calculate bounding x and y values
		int xMin = game.mapX / TILE_SIZE;
		int yMin = game.mapY / TILE_SIZE;
		int xMax = (p.width + game.mapX - 1) / TILE_SIZE + 1;
		int yMax = (p.height + game.mapY - BAR_HEIGHT - 1) / TILE_SIZE + 1;

		// draw the tiles
		for (int x = xMin; x < xMax; x++) {
			for (int y = yMin; y < yMax; y++) {
				// use lighting
				int alpha = game.lighting[y][x];
				
				// use a different color for walls
				/*if (tiles[y][x].wall) {
					int mono = alpha * 128 / 255;
					p.fill(p.color(mono, mono, mono));
				} else {*/
					Color c = new Color(game.tiles[y][x].color);
					int r = c.getRed() * alpha / 255;
					int g = c.getGreen() * alpha / 255;
					int b = c.getBlue() * alpha / 255;
					p.fill(p.color(r, g, b));
				//}

				// draw the tile
				p.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				if (game.field[y][x]) {
					p.fill(0);
					//p.rect(x * TILE_SIZE, y * TILE_SIZE, 4, 4);
				}
				if (!game.tiles[y][x].wall) {
					p.fill(0);
				//	p.text(path[y][x], x * TILE_SIZE + 8, y * TILE_SIZE + 8);
				}
			}
		}

		p.translate(game.mapX, game.mapY);
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
			}
		}
		p.noStroke();
		
		// TODO: only draw if can
		for (Projectile pr : game.projectiles)
			pr.draw();
	}
	static void drawBuildingPlaceover() {
		if (!game.isPlacingBuilding()) {
			return;
		}
		
		p.translate(-game.mapX, -game.mapY);

		int x = (p.mouseX + game.mapX) / 32;
		int y = (p.mouseY + game.mapY - BAR_HEIGHT) / 32;
		int w = BuildingStats.cols[game.placingBuilding];
		int h = BuildingStats.rows[game.placingBuilding];

		// draw a transparency showing where the building will be placed
		if (game.canPlaceBuilding(x, y, w, h)) {
			p.fill(0x80208000);
		} else {
			p.fill(0x80FF0000);
		}
		p.noStroke();
		p.rect(x * TILE_SIZE, y * TILE_SIZE, w * TILE_SIZE, h * TILE_SIZE);

		p.translate(game.mapX, game.mapY);
	}
	static void drawField() {
		// TODO: improve performance
		if (game.placingBuilding != -1) {
			p.translate(-game.mapX, -game.mapY);

			// calculate bounding x and y values
			int xMin = game.mapX / TILE_SIZE;
			int yMin = game.mapY / TILE_SIZE;
			int xMax = (p.width + game.mapX - 1) / TILE_SIZE + 1;
			int yMax = (p.height + game.mapY - BAR_HEIGHT - 1) / TILE_SIZE + 1;

			// draw the tiles
			p.fill(p.color(64, 128, 255, 64));
			for (int i = xMin; i < xMax; i++) {
				for (int j = yMin; j < yMax; j++) {
					if (game.field[j][i]) {
						p.rect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
					}
				}
			}

			p.translate(game.mapX, game.mapY);
		}
	}
	static void drawLoseWin() {
		if (game.win == -1 && game.lose == -1) {
			return;
		}
		
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
