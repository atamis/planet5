/*
 * Button.java
 * James Zhang
 * July 17 2012
 * 
 */

package planet5.framework;

import java.awt.Rectangle;

import processing.core.PApplet;

public abstract class Button {
	// provides many methods and variables
	public PApplet p;

	// the frame that will listen to this button click
	public Frame listener;

	// text to be displayed on the button
	public String text;

	// the command describing this button's action when clicked
	public String command;

	// the location and size of the button
	public Rectangle bounds;

	// whether the button is enabled
	public boolean enabled = true;

	// whether the mouse was pressed inside the button
	public boolean pressedInside = false;

	// constructor
	public Button(Rectangle bounds, String text, String command) {
		//TODO: textAlign(...baseline
		this.bounds = bounds;
		this.text = text;
		this.command = command;
	}

	// called when the button is pressed
	protected void pressed() {}

	protected void clicked() {}

	// properties
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}

	public String getText() {
		return text;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public int getX() {
		return bounds.x;
	}

	public int getY() {
		return bounds.y;
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getHeight() {
		return bounds.height;
	}

	public boolean isPressedInside() {
		return pressedInside;
	}

	// painting methods
	protected void paintComponent() {
		boolean mouseInside = bounds.contains(p.mouseX, p.mouseY);

		if (!enabled) {
			paintDisabledState();
		} else if (pressedInside && mouseInside) {
			paintPressedState();
		} else if (p.focused && !p.mousePressed && mouseInside) {
			paintHoveredState();
		} else {
			paintDefaultState();
		}
	}

	public abstract void paintDisabledState();

	public abstract void paintDefaultState();

	public abstract void paintPressedState();

	public abstract void paintHoveredState();

	// updating method
	public void update() {
		// check every frame to see if the mouse was released
		if (!p.focused || !p.mousePressed) {
			pressedInside = false;
			p.mousePressed = false;
		}
	}

	// input methods
	public void mousePressed() {
		if (p.mouseButton == p.LEFT
				&& bounds.contains(p.mouseX, p.mouseY)) {
			pressedInside = true;
			listener.buttonPressed(command);
			pressed();
		}
	}

	public void mouseReleased() {
		if (p.mouseButton == p.LEFT && pressedInside
				&& bounds.contains(p.mouseX, p.mouseY)) {
			listener.buttonClicked(command);
			clicked();
		}
	}
}
