package planet5.game.gen;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;
import planet5.game.Tile;

public class NoiseRandomGenerator implements Generator {
	public Map gen(Applet p, GameFrame game, int width,
			int height) {
		Tile[][] tiles = new Tile[height][width];
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				tiles[x][y] = new Tile(0xffffff,
						(p.noise(x * 0.1f, y * 0.1f) > 0.5));
				float xf = x * 0.03f, yf = y * 0.03f;
				tiles[x][y].color = p.color(255 * p.noise(xf, yf),
						255 * p.noise(xf, 0, yf), 255 * p.noise(0, xf, yf));
			}
		}
		return new Map(p, game, tiles);
	}

}
