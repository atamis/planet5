package planet5.game;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;

import planet5.Main;
import planet5.config.BuildingStats;
import planet5.config.Globals;
import planet5.config.UpgradeStats;
import planet5.config.Upgrades;
import planet5.framework.Applet;
import planet5.gfx.ParticleSystem;
import planet5.loaders.Fonts;
import planet5.loaders.SpriteMaster;
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
	//public int[] savedX, savedY;

	public int buildTime, buildHealth;
	Enemy target = null;
	boolean powered = false;
	int reach = 0;
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
			if (alpha > 255)
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
			p.fill(255, 255, 0);
			p.textFont(Fonts.consolas32);
			p.textAlign(p.LEFT, p.TOP);
			p.text("!", x, y);
		} else if (powerSource != null) {
			
		}
	}
	
	public void drawConnection(Applet p, int mapX, int mapY, ParticleSystem ps) {
		// don't draw connection if no source
		if (powerSource == null)
			return;
		
		// don't draw connection if unpowered
		if (!powered)
			return;
		
		// don't draw connection if still building
		if (buildTime != -1)
			return;
		
		// calculate building center and source center
		int x1 = 32*col + 16*width - mapX;
		int y1 = 32*row + 16*height - mapY;
		int x2 = 32*powerSource.col + 16*powerSource.width - mapX;
		int y2 = 32*powerSource.row + 16*powerSource.height - mapY;

		// only draw connection if the line will show on the screen
		if (!new Rectangle(0, 0, p.width, p.height - Game.BAR_HEIGHT).intersectsLine(x1, y1, x2, y2))
			return;
		
		ps.connectionParticles(x1 + mapX, y1 + mapY, x2 + mapX, y2 + mapY);
		// draw a connection between (x1, y1) and (x2, y2)
		p.pushS();
		p.strokeWeight(3);
		p.stroke(p.map((float) Math.sin(p.millis() * 0.005), -1, 1, 30, 150), 0, 0);
		p.line(x1, y1, x2, y2);
		p.popS();
		
		// TODO better graphics for line
	}

	public Enemy findClosestEnemy(Game game, int range) {
		searchMin = 32 * 32 * range * range;
		searchResult = null;
		
		// this is a close imitation of breadth-first search
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
		int x1 = col * TILE_SIZE + width * TILE_SIZE / 2;
		int y1 = row * TILE_SIZE + height * TILE_SIZE / 2;
		int x2 = enemy.bounds.x + enemy.ENEMY_SIZE / 2;
		int y2 = enemy.bounds.y + enemy.ENEMY_SIZE / 2;
		int dx = x1 - x2;
		int dy = y1 - y2;
		return (dx * dx + dy * dy);
	}
}
