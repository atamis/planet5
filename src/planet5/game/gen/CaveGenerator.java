package planet5.game.gen;

import java.util.ArrayList;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;
import planet5.game.Tile;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class CaveGenerator implements Generator {
	public Map gen(Applet p, GameFrame game, int width, int height) {
		Tile[][] tiles = new Tile[width][height];
		// Number of "caverns" and the number of connections each cavern has to
		// other caverns.
		int num_points = 10;
		int num_connections = 4;

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, true);
			}
		}

		// Get all the points generated
		ArrayList<PVector> points = new ArrayList<PVector>(num_points);

		for (int i = 0; i < num_points; i++) {
			int point_x = (int) (p.random(width) - 1);
			int point_y = (int) (p.random(height) - 1);
			points.add(new PVector(point_x, point_y));
		}

		// Make the graphics and prepare it for drawing.
		PGraphics pg = p.createGraphics(width, height, PConstants.P2D);
		pg.beginDraw();
		pg.background(0xffffff);
		pg.fill(0);
		pg.stroke(0);
		pg.strokeWeight(3);

		// For each point...
		for (PVector loc : points) {
			pg.point(loc.x, loc.y);
			// hollow out a cavern...
			pg.ellipse(loc.x, loc.y, 7, 7);
			// and draw connections to some random other caverns.
			for (int i = 0; i < num_connections; i++) {
				PVector other_loc = points
						.get((int) p.random(points.size() - 1));
				pg.line(loc.x, loc.y, other_loc.x, other_loc.y);
			}
		}

		// Convert the graphics to tiles.
		tiles = GenUtil.graphicsToTiles(pg, tiles);
		pg.endDraw();

		// We're done folks!
		return new Map(p, game, tiles);

	}

}
