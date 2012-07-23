package planet5.game.gen;

import java.util.ArrayList;

import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;
import planet5.game.Tile;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class VoronoiPerlinNoiseGenerator implements Generator {

	@Override
	public Map gen(Applet p, GameFrame game, int width, int height) {
		Tile[][] tiles = new Tile[width][height];

		// Set up the tiles.
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, false);
			}
		}

		// This really just adjusts the resolution of the regions. How closely
		// the match the Perlin noise.
		int num_points = width;

		// Create and set up the graphics.
		PGraphics pg = p.createGraphics(width, height, PConstants.P2D);
		pg.beginDraw();
		pg.background(0xffffff);
		pg.fill(0);
		pg.noStroke();

		// Generate some random points.
		ArrayList<PVector> points = new ArrayList<PVector>();
		for (int i = 0; i < num_points; i++) {
			points.add(GenUtil.randomVector(width, height));
		}

		// Convert the points to the stupid format the Voronoi library uses.
		float[][] ary_points = GenUtil.vectorFloatConvert(points);

		// Set up the voronoi class.
		Voronoi myVoronoi = new Voronoi(ary_points);

		MPolygon[] myRegions = myVoronoi.getRegions();

		// For each region...
		for (int i = 0; i < myRegions.length; i++) {
			float[][] regionCoordinates = myRegions[i].getCoords();

			// Use Perlin noise to determine whether to include this region as a
			// wall.
			if ((p.noise((float) (regionCoordinates[0][0] * 0.1),
					(float) (regionCoordinates[0][1] * 0.1)) > 0.5)) {

				pg.fill(255);
				myRegions[i].draw(pg); // draw this shape
			}
		}

		pg.endDraw();

		// At it to tiles.
		tiles = GenUtil.graphicsToTiles(pg, tiles);

		// Invert the walls.
		for (Tile[] t_ary : tiles) {
			for (Tile t : t_ary) {
				t.wall = !t.wall;
			}
		}

		// TODO Auto-generated method stub
		return new Map(p, game, tiles);
	}

}
