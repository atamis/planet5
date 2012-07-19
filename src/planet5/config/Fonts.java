package planet5.config;

import processing.core.PApplet;
import processing.core.PFont;

public final class Fonts {
	public static PFont consolas16;
	public static PFont consolas32;
	
	public static void load(PApplet applet) {
		consolas16 = applet.loadFont("Consolas-16.vlw");
		consolas32 = applet.loadFont("Consolas-32.vlw");
	}
}
