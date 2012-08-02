package planet5.game;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PFont;

public class GameButton {
	// listening variables
	private Game listener;
	private boolean pressedInside = false;
	private boolean pressedAnywhere = false;

	// properties
	public String text;
	public Rectangle bounds;
	public boolean enabled = true;
	public boolean visible = true;
	
	// special variables
	private PApplet p;
	private PFont font;

	// constructor
	public GameButton(Game listener, PApplet p, Rectangle bounds, String text, PFont font) {
		this.listener = listener;
		this.p = p;
		this.bounds = bounds;
		this.text = text;
		this.font = font;
	}

	// painting methods
	protected void draw(int mouseX, int mouseY, boolean windowFocused) {
		if (!visible || !enabled) {
			return;
		}
		
		boolean mouseInside = bounds.contains(mouseX, mouseY);
		if (!enabled) {
			drawButton(192);
		} else if (pressedInside && mouseInside) {
			drawButton(176);
		} else if (windowFocused && !pressedAnywhere && mouseInside) {
			drawButton(208);
		} else {
			drawButton(192);
		}
	}

	private void drawButton(int color) {
		p.noStroke();
		
		// background
		p.fill(p.color(color - 160));
		p.rect(bounds.x, bounds.y, bounds.width, bounds.height);
		
		// text
		p.fill(p.color(color + 16));
		p.textAlign(p.CENTER, p.CENTER);
		p.textFont(font);
		p.text(text, bounds.x, bounds.y - p.textDescent() / 2, bounds.width, bounds.height);
	}

	// updating method
	public void update(boolean windowFocused) {
		// check every frame to see if the mouse was released
		if (!windowFocused) {
			pressedAnywhere = false;
			pressedInside = false;
		}
	}

	// input methods
	public void mousePressed(int x, int y, int mouseButton) {
		if (mouseButton == MouseEvent.BUTTON1) {
			if (enabled && visible && bounds.contains(x, y)) {
				pressedInside = true;
			}
			pressedAnywhere = true;
		}
	}

	public void mouseReleased(int x, int y, int mouseButton) {
		if (mouseButton == MouseEvent.BUTTON1) {
			if (pressedInside && enabled && visible && bounds.contains(x, y)) {
				listener.buttonClicked(text);
			}
			pressedAnywhere = false;
			pressedInside = false;
		}
	}
}
