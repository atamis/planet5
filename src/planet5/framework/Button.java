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
	protected PApplet parent;

	// the frame that will listen to this button click
	private Frame listener;

	// text to be displayed on the button
	private String text;

	// the command describing this button's action when clicked
	private String command;

	// the location and size of the button
	private Rectangle bounds;

	// whether the button is enabled
	private boolean enabled = true;

	// whether the mouse was pressed inside the button
	private boolean pressedInside = false;

	// constructor
	public Button(Frame listener, Rectangle bounds, String text, String command) {
		this.listener = listener;
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
		boolean mouseInside = bounds.contains(parent.mouseX, parent.mouseY);

		if (!enabled) {
			paintDisabledState();
		} else if (pressedInside && mouseInside) {
			paintPressedState();
		} else if (!parent.mousePressed && mouseInside) {
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
		if (!parent.mousePressed) {
			pressedInside = false;
		}
	}

	// input methods
	public void mousePressed() {
		if (parent.mouseButton == parent.LEFT
				&& bounds.contains(parent.mouseX, parent.mouseY)) {
			pressedInside = true;
			if (listener != null) {
				listener.buttonPressed(command);
			}
			pressed();
		}
	}

	public void mouseReleased() {
		if (parent.mouseButton == parent.LEFT && pressedInside
				&& bounds.contains(parent.mouseX, parent.mouseY)) {
			if (listener != null) {
				listener.buttonClicked(command);
			}
			clicked();
		}
	}
}
