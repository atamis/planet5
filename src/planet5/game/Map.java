package planet5.game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import planet5.config.BuildingStats;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Map {
	// copied constants
	public static final int BAR_HEIGHT = GameFrame.BAR_HEIGHT;
	public static final int TILE_SIZE = Globals.TILE_SIZE;

	// color constants
	public static final int TRANSPARENT_RED = 0x80FF0000;
	public static final int TRANSPARENT_GREEN = 0x80208000;
	// public static final int GRAY = 0xFF808080;

	// reference variables
	public Applet p;
	public GameFrame game;

	// tile variables
	public Tile[][] tiles;
	public int[][] lighting;
	public final int[] hourToLighting = { 0, 0, 0, 0, 32, 64, 96, 128, 160, 192, 224, 255, 
			255, 255, 255, 255, 255, 224, 192, 160, 128, 96, 64, 32 };
	public final int[] enemySpawnCount = { 5, 6, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4 };
	public int[][] path;
	public int tileWidth;
	public int tileHeight;
	public int mapX = 0;
	public int mapY = 0;

	// building variables
	public ArrayList<Building> buildings = new ArrayList<Building>();

	// unit variables
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public Hero hero;

	// constructors
	public Map(Applet parent, GameFrame game, Tile[][] tiles) {
		p = parent;
		this.game = game;
		this.tiles = tiles;
		tileWidth = tiles[0].length;
		tileHeight = tiles.length;
		lighting = new int[tileHeight][tileWidth];

		hero = new Hero(p, this, 0, 0);
		// TODO: better placement
		for (int i = 0; hero == null && i < tileHeight; i++) {
			for (int j = 0; hero == null && j < tileWidth; j++) {
				if (!tiles[i][j].wall) {
					hero = new Hero(p, this, j * TILE_SIZE, i * TILE_SIZE);
				}
			}
		}
		
		Building base = null;
		for (int i = 0; buildings.size() == 0 && i < tileHeight; i++) {
			for (int j = 0; buildings.size() == 0 && j < tileWidth; j++) {
				boolean good = true;
				for (int k = 0; good && k < BuildingStats.cols[0]; k++) {
					for (int m = 0; good && m < BuildingStats.rows[0]; m++) {
						if (tiles[i + m][j + k].wall
								|| (j + k == hero.x / TILE_SIZE && i + m == hero.y
										/ TILE_SIZE)) {
							good = false;
						}
					}
				}
				if (good) {
					base = new Building(0, j, i);
					buildings.add(base);
					for (int k = 0; k < BuildingStats.cols[0]; k++) {
						for (int m = 0; m < BuildingStats.rows[0]; m++) {
							tiles[i + m][j + k].building = base;
						}
					}
				}
			}
		}

		// calculate path array
		calculatePathing(base);
	}
	
	public void calculatePathing(Building base) {
		path = new int[tileHeight][tileWidth];
		
		class AStarItem implements Comparable {
			public int dist, row, col;
			
			public AStarItem (int dist, int row, int col) {
				this.dist = dist;
				this.row = row;
				this.col = col;
			}
			
			@Override
			public int compareTo(Object other) {
				return dist - ((AStarItem) other).dist;
			}
		}
		
		ArrayList<AStarItem> AStarStack = new ArrayList<AStarItem>();
		for (int i = 0; i < BuildingStats.cols[0]; i++) {
			for (int j = 0; j < BuildingStats.rows[0]; j++) {
				AStarStack.add(new AStarItem(0, base.row + j, base.col + i));
			}
		}
		
		while (AStarStack.size() != 0) {
			
			
		}
	}

	// both update and draw are repeatedly called. update is called first
	public void update() {
		calculateVariables();
		updateMap();
		updateHero();
		updateBuildings();
		recalculateLighting();
		spawnEnemies();
		updateEnemies();
		checkGameEvents();
	}
	
	public void calculateVariables() {
		
	}

	private void updateMap() {
		// TODO
	}

	private void updateHero() {
		if (game.paused || game.help) {
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
		// update energy
		for (Building building : buildings) {
			game.energy += BuildingStats.gen[building.type];
		}
		if (game.energy > game.maxEnergy) {
			game.energy = game.maxEnergy;
		}
	}

	private void spawnEnemies() {
		int enemiesToSpawn = enemySpawnCount[game.hour];
		double chance = enemiesToSpawn / 100.0; // TODO multiply
		// TODO: use enemies.size()
		game.gameTime = 0;
		for (int i = 0; i < tileHeight; i++) {
			for (int j = 0; j < tileWidth; j++) {
				if (!tiles[i][j].wall && tiles[i][j].building == null &&
						lighting[i][j] < 128 && Math.random() < 0.0001) {
					Enemy enemy = new Enemy(j * TILE_SIZE, i * TILE_SIZE, this, game);
					enemies.add(enemy);// TODO: calculate a path
				}
				/*
				if (!tiles[i][j].wall && tiles[i][j].building == null &&
						lighting[i][j] < 128 && Math.random() < chance) {
					Enemy enemy = new Enemy(j * TILE_SIZE, i * TILE_SIZE);
					enemies.add(enemy);
				}//*/
			}
		}
	}

	private void updateEnemies() {
		// TODO: move along path or attack
	}
	// method takes ~30us to 0.1ms
	private void recalculateLighting() {
		// TODO: walls block light
		
		// global lighting based on time of day
		int hour = (game.gameTime / 1500) % 24;
		for (int i = 0; i < tileHeight; i++) {
			Arrays.fill(lighting[i], hourToLighting[hour]);
		}

		// buildings produce light
		for (Building building : buildings) {
			produceLight(building.col, building.row, building.width,
					building.height, BuildingStats.light[building.type]);
		}

		// hero produces light
		produceLight2(hero.x + hero.HERO_SIZE / 2, hero.y + hero.HERO_SIZE / 2, 16 * 32);
	}
	
	private void produceLight(int col, int row, int width, int height, int brightness) {
		--width;
		--height;
		
		// calculate how much to loop
		int radius = brightness / TILE_SIZE;
		int xMax = col + width + radius;
		int yMax = row + height + radius;
		int xCenter = col * TILE_SIZE + width * TILE_SIZE / 2;
		int yCenter = row * TILE_SIZE + height * TILE_SIZE / 2;
		
		// calculate 
		for (int i = col - radius; i < xMax; i++) {
			for (int j = row - radius; j < yMax; j++) {
				addLight(xCenter, yCenter, i, j, brightness);
			}
		}
	}
	private void produceLight2(int x, int y, int brightness) {
		// calculate how much to loop
		int radius = brightness / TILE_SIZE;
		int xMax = x / TILE_SIZE + radius;
		int yMax = y / TILE_SIZE + radius;
		
		// calculate 
		for (int i = x / TILE_SIZE - radius; i < xMax; i++) {
			for (int j = y / TILE_SIZE - radius; j < yMax; j++) {
				addLight(x, y, i, j, brightness);
			}
		}
	}

	private void addLight(int x, int y, int col, int row, int brightness) {
		// don't draw anything that's out of bounds
		if (col < 0 || row < 0 || col >= tileWidth || row >= tileHeight) {
			return;
		}
		
		int add = brightness - (int) (Math.sqrt(p.sq(x - col * TILE_SIZE) + p.sq(y - row * TILE_SIZE)));
		if (add < 0) {
			add = 0;
		}
		
		lighting[row][col] += add;
		if (lighting[row][col] > 255) {
			lighting[row][col] = 255;
		}
	}

	public void checkGameEvents() {
		
	}
	
	// drawing methods
	public void draw() {
		drawTiles();
		drawBuildings();
		hero.draw();
		drawEnemies();
		drawProjectiles();
		drawBuildingPlaceover();
	}

	// method takes ~2.1ms
	public void drawTiles() {
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
				
				// TODO: don't draw tile if building is over it?
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
	public void drawBuildings() {
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
	public void drawEnemies() {
		for (Enemy enemy : enemies) {
			enemy.draw();
		}
	}
	public void drawProjectiles() {
		
	}
	public void drawBuildingPlaceover() {
		if (!isPlacingBuilding()) {
			return;
		}
		
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

	// building related methods
	public boolean isPlacingBuilding() {
		return (game.placingBuilding != -1 && p.mouseY > BAR_HEIGHT);
	}

	// TODO: only allow building on places with light or something
	// TODO: also energy fields u no
	public boolean canPlaceBuilding(int x, int y, int w, int h) {
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
				if (c >= tileWidth || r >= tileHeight || tiles[r][c].wall
						|| tiles[r][c].building != null) {
					return false;
				}

				// check for placing over hero
				if ((c == left || c == right) && (r == up || r == down)) {
					return false;
				}
			}
		}
		
		// check for placing over enemies
		Rectangle building = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, w * TILE_SIZE, h * TILE_SIZE);
		for (Enemy enemy : enemies) {
			if (building.intersects(enemy.bounds)) {
				return false;
			}
		}
		
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

}
