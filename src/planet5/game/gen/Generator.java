package planet5.game.gen;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Game;

public interface Generator {
	public void gen(Applet p, Game game, int width,
			int height);
}
