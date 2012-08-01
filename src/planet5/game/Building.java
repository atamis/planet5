package planet5.game;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;

import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import planet5.config.SpriteMaster;
import planet5.config.UpgradeStats;
import planet5.config.Upgrades;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Building {
	public static final int TILE_SIZE = Globals.TILE_SIZE;

	// public static final int

	public int type;
	public int row;

	public int col;
	int width, height;

	float hp;
	float maxHp;
	int lastFireTime = 0;
	float rad;

	public int buildTime, buildHealth;
	Enemy target = null;
	boolean powered = false;
	Building powerSource = null;
	int current_upgrade = Upgrades.gen;
	
	// enemy search variables
	private int searchMin;
	private Enemy searchResult;
	
	public Building(int type, int x, int y, int gameTime) {
		rad = 0;
		this.type = type;
		this.row = y;
		this.col = x;
		this.buildTime = 0;
		this.buildHealth = 0;
		this.width = BuildingStats.cols[type];
		this.height = BuildingStats.rows[type];
		maxHp = BuildingStats.getHealth(type);
		hp = maxHp;
	}
	
	public boolean update(int elapsedMillis) {
		if (buildTime != -1) {
			if (buildTime < 5000) {
				buildTime += elapsedMillis;
				buildHealth += elapsedMillis;
			}
			if (buildTime >= 5000) {
				buildTime = -1;
				return true;
			}
		}
		
		if(type == 4 && buildTime == -1) {
			UpgradeStats.level[current_upgrade] += elapsedMillis;
		}

		float oldMaxHp = maxHp;
		maxHp = BuildingStats.getHealth(type);
		if (maxHp > oldMaxHp)
			hp += maxHp - oldMaxHp;
		

		
		return false;
	}

	public void calcAngle(PApplet p, int x, int y) {
		if (target != null) {
			int centerx = x + (BuildingStats.cols[type] * TILE_SIZE) / 2;
			int centery = y + (BuildingStats.rows[type] * TILE_SIZE) / 2;
			PVector c = new PVector(centerx, centery);
			PVector e = target.screenLoc();
			e.add(Enemy.ENEMY_SIZE / 2, Enemy.ENEMY_SIZE / 2, 0);
			//p.line(c.x, c.y, e.x, e.y);
			rad = p.HALF_PI + (float) p.atan2((e.y - c.y), (e.x - c.x));
		}
	}

	public void drawHp(PApplet p, int x, int y, int selectedTime) {
		if (buildTime != -1)
			return;
		
		final int hpHeight = 8;
		
		// background
		p.noStroke();
		p.fill(0);
		p.rect(x, y, width * TILE_SIZE, hpHeight);
		
		// bar
		int fill = (int) ((width * TILE_SIZE - 2) * (hp / maxHp));
		p.fill(0xFFC00000);
		if (buildTime != -1) {
			fill = (int) ((width * TILE_SIZE - 2) * buildHealth * (hp / maxHp) / 5000);
			p.fill(0xFF204080);
		}
		p.rect(x + 1, y + 1, fill, hpHeight - 2);
	}

	public void draw(PApplet p, int x, int y, int selectedTime) {
		calcAngle(p, x, y);

		if (hp <= 0) {
			return;
		}

		p.noStroke();

		// background
		p.stroke(0xFF404040);
		p.strokeWeight(1);
		p.textFont(Fonts.consolas16);
		p.fill(0xFF808080);

		// p.rect(x, y + hpHeight, width * TILE_SIZE - 1, height * TILE_SIZE -
		// hpHeight - 1);
		p.image(SpriteMaster.instance(p).building_sprites[type], x, y);
		// laser gun
		if (type == 5 || type == 6) {
			if (target != null) {

				p.pushStyle();
				p.stroke(0xFFFF0000);
				p.strokeWeight(2);
				if (type == 5) {
					p.line(x + (width * TILE_SIZE) / 2, y + (height * TILE_SIZE)
							/ 2, target.screenLoc().x + target.ENEMY_SIZE / 2,
							target.screenLoc().y + target.ENEMY_SIZE / 2);
				}
				p.popStyle();
			}

			p.pushMatrix();
			p.translate(x + (BuildingStats.cols[type] * TILE_SIZE) / 2, y
					+ (BuildingStats.rows[type] * TILE_SIZE) / 2);
			p.rotate(rad);
			PImage sprite;
			if(type == 5)
				sprite = SpriteMaster.instance(p).laser_gun;
			else
				sprite = SpriteMaster.instance(p).mortar_gun;

			p.image(sprite, -sprite.width / 2, -sprite.height / 2);
			p.popMatrix();
		}
		if (selectedTime != -1) {
			int alpha = (selectedTime / 2) % 511;
			if (alpha > 255)// TODO: fine tuning
				alpha = 511 - alpha;
			p.fill(p.color(32, 128, 0, alpha / 2 + 128));
			p.rect(x, y, width * TILE_SIZE - 1, height * TILE_SIZE - 1);
			p.textAlign(p.CENTER, p.CENTER);
			p.textSize(12);
			p.fill(0);
			p.text("sell", x, y, width * TILE_SIZE - 1, height * TILE_SIZE - 1);
		}

		// text
		p.textAlign(p.LEFT, p.TOP);
		p.fill(0);
		p.textSize(16);
		// p.text("" + type, x + 8, y + 8);
		if (buildTime != -1) {
			p.noStroke();
			
			int fill = (int) ((width * TILE_SIZE - 2) * buildHealth * (hp / maxHp) / 5000);

			p.fill(0);
			p.rect(x, y, fill + 2, 8);
			
			p.fill(0xFF204080);
			p.rect(x + 1, y + 1, fill, 8 - 2);
		}

		// if building is not powered draw the sign
		// otherwise draw the connection
		if (!powered) {
			// TODO: optimize
			p.text("!", x + 8, y + 8);
		} else if (powerSource != null) {

		}
	}

	public Enemy findClosestEnemy(Game game, int range) {
		searchMin = 32 * 32 * range * range;
		searchResult = null;
		
		// this is a close imitation of breadth-first search
		// TODO: can always optimize more (with a more full breadth search, or larger enemy array)
		for (int i = 1; i <= range; i++) {
			// values
			int top = row - i;
			int left = col - i;
			int bottom = row + i + height - 1;
			int right = col + i + width - 1;
			
			// looping values
			int loopTop = Math.max(0, top);
			int loopLeft = Math.max(0, left);
			int loopBottom = Math.min(bottom, game.tileHeight - 1);
			int loopRight = Math.min(right, game.tileWidth - 1);
			
			// add enemies on the left and right sides
			for (int j = loopTop; j <= loopBottom; j++) {
				if (left == loopLeft)
					findClosestEnemy(game.enemyArrayCenter[j][left]);
				if (right == loopRight)
					findClosestEnemy(game.enemyArrayCenter[j][right]);
			}
			
			// add enemies top and bottom sides
			for (int j = loopLeft; j <= loopRight; j++) {
				if (top == loopTop)
					findClosestEnemy(game.enemyArrayCenter[top][j]);
				if (bottom == loopBottom)
					findClosestEnemy(game.enemyArrayCenter[bottom][j]);
			}
			
			// return the closest enemy
			if (searchResult != null)
				return searchResult;
		}
		
		return null;
	}
	
	private void findClosestEnemy(ArrayList<Enemy> enemies) {
		for (Enemy enemy : enemies) {
			int dist = distToEnemy(enemy);
			if (dist < searchMin) {
				searchMin = dist;
				searchResult = enemy;
			}
		}
	}

	private int distToEnemy(Enemy enemy) {
		// TODO: use all four squares if needed
		int x1 = col * TILE_SIZE + width * TILE_SIZE / 2;
		int y1 = row * TILE_SIZE + height * TILE_SIZE / 2;
		int x2 = enemy.bounds.x + enemy.ENEMY_SIZE / 2;
		int y2 = enemy.bounds.y + enemy.ENEMY_SIZE / 2;
		int dx = x1 - x2;
		int dy = y1 - y2;
		return (dx * dx + dy * dy);
	}
}
