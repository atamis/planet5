package planet5.game.gen;

import java.util.ArrayList;

import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;
import planet5.game.Tile;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class VoronoiPerlinNoiseGenerator implements Generator {

	@Override
	public Map gen(Applet p, GameFrame game, int width, int height) {
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, false);
			}
		}

		int num_points = width;

		PGraphics pg = p.createGraphics(width, height, p.P2D);
		pg.beginDraw();
		pg.background(0xffffff);
		pg.fill(0);
		pg.noStroke();

		ArrayList<PVector> points = new ArrayList<PVector>();
		for (int i = 0; i < num_points; i++) {
			points.add(GenUtil.randomVector(p, width, height));
		}

		float[][] ary_points = GenUtil.vectorFloatConvert(points);
		Voronoi myVoronoi = new Voronoi(ary_points);

		MPolygon[] myRegions = myVoronoi.getRegions();

		for (int i = 0; i < myRegions.length; i++) {
			// an array of points
			float[][] regionCoordinates = myRegions[i].getCoords();
			if ((p.noise((float) (regionCoordinates[0][0] * 0.1),
					(float) (regionCoordinates[0][1] * 0.1)) > 0.5)) {
				
				pg.fill(255);
				myRegions[i].draw(pg); // draw this shape
			}
		}

		pg.endDraw();

		tiles = GenUtil.graphicsToTiles(pg, tiles);
		
		for(Tile[] t_ary : tiles) {
			for (Tile t : t_ary) {
				t.wall = ! t.wall;
			}
		}

		// TODO Auto-generated method stub
		return new Map(p, game, tiles);
	}

}
