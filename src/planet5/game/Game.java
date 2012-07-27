package planet5.game;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import planet5.config.BuildingStats;
import planet5.config.EnemyLevel;
import planet5.config.EnemyStats;
import planet5.config.Fonts;
import planet5.config.UpgradeStats;
import planet5.framework.Applet;
import planet5.game.gen.CaveGenerator;

public class Game {
	// graphical constants
	static final int TILE_SIZE = 32;
	static final int BAR_HEIGHT = 45;

	// time constants
	private static final int MILLIS_PER_DAY = 10 * 60 * 1000;
	private static final int MILLIS_PER_HOUR = MILLIS_PER_DAY / 24;
	private static final int MILLIS_PER_MINUTE = MILLIS_PER_HOUR / 60;
	private static final int GAME_START_TIME = 12 * MILLIS_PER_HOUR;

	public boolean paused = false, help = false;
	public int lastFrameRate = 10, lastFrameRateUpdate = 0;
	public int curEnergy = 0, maxEnergy = 1000;

	// game time
	public int gameMillis;
	private long lastUpdateTime;
	public int day, hour, minute;

	// building variables
	public int placingBuilding = -1;
	public Building selectedBuilding = null;

	// constants
	final int FIELD_RADIUS = 8;
	final int FIELD_RADIUS_SQ = TILE_SIZE * TILE_SIZE * FIELD_RADIUS
			* FIELD_RADIUS;

	// tile variables
	public Tile[][] tiles;
	public int[][] lighting;
	public boolean[][] field;
	public final int[] hourToLighting = { 0, 0, 0, 0, 32, 64, 96, 128, 160,
			192, 224, 255, 255, 255, 255, 255, 255, 224, 192, 160, 128, 96, 64,
			32 };
	public final double[] enemySpawnChances = { 0.0005, 0.0006, 0.0006, 0.0005,
			0.0004, 0.0003, 0.0002, 0.0001, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0.0001, 0.0002, 0.0003, 0.0004 };
	public int[][] path;
	public int tileWidth, tileHeight;
	public int mapX = 0, mapY = 0;
	public int lose = -1, win = -1;

	// building and variables
	public ArrayList<Building> buildings = new ArrayList<Building>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public Hero hero;
	public Building base;

	private GameButton helpButton, pauseButton, playAgainButton;
	private ConfirmButton quitButton;

	//
	private GameListener listener;

	// special variables
	private Applet p;

	// constructors
	public Game(GameListener listener, Applet p) {
		this.listener = listener;
		this.p = p;

		// add buttons
		int w = p.width;

		helpButton = new GameButton(this, p, new Rectangle(w - 63 - 64 - 64, 0,
				63, 23), "Help", Fonts.consolas16);
		pauseButton = new GameButton(this, p, new Rectangle(w - 63 - 64, 0, 63,
				23), "Pause", Fonts.consolas16);
		quitButton = new ConfirmButton(this, p,
				new Rectangle(w - 63, 0, 63, 23), "Quit");
		playAgainButton = new GameButton(this, p, new Rectangle((w - 300) / 2,
				425, 300, 49), "Play Again", Fonts.consolas32);
		playAgainButton.visible = false;

		restartGame();

		/*
		 * // TODO: better placement for (int i = 0; hero == null && i <
		 * tileHeight; i++) { for (int j = 0; hero == null && j < tileWidth;
		 * j++) { if (!tiles[i][j].wall) { hero = new Hero(p, this, j *
		 * TILE_SIZE, i * TILE_SIZE); } } }
		 * 
		 * for (int i = 0; buildings.size() == 0 && i < tileHeight; i++) { for
		 * (int j = 0; buildings.size() == 0 && j < tileWidth; j++) { boolean
		 * good = true; for (int k = 0; good && k < BuildingStats.cols[0]; k++)
		 * { for (int m = 0; good && m < BuildingStats.rows[0]; m++) { if
		 * (tiles[i + m][j + k].wall || (j + k == hero.x / TILE_SIZE && i + m ==
		 * hero.y / TILE_SIZE)) { good = false; } } } if (good) { base = new
		 * Building(0, j, i, gameMillis); buildings.add(base); for (int k = 0; k
		 * < BuildingStats.cols[0]; k++) { for (int m = 0; m <
		 * BuildingStats.rows[0]; m++) { tiles[i + m][j + k].building = base; }
		 * } } } }//
		 */
	}

	private void restartGame() {
		lastUpdateTime = System.currentTimeMillis();
		
		UpgradeStats.reset();
		EnemyLevel.reset();

		// TODO
		hero = new Hero(p, this, 0, 0);
		buildings.clear();
		enemies.clear();
		(new CaveGenerator()).gen(p, this, 200, 200);

		tileWidth = tiles[0].length;
		tileHeight = tiles.length;
		lighting = new int[tileHeight][tileWidth];
		field = new boolean[tileHeight][tileWidth];

		// calculate path array
		calculatePathing();
		recalculateField();
		gameMillis = GAME_START_TIME;

		// TODO:... refactor
		win = -1;
		lose = -1;
		pauseButton.enabled = true;
		playAgainButton.visible = false;

		pauseButton.text = "Pause";
		curEnergy = 0;
		paused = false;
		help = false;
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

			if (p.x != 0 && path[p.y][p.x - 1] > dist
					&& !tiles[p.y][p.x - 1].wall) {
				path[p.y][p.x - 1] = dist;
				nextStack.add(new Point(p.x - 1, p.y));
			}
			if (p.y != 0 && path[p.y - 1][p.x] > dist
					&& !tiles[p.y - 1][p.x].wall) {
				path[p.y - 1][p.x] = dist;
				nextStack.add(new Point(p.x, p.y - 1));
			}
			if (p.x != tileWidth - 1 && path[p.y][p.x + 1] > dist
					&& !tiles[p.y][p.x + 1].wall) {
				path[p.y][p.x + 1] = dist;
				nextStack.add(new Point(p.x + 1, p.y));
			}
			if (p.y != tileHeight - 1 && path[p.y + 1][p.x] > dist
					&& !tiles[p.y + 1][p.x].wall) {
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
	public void lose() {
		lose = 0;
		pauseButton.enabled = false;
		playAgainButton.visible = true;
	}

	public void win() {
		win = 0;
		pauseButton.enabled = false;
		playAgainButton.visible = true;
	}

	// game stopping methods
	public boolean paused() {
		return paused || help || win != -1 || lose != -1;
	}

	// both update and draw are repeatedly called. update is called first
	public void draw() {
		GameRenderer.draw(this);
		helpButton.draw(p.mouseX, p.mouseY, p.focused);
		pauseButton.draw(p.mouseX, p.mouseY, p.focused);
		quitButton.draw(p.mouseX, p.mouseY, p.focused);
		playAgainButton.draw(p.mouseX, p.mouseY, p.focused);
	}

	public void update() {
		// update buttons
		helpButton.update(p.focused);
		pauseButton.update(p.focused);
		quitButton.update(p.focused);
		playAgainButton.update(p.focused);

		// calculate elapsed time
		int elapsedMillis = (int) (System.currentTimeMillis() - lastUpdateTime);
		lastUpdateTime = System.currentTimeMillis();

		// recalculate game time
		if (!paused()) {
			gameMillis += elapsedMillis;
			day = gameMillis / MILLIS_PER_DAY;
			hour = (gameMillis % MILLIS_PER_DAY) / MILLIS_PER_HOUR;
			minute = (gameMillis % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
		}

		recalculateLighting(); // 1629374 => 862032
		if (!paused()) {
			calculateVariables(); // 3126 ... not even used
			updateMap(); // 8486 ... not even used
			updateHero(elapsedMillis); // 117468 if moving, 3573 if not
			updateBuildings(elapsedMillis); // 5360-12059 with a lot of
											// buildings
			updateProjectiles(elapsedMillis);
			spawnEnemies(elapsedMillis); // 9351930 => 2234
			updateEnemies(elapsedMillis); // 12836684 => 200099
			checkGameEvents(); // 1787
			EnemyLevel.add(elapsedMillis);
		}

		// remove building buy if not enough energy
		// TODO: change to yellow or something
		if (placingBuilding != -1
				&& curEnergy < BuildingStats.costs[placingBuilding]) {
			placingBuilding = -1;
		}
	}

	public void calculateVariables() {

	}

	private void updateMap() {
		// TODO
	}

	private void updateHero(int elapsedMillis) {
		hero.update(elapsedMillis);

		// update map position
		mapX = (int) hero.x - p.width / 2;
		mapY = (int) hero.y - (p.height - BAR_HEIGHT) / 2;

		// constrain map position
		mapX = p.constrain(mapX, 0, tileWidth * TILE_SIZE - p.width);
		mapY = p.constrain(mapY, 0, tileHeight * TILE_SIZE - p.height
				+ BAR_HEIGHT);
	}

	private void updateBuildings(int elapsedMillis) {
		// update energy, kill enemies
		boolean recalc = false;
		for (int i = 0; i < buildings.size(); i++) {
			Building building = buildings.get(i);
			if (building.powered) {
				if (building.update(elapsedMillis))
					recalc = true;
				
				if (building.buildTime != -1)
					continue;
				
				if (building == base) {
					curEnergy += elapsedMillis * BuildingStats.getGen(base.type);
				} else if (hour >= 8 && hour < 20) {
					curEnergy += elapsedMillis * BuildingStats.getGen(building.type);
				}
				
				// reset variables
				building.target = null;
				
				int range = 0;
				// check for reload time, assign range

				if (curEnergy < elapsedMillis * BuildingStats.getDraw(building.type)) {
					continue;
				}
				if (building.type == 5) {
					range = 32 * 4; // TODO: put constants elsewhere
				} else if (building.type == 6) {
					if (gameMillis - building.lastFireTime < 25) {
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
				
				if (building.target == null || building.target.isDead() || building.target.willBeDead())
					continue;
				
				// deal damage, remove dead enemies
				if (building.type == 5) {
					// TODO: constant
					building.target.takeDamage(10);
				} else if (building.type == 6) {
					Projectile pr = new Projectile(this, p, building.col * TILE_SIZE + TILE_SIZE, building.row * TILE_SIZE + TILE_SIZE);
					pr.target = building.target;
					pr.target.takeFutureDamage(Projectile.DAMAGE);
					projectiles.add(pr);
					//building.target = null;
				}
				
				// consume energy
				// TODO: lasers consume elapsedMillis*
				// TODO: mortars don't consume elapsedMillis*
				curEnergy -= elapsedMillis * BuildingStats.getDraw(building.type);
				
				// set reload time
				building.lastFireTime = gameMillis;
			}
		}
		
		if (recalc)
			recalculateField();
		
		// limit max energy
		if (curEnergy > maxEnergy)
			curEnergy = maxEnergy;
	}

	private void updateProjectiles(int elapsedMillis) {
		Iterator<Projectile> iterator = projectiles.iterator();
		while (iterator.hasNext()) {
			Projectile projectile = iterator.next();
			if (projectile.remove)
				iterator.remove();
			else
				projectile.update(elapsedMillis);
		}
	}

	private int dist(Building turret, Enemy enemy) {
		int x1 = turret.col * TILE_SIZE + turret.width * TILE_SIZE / 2;
		int y1 = turret.row * TILE_SIZE + turret.height * TILE_SIZE / 2;
		int x2 = (int) enemy.bounds.x + enemy.ENEMY_SIZE / 2;
		int y2 = (int) enemy.bounds.y + enemy.ENEMY_SIZE / 2;
		return (int) Math.hypot(x1 - x2, y1 - y2);
	}

	private void spawnEnemies(int elapsedMillis) {
		int maxEnemyCount = tileWidth * tileHeight / 900;
		maxEnemyCount = 100000;
		double chance = elapsedMillis * enemySpawnChances[hour] * 0.01;

		for (int i = 0; i < tileHeight; i++) {
			for (int j = 0; j < tileWidth; j++) {
				if (enemies.size() >= maxEnemyCount) {
					return;
				}

				if (!tiles[i][j].wall && tiles[i][j].building == null
						&& lighting[i][j] < 128 && Math.random() < chance) {
					int type = (int) (3 * Math.random());
					Enemy enemy = new Enemy(j * TILE_SIZE, i * TILE_SIZE, type,
							this, p);
					enemies.add(enemy);
				}
			}
		}
	}

	private void updateEnemies(int elapsedMillis) {
		for (Enemy enemy : enemies) {
			// find a target
			enemy.attacked = false;
			Rectangle inflated = new Rectangle((int) enemy.bounds.x,
					(int) enemy.bounds.y, enemy.ENEMY_SIZE, enemy.ENEMY_SIZE);
			inflated.grow(1, 1);
			int damage = (int) EnemyStats.getDamage(enemy.type);

			// target base first
			if (inflated.intersects(base.col * TILE_SIZE, base.row * TILE_SIZE,
					base.width * TILE_SIZE, base.height * TILE_SIZE)) {
				enemy.attacked = true;
				base.hp -= damage;
				if (base.hp <= 0) {
					base.hp = 0;
					// TODO: explosion
				}
				// TODO: static screen
			}

			// target buildings next
			// TODO: for the total thing: boolean removed = false;
			if (!enemy.attacked) {
				Iterator<Building> iterator = buildings.iterator();
				while (iterator.hasNext()) {
					Building building = iterator.next();
					if (inflated.intersects(building.col * TILE_SIZE,
							building.row * TILE_SIZE, building.width
									* TILE_SIZE, building.height * TILE_SIZE)) {
						enemy.attacked = true;
						building.hp -= damage;
						if (building.hp <= 0) {
							building.hp = 0;

							// o_o
							iterator.remove();
							for (int i = 0; i < building.height; i++) {
								for (int j = 0; j < building.width; j++) {
									tiles[building.row + i][building.col + j].building = null;
								}
							}
							recalculateField();
						}
						break;
					}
				}
			}

			// target hero otherwise
			if (!enemy.attacked) {
				if (inflated.intersects(hero.x, hero.y, hero.HERO_SIZE,
						hero.HERO_SIZE)) {
					enemy.attacked = true;
					hero.curHp -= damage;
					if (hero.curHp < 0) {
						hero.curHp = 0;
					}
					// TODO: red screen
				}
			}

			if (!enemy.attacked) {
				enemy.move(elapsedMillis);
			}
		}

		Iterator<Enemy> iterator = enemies.iterator();
		while (iterator.hasNext()) {
			Enemy enemy = iterator.next();
			if (enemy.isDead())
				iterator.remove();
		}
	}

	private void recalculateLighting() {
		// method takes ~30us to 0.1ms
		// TODO: walls block light?

		// global lighting based on time of day
		for (int i = 0; i < tileHeight; i++) {
			Arrays.fill(lighting[i], hourToLighting[hour]);
		}

		// buildings produce light
		for (Building building : buildings) {
			if (BuildingStats.light[building.type] != 0 && building.powered
					&& building.buildTime == -1) {
				produceLight(building.col, building.row, building.width,
						building.height, BuildingStats.light[building.type]);
			}
		}

		// hero produces light
		produceLight2(hero.x + hero.HERO_SIZE / 2, hero.y + hero.HERO_SIZE / 2,
				16 * 32);
	}

	private void produceLight(int col, int row, int width, int height,
			int brightness) {
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

		int add = brightness
				- (int) (Math.hypot(x - col * TILE_SIZE, y - row * TILE_SIZE));
		if (add < 0) {
			add = 0;
		}

		lighting[row][col] += add;
		if (lighting[row][col] > 255) {
			lighting[row][col] = 255;
		}
	}

	public void checkGameEvents() {
		if (hero.curHp <= 0 || base.hp <= 0) {
			lose();
		}
	}

	// building related methods
	public boolean isPlacingBuilding() {
		return (placingBuilding != -1 && p.mouseY > BAR_HEIGHT);
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
					// energized = true;
				}

				// check for placing over hero
				if ((c == left || c == right) && (r == up || r == down)) {
					return false;
				}
			}
		}

		if (!energized) {
			// return false;
		}

		// check for placing over enemies
		Rectangle building = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, w
				* TILE_SIZE, h * TILE_SIZE);
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
		int w = BuildingStats.cols[placingBuilding];
		int h = BuildingStats.rows[placingBuilding];
		int buildingType = placingBuilding;

		if (isPlacingBuilding() && canPlaceBuilding(x, y, w, h)) {
			Building placedBuilding = new Building(buildingType, x, y,
					gameMillis);
			curEnergy -= BuildingStats.costs[buildingType];
			addBuilding(placedBuilding);
		}
	}

	public void setBase(Building base) {
		// TODO: use these 3 methods and refactor?
		this.base = base;
		base.buildTime = -1;
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
		curEnergy += BuildingStats.costs[building.type];
		if (curEnergy > maxEnergy) {
			curEnergy = maxEnergy;
			// TODO: only do this once?
		}
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
			if (building.type <= 1 && building.powered
					&& building.buildTime == -1) { // or <= 2
				int x = building.col * TILE_SIZE + (building.width - 1)
						* TILE_SIZE / 2;
				int y = building.row * TILE_SIZE + (building.height - 1)
						* TILE_SIZE / 2;
				int left = Math.max(0, building.col - FIELD_RADIUS);
				int right = Math.min(tileWidth - 1, building.col
						+ building.width + FIELD_RADIUS);
				int top = Math.max(0, building.row - FIELD_RADIUS);
				int bottom = Math.min(tileHeight - 1, building.row
						+ building.height + FIELD_RADIUS);

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
		int energy = this.curEnergy;
		int maxEnergy = 0;
		for (Building building : buildings) {
			if (building.powered && building.buildTime == -1) {
				maxEnergy += BuildingStats.getCap(building.type);
			}
		}
		if (energy > maxEnergy) {
			energy = maxEnergy;
		}
		this.maxEnergy = maxEnergy;
		this.curEnergy = energy;

		// calculate connections
		// TODO: power sources must lead to base AND if multiple, connect to
		// closest
		// TODO: buildings connect to closest one
		for (Building building : buildings) {

		}
	}

	private boolean willPower(Building source, Building building) {
		int x1 = source.col * TILE_SIZE + source.width * TILE_SIZE / 2;
		int y1 = source.row * TILE_SIZE + source.height * TILE_SIZE / 2;

		for (int i = 0; i < building.width; i++) {
			for (int j = 0; j < building.height; j++) {
				int x2 = (building.col + i) * TILE_SIZE + building.width
						* TILE_SIZE / 2;
				int y2 = (building.row + j) * TILE_SIZE + building.height
						* TILE_SIZE / 2;

				int dx = x1 - x2;
				int dy = y1 - y2;

				if (dx * dx + dy * dy < FIELD_RADIUS_SQ) {
					return true;
				}
			}
		}

		return false;
	}

	// key event handlers
	public void keyPressed(int keyCode) {
		int intKey = keyCode - '0';

		hero.keyPressed();

		// update building placement
		if (intKey >= 1 && intKey <= BuildingStats.rows.length - 1) {
			if (placingBuilding == intKey) {
				// placingBuilding = -1;
			} else {
				placingBuilding = intKey;
			}
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			placingBuilding = -1;
			selectedBuilding = null;
		} else if (keyCode == KeyEvent.VK_SPACE) {
			selectedBuilding = null;
		} else if (keyCode == KeyEvent.VK_Q) {
			if (selectedBuilding != null) {
				sellBuilding(selectedBuilding);
			}
		}
	}

	public void keyReleased(int keyCode) {
		// TODO Auto-generated method stub

	}

	// mouse event handlers
	public void mousePressed(int x, int y, int mouseButton) {
		helpButton.mousePressed(x, y, mouseButton);
		pauseButton.mousePressed(x, y, mouseButton);
		quitButton.mousePressed(x, y, mouseButton);
		playAgainButton.mousePressed(x, y, mouseButton);

		for (int i = 0; i < 6; i++) {
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			if (mouseButton == MouseEvent.BUTTON1 && x >= boxX && y >= 1
					&& curEnergy >= BuildingStats.costs[i + 1]
					&& curEnergy >= BuildingStats.costs[i + 1]
					&& x <= boxX + BAR_HEIGHT - 2 && y <= BAR_HEIGHT) {
				if (placingBuilding == i + 1) {
					placingBuilding = -1;
				} else {
					placingBuilding = i + 1;
				}
			}
		}

		// do building related options
		if (x >= BAR_HEIGHT) {
			int col = (x + mapX) / TILE_SIZE;
			int row = (y + mapY - BAR_HEIGHT) / TILE_SIZE; // TODO: refactor,
															// update every
															// tick?
			Building building = tiles[row][col].building;

			// check if a building can be sold
			if (building != null && selectedBuilding == building) {
				sellBuilding(building);
			}

			// check if a building can be selected
			if (building != null && building != base) {
				selectedBuilding = building;
				placingBuilding = -1;
			} else {
				selectedBuilding = null;
			}
		}

		// TODO check if an enemy can be selected (or hover?)

		if (mouseButton == MouseEvent.BUTTON1 && placingBuilding != -1 && !help) {
			placeBuilding();
		}
	}

	public void mouseReleased(int x, int y, int mouseButton) {
		// TODO (+mousePressed)
		helpButton.mouseReleased(x, y, mouseButton);
		pauseButton.mouseReleased(x, y, mouseButton);
		quitButton.mouseReleased(x, y, mouseButton);
		playAgainButton.mouseReleased(x, y, mouseButton);
	}

	// button event handlers
	public void buttonClicked(String command) {
		if (command.equals("Quit")) {
			listener.quit();
		} else if (command.equals("Pause")) {
			pauseButton.text = "Resume";
			paused = true;
		} else if (command.equals("Resume")) {
			pauseButton.text = "Pause";
			paused = false;
		} else if (command.equals("Help")) {
			help = !help;
		} else if (command.equals("Play Again")) {
			restartGame();
		}
	}
}
