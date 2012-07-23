package planet5.game.gen;

import java.util.ArrayList;

import planet5.game.Tile;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class GenUtil {
	/*
	 * White is a wall.
	 */
	public static Tile[][] graphicsToTiles(PGraphics pg, Tile[][] tiles) {
		pg.loadPixels();
		int[] pixels = pg.pixels;
		for (int x = 0; x < pg.width; x++) {
			for (int y = 0; y < pg.height; y++) {
				if (pixels[y * pg.width + x] == 0xffffff)
					tiles[x][y].wall = true;
				else
					tiles[x][y].wall = false;
			}
		}
		return tiles;
	}

	public static float[][] vectorFloatConvert(ArrayList<PVector> points) {
		float[][] results = new float[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			results[i][0] = p.x;
			results[i][1] = p.y;
		}
		return results;
	}

	static PVector randomVector(PApplet p, int width, int height) {
		return new PVector(p.random(width), p.random(height));
	}

}
