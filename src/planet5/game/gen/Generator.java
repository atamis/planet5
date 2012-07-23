package planet5.game.gen;

import planet5.frames.GameFrame;
import planet5.framework.Applet;
import planet5.game.Map;

public interface Generator {
	public Map gen(Applet p, GameFrame game, int width,
			int height);
}
