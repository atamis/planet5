package planet5.loaders;

import processing.core.PApplet;
import processing.core.PFont;

public final class Fonts {
	public static PFont consolas8;
	public static PFont consolas16;
	public static PFont consolas32;
	public static PFont consolas96;
	
	public static void load(PApplet applet) {
		consolas8 = applet.loadFont("Consolas-8.vlw");
		consolas16 = applet.loadFont("Consolas-16.vlw");
		consolas32 = applet.loadFont("Consolas-32.vlw");
		consolas96 = applet.loadFont("Consolas-96.vlw");
	}
}
