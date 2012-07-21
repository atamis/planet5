package planet5.game;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
	public static final int GRAY = 0xFF808080;
	
	// reference variables
	public Applet p;
	public GameFrame game;
	
	// tile variables
	public Tile[][] tiles;
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
		
		// TODO: better placing
		for (int i = 0; hero == null && i < tileHeight; i++) {
			for (int j = 0; hero == null && j < tileWidth; j++) {
				if (!tiles[i][j].wall) {
					hero = new Hero(p, this, i * TILE_SIZE + TILE_SIZE / 2, j * TILE_SIZE + TILE_SIZE / 2);
				}
			}
		}
	}

	// timePassed is the amount of milliseconds between the last update and this
	// one
	// both update and draw are repeatedly called. update is called first
	public void update(int timePassed) {
		updateMap(timePassed);
		updateHero(timePassed);
		updateBuildings(timePassed);
		spawnEnemies(timePassed);
		updateEnemies(timePassed);
		recalculateLighting();
		// checkGameEvents
	}

	private void updateMap(int timePassed) {
		timeOfDay += timePassed;
	}

	private void updateHero(int timePassed) {
		hero.update(timePassed);
		
		// update map position
		mapX = (int) hero.x - p.width / 2;
		mapY = (int) hero.y - (p.height - BAR_HEIGHT) / 2;

		// constrain map position
		mapX = p.constrain(mapX, 0, tileWidth * TILE_SIZE - p.width);
		mapY = p.constrain(mapY, 0, tileHeight * TILE_SIZE - p.height
				+ BAR_HEIGHT);
	}

	private void updateBuildings(int timePassed) {
		// TODO
	}

	private void spawnEnemies(int timePassed) {
		// TODO: use the following to spawn enemies:
		// timeOfDay: how many monsters to spawn
		// tiles: where to spawn the monsters
		// and maybe more
	}

	private void updateEnemies(int timePassed) {
		// TODO
	}

	private void recalculateLighting() {
		// TODO: use the following to recalculate the lighting of tiles:
		// timeOfDay: cover entire map
		// tiles: walls may block light
		// buildings: produce their own light
		// hero: produces lighting
		// max light is 8, minimum is 0
	}

	public void draw() {
		drawTiles();
		drawBuildings();
		hero.draw(-mapX, -mapY);
		// TODO: draw enemies
		// TODO: draw projectiles
	}

	// basic drawing methods
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
				// use a different color for walls
				if (tiles[x][y].wall) {
					p.fill(GRAY);
				} else {
					p.fill(tiles[x][y].color);
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

		// draw place building transparency
		if (isPlacingBuilding()) {
			drawBuildingPlaceover();
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
		int left = (hero.x - hero.RADIUS) / TILE_SIZE;
		int up = (hero.y - hero.RADIUS) / TILE_SIZE;
		int right = (hero.x + hero.RADIUS) / TILE_SIZE;
		int down = (hero.y + hero.RADIUS) / TILE_SIZE;
		
		// loop through the tiles this building covers
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				// calculate column and row
				int c = x + i, r = y + j;
				
				// check for placing over walls and buildings
				if (c >= tileWidth|| r >= tileHeight
						|| tiles[c][r].wall || tiles[c][r].building != null) {
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
			buildings.add(placedBuilding);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					int c = x + i, r = y + j;
					tiles[c][r].building = placedBuilding;
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
		Tile[][] tiles = new Tile[width][height];
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, r.nextBoolean());
			}
		}
		return new Map(p, game, tiles);
	}

	public static Map noiseRandomLevel(Applet p, GameFrame game, int width,
			int height) {
		Tile[][] tiles = new Tile[width][height];
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
