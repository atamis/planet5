package planet5.game.gen;

import java.util.Random;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;
import planet5.game.Tile;

public class ReallyRandomMapGenerator implements Generator {
	public Map gen(Applet p, GameFrame game, int width,
			int height) {
		Random r = new Random();
		Tile[][] tiles = new Tile[height][width];
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff, r.nextBoolean());
			}
		}
		return new Map(p, game, tiles);
	}
}
