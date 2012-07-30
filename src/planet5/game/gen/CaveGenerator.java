package planet5.game.gen;

import java.awt.Color;
import java.util.ArrayList;

import planet5.config.BuildingStats;
import planet5.config.Globals;
import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Building;
import planet5.game.Game;
import planet5.game.Hero;
import planet5.game.Tile;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class CaveGenerator implements Generator {
	@Override
	public void gen(Applet p, Game game, int width, int height) {
		Tile[][] tiles = new Tile[width][height];
		// Number of "caverns" and the number of connections each cavern has to
		// other caverns.
		int num_points = width/5;
		int num_connections = p.max(2, width/100);

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				float hue = (float) 0.1;
				float sat = p.map(p.noise(x*0.05f, y * 0.05f), 0, 1, (float) 0.52, 1);
				float val = p.map(p.noise(x*0.053f + 1, y * 0.053f + 1), 0, 1, (float) 0.25, 1);
				tiles[x][y] = new Tile(Color.HSBtoRGB(hue, sat, val), true);
			}
		}

		// Get all the points generated
		ArrayList<PVector> points = new ArrayList<PVector>(num_points);
		points.add(new PVector(width/2, height/2));
		

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

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				float hue = (float) 0.1;
				float sat = p.map(p.noise(x*0.05f, y * 0.05f), 0, 1, (float) 0.52, 1);
				float val = p.map(p.noise(x*0.053f + 1, y * 0.053f + 1), 0, 1, (float) 0.25, 1);;
				if(tiles[x][y].wall)
					val = p.max(0.0f, val - 0.2f);
				tiles[x][y].color = Color.HSBtoRGB(hue, sat, val);
			}
		}

		game.tiles = tiles;
		
		int x = (int) (width/2.0);
		int y = (int) (height/2.0);
		
		// Put the hero in the center.
		game.hero = new Hero(p, game, 0, 0);
		game.hero.x = x * Globals.TILE_SIZE;
		game.hero.y = y * Globals.TILE_SIZE;
		game.hero.kiloX = 1000 * game.hero.x;
		game.hero.kiloY = 1000 * game.hero.y;
		
		Building base = new Building(0, x + 1, y, game.gameMillis);
		game.setBase(base);
	}

}
