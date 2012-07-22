package planet5.game;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import processing.core.PApplet;
import processing.core.PVector;

public class Map {
	// copied constants
	public static final int BAR_HEIGHT = GameFrame.BAR_HEIGHT;
	public static final int TILE_SIZE = Globals.TILE_SIZE;

	// color constants
	public static final int TRANSPARENT_RED = 0x80FF0000;
	public static final int TRANSPARENT_GREEN = 0x80208000;
	//public static final int GRAY = 0xFF808080;
	
	// reference variables
	public Applet p;
	public GameFrame game;
	
	// tile variables
	public Tile[][] tiles;
	public int[][] lighting;
	final int[] hourToLighting = { 0, 0, 0, 0, 32, 64, 96, 128, 160, 192, 224, 255, 
			255, 255, 255, 255, 255, 224, 192, 160, 128, 96, 64, 32 };
	public int tileWidth;
	public int tileHeight;
	public int mapX = 0;
	public int mapY = 0;
	
	// building variables
	public ArrayList<Building> buildings = new ArrayList<Building>();
	
	// unit variables
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public Hero hero;

	// other variables
	int timeOfDay;

	// constructors
	public Map(Applet parent, GameFrame game, Tile[][] tiles) {
		p = parent;
		this.game = game;
		this.tiles = tiles;
		tileWidth = tiles[0].length;
		tileHeight = tiles.length;
		// tiles[y][x]
		
		lighting = new int[tileHeight][tileWidth];
		
		// TODO: better placement
		for (int i = 0; hero == null && i < tileHeight; i++) {
			for (int j = 0; hero == null && j < tileWidth; j++) {
				if (!tiles[i][j].wall) {
					hero = new Hero(p, this, j * TILE_SIZE, i * TILE_SIZE);
				}
			}
		}

		// TODO: better placement
		for (int i = 0; buildings.size() == 0 && i < tileHeight; i++) {
			for (int j = 0; buildings.size() == 0 && j < tileWidth; j++) {
				boolean good = true;
				for (int k = 0; good && k < BuildingStats.cols[0]; k++) {
					for (int m = 0; good && m < BuildingStats.rows[0]; m++) {
						if (tiles[i + m][j + k].wall || (j + k == hero.x / TILE_SIZE && i + m == hero.y / TILE_SIZE)) {
							good = false;
						}
					}
				}
				if (good) {
					Building base = new Building(0, j, i);
					buildings.add(base);
					for (int k = 0; k < BuildingStats.cols[0]; k++) {
						for (int m = 0; m < BuildingStats.rows[0]; m++) {
							tiles[i + m][j + k].building = base;
						}
					}
				}
			}
		}
	}

	// timePassed is the amount of milliseconds between the last update and this
	// one
	// both update and draw are repeatedly called. update is called first
	public void update() {
		updateMap();
		updateHero();
		updateBuildings();
		spawnEnemies();
		updateEnemies();
		recalculateLighting();
		// checkGameEvents
	}

	private void updateMap() {
		//TODO
	}

	private void updateHero() {
		if (game.paused) {
			return;
		}
		
		hero.update();
		
		// update map position
		mapX = (int) hero.x - p.width / 2;
		mapY = (int) hero.y - (p.height - BAR_HEIGHT) / 2;

		// constrain map position
		mapX = p.constrain(mapX, 0, tileWidth * TILE_SIZE - p.width);
		mapY = p.constrain(mapY, 0, tileHeight * TILE_SIZE - p.height
				+ BAR_HEIGHT);
	}

	private void updateBuildings() {
		// TODO
	}

	private void spawnEnemies() {
		// TODO: use the following to spawn enemies:
		// timeOfDay: how many monsters to spawn
		// tiles: where to spawn the monsters
		// and maybe more
	}

	private void updateEnemies() {
		// TODO
	}
	private void recalculateLighting() {
		// TODO: use the following to recalculate the lighting of tiles:
		// timeOfDay: cover entire map
		// tiles: walls may block light
		// buildings: produce their own light
		// hero: produces lighting
		
		// global lighting based on time of day
		// optimized: saved ~0.06 ms
		int hour = (game.gameTime / 1500) % 24;
		for(int i = 0; i < tileHeight; i++) {
			Arrays.fill(lighting[i], hourToLighting[hour]);
		}
		
		// TODO: only allow building on places with light or something
		// TODO: also energy fields u no
		// buildings produce light
		for (Building building : buildings) {
			produceLight(building.col, building.row, building.width, building.height, BuildingStats.light[building.type]);
		}
		
		// hero produces light
		produceLight((hero.x + hero.HERO_SIZE / 2) / hero.HERO_SIZE, 
				(hero.y + hero.HERO_SIZE / 2) / hero.HERO_SIZE, 1, 1, 16 * 32);
	}
	private void produceLight(int col, int row, int width, int height, int brightness) {
		int x = 32 * col + 32 * (width - 1) / 2;//TODO
		int y = 32 * row + 32 * (height - 1) / 2;
		width -= 1;
		height -= 1;
		
		for (int j = 0; j < brightness / TILE_SIZE; j++) {
			// top and bottom
			for (int i = col; i <= col + width; i++) {
				addLight(x, y, i, row, brightness);
				addLight(x, y, i, row + height, brightness);
			}
			
			// left and right
			for (int i = row + 1; i <= row + height - 1; i++) {
				addLight(x, y, col, i, brightness);
				addLight(x, y, col + width, i, brightness);
			}
			--col;
			--row;
			width += 2;
			height += 2;
		}
	}
	private void addLight(int x, int y, int col, int row, int brightness) {
		if (col < 0 || row < 0 || col >= tileWidth || row >= tileHeight) {
			return;
		}
		// TODO: refactor
		int value = lighting[row][col];
		int add = brightness - (int) (p.sqrt(p.sq(x - col * 32) + p.sq(y - row * 32)));
		value += p.max(0, add);
		value = p.constrain(value, 0, 255);
		lighting[row][col] = value;
	}

	public void draw() {
		drawTiles();
		drawBuildings();
		hero.draw(-mapX, -mapY);
		// TODO: draw enemies
		// TODO: draw projectiles

		// draw place building transparency
		if (isPlacingBuilding()) {
			drawBuildingPlaceover();
		}
	}

	// basic drawing methods
	public void drawTiles() {
		// optimized: saved ~1ms
		p.translate(-mapX, -mapY);
		p.noStroke();

		// calculate bounding x and y values
		int xMin = mapX / TILE_SIZE;
		int yMin = mapY / TILE_SIZE;
		int xMax = (p.width + mapX - 1) / TILE_SIZE + 1;
		int yMax = (p.height + mapY - BAR_HEIGHT - 1) / TILE_SIZE + 1;

		// draw the tiles
		for (int x = xMin; x < xMax; x++) {
			for (int y = yMin; y < yMax; y++) {
				// use lighting
				int alpha = lighting[y][x];
				
				// TODO: dont draw if building over it?
				//if (tiles[y][x].building != null)
				//	continue;

				// use a different color for walls
				if (tiles[y][x].wall) {
					int mono = alpha * 128 / 255;
					p.fill(p.color(mono, mono, mono));
				} else {
					Color c = new Color(tiles[y][x].color);
					int r = c.getRed() * alpha / 255;
					int g = c.getGreen() * alpha / 255;
					int b = c.getBlue() * alpha / 255;
					p.fill(p.color(r, g, b));
				}

				// draw the tile
				p.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}
		
		p.translate(mapX, mapY);
	}

	private void drawBuildings() {
		// draw all the buildings
		for (Building building : buildings) {
			// calculate variables
			int x = building.col * TILE_SIZE - mapX;
			int y = building.row * TILE_SIZE - mapY;
			int w = building.width * TILE_SIZE;
			int h = building.height * TILE_SIZE;

			// only draw building if it will show up
			if (x + w >= 0 && y + h >= 0 && x <= p.width
					&& y <= p.height - BAR_HEIGHT) {
				building.draw(p, x, y);
			}
		}
	}

	// building related methods
	public boolean isPlacingBuilding() {
		return (game.placingBuilding != -1 && p.mouseY > BAR_HEIGHT);
	}

	public void drawBuildingPlaceover() {
		p.translate(-mapX, -mapY);
		
		int x = (p.mouseX + mapX) / 32;
		int y = (p.mouseY + mapY - BAR_HEIGHT) / 32;
		int w = BuildingStats.cols[game.placingBuilding];
		int h = BuildingStats.rows[game.placingBuilding];

		// draw a transparency showing where the building will be placed
		if (canPlaceBuilding(x, y, w, h)) {
			p.fill(TRANSPARENT_GREEN);
		} else {
			p.fill(TRANSPARENT_RED);
		}
		p.noStroke();
		p.rect(x * TILE_SIZE, y * TILE_SIZE, w * TILE_SIZE, h * TILE_SIZE);

		p.translate(mapX, mapY);
	}

	public boolean canPlaceBuilding(int x, int y, int w, int h) {
		// TODO: refactor this
		int left = hero.x / TILE_SIZE;
		int up = hero.y / TILE_SIZE;
		int right = (hero.x + hero.HERO_SIZE - 1) / TILE_SIZE;
		int down = (hero.y + hero.HERO_SIZE - 1) / TILE_SIZE;
		
		// loop through the tiles this building covers
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				// calculate column and row
				int c = x + i, r = y + j;
				
				// check for placing over walls and buildings
				if (c >= tileWidth|| r >= tileHeight
						|| tiles[r][c].wall || tiles[r][c].building != null) {
					return false;
				}
				
				// check for placing over hero
				if ((c == left || c == right) && (r == up || r == down)) {
					return false;
				}
			}
		}
		
		
		// TODO: check for placing over enemies

		return true;
	}

	public void placeBuilding() {
		int x = (p.mouseX + mapX) / 32;
		int y = (p.mouseY + mapY - BAR_HEIGHT) / 32;
		int w = BuildingStats.cols[game.placingBuilding];
		int h = BuildingStats.rows[game.placingBuilding];
		int buildingType = game.placingBuilding;

		if (isPlacingBuilding() && canPlaceBuilding(x, y, w, h)) {
			Building placedBuilding = new Building(buildingType, x, y);
			game.energy -= BuildingStats.costs[buildingType];
			buildings.add(placedBuilding);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					int c = x + i, r = y + j;
					tiles[r][c].building = placedBuilding;
				}
			}
		}
	}

	public void removeBuilding() {
		// TODO: parameters too
	}
	
	// level generation methods
	public static Map reallyRandomLevel(Applet p, GameFrame game, int width,
			int height, Random r) {
		Tile[][] tiles = new Tile[height][width];
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, r.nextBoolean());
			}
		}
		return new Map(p, game, tiles);
	}

	public static Map noiseRandomLevel(Applet p, GameFrame game, int width,
			int height) {
		Tile[][] tiles = new Tile[height][width];
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff,
						(p.noise(x * 0.1f, y * 0.1f) > 0.5));
				float xf = x * 0.03f, yf = y * 0.03f;
				tiles[x][y].color = p.color(255 * p.noise(xf, yf),
						255 * p.noise(xf, 0, yf), 255 * p.noise(0, xf, yf));
			}
		}
		return new Map(p, game, tiles);
	}
}
