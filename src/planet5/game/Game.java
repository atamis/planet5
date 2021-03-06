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
import planet5.config.Globals;
import planet5.config.UpgradeStats;
import planet5.framework.Applet;
import planet5.game.gen.CaveGenerator;
import planet5.gfx.ParticleSystem;
import planet5.loaders.Fonts;
import planet5.loaders.SoundMaster;

public class Game {
	// graphical constants
	static final int TILE_SIZE = 32;
	public static final int BAR_HEIGHT = 45;

	// time constants
	// 10 mintues for 25 hours
	private static final int MILLIS_PER_MINUTE = 416;
	private static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	private static final int MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	private static final int GAME_START_TIME = 8 * MILLIS_PER_HOUR;
	//private static final int GAME_START_TIME = (39*2) * MILLIS_PER_HOUR/4;
	public static double gameSpeedMultiplier = 1;

	public boolean paused = false, help = false;
	public int lastFrameRate = 10, lastFrameRateUpdate = 0;
	public float curEnergy = 0, maxEnergy = 1000;

	// game time
	public int gameMillis;
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
	public byte[][] pathX, pathY;
	public int tileWidth, tileHeight;
	public int mapX = 0, mapY = 0;
	public int lose = -1, win = -1;

	// building and variables
	public ArrayList<Building> buildings = new ArrayList<Building>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public ArrayList<Enemy>[][] enemyArrayCenter, enemyArrayCorner;
	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public Hero hero;
	public Building base;

	private GameButton muteButton, playAgainButton, helpButton;
	private RadioButton[] speedButtons = new RadioButton[5];
	private RadioButton pauseButton;
	private ConfirmButton quitButton;
	public ParticleSystem ps;

	boolean noEnemies = true;
	int remainderMillis = 0;

	//
	private GameListener listener;

	// special variables
	private Applet p;
	private long lastUpdateTime;
	int tW, tH;

	// constructors
	public Game(GameListener listener, Applet p, int wi, int he) {
		tW=wi;
		tH=he;
		
		this.listener = listener;
		this.p = p;
		ps = new ParticleSystem();
		// add buttons
		int w = p.width;

		int speed = 16;
		for (int i = 0; i < speedButtons.length; i++) {
			String text;
			if (speed == 8)
				speed = 4;
			if (speed == 0)
				text = ".5x";
			else
				text = speed + "x";
			speedButtons[i] = new RadioButton(this, p, new Rectangle(w - 63 - 64 - 64-64 - 32*speedButtons.length + 32*i, 0, 31,
					23), text);
			speed /= 2;
		}

		pauseButton = new RadioButton(this, p, new Rectangle(w - 63 - 64 - 64-64, 0, 63,
				23), "Pause");
		helpButton = new GameButton(this, p, new Rectangle(w - 63 - 64-64, 0,
				63, 23), "Help", Fonts.consolas16);
		muteButton = new GameButton(this, p, new Rectangle(w - 63 - 64, 0,
				63, 23), "Mute", Fonts.consolas16);
		quitButton = new ConfirmButton(this, p,
				new Rectangle(w - 63, 0, 63, 23), "Quit");
		playAgainButton = new GameButton(this, p, new Rectangle((w - 300) / 2,
				425, 300, 49), "Play Again", Fonts.consolas32);
		playAgainButton.visible = false;

		//restartGame();
	}

	public void restartGame() {
		int width = tW, height = tH;

		// map
		tileWidth = width;
		tileHeight = height;

		// units
		buildings.clear();
		enemies.clear();
		projectiles.clear();
		enemyArrayCenter = new ArrayList[tileHeight][tileWidth];
		enemyArrayCorner = new ArrayList[tileHeight][tileWidth];
		for (int x = 0; x < tileWidth; x++)
			for (int y = 0; y < tileHeight; y++) {
				enemyArrayCenter[y][x] = new ArrayList<Enemy>();
				enemyArrayCorner[y][x] = new ArrayList<Enemy>();
			}
		
		lighting = new int[tileHeight][tileWidth];
		field = new boolean[tileHeight][tileWidth];
		(new CaveGenerator()).gen(p, this, width, height);
		
		calculatePathing();
		recalculateField();
		
		// upgrades
		UpgradeStats.reset();
		EnemyLevel.reset();

		// game events
		win = -1;
		lose = -1;
		playAgainButton.visible = false;
		
		// bar
		pauseButton.text = "Pause";
		curEnergy = 0;
		paused = false;
		help = false;
		
		unselectButtons();
		gameSpeedMultiplier = 1;
		speedButtons[3].selected = true;
		recalculateField();
		
		if (Globals.MUTE) {
			muteButton.text="Unmute";
		} else {
			muteButton.text="Mute";
		}
		
		// game time
		placingBuilding = -1;
		noEnemies = true;
		gameMillis = GAME_START_TIME;
		remainderMillis = 0;
		lastUpdateTime = System.currentTimeMillis();
		ps.particles.clear();
	}
	
	public void calculatePathing() {
		
		aStar();
		
		pathX = new byte[tileHeight][tileWidth];
		pathY = new byte[tileHeight][tileWidth];
		//computedX = new byte[32*tileHeight][32*tileWidth];
		//computedY = new byte[32*tileHeight][32*tileWidth];
		
		// calculate path for every pixel that the enemy can go through
		// enemies can only start at where they spawn, in the center of a tile
		for (int col = 0; col < tileWidth; col++) {
			for (int row = 0; row < tileHeight; row++) {
				// only simulate spawned enemies at valid tiles
				if (path[row][col] != Integer.MAX_VALUE && path[row][col] != 0) {
					// find the next tile to move to
					byte xOffset = 0;
					byte yOffset = 0;
					
					// try moving up, left, right, and down
					if (row != 0 && path[row-1][col] < path[row][col])
						yOffset = -1;
					if (col != 0 && path[row][col-1] < path[row][col])
						xOffset = -1;
					if (col != tileHeight-1 && path[row][col+1] < path[row][col])
						xOffset = 1;
					if (row != tileWidth-1 && path[row+1][col] < path[row][col])
						yOffset = 1;

					// if moving diagonally doesn't help, just move horizontally
					if (path[row + yOffset][col + xOffset] >= path[row][col])
						yOffset = 0;
					
					// assign the value
					pathX[row][col] = xOffset;
					pathY[row][col] = yOffset;
					
					/*
					
					// assign values in computed array to go to the next one
					// note: it's offset by -8
					// something's wrong with the offset b/c its four 8x8s
					int r = 32*row + 8;
					int c = 32*col + 8;
					for (int i = 0; i < 32; i++) {
						if (r < 0 || c < 0)
							break;
						
						computedX[r][c] = xOffset;
						computedY[r][c] = yOffset;
						r += yOffset;
						c += xOffset;
					}
					
					// if the next one isn't set yet then set it
					if (r >= 0 && c >= 0) {
						if (computedX[r][c] == 0 && computedY[r][c] == 0) {
							computedX[r][c] = xOffset;
							computedY[r][c] = yOffset;
						}
					}
					
					//*/
				}
			}
		}
	}

	private void aStar() {
		path = new int[tileHeight][tileWidth];
		
		// clear path with max value integers
		for (int i = 0; i < path.length; i++)
			Arrays.fill(path[i], Integer.MAX_VALUE);

		ArrayList<Point> stack = new ArrayList<Point>();

		// add points for the outline of the base
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				path[base.row + j][base.col + i] = 0;
				if (i == 1 && j == 1)
					continue;
				stack.add(new Point(base.col + i, base.row + j));
			}
		}

		// perform astar on the stack
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
		lose = 120;
	}

	public void win() {
		win = 120;
	}

	// game stopping methods
	public boolean paused() {
		return paused || help || win != -1 || lose != -1;
	}

	// both update and draw are repeatedly called. update is called first
	public void draw() {
		GameRenderer.draw(this);
		helpButton.draw(p.mouseX, p.mouseY, p.focused);
		muteButton.draw(p.mouseX, p.mouseY, p.focused);
		pauseButton.draw(p.mouseX, p.mouseY, p.focused);
		quitButton.draw(p.mouseX, p.mouseY, p.focused);
		playAgainButton.draw(p.mouseX, p.mouseY, p.focused);
		for (int i = 0; i < speedButtons.length; i++)
			speedButtons[i].draw(p.mouseX, p.mouseY, p.focused);
	}

	int i2=0;
	public void update() {
		if (!p.focused)
			rightButtonPressed = false;
		
		// update buttons
		helpButton.update(p.focused);
		muteButton.update(p.focused);
		pauseButton.update(p.focused);
		quitButton.update(p.focused);
		playAgainButton.update(p.focused);
		for (int i = 0; i < speedButtons.length; i++)
			speedButtons[i].update(p.focused);

		// calculate elapsed time
		int elapsedMillis = (int) (System.currentTimeMillis() - lastUpdateTime);
		lastUpdateTime = System.currentTimeMillis();
		elapsedMillis *= gameSpeedMultiplier;

		// recalculate game time
		if (!paused()) {
			gameMillis += elapsedMillis;
			day = gameMillis / MILLIS_PER_DAY;
			hour = (gameMillis % MILLIS_PER_DAY) / MILLIS_PER_HOUR;
			minute = (gameMillis % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
		}

		if (win!=-1 || lose!=-1) {
			i2+=elapsedMillis/gameSpeedMultiplier;
			if(i2<0)
				i2=0;
			while(i2>=0){
				i2-=16;
				if (win > 0)
					--win;
				if (lose > 0)
					--lose;
				if (win == 0 || lose == 0)
					playAgainButton.visible = true;
			}
		}

		recalculateLighting();
		if (!paused()) {
			int TICK_MILLIS = 16;
			if (gameSpeedMultiplier == 0.5)
				TICK_MILLIS = 5;
			
			remainderMillis += elapsedMillis;
			
			// spawn enemies only once because it takes a while
			int times = (remainderMillis + TICK_MILLIS) / TICK_MILLIS;
			spawnEnemies(times * TICK_MILLIS);
			
			while (remainderMillis >= 0) {
				remainderMillis -= TICK_MILLIS;
				ps.update(TICK_MILLIS);
				updateHero(TICK_MILLIS);
				updateBuildings(TICK_MILLIS);
				updateProjectiles(TICK_MILLIS);
				updateEnemies(TICK_MILLIS); // MAKE THIS FASTER.
				EnemyLevel.add(TICK_MILLIS);
			}
			
			// if there are monsters on the map set game to 1x
			if (enemies.size() != 0 && noEnemies) {
				//for (int i = 0; i < 4; i++)
				//	speedButtons[i].enabled = false;
				unselectButtons();
				gameSpeedMultiplier = 1;
				speedButtons[3].selected = true;
				noEnemies = false;
			} else if (enemies.size() == 0) {
				noEnemies = true;
				//for (int i = 0; i < 4; i++)
				//	speedButtons[i].enabled = true;
			}
			
			checkGameEvents();
			recalculateEnergyValues();
		}

		// remove building buy if not enough energy
		if (placingBuilding != -1
				&& curEnergy < BuildingStats.costs[placingBuilding]) {
			placingBuilding = -1;
		}
	}

	private void updateHero(int elapsedMillis) {
		hero.update(elapsedMillis);

		// update map position
		mapX = hero.x - p.width / 2;
		mapY = hero.y - (p.height - BAR_HEIGHT) / 2;

		// constrain map position
		mapX = p.constrain(mapX, 0, tileWidth * TILE_SIZE - p.width);
		mapY = p.constrain(mapY, 0, tileHeight * TILE_SIZE - p.height
				+ BAR_HEIGHT);
	}

	// updating buildings
	private void updateBuildings(int elapsedMillis) {
		// update buildings and energy values
		boolean recalc = false;
		for (int i = 0; i < buildings.size(); i++) {
			Building building = buildings.get(i);
			if (building.powered) {
				if (building.update(elapsedMillis))
					recalc = true;
				
				if (building.buildTime != -1)
					continue;
				
				if (building == base)
					curEnergy += elapsedMillis * BuildingStats.getGen(base.type);
				else if (hour >= 8 && hour < 20)
					curEnergy += elapsedMillis * BuildingStats.getGen(building.type);
			}
		}
		
		// use energy to kill enemies
		for (int i = 0; i < buildings.size(); i++) {
			Building building = buildings.get(i);
			if (building.powered && building.buildTime == -1) {
				if (building.type != 5 && building.type != 6)
					continue;
				
				// reset variables
				building.target = null;
				
				// check for reload time
				if (curEnergy < elapsedMillis * BuildingStats.getDraw(building.type)) {
					continue;
				}
				
				// assign range
				int range = 0;
				if (building.type == 5) {
					range = 32 * 4;
				} else if (building.type == 6) {
					if (gameMillis - building.lastFireTime < BuildingStats.mortar_reload_time) {
						continue;
					}
					range = 32 * 8;
				} else {
					continue;
				}

				// find the closest enemy
				building.target = building.findClosestEnemy(this, range / 32);

				if (building.target == null || building.target.isDead()
						|| building.target.willBeDead())
					continue;

				// deal damage, remove dead enemies, consume energy
				if (building.type == 5) {
					building.target
							.takeDamage((int) (elapsedMillis * BuildingStats
									.getDamage(5)));
					SoundMaster.laser_fire.trigger();
					curEnergy -= elapsedMillis
							* BuildingStats.getDraw(building.type);
					if (building.target.isDead()) {
						//ps.bloodBang(building.target.bounds.x, building.target.bounds.y);
					}
				} else if (building.type == 6) {
					Projectile pr = new Projectile(this, p, building.col
							* TILE_SIZE + TILE_SIZE, building.row * TILE_SIZE
							+ TILE_SIZE);
					pr.target = building.target;
					pr.target
							.takeFutureDamage((int) BuildingStats.getDamage(6));
					projectiles.add(pr);
					ps.mortarExhaust(building.col * TILE_SIZE
							+ (BuildingStats.cols[6] * TILE_SIZE) / 2, building.row
							* TILE_SIZE + (BuildingStats.rows[6] * TILE_SIZE) / 2);
					curEnergy -= BuildingStats.getDraw(building.type);
					building.lastFireTime = gameMillis;
					// building.target = null;
				}

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

	private void spawnEnemies(int elapsedMillis) {
		int maxEnemyCount = tileWidth * tileHeight / 900;
		maxEnemyCount = (int) (EnemyStats.getSpawn() * 100);
		// maxEnemyCount = 20;
		// double chance = elapsedMillis * enemySpawnChances[hour] * 0.01;

		int trials = (int) (elapsedMillis * EnemyStats.getSpawn() * 2);
		//int trials = (int) (elapsedMillis * EnemyStats.getSpawn() / 4);
		// int trials = 10;
		//maxEnemyCount=10000;trials=10000-enemies.size();
		for (int i = 0; i < trials && enemies.size() < maxEnemyCount; i++) {
			if (p.random(100) < EnemyStats.getSpawn()) {
				int x = (int) (tileWidth * Math.random());
				int y = (int) (tileHeight * Math.random());
				if (!tiles[y][x].wall && tiles[y][x].building == null
						&& lighting[y][x] <= 128) {
					int type = (int) (3 * Math.random());
					Enemy enemy = new Enemy(x * TILE_SIZE, y * TILE_SIZE, type,
							this, p);
					enemies.add(enemy);
					enemyArrayCenter[enemy.center.y / TILE_SIZE][enemy.center.x
							/ TILE_SIZE].add(enemy);
					enemyArrayCorner[enemy.bounds.y / TILE_SIZE][enemy.bounds.x
							/ TILE_SIZE].add(enemy);
				}
			}
		}
		
		if (day != 0 && hour == 0 && enemies.size() == 0)
			win();
	}

	private void updateEnemies(int elapsedMillis) {
		long l=System.nanoTime();

		// keep base and hero dimensions calculated
		Rectangle baseBounds = new Rectangle(base.col * TILE_SIZE, base.row * TILE_SIZE,
					base.width * TILE_SIZE, base.height * TILE_SIZE);
		baseBounds.grow(1, 1);
		Rectangle heroBounds = new Rectangle(hero.x, hero.y, hero.SIZE, hero.SIZE);
		heroBounds.grow(1, 1);
		
		// loop through all enemies
		Iterator<Enemy> enemyIterator = enemies.iterator();
		while (enemyIterator.hasNext()) {
			Enemy enemy = enemyIterator.next();

			// remove dead enemies
			if (enemy.isDead()) {
				enemyIterator.remove();
				ps.bloodBang(enemy.bounds.x+8, enemy.bounds.y+8);
				enemyArrayCenter[enemy.center.y / TILE_SIZE][enemy.center.x / TILE_SIZE].remove(enemy);
				enemyArrayCorner[enemy.bounds.y / TILE_SIZE][enemy.bounds.x / TILE_SIZE].remove(enemy);
				//PVector loc = enemy.screenLoc();
				//ps.bloodBang(enemy.bounds.x, enemy.bounds.y);
				continue;
			}
			
			// find a target
			enemy.attacked = false;
			
			int damage = EnemyStats.getDamage(enemy.type);

			// target base first
			if (enemy.bounds.intersects(baseBounds)) {
				enemy.attacked = true;
				base.hp -= damage;
				if (base.hp <= 0) {
					base.hp = 0;
					ps.explosion(
							base.col
									* TILE_SIZE
									+ (BuildingStats.cols[base.type] * TILE_SIZE)
									/ 2,
							base.row
									* TILE_SIZE
									+ (BuildingStats.rows[base.type] * TILE_SIZE)
									/ 2);
				}
				// TODO: static screen
			}

			// target buildings next
			if (!enemy.attacked) {
				int top = enemy.bounds.y / TILE_SIZE;
				int left = enemy.bounds.x / TILE_SIZE;
				int bottom = (enemy.bounds.y + enemy.ENEMY_SIZE) / TILE_SIZE;
				int right = (enemy.bounds.x + enemy.ENEMY_SIZE) / TILE_SIZE;
				Building target = null;
				
				if (tiles[top][left].building != null)
					target = tiles[top][left].building;
				else if (tiles[bottom][left].building != null)
					target = tiles[bottom][left].building;
				else if (right != left) {
					if (tiles[top][right].building != null)
						target = tiles[top][right].building;
					else if (tiles[bottom][right].building != null)
						target = tiles[bottom][right].building;
				}
				
				if (target != null) {
					enemy.attacked = true;
					target.hp -= damage;
					if (target.hp <= 0) {
						ps.explosion(target.col * TILE_SIZE + (BuildingStats.cols[target.type] * TILE_SIZE)/2, target.row * TILE_SIZE + (BuildingStats.rows[target.type] * TILE_SIZE)/2);
					}
				}
			}

			// target hero otherwise
			if (!enemy.attacked) {
				if (enemy.bounds.intersects(heroBounds)) {
					enemy.attacked = true;
					hero.curHp -= damage;
					if (hero.curHp < 0) {
						hero.curHp = 0;
					}
					// TODO: red screen
				}
			}

			if (!enemy.attacked)
				enemy.move(elapsedMillis);
		}
		
		// remove destroyed buildings
		boolean recalculate = false;
		Iterator<Building> buildingIterator = buildings.iterator();
		while (buildingIterator.hasNext()) {
			Building building = buildingIterator.next();
			if (building.hp <= 0) { // explosion?
				building.hp = 0;
				buildingIterator.remove();
				for (int i = 0; i < building.height; i++)
					for (int j = 0; j < building.width; j++) {
						tiles[building.row + i][building.col + j].building = null;
					}
				recalculate = true;
			}
		}
		
		if (recalculate)
			recalculateField();
		
		if(enemies.size()==0)return;
		long t=(System.nanoTime()-l);
		if (Globals.DEBUG&&p.frameCount%20==0)
		p.println(" tot=" + t + " count=" + enemies.size() + " avg=" + (t/enemies.size()));
		// run1: avg=30-60k
		// final run: avg=
	}

	private void recalculateLighting() {
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
		produceLight2(hero.x + hero.SIZE / 2, hero.y + hero.SIZE / 2,
				256);
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

	public boolean isPlaying() {
		return (!help && win == -1 && lose == -1);
	}
	
	// building related methods
	public boolean isPlacingBuilding() {
		return (placingBuilding != -1 && p.mouseY > BAR_HEIGHT);
	}

	public boolean canPlaceBuilding(int x, int y, int w, int h) {
		int left = hero.x / TILE_SIZE;
		int up = hero.y / TILE_SIZE;
		int right = (hero.x + hero.SIZE - 1) / TILE_SIZE;
		int down = (hero.y + hero.SIZE - 1) / TILE_SIZE;

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

		int loopTop = Math.max(0, y - 1);
		int loopLeft = Math.max(0, x - 1);
		int loopRight = Math.min(x + w, tileWidth - 1);
		int loopBottom = Math.min(y + h, tileHeight - 1);
		for (int i = loopLeft; i <= loopRight; i++)
			for (int j = loopTop; j <= loopBottom; j++)
				for (Enemy enemy : enemyArrayCorner[j][i])
					if (building.intersects(enemy.bounds))
						return false;

		return true;
	}

	public boolean placeBuilding() {
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
			return true;
		}
		
		return false;
	}

	public void setBase(Building base) {
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
		curEnergy += BuildingStats.costs[building.type] / 2;
		if (curEnergy > maxEnergy) {
			curEnergy = maxEnergy;
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
		// 
		recalculateField();
	}

	public void recalculateField() {
		for (int i = 0; i < tileHeight; i++)
			Arrays.fill(field[i], false);

		calculatePoweredBuildings();

		for (Building building : buildings) {
			if (building.type <= 1 && building.powered
					&& building.buildTime == -1) { // or <= 2
				int x = building.col * TILE_SIZE + building.width
						* TILE_SIZE / 2;
				int y = building.row * TILE_SIZE + building.height
						* TILE_SIZE / 2;
				int left = Math.max(0, building.col - FIELD_RADIUS);
				int right = Math.min(tileWidth - 1, building.col
						+ building.width + FIELD_RADIUS);
				int top = Math.max(0, building.row - FIELD_RADIUS);
				int bottom = Math.min(tileHeight - 1, building.row
						+ building.height + FIELD_RADIUS);

				for (int i = left; i <= right; i++) {
					for (int j = top; j <= bottom; j++) {
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

	public void recalculateEnergyValues() {
		// calculate energy values
		float energy = this.curEnergy;
		float maxEnergy = 0;
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
	}
	public void calculatePoweredBuildings() {
		// clear everything
		for (Building building : buildings) {
			building.powered = false;
			building.powerSource = null;
			building.reach = 32;
		}

		// power all the buildings
		ArrayList<Building> stack = new ArrayList<Building>();
		ArrayList<Building> nextStack = new ArrayList<Building>();
		stack.add(base);
		base.powered = true;

		while (stack.size() != 0) {
			Building source = stack.remove(0);

			for (Building building : buildings) {
				// calculate whether a building will be powered
				if (!building.powered) {
					if (willPower(source, building)) {
						building.powered = true;
						if (building.type == 1 && building.buildTime == -1) {
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
		
		// calculate distances between the buildings
		int size = buildings.size();
		int[][] distances = new int[size][size];
		for (int i = 0; i < size; i++) {
			Building source = buildings.get(i);
			if (source.type == 0 || source.type == 1) {
				for (int j = 0; j < size; j++) {
					Building b = buildings.get(j);
					if (willPower(source, b)) {
						distances[i][j] = (int) buildingDistSq(source, b);
					}
				}
			}
		}
		
		// calculate connections
		while (true) {
			int min = Integer.MAX_VALUE;
			Building client = null, source = null;
			
			// find the closest connection possible
			// source must have a powerSource OR be the base
			// client must not have a powerSource AND must be within range AND must not be building AND must be powered
			for (int i = 0; i < size; i++) {
				Building s = buildings.get(i);
				if (s.powerSource == null && s.type != 0)
					continue;
				
				for (int j = 0; j < size; j++) {
					Building c = buildings.get(j);
					if (c.powerSource != null || distances[i][j] == 0 || c.buildTime != -1 || !c.powered)
						continue;
					
					if (distances[i][j] < min) {
						min = distances[i][j];
						source = s;
						client = c;
					}
				}
			}
			
			// apply the connection
			if (min == Integer.MAX_VALUE)
				break;
			
			client.powerSource = source;
		}
	}
	
	private double buildingDistSq(Building b1, Building b2) {
		int x1 = 32*b1.col + 16*b1.width - mapX;
		int y1 = 32*b1.row + 16*b1.height - mapY;
		int x2 = 32*b2.col + 16*b2.width - mapX;
		int y2 = 32*b2.row + 16*b2.height - mapY;
		
		int dx = x1 - x2;
		int dy = y1 - y2;
		
		return dx*dx + dy*dy;
	}

	private boolean willPower(Building source, Building building) {
		int x1 = source.col * TILE_SIZE + source.width * TILE_SIZE / 2;
		int y1 = source.row * TILE_SIZE + source.height * TILE_SIZE / 2;

		for (int i = 0; i < building.width; i++) {
			for (int j = 0; j < building.height; j++) {
				int x2 = (building.col + i) * TILE_SIZE;
				int y2 = (building.row + j) * TILE_SIZE;

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
		if (win!=-1||lose!=-1)
			return;
		int intKey = keyCode - '0';

		hero.keyPressed();

		// update building placement
		if (intKey >= 1 && intKey <= BuildingStats.rows.length - 1) {
			if (selectedBuilding != null && selectedBuilding.type == 4) {
				if (intKey == 6) {
				} else {
					selectedBuilding.current_upgrade = intKey - 1;
				}
			} else if (placingBuilding == intKey) {
				// placingBuilding = -1;
			} else {
				placingBuilding = intKey;
			}
			selectedBuilding = null;
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			placingBuilding = -1;
			selectedBuilding = null;
		} else if (keyCode == KeyEvent.VK_SPACE) {
			placingBuilding = -1;
			selectedBuilding = null;
		} else if (keyCode == KeyEvent.VK_Q) {
			if (selectedBuilding != null) {
				sellBuilding(selectedBuilding);
				selectedBuilding = null;
			}
		}
	}

	public void keyReleased(int keyCode) {}

	// mouse event handlers
	public boolean rightButtonPressed = false;
	public void mousePressed(int x, int y, int mouseButton) {
		helpButton.mousePressed(x, y, mouseButton);
		muteButton.mousePressed(x, y, mouseButton);
		pauseButton.mousePressed(x, y, mouseButton);
		quitButton.mousePressed(x, y, mouseButton);
		playAgainButton.mousePressed(x, y, mouseButton);
		for (int i = 0; i < speedButtons.length; i++)
			speedButtons[i].mousePressed(x, y, mouseButton);
		
		if (mouseButton == MouseEvent.BUTTON3) {
			rightButtonPressed = true;
		}

		if (win!=-1||lose!=-1)
			return;

		if (mouseButton != MouseEvent.BUTTON1)
			return;

		// building bar select
		for (int i = 0; i < 6; i++) {
			int boxX = i * (BAR_HEIGHT - 1) + 1;
			if (mouseButton == MouseEvent.BUTTON1 && x >= boxX && y >= 1
					&& x <= boxX + BAR_HEIGHT - 2 && y <= BAR_HEIGHT) {
				if (selectedBuilding != null && selectedBuilding.type == 4) {
					if (i == 5) {
						selectedBuilding = null;
					} else {
						selectedBuilding.current_upgrade = i;
						selectedBuilding = null;
					}
				}
				else if (placingBuilding == i + 1
						&& curEnergy >= BuildingStats.costs[i + 1]
								&& curEnergy >= BuildingStats.costs[i + 1]) {
					placingBuilding = -1;
				} else {
					placingBuilding = i + 1;
				}
			}
		}

		// place THEN do building-related options
		if (mouseButton == MouseEvent.BUTTON1 && placingBuilding != -1 && !help && placeBuilding()) {
			
		} else if (y >= BAR_HEIGHT) {
			int col = (x + mapX) / TILE_SIZE;
			int row = (y + mapY - BAR_HEIGHT) / TILE_SIZE;
			Building building = tiles[row][col].building;

			// if there's a building, sell or select; otherwise do nothing
			if (building != null && selectedBuilding == building) {
				sellBuilding(building);
				selectedBuilding = null;
			} else if (building != null && building != base) {
				selectedBuilding = building;
				placingBuilding = -1;
			} else {
				selectedBuilding = null;
			}
		}
	}

	public void mouseReleased(int x, int y, int mouseButton) {
		if (mouseButton == MouseEvent.BUTTON3) {
			rightButtonPressed = false;
		}
		
		helpButton.mouseReleased(x, y, mouseButton);
		muteButton.mouseReleased(x, y, mouseButton);
		pauseButton.mouseReleased(x, y, mouseButton);
		quitButton.mouseReleased(x, y, mouseButton);
		playAgainButton.mouseReleased(x, y, mouseButton);
		for (int i = 0; i < speedButtons.length; i++)
			speedButtons[i].mouseReleased(x, y, mouseButton);
	}

	private void cleanup() {
		//pathX = null;
		//pathY = null;
		//computedX=null;
		//computedY=null;
	}
	
	// button event handlers
	public void buttonClicked(String command) {
		if (command.equals("Quit")) {
			cleanup();
			listener.quit();
		} else if (command.equals("Mute")) {
			muteButton.text="Unmute";
			SoundMaster.mute();
			Globals.MUTE = true;
		} else if (command.equals("Unmute")) {
			muteButton.text="Mute";
			Globals.MUTE = false;
			SoundMaster.unmute();
		} else if (command.equals("Help")) {
			if (win <= 0 && lose <= 0) {
				help = !help;
				if (help)
					playAgainButton.enabled = false;
				else if (win == 0 || lose == 0)
					playAgainButton.enabled = true;
			}
			
		} else if (command.equals("Play Again")) {
			restartGame();
		} else if (command.equals("Pause")) {
			unselectButtons();
			pauseButton.selected = true;
			paused = true;
			//gameSpeedMultiplier = 0;
		} else if (command.equals("1x")) {
			unselectButtons();
			gameSpeedMultiplier = 1;
		} else if (command.equals("2x")) {
			unselectButtons();
			gameSpeedMultiplier = 2;
		} else if (command.equals("4x")) {
			unselectButtons();
			gameSpeedMultiplier = 4;
		} else if (command.equals(".5x")) {
			unselectButtons();
			gameSpeedMultiplier = 0.5;
		} else if (command.equals("16x")) {
			unselectButtons();
			gameSpeedMultiplier = 16;
		}
	}
	
	private void unselectButtons() {
		paused = false;
		pauseButton.selected = false;
		for (int i = 0; i < speedButtons.length; i++)
			speedButtons[i].selected = false;
	}

	public float getTotalGen() {
		float x = 0;
		
		for(Building b : buildings) {
			x += BuildingStats.getGen(b.type);
		}

		return x;
	}
}
