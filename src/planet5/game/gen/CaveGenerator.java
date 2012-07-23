package planet5.game.gen;

import java.util.ArrayList;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;
import planet5.game.Tile;
import processing.core.PGraphics;
import processing.core.PVector;

public class CaveGenerator implements Generator {
	public Map gen(Applet p, GameFrame game, int width, int height) {
		Tile[][] tiles = new Tile[width][height];
		int num_points = 10;
		int num_connections = 4;
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, true);
			}
		}

		ArrayList<PVector> points = new ArrayList<PVector>(num_points);

		PGraphics pg = p.createGraphics(width, height, p.P2D);
		pg.beginDraw();
		pg.background(0xffffff);
		pg.fill(0);
		pg.stroke(0);
		pg.strokeWeight(3);

		for (int i = 0; i < num_points; i++) {
			int point_x = (int) (p.random(width) - 1);
			int point_y = (int) (p.random(height) - 1);
			points.add(new PVector(point_x, point_y));
		}

		for (PVector loc : points) {
			pg.point(loc.x, loc.y);
			pg.ellipse(loc.x, loc.y, 7, 7);
			for (int i = 0; i < num_connections; i++) {
				PVector other_loc = points
						.get((int) p.random(points.size() - 1));
				pg.line(loc.x, loc.y, other_loc.x, other_loc.y);
			}
		}

		tiles = GenUtil.graphicsToTiles(pg, tiles);
		pg.endDraw();

		return new Map(p, game, tiles);

	}

}
