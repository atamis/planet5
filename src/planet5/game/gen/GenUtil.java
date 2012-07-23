package planet5.game.gen;

import java.util.ArrayList;
import java.util.Random;

import planet5.game.Tile;
import processing.core.PGraphics;
import processing.core.PVector;

public class GenUtil {
	/**
	 * Take the pixels from a PGraphics instance and put them into a
	 * multidimensional tiles array.
	 * 
	 * @param pg
	 *            the PGraphics instance.
	 * @param tiles
	 *            the preexisting tiles array.
	 * @return the tile array mapped to the PGraphics instance, where white is a
	 *         wall.
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

	/**
	 * Convert a sane ArrayList of PVectors into the insane format that the
	 * Voronoi library uses.
	 * 
	 * @param points
	 *            an ArrayList of PVectors to convert.
	 * @return an array of arrays of floats, where float[i][0] is the x value of
	 *         the ith point, and float[i][1] is the y value.
	 */
	public static float[][] vectorFloatConvert(ArrayList<PVector> points) {
		float[][] results = new float[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			results[i][0] = p.x;
			results[i][1] = p.y;
		}
		return results;
	}

	/**
	 * Return a random PVector.
	 * 
	 * @param width
	 *            the maximum x value
	 * @param height
	 *            the maximum y value
	 * @return a PVector with x values between 0 and width, and y values between
	 *         0 and height.
	 */
	static PVector randomVector(int width, int height) {
		Random r = new Random();
		return new PVector(r.nextFloat() * width, r.nextFloat() * height);
	}

}
