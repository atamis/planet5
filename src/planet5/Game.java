package planet5;

import processing.core.*;

public class Game extends PApplet {
	public int ticks = 0;
	public PFont consolas16;
	/**
	 * Make Eclipse happy.
	 */
	private static final long serialVersionUID = 1L;

	public void setup() {
		consolas16 = loadFont("Consolas-32.vlw");
		textFont(consolas16, 32);
	}
	
	public void draw() {
		ticks++;
		background(map(sin((float) (ticks*0.1)), -1, 1, 0, 255));
		
		text("This is a test", 0, 0, 100, 100);
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[] {"planet5.Game" });
	}
}
