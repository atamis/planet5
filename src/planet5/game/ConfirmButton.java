package planet5.game;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import planet5.config.Fonts;
import planet5.framework.Button;
import processing.core.PApplet;
import processing.core.PFont;

public class ConfirmButton {
	// listening variables
	private Game listener;
	private boolean pressedInside = false;
	private boolean pressedAnywhere = false;
	private boolean confirm = false;
	private int x, y;

	// properties
	public String text;
	public Rectangle bounds;
	
	// special variables
	private PApplet p;

	// constructor
	public ConfirmButton(Game listener, PApplet p, Rectangle bounds, String text) {
		this.listener = listener;
		this.p = p;
		this.bounds = bounds;
		this.text = text;
	}

	// painting methods
	protected void draw(int mouseX, int mouseY, boolean windowFocused) {
		boolean mouseInside = bounds.contains(mouseX, mouseY);
		if (pressedInside && mouseInside) {
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
		p.textFont(Fonts.consolas16);
		String text = this.text;
		if (confirm) {
			text = "Sure?";
		}
		p.text(text, bounds.x, bounds.y - p.textDescent() / 2, bounds.width, bounds.height);
	}

	// updating method
	public void update(boolean windowFocused) {
		final int CONFIRM_BOX = 0;
		if (!bounds.contains(p.mouseX, p.mouseY) || 
				Math.abs(x - p.mouseX) > CONFIRM_BOX || Math.abs(y - p.mouseY) > CONFIRM_BOX) {
			confirm = false;
		}
		
		// check every frame to see if the mouse was released
		if (!windowFocused) {
			pressedAnywhere = false;
			pressedInside = false;
		}
	}

	// input methods
	public void mousePressed(int x, int y, int mouseButton) {
		if (mouseButton == MouseEvent.BUTTON1) {
			if (bounds.contains(x, y)) {
				pressedInside = true;
			}
			pressedAnywhere = true;
		}
	}

	public void mouseReleased(int x, int y, int mouseButton) {
		if (mouseButton == MouseEvent.BUTTON1) {
			if (pressedInside && bounds.contains(x, y)) {
				if (confirm) {
					listener.buttonClicked(text);
					confirm = false;
				} else {
					this.x = x;
					this.y = y;
					confirm = true;
				}
				pressedAnywhere = false;
				pressedInside = false;
			}
		}
	}
}
