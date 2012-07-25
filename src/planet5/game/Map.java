package planet5.game;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import planet5.Game;
import planet5.config.BuildingStats;
import planet5.config.EnemyStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Map {
	// copied constants
	public static final int BAR_HEIGHT = GameFrame.BAR_HEIGHT;
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	
	// constants
	final int FIELD_RADIUS = 8;
	final int FIELD_RADIUS_SQ = TILE_SIZE * TILE_SIZE * FIELD_RADIUS * FIELD_RADIUS;

	// reference variables
	public Applet p;
	public GameFrame game;

	// tile variables
	public Tile[][] tiles;
	public int[][] lighting;
	public boolean[][] field;
	public final int[] hourToLighting = { 0, 0, 0, 0, 32, 64, 96, 128, 160, 192, 224, 255, 
			255, 255, 255, 255, 255, 224, 192, 160, 128, 96, 64, 32 };
	public final double[] enemySpawnChances = { 0.0005, 0.0006, 0.0006, 0.0005, 0.0004, 0.0003, 0.0002, 0.0001, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0.0001, 0.0002, 0.0003, 0.0004 };
	public int[][] path;
	public int tileWidth, tileHeight;
	public int mapX = 0, mapY = 0;
	public int lose = -1, win = -1;

	// building and variables
	public ArrayList<Building> buildings = new ArrayList<Building>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public Hero hero;
	public Building base;

	// constructors
	public Map(Applet parent, GameFrame game, Tile[][] tiles) {
		p = parent;
		this.game = game;
		this.tiles = tiles;
		tileWidth = tiles[0].length;
		tileHeight = tiles.length;
		lighting = new int[tileHeight][tileWidth];
		field = new boolean[tileHeight][tileWidth];

		hero = new Hero(p, this, 0, 0);
		// TODO: better placement
		for (int i = 0; hero == null && i < tileHeight; i++) {
			for (int j = 0; hero == null && j < tileWidth; j++) {
				if (!tiles[i][j].wall) {
					hero = new Hero(p, this, j * TILE_SIZE, i * TILE_SIZE);
				}
			}
		}
		
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
	}
	public void calculatePathing() {
		path = new int[tileHeight][tileWidth];
		for (int i = 0; i < path.length; i++) {
			Arrays.fill(path[i], Integer.MAX_VALUE);
		}

		ArrayList<Point> stack = new ArrayList<Point>();
		
		for (int i = 0; i < BuildingStats.cols[0]; i++) {
			for (int j = 0; j < BuildingStats.rows[0]; j++) {
				path[base.row + j][base.col + i] = 0;
				stack.add(new Point(base.col + i, base.row + j));
			}
		}
		
		int dist = 1;
		ArrayList<Point> nextStack = new ArrayList<Point>();
		while (stack.size() != 0) {
			Point p = stack.remove(0);
			
			if (p.x != 0 && path[p.y][p.x - 1] > dist && !tiles[p.y][p.x - 1].wall) {
				path[p.y][p.x - 1] = dist;
				nextStack.add(new Point(p.x - 1, p.y));
			}
			if (p.y != 0 && path[p.y - 1][p.x] > dist && !tiles[p.y - 1][p.x].wall) {
				path[p.y - 1][p.x] = dist;
				nextStack.add(new Point(p.x, p.y - 1));
			}
			if (p.x != tileWidth - 1 && path[p.y][p.x + 1] > dist && !tiles[p.y][p.x + 1].wall) {
				path[p.y][p.x + 1] = dist;
				nextStack.add(new Point(p.x + 1, p.y));
			}
			if (p.y != tileHeight - 1 && path[p.y + 1][p.x] > dist && !tiles[p.y + 1][p.x].wall) {
				path[p.y + 1][p.x] = dist;
				nextStack.add(new Point(p.x, p.y + 1));
			}
			
			if (stack.size() == 0) {
				stack = nextStack;
				nextStack = new ArrayList<Point>();
				++dist;
			}
		}
	}

	// game ending methods
	public void gameLost() {
		lose = 0;
	}
	public void gameWon() {
		win = 0;
	}
	
	// game stopping methods
	public boolean gamePaused() {
		return game.paused || game.help || win != -1 || lose != -1;
	}
	
	// both update and draw are repeatedly called. update is called first
	public void update() {
		if (!gamePaused()) {
			calculateVariables();
			updateMap();
			updateHero();
			updateBuildings();
		}
		recalculateLighting();
		if (!gamePaused()) { 
			spawnEnemies();
			updateEnemies();
			checkGameEvents();
		}
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
		// update energy, kill enemies
		for (Building building : buildings) {
			if (building.powered) {
				if (game.hour >= 8 && game.hour < 20) {
					game.energy += Game.speed * BuildingStats.gen[building.type];
				}
				
				// reset variables
				building.target = null;
				
				int range = 0;
				// check for reload time, assign range
				if (game.energy < BuildingStats.draw[building.type]) {
					continue;
				}
				if (building.type == 5) {
					range = 32 * 4; // TODO: put constants elsewhere
				} else if (building.type == 6) {
					if (game.gameTime - building.lastFireTime < 25) {
						continue;
					}
					range = 32 * 8;
				} else {
					continue;
				}
				
				// find the closest enemy
				int min = Integer.MAX_VALUE;
				for (Enemy enemy : enemies) {
					int dist = dist(building, enemy);
					if (dist < range && dist < min) {//TODO: can change to only <min if min=range init
						min = dist;
						building.target = enemy;
					}
				}
				if (building.target == null) {
					continue;
				}
				
				// deal damage, remove dead enemies
				if (building.type == 5) {
					// TODO: constant
					building.target.hp -= 10;
				} else if (building.type == 6) {
					// aoe
				}
				
				// consume energy
				game.energy -= BuildingStats.draw[building.type];
				
				// set reload time
				building.lastFireTime = game.gameTime;
			}
		}
		
		// limit max energy
		if (game.energy > game.maxEnergy) {
			game.energy = game.maxEnergy;
		}
	}
	private int dist(Building turret, Enemy enemy) {
		int x1 = turret.col * TILE_SIZE + turret.width * TILE_SIZE / 2; 
		int y1 = turret.row * TILE_SIZE + turret.height * TILE_SIZE / 2;
		int x2 = enemy.bounds.x + enemy.ENEMY_SIZE / 2; 
		int y2 = enemy.bounds.y + enemy.ENEMY_SIZE / 2;
		return (int) Math.hypot(x1 - x2, y1 - y2);
	}
	private void spawnEnemies() {
		int maxEnemyCount = tileWidth * tileHeight / 900;
		double chance = Game.speed * enemySpawnChances[game.hour] * 0.01;
		
		for (int i = 0; i < tileHeight; i++) {
			for (int j = 0; j < tileWidth; j++) {
				if (enemies.size() >= maxEnemyCount) {
					return;
				}
				
				if (!tiles[i][j].wall && tiles[i][j].building == null &&
						lighting[i][j] < 128 && Math.random() < chance) {
					int type = (int) (3 * Math.random());
					Enemy enemy = new Enemy(j * TILE_SIZE, i * TILE_SIZE, type, this, game);
					enemies.add(enemy);
				}
			}
		}
	}
	private void updateEnemies() {
		for (Enemy enemy : enemies) {
			// find a target
			enemy.attacked = false;
			Rectangle inflated = new Rectangle(enemy.bounds);
			inflated.grow(1, 1);
			int damage = (int) EnemyStats.damage[enemy.type];
			
			// target base first
			if (inflated.intersects(base.col * TILE_SIZE, base.row * TILE_SIZE, base.width * TILE_SIZE, base.height * TILE_SIZE)) {
				enemy.attacked = true;
				base.hp -= damage;
				if (base.hp <= 0) {
					base.hp = 0;
					// TODO: explosion
				}
				// TODO: static screen
			}
			
			// target buildings next
			if (!enemy.attacked) {
				for (Building building : buildings) {
					if (inflated.intersects(building.col * TILE_SIZE, building.row * TILE_SIZE, building.width * TILE_SIZE, building.height * TILE_SIZE)) {
						enemy.attacked = true;
						building.hp -= damage;
						if (building.hp <= 0) {
							building.hp = 0;
							removeBuilding(building);
							// TODO: explosion
						}
						break;
					}
				}
			}
			
			// target hero otherwise
			if (!enemy.attacked) {
				if (inflated.intersects(hero.x, hero.y, hero.HERO_SIZE, hero.HERO_SIZE)) {
					enemy.attacked = true;
					hero.hp -= damage;
					if (hero.hp < 0) {
						hero.hp = 0;
					}
					// TODO: red screen
				}
			}
			
			if (!enemy.attacked) {
				enemy.move();
			}
		}
	}
	
	private void recalculateLighting() {
		// method takes ~30us to 0.1ms
		// TODO: walls block light
		
		// global lighting based on time of day
		int hour = (game.gameTime / 1500) % 24;
		for (int i = 0; i < tileHeight; i++) {
			Arrays.fill(lighting[i], hourToLighting[hour]);
		}

		// buildings produce light
		for (Building building : buildings) {
			if (BuildingStats.light[building.type] != 0 && building.powered) {
				produceLight(building.col, building.row, building.width,
						building.height, BuildingStats.light[building.type]);
			}
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
		
		int add = brightness - (int) (Math.hypot(x - col * TILE_SIZE, y - row * TILE_SIZE));
		if (add < 0) {
			add = 0;
		}
		
		lighting[row][col] += add;
		if (lighting[row][col] > 255) {
			lighting[row][col] = 255;
		}
	}

	public void checkGameEvents() {
		if (hero.hp <= 0 || base.hp <= 0) {
			gameLost();
		}
	}
	
	// drawing methods
	public void draw() {
		drawTiles();
		drawBuildings();
		hero.draw();
		drawEnemies();
		drawProjectiles();
		if (!game.help) {
			drawBuildingPlaceover();
			drawField();
		}
		drawLoseWin();
	}
	public void drawTiles() {
		// method takes ~2.1ms
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
				/*if (tiles[y][x].wall) {
					int mono = alpha * 128 / 255;
					p.fill(p.color(mono, mono, mono));
				} else {*/
					Color c = new Color(tiles[y][x].color);
					int r = c.getRed() * alpha / 255;
					int g = c.getGreen() * alpha / 255;
					int b = c.getBlue() * alpha / 255;
					p.fill(p.color(r, g, b));
				//}

				// draw the tile
				p.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				if (field[y][x]) {
					p.fill(0);
					//p.rect(x * TILE_SIZE, y * TILE_SIZE, 4, 4);
				}
				if (!tiles[y][x].wall) {
					p.fill(0);
				//	p.text(path[y][x], x * TILE_SIZE + 8, y * TILE_SIZE + 8);
				}
			}
		}

		p.translate(mapX, mapY);
	}
	public void drawBuildings() {
		// TODO: use this?
		Building hoveredBuilding = null;
		if (p.mouseX >= BAR_HEIGHT) {
			int x = (p.mouseX + mapX) / TILE_SIZE;
			int y = (p.mouseY + mapY - BAR_HEIGHT) / TILE_SIZE; // TODO: refactor, update every tick?
			hoveredBuilding = tiles[y][x].building;
		}
		
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
				if (building == game.selectedBuilding) {
					building.draw(p, x, y, p.millis());
				} else {
					building.draw(p, x, y, -1);
				}
			}
		}
	}
	public void drawEnemies() {
		for (Enemy enemy : enemies) {
			enemy.draw();
		}
	}
	public void drawProjectiles() {
		p.stroke(0xFFFF0000);
		p.strokeWeight(2);
		for (Building building : buildings) {
			int x = building.col * TILE_SIZE - mapX;
			int y = building.row * TILE_SIZE - mapY;
			int w = building.width * TILE_SIZE;
			int h = building.height * TILE_SIZE;

			if (x + w >= 0 && y + h >= 0 && x <= p.width
					&& y <= p.height - BAR_HEIGHT) {
				Enemy e = building.target;
				if (e != null) {
					
					
					if (e.hp < 0) {
						enemies.remove(e);
					}
				}
			}
		}
		p.noStroke();
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
			p.fill(0x80208000);
		} else {
			p.fill(0x80FF0000);
		}
		p.noStroke();
		p.rect(x * TILE_SIZE, y * TILE_SIZE, w * TILE_SIZE, h * TILE_SIZE);

		p.translate(mapX, mapY);
	}
	public void drawField() {
		// TODO: improve performance
		if (game.placingBuilding != -1) {
			p.translate(-mapX, -mapY);

			// calculate bounding x and y values
			int xMin = mapX / TILE_SIZE;
			int yMin = mapY / TILE_SIZE;
			int xMax = (p.width + mapX - 1) / TILE_SIZE + 1;
			int yMax = (p.height + mapY - BAR_HEIGHT - 1) / TILE_SIZE + 1;

			// draw the tiles
			p.fill(p.color(64, 128, 255, 64));
			for (int i = xMin; i < xMax; i++) {
				for (int j = yMin; j < yMax; j++) {
					if (field[j][i]) {
						p.rect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
					}
				}
			}

			p.translate(mapX, mapY);
		}
	}
	public void drawLoseWin() {
		p.textFont(Fonts.consolas32);
		p.textSize(64);
		p.textAlign(p.CENTER, p.CENTER);
		if (win != -1) {
			p.fill(0xFF4080FF);
			p.text("You Win", 0, 0, p.width, p.height);
		} else if (lose != -1) {
			p.fill(0xFFC00000);
			p.text("You Lose", 0, 0, p.width, p.height);
		}
	}
	
	// building related methods
	public boolean isPlacingBuilding() {
		return (game.placingBuilding != -1 && p.mouseY > BAR_HEIGHT);
	}
	public boolean canPlaceBuilding(int x, int y, int w, int h) {
		// TODO: only allow building on places with light or something
		// TODO: also energy fields u no
		int left = hero.x / TILE_SIZE;
		int up = hero.y / TILE_SIZE;
		int right = (hero.x + hero.HERO_SIZE - 1) / TILE_SIZE;
		int down = (hero.y + hero.HERO_SIZE - 1) / TILE_SIZE;

		// loop through the tiles this building covers
		boolean energized = false;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				// calculate column and row
				int c = x + i, r = y + j;

				// check for placing over walls and buildings
				if (c >= tileWidth || r >= tileHeight || tiles[r][c].wall
						|| tiles[r][c].building != null) {
					return false;
				}
				
				if (field[r][c]) {
					//energized = true;
				}

				// check for placing over hero
				if ((c == left || c == right) && (r == up || r == down)) {
					return false;
				}
			}
		}
		
		if (!energized) {
			//return false;
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
			addBuilding(placedBuilding);
		}
	}
	public void setBase(Building base) {
		// TODO: use these 3 methods and refactor?
		this.base = base;
		if (buildings.size() == 0) {
			buildings.add(base);
		} else {
			buildings.set(0, base);
		}

		for (int i = 0; i < base.height; i++) {
			for (int j = 0; j < base.width; j++) {
				tiles[base.row + i][base.col + j].building = base;
			}
		}
		recalculateField();
	}
	public void addBuilding(Building building) {
		buildings.add(building);
		for (int i = 0; i < building.height; i++) {
			for (int j = 0; j < building.width; j++) {
				tiles[building.row + i][building.col + j].building = building;
			}
		}
		recalculateField();
	}
	public void sellBuilding(Building building) {
		// TODO:
		removeBuilding(building);
	}
	public void removeBuilding(Building building) {
		buildings.remove(building);
		for (int i = 0; i < building.height; i++) {
			for (int j = 0; j < building.width; j++) {
				tiles[building.row + i][building.col + j].building = null;
			}
		}
		recalculateField();
	}
	public void recalculateField() {
		for (int i = 0; i < tileHeight; i++) {
			Arrays.fill(field[i], false);
		}
		
		// TODO: can optimize this but probably unnecessary
		calculatePoweredBuildings();

		for (Building building : buildings) {
			if (building.type <= 1 && building.powered) { // or <= 2
				int x = building.col * TILE_SIZE + (building.width - 1) * TILE_SIZE / 2; 
				int y = building.row * TILE_SIZE + (building.height - 1) * TILE_SIZE / 2;
				int left = Math.max(0, building.col - FIELD_RADIUS);
				int right = Math.min(tileWidth - 1, building.col + building.width + FIELD_RADIUS);
				int top = Math.max(0, building.row - FIELD_RADIUS);
				int bottom = Math.min(tileHeight - 1, building.row + building.height + FIELD_RADIUS);
				
				for (int i = left; i < right; i++) {
					for (int j = top; j < bottom; j++) {
						int dx = i * TILE_SIZE - x;
						int dy = j * TILE_SIZE - y;
						if (dx * dx + dy * dy < FIELD_RADIUS_SQ) {
							field[j][i] = true;
						}
					}
				}
			}
		}
	}
	public void calculatePoweredBuildings() {
		// clear everything
		for (Building building : buildings) {
			building.powered = false;
			building.powerSource = null;
		}
		
		// power all the buildings
		ArrayList<Building> stack = new ArrayList<Building>();
		ArrayList<Building> nextStack = new ArrayList<Building>();
		stack.add(base);
		base.powered = true;
		
		while (stack.size() != 0) {
			Building source = stack.remove(0);
			
			for (Building building : buildings) {
				if (!building.powered) {
					if (willPower(source, building)) {
						building.powered = true;
						if (building.type == 1) {
							nextStack.add(building);
						}
					}
				}
			}
			
			if (stack.size() == 0) {
				stack = nextStack;
				nextStack = new ArrayList<Building>();
			}
		}

		// calculate energy values
		int energy = game.energy;
		int maxEnergy = 0;
		for (Building building : buildings) {
			if (building.powered) {
				maxEnergy += BuildingStats.cap[building.type];
			}
		}
		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
		game.maxEnergy = maxEnergy;
		game.energy = energy;
		
		// calculate connections
		// TODO: power sources must lead to base AND if multiple, connect to closest
		// TODO: buildings connect to closest one
		for (Building building : buildings) {
			
		}
	}
	private boolean willPower(Building source, Building building) {
		int x1 = source.col * TILE_SIZE + source.width * TILE_SIZE / 2; 
		int y1 = source.row * TILE_SIZE + source.height * TILE_SIZE / 2;
		
		for (int i = 0; i < building.width; i++) {
			for (int j = 0; j < building.height; j++) {
				int x2 = (building.col + i) * TILE_SIZE + building.width * TILE_SIZE / 2; 
				int y2 = (building.row + j) * TILE_SIZE + building.height * TILE_SIZE / 2;
				
				int dx = x1 - x2;
				int dy = y1 - y2;
				
				if (dx * dx + dy * dy < FIELD_RADIUS_SQ) {
					return true;
				}
			}
		}
		
		return false;
	}
}
