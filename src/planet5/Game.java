package planet5;

import planet5.framework.Applet;
import processing.core.PApplet;

public class Game extends Applet {
	@Override
	public void setup() {
		smooth();
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "planet5.Game" });
	}
}
