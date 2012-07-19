package planet5.game;

import java.util.ArrayList;
import java.util.Random;

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
	
	public Map(Applet parent, Tile[][] tiles) {
		p = parent;
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
	
	public void draw(PVector offset, int size) {
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				Tile t = tiles[x][y];
				PVector loc = new PVector(x*size, y*size);
				loc.add(offset);
				p.stroke(0);
				if(t.wall) {
					p.strokeWeight(2);
				} else {
					p.strokeWeight(1);
				}
				p.fill(t.color);
				p.rect(loc.x, loc.y, size, size);
			}
		}
	}

	public void draw(Applet p) {
		p.background(255);
		draw(new PVector(0, 48), 32);
		// TODO: draw the following:
		// tiles first
		// then buildings and enemies
		// then projectiles
		
		// gui background
		p.fill(0);
		p.rect(0, 0, p.width, 47);
		
		// draw gui elements
		if (placing) {
			int x = p.mouseX, y = p.mouseY;
			y -= 48;
			x /= 32;
			y /= 32;
			// TODO: account for how big the rectangle is
			if (x < tiles.length && y < tiles[0].length) {
				p.strokeWeight(2);
				p.fill(p.color(255, 0, 0, 128));
				p.rect(x * 32, y * 32 + 48, 64, 64);
			}
		}
	}

	public void draw(PApplet p, PVector offset, int size) {
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				Tile t = tiles[x][y];
				PVector loc = new PVector(x*size, y*size);
				loc.add(offset);
				p.stroke(0);
				if(t.wall) {
					p.strokeWeight(2);
				} else {
					p.strokeWeight(1);
				}
				p.fill(t.color);
				p.rect(loc.x, loc.y, size, size);
			}
		}
	}
	
	public static Map reallyRandomLevel(Applet p, int width, int height, Random r) {
		Tile[][] tiles = new Tile[width][height];
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, r.nextBoolean());
			}
		}
		return new Map(p, tiles);
	}
	
	public static Map noiseRandomLevel(Applet p, int width, int height) {
		Tile[][] tiles = new Tile[width][height];
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, (p.noise(x*0.1f, y*0.1f) > 0.5));
			}
		}
		return new Map(p, tiles);

	}
	public void reset_TEMP() {
		placing = false;
	}

	boolean placing = false;
	public void placeBuilding_TEMP() {
		placing = true;
	}
	public void placeBuildingActual_TEMP() {
		if (placing) {
			
		}
	}
}
