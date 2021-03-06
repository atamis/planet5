package planet5.game;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import planet5.config.BuildingStats;
import planet5.config.EnemyStats;
import planet5.config.Globals;
import planet5.config.UpgradeStats;
import planet5.framework.Applet;
import planet5.loaders.Fonts;
import planet5.loaders.SpriteMaster;

public final class GameRenderer {
	private static Applet p;
	private static Game game;
	public static int mapX, mapY;
	
	private static final int BAR_HEIGHT = Game.BAR_HEIGHT;
	private static final int TILE_SIZE = Game.TILE_SIZE;
	
	private GameRenderer() {
	}
	
	public static void init(Applet parent) {
		p = parent;
	}

	public static void draw(Game game) {
		GameRenderer.game = game;
		mapX = game.mapX;
		mapY = game.mapY;
		
		if (game.help) {
			drawHelp();
			
			drawBarBackground();
			drawBarBuildings();
			drawBarUi();
			return;
		}
		
		if (game.win != 0 && game.lose != 0) {
			//*
			p.translate(0, BAR_HEIGHT);
			
			p.translate(-game.mapX, -game.mapY);
			drawTiles();
			p.translate(game.mapX, game.mapY);

			p.translate(-game.mapX, -game.mapY);
			if(Globals.PARTICLES)
				game.ps.draw(p, false);
			p.translate(game.mapX, game.mapY);
			
			if (Globals.CONNECTIONS)
				for (Building building : game.buildings) {
					building.drawConnection(p, game.mapX, game.mapY, game.ps);
				}
			drawBuildings();
			
			p.translate(-game.mapX, -game.mapY);
			game.hero.draw();
			p.translate(game.mapX, game.mapY);
			
			drawEnemies();
			
			p.translate(-game.mapX, -game.mapY);
			if(Globals.PARTICLES)
				game.ps.draw(p, true);
			p.translate(game.mapX, game.mapY);

			drawProjectiles();
			
			p.translate(-game.mapX, -game.mapY);
			drawBuildingPlaceover();
			p.translate(game.mapX, game.mapY);
			//drawLoseWin();
			
			p.translate(0, -BAR_HEIGHT);
			//*/
		}
		
		int alpha = 0;
		if (game.win != -1)
			alpha = 255 - game.win * 255 / 120;
		else if (game.lose != -1)
			alpha = 255 - game.lose * 255 / 120;
		if (alpha != 0) {
			p.noStroke();
			p.fill(p.color(0,0,0,alpha));
			p.rect(0, BAR_HEIGHT, p.width, p.height -BAR_HEIGHT);
			if (alpha==255){
				p.translate(0, BAR_HEIGHT);
				drawLoseWin();
				p.translate(0, -BAR_HEIGHT);
			}
		}
		
		// draw bar
		drawBarBackground();
		
		drawBarBuildings();

		drawBarUi();
		
		if(Globals.DEBUG)
			drawDebug();
		//*/
	}

	private static void drawHelp() {
		p.background(32);
		p.fill(224);
		p.textFont(Fonts.consolas16);
		p.textAlign(p.LEFT, p.TOP);
		String text = "[WASD] to move the hero\n" +
"[ESC] or [SPACE] to cancel a selection\n" +
"[123456] to build a building\n" +
"RIGHT CLICK to shoot\n" +
"\n" +
"1 - relay: relays energy field. buildings need energy to work\n" +
"2 - solar farm: produces solar energy during the day\n" +
"3 - capacitor: stores energy\n" +
"4 - science lab: gives upgrades\n" +
"5 - laser: single target constant damage\n" +
"6 - mortar: rocket AOE damage\n" +
"\n" +
"Use the buildings at your disposal to survive the ever growing hordes of " +
"aliens. If you or your main base dies, you lose. Enemies will grow in health, " +
"damage, speed, and number. They will destroy everything in their path to reach " +
"your main base.\n" +
"\n" +
"At the science lab, you can improve your buildings in a variety of ways.\n" +
"\n" +
"1 - HP - health of buildings\n" +
"2 - GEN - energy generation\n" +
"3 - CAP - energy capacitance\n" +
"4 - DMG - turret damage\n" +
"5 - ??? - mystery upgrade\n" +
"\n" +
"Monsters too will continually improve. Monsters spawn in darkness only, 8pm to " +
"7:59 am. During the day, monsters that spawned during the night may still " +
"attack, but no new monsters will spawn.\n" +
"\n" +
"To win, there must be no monsters on the map at midnight.";
		p.text(text, 32, BAR_HEIGHT + 32, p.width - 64, p.height - BAR_HEIGHT - 64);
	}

	private static void drawDebug() {

		
		p.pushMatrix();
		p.pushStyle();
		

		/*
		String[] strings = {"FPS: " + p.frameRate,
				"LaserDMG: " + BuildingStats.getDamage(5),
				"MortarDMG: " + BuildingStats.getDamage(6),
				"Gen: " + game.getTotalGen(),
				"MHealth: " + EnemyStats.getHP(0),
				"MDamage: " + EnemyStats.getDamage(0),
				"MSpeed: " + EnemyStats.getSpeed(0),
				"MaxM: " + EnemyStats.getSpawn() * 100,
				"#Monsters: " + game.enemies.size(),
				"Particles: " + game.ps.particles.size()};
		//*/
		String text = "FPS: " + p.frameRate + "\n" +
				"LaserDMG: " + BuildingStats.getDamage(5) + "\n" +
				"MortarDMG: " + BuildingStats.getDamage(6) + "\n" +
				"Gen: " + game.getTotalGen() + "\n" +
				"MHealth: " + EnemyStats.getHP(0) + "\n" +
				"MDamage: " + EnemyStats.getDamage(0) + "\n" +
				"MSpeed: " + EnemyStats.getSpeed(0) + "\n" +
				"MaxM: " + EnemyStats.getSpawn() * 100 + "\n" +
				"#Monsters: " + game.enemies.size() + "\n" +
				"Particles: " + game.ps.particles.size();

		
		int width = 140, height = 180;
		
		p.translate(0, p.height-height);
		p.fill(200, 200, 200, 100);
		p.rect(0, 0, width, height);

		p.textFont(Fonts.consolas16);
		p.textAlign(p.LEFT, p.CENTER);
		p.textSize(12);
		p.noStroke();
		p.fill(0);
		p.text(text, 4, 0, width - 8, height);
		
		/*
		for(int i = 0; i < height/p.textAscent() + 5; i++) {
			p.pushMatrix();
			p.translate(0, i * (p.textAscent() + 5));
			p.text(strings[i%strings.length], 0, 0);
			//p.rect(0, 0, 10, 10);
			p.popMatrix();
		}
		//*/
		
		p.popStyle();
		p.popMatrix();
	}

	// drawing methods
	static void drawBarBackground() {
		p.noStroke();
		p.fill(0xFF202020);
		p.rect(0, 0, p.width, BAR_HEIGHT + 1);
		p.fill(0);
		//p.rect(p.width - 2 * 64, 0, 2 * 64, 23);
		p.rect(p.width - 3 * 64 - 5 * 32-64, 0, 3 * 64 + 5 * 32+64, 23);
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
			}
			
			if (p.mouseX >= boxX && p.mouseY >= 1 &&
					p.mouseX <= boxX + BAR_HEIGHT - 2 && p.mouseY <= BAR_HEIGHT) {
				p.pushStyle();
				
				// draw upgrade tool-tip or building tool-tip
				
				p.textFont(Fonts.consolas16);
				if (game.selectedBuilding != null && game.selectedBuilding.type == 4) {
					if (i == 5) {
						p.fill(0xFF808080);
						p.rect(4, BAR_HEIGHT + 4, 80, 24);
						p.fill(0);
						p.textAlign(p.CENTER, p.TOP);
						p.text("Cancel", 4+4, BAR_HEIGHT+4+4, 80-2*4,28);
					} else {
						int tipWidth=400;
						int tipHeight = 70;
						if(i==0)tipHeight=50;
						String[] names = { "Health", "Energy Generation", "Capacitance", "Damage", "Mystery Upgrade" };
						String[] descrips = { "Increases the max health of buildings.",
								"Increases the rate at which energy is generated from all sources.",
								"Increases the maximum amount of energy that can be stored.",
								"Increases the damage of laser turrets and mortars.",
								"No one knows what this does. That includes the programmers!" };
						
						p.fill(0xFF808080);
						p.rect(4, BAR_HEIGHT + 4, tipWidth, tipHeight);
						
						p.fill(0);
						p.textAlign(p.LEFT, p.CENTER);
						p.text(names[i], 4+4, BAR_HEIGHT+4, tipWidth,20);

						p.fill(192, 192, 0);
						p.textAlign(p.RIGHT, p.CENTER);
						p.text("Level " + (int)(UpgradeStats.level[i]/50000), 4-4, BAR_HEIGHT+4, tipWidth,20);
						
						p.fill(32);
						p.textAlign(p.LEFT, p.TOP);
						p.text(descrips[i], 4+4+4, BAR_HEIGHT+4+20+4, tipWidth-2*4-2*4,80);
					}
				} else {
					int tipWidth=400;
					int tipHeight = 90;
					
					if (i==0 || i==3 || i==4) {
						tipHeight=70;
					}
					
					p.fill(0xFF808080);
					p.rect(4, BAR_HEIGHT + 4, tipWidth, tipHeight);
					
					p.fill(0);
					p.textAlign(p.LEFT, p.CENTER);
					p.text(BuildingStats.names[i+1], 4+4, BAR_HEIGHT+4, tipWidth,20);

					if (buyable)
						p.fill(192, 192, 0);
					else
						p.fill(192, 0, 0);
					p.textAlign(p.RIGHT, p.CENTER);
					p.text(BuildingStats.costs[i+1] + " Energy", 4-4, BAR_HEIGHT+4, tipWidth,20);
					
					p.fill(32);
					p.textAlign(p.LEFT, p.TOP);
					p.text(BuildingStats.descriptions[i+1], 4+4+4, BAR_HEIGHT+4+20+4, tipWidth-2*4-2*4,80);
				}
				
				p.popStyle();
			}
			
			p.rect(boxX, 1, BAR_HEIGHT - 2, BAR_HEIGHT - 1);
			p.fill(0);
			
			String text = "" + (i + 1);
			if (game.selectedBuilding != null && game.selectedBuilding.type == 4) {
				String[] vals = { "HP", "GEN", "CAP", "DMG", "???", "X" };
				/*
				if (i == 5)
					text = "X";
				else
					text = "[" + text + "]";
				//*/
				text = vals[i];
				p.text(text, boxX, 1 - p.textDescent() / 2, BAR_HEIGHT - 2,
						BAR_HEIGHT - 1);
			} else {
				final int border = 8;
				p.image(SpriteMaster.instance(p).building_sprites[i+1], boxX + border, 1 + border, BAR_HEIGHT - 2 - 2*border,
						BAR_HEIGHT - 1 - 2*border);
			}
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
		p.text(String.format("%d/%d", (int) game.curEnergy, (int) game.maxEnergy), energy_bar_start
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
		
		//*
		p.textAlign(p.RIGHT, p.CENTER);
		String text = "fps: " + game.lastFrameRate;
		//	text = Game.DOUBLE_ASDF + "x! " + text;
		p.text(text, energy_bar_start + 8, 1 - p.textDescent() / 2, energy_bar_width - 3 * 64-64 - 16-5*32, BAR_HEIGHT / 2 - 1);
		//*/
		if (p.millis() - game.lastFrameRateUpdate >= 1000) {
			game.lastFrameRate = (int) p.frameRate;
			game.lastFrameRateUpdate = p.millis(); // DOESNT BELONG HERE.
		}
	}
	
	static void drawTiles() {
		boolean drawField = (game.placingBuilding != -1 || (game.selectedBuilding != null && game.selectedBuilding.type == 1)) && game.isPlaying();
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
		
		boolean drew=false;
		Building hoveredBuilding=null;
		if (p.mouseY >= BAR_HEIGHT) {
			int x = (p.mouseX + game.mapX) / TILE_SIZE;
			int y = (p.mouseY + game.mapY - BAR_HEIGHT) / TILE_SIZE;
			hoveredBuilding = game.tiles[y][x].building;
			
			if (hoveredBuilding != null && hoveredBuilding.buildTime == -1) {
				p.textFont(Fonts.consolas16);
				p.textAlign(p.CENTER, p.CENTER);
				p.textSize(16);
				p.fill(0);
				//int percent = (int)(100*hoveredBuilding.hp/hoveredBuilding.maxHp);
				//p.text(percent+"%", hoveredBuilding.col*TILE_SIZE-game.mapX + 1, hoveredBuilding.row*TILE_SIZE-game.mapY - p.textDescent() / 2, hoveredBuilding.width * TILE_SIZE - 2, 17);

				hoveredBuilding.drawHp(p, hoveredBuilding.col * TILE_SIZE - game.mapX, hoveredBuilding.row * TILE_SIZE - game.mapY);

				if(hoveredBuilding.type==5||hoveredBuilding.type==6){
					drew=true;
				}
			}
		}

		if (game.isPlacingBuilding()&&game.isPlaying()&&(game.placingBuilding==6||game.placingBuilding==5)) {

			int x = (p.mouseX + game.mapX) / 32;
			int y = (p.mouseY + game.mapY - BAR_HEIGHT) / 32;
			//int drawX = (p.mouseX) / 32;
			//int drawY = (p.mouseY - BAR_HEIGHT) / 32;
			
			drawTurretRange(x, y, game.placingBuilding);
		} else if (game.selectedBuilding != null && (game.selectedBuilding.type==6 ||game.selectedBuilding.type==5) ) {
			drawTurretRange(game.selectedBuilding.col, game.selectedBuilding.row, game.selectedBuilding.type);
		} else if(drew) {
			drawTurretRange(hoveredBuilding.col, hoveredBuilding.row, hoveredBuilding.type);
		}
	}
	static void drawEnemies() {
		if (p.mouseY >= BAR_HEIGHT) {
			int col = (p.mouseX + game.mapX) / TILE_SIZE -2;
			int row = (p.mouseY + game.mapY - BAR_HEIGHT) / TILE_SIZE-2;
			int right = col + 2*4;
			int bottom = row + 2*4;
			if (col < 0)
				col = 0;
			if (row < 0)
				row = 0;
			if (right >= game.tileWidth)
				right = game.tileWidth - 1;
			if (bottom >= game.tileHeight)
				bottom = game.tileHeight - 1;
			int x = p.mouseX+game.mapX;
			int y = p.mouseY+game.mapY-BAR_HEIGHT;
			Point pt = new Point(x,y);
			for (int i = col; i <= right; i++)
				for (int j = row; j <= bottom; j++) {
					ArrayList<Enemy> arr = game.enemyArrayCenter[j][i];
					for (int k = arr.size() - 1; k >= 0; k--) {
						Enemy e=arr.get(k);
						if (pt.distanceSq(e.center.x,e.center.y)<32*32) {
							// draw hp bar
							e.drawHp = true;
							
							// quit
							//i=right+1;
							//j=bottom+1;//actually j=bottom
							//break;
						}
					}
				}
		}
	
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
				//game.enemies.remove(e); // shouldn't be here
				//game.enemyArrayCenter[e.center.y / TILE_SIZE][e.center.x / TILE_SIZE].remove(e);
				//game.enemyArrayCorner[e.bounds.y / TILE_SIZE][e.bounds.x / TILE_SIZE].remove(e);
				//building.target = null;
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
		//p.rect((p.width - 300) / 2, 290, 300, 89);
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
		
	static void drawTurretRange(int x, int y, int type) {
		int drawX = x*TILE_SIZE-game.mapX;
		int drawY = y*TILE_SIZE-game.mapY;
		
		p.pushStyle();
		p.ellipseMode(p.CENTER);
		p.stroke(p.color(32, 64, 128, 192));
		p.fill(p.color(48, 96, 192, 96));
		if (type == 5) {
			p.ellipse(drawX+TILE_SIZE/2, drawY+TILE_SIZE/2, 2*32*4, 32*4*2);
		} else if (type == 6) {
			p.ellipse(drawX+TILE_SIZE, drawY+TILE_SIZE, 2*32*8, 32*8*2);
		}
		p.popStyle();
	}
}
