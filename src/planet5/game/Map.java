package planet5.game;

import java.util.ArrayList;

import planet5.framework.Applet;

public class Map {
	// the time of day stored in milliseconds
	int timeOfDay;
	Tile[][] tiles;
	ArrayList<Building> buildings = new ArrayList<Building>();
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	Hero hero = new Hero();
	
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

	public void draw(Applet p) {
		p.background(255);
		// TODO: draw the following:
		// tiles first
		// then buildings and enemies
		// then projectiles
		
		// draw gui elements
		
		// gui background
		/*
		p.strokeWeight(1);
		p.stroke(0);
		p.line(0, p.height - 151, 0, p.height);
		p.line(0, p.height - 152, p.width, p.height - 152);
		p.line(p.width - 1, p.height - 151, p.width - 1, p.height);
		p.line(0, p.height - 1, p.width, p.height - 1);
		p.noStroke();
		p.fill(32);
		p.rect(1, p.height - 150 - 1, p.width - 2, 150);*/
		p.fill(0);
		p.rect(0, p.height - 150, p.width, 150);
	}
}
