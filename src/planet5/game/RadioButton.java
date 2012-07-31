package planet5.game;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import planet5.config.Fonts;
import processing.core.PApplet;

public class RadioButton {
	// listening variables
	private Game listener;
	private boolean pressedInside = false;
	private boolean pressedAnywhere = false;

	// properties
	public String text;
	public Rectangle bounds;
	public boolean selected = false;
	public boolean enabled = true;
	
	// special variables
	private PApplet p;

	// constructor
	public RadioButton(Game listener, PApplet p, Rectangle bounds, String text) {
		this.listener = listener;
		this.p = p;
		this.bounds = bounds;
		this.text = text;
	}

	// painting methods
	protected void draw(int mouseX, int mouseY, boolean windowFocused) {
		boolean mouseInside = bounds.contains(mouseX, mouseY);
		
		if (selected) {
			drawButton(-1);
		} else if (!enabled) {

		} else if (pressedInside && mouseInside) {
			drawButton(192);
		} else if (windowFocused && !pressedAnywhere && mouseInside) {
			drawButton(224);
		} else {
			drawButton(208);
		}
	}

	private void drawButton(int color) {
		p.noStroke();
		
		// background
		if (color == -1)
			p.fill(128, 128, 64);
		else
			p.fill(p.color(color - 176));
		p.rect(bounds.x, bounds.y, bounds.width, bounds.height);

		// text
		p.fill(p.color(color + 16));
		p.textAlign(p.CENTER, p.CENTER);
		p.textFont(Fonts.consolas16);
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
			if (enabled && bounds.contains(x, y)) {
				pressedInside = true;
			}
			pressedAnywhere = true;
		}
	}

	public void mouseReleased(int x, int y, int mouseButton) {
		if (mouseButton == MouseEvent.BUTTON1) {
			if (pressedInside && enabled && bounds.contains(x, y)) {
				listener.buttonClicked(text);
				selected = true;
			}
			pressedAnywhere = false;
			pressedInside = false;
		}
	}
}
