package planet5.game;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import processing.core.PApplet;
import processing.core.PVector;

public class Map {
	// the time of day stored in milliseconds
	int timeOfDay;
	Tile[][] tiles;
	ArrayList<Building> buildings = new ArrayList<Building>();
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	Hero hero = new Hero();
	Applet p;
	GameFrame game;
	public int mapX = 100, mapY = 100;
	
	public Map(Applet parent, GameFrame game, Tile[][] tiles) {
		p = parent;
		this.game = game;
		this.tiles = tiles;
	}
	
	// timePassed is the amount of milliseconds between the last update and this one
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
		// TODO Auto-generated method stub
		
	}

	private void updateBuildings(int timePassed) {
		// TODO Auto-generated method stub
		
	}
	
	private void spawnEnemies(int timePassed) {
		// TODO: use the following to spawn enemies:
		// timeOfDay: how many monsters to spawn
		// tiles: where to spawn the monsters
		// and maybe more
	}
	
	private void updateEnemies(int timePassed) {
		// TODO Auto-generated method stub
		
	}
	
	private void recalculateLighting() {
		// TODO: use the following to recalculate the lighting of tiles:
		// timeOfDay: cover entire map
		// tiles: walls may block light
		// buildings: produce their own light
	}

	public void draw() {
		// TODO: TEMP
		int move = 2;
		if (p.pressedKeys[KeyEvent.VK_W]) mapY-=move;
		if (p.pressedKeys[KeyEvent.VK_A]) mapX-=move;
		if (p.pressedKeys[KeyEvent.VK_S]) mapY+=move;
		if (p.pressedKeys[KeyEvent.VK_D]) mapX+=move;
		mapX=p.constrain(mapX, 0, 32*tiles.length-p.width);
		mapY=p.constrain(mapY, 0, 32*tiles[0].length-p.height+45);
		
		draw(32);
		// TODO: draw the following:
		// tiles first
		// then buildings and enemies
		// then projectiles
	}

	public void draw(int size) {
		p.translate(-mapX, -mapY);
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				Tile t = tiles[x][y];
				PVector loc = new PVector(x*size, y*size);
				
				// dont draw invisible ones
				int width = p.width;
				int height = p.height - 45;
				if (loc.x < -size+mapX || loc.y < -size+mapY || loc.x > width+mapX || loc.y > height+mapY) {
					continue;
				}
				
				p.noStroke();
				//p.stroke(0);
				if(t.wall) {
					//Color c = new Color(t.color);
					//p.fill(p.color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue()));
					p.fill(p.color(128));
				} else {
					p.fill(t.color);
				}
				
				if (t.building != null) {
					p.fill(p.color(192));
				}
				//p.fill(t.color);
				p.rect(loc.x, loc.y, size, size);
				if (t.building != null) {
					p.fill(p.color(0));
					p.stroke(p.color(0));
					p.strokeWeight(1);
					p.textFont(Fonts.consolas16);
					p.text("" + t.building.type, loc.x, loc.y, 32, 32);
				}
			}
		}

		// TODO: ...
		if (isValidBuilding() && p.mouseY > 45) {
			drawBuildingPlaceover(canPlaceBuilding());
		}

		p.translate(mapX, mapY);
	}
	
	public void drawBuildingPlaceover(boolean canPlace) {
		int x = p.mouseX, y = p.mouseY;
		x += mapX;
		y += mapY;
		y -= 45;
		x /= 32;
		y /= 32;
		int width=BuildingStats.cols[game.placingBuilding];
		int height=BuildingStats.rows[game.placingBuilding];
		if (canPlaceBuilding()) {
			p.fill(p.color(32, 128, 0, 128));
		} else {
			p.fill(p.color(255, 0, 0, 128));
		}
		p.rect(32 * x, 32 * y, width * 32, height * 32);
	}
	
	public boolean isValidBuilding() {
		return (game.placingBuilding != -1 && p.mouseY > 45 &&
				game.placingBuilding < BuildingStats.cols.length);
	}
	
	public boolean canPlaceBuilding() {
		int x = p.mouseX, y = p.mouseY;
		x += mapX;
		y += mapY;
		y -= 45;
		x /= 32;
		y /= 32;
		int width=BuildingStats.cols[game.placingBuilding];
		int height=BuildingStats.rows[game.placingBuilding];
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int c = x + i, r = y + j;
				if (c >= tiles.length || r >= tiles[0].length || tiles[c][r].wall || tiles[c][r].building != null) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static Map reallyRandomLevel(Applet p, GameFrame game, int width, int height, Random r) {
		Tile[][] tiles = new Tile[width][height];
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, r.nextBoolean());
			}
		}
		return new Map(p, game, tiles);
	}
	
	public static Map noiseRandomLevel(Applet p, GameFrame game, int width, int height) {
		Tile[][] tiles = new Tile[width][height];
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, (p.noise(x*0.1f, y*0.1f) > 0.5));
				float xf = x * 0.03f, yf = y * 0.03f;
				tiles[x][y].color = p.color(255 * p.noise(xf, yf),
						255 * p.noise(xf, 0, yf),
						255 * p.noise(0, xf, yf));
			}
		}
		return new Map(p, game, tiles);

	}

	public void placeBuilding() {
		if (isValidBuilding() && canPlaceBuilding()) {
			int x = p.mouseX, y = p.mouseY;
			x += mapX;
			y += mapY;
			y -= 45;
			x /= 32;
			y /= 32;
			int width=BuildingStats.cols[game.placingBuilding];
			int height=BuildingStats.rows[game.placingBuilding];
			for(int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int c = x + i, r = y + j;
					tiles[c][r].building = new Building(game.placingBuilding);
				}
			}
		}
	}
}
