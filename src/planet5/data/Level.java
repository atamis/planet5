package planet5.data;

import java.util.Random;

import planet5.math.ImprovedNoise;
import processing.core.PApplet;
import processing.core.PVector;

public class Level {
	public int width;
	public int height;
	public Tile[][] tiles;
	
	public Level(Tile[][] tiles) {
		this.tiles = tiles;
		width = tiles.length;
		height = tiles[0].length;
		
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
	
	public static Level reallyRandomLevel(int width, int height) {
		return reallyRandomLevel(width, height, new Random());
	}
	
	public static Level reallyRandomLevel(int width, int height, Random r) {
		Tile[][] tiles = new Tile[width][height];
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, r.nextBoolean());
			}
		}
		return new Level(tiles);
	}
	
	public static Level noiseRandomLevel(int width, int height) {
		return noiseRandomLevel(width, height, (new Random()).nextInt());
	}
	
	public static Level noiseRandomLevel(int width, int height, int seed) {
		Tile[][] tiles = new Tile[width][height];
		for(int x = 0; x < tiles.length; x++) {
			for(int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, (ImprovedNoise.noise(x*0.1, y*0.1, seed) > 0));
			}
		}
		return new Level(tiles);

	}
}
