/*
 * Applet.java
 * James Zhang
 * July 17 2012
 * The Applet class extends a PApplet and manages multiple Frames classes. Each
 * Frame acts like a PApplet when it is focused. This class keeps track of the
 * pressed keys and supports changing frames, optionally with transitions.
 */

package planet5.framework;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;

import processing.core.PApplet;

public class Applet extends PApplet implements MouseWheelListener {
	// variables
	Frame currentFrame = null;
	Transition currentTransition = null;

	// stores whether a key is pressed, using key codes
	public boolean[] pressedKeys = new boolean[65536];

	public Applet() {
		addMouseWheelListener(this);
	}

	// drawing and updating method
	@Override
	public void draw() {
		// clear pressed keys if focus is lost and update game time
		if (!focused) {
			Arrays.fill(pressedKeys, false);
		}

		// if a transition is going then draw that instead of the frame
		if (currentTransition != null) {
			currentTransition.draw();
		} else if (currentFrame != null) {
			currentFrame.update();
			currentFrame.paint();
		}
	}

	// sets the frame automatically
	public void setFrame(Frame frame) {
		currentFrame = frame;
	}

	// transitions to another frame
	public void transitionFrame(Frame frame, Transition transition) {
		transition.start(frame);
	}

	// the following methods pass on keyboard and mouse input to the current
	// frame if input should be processed
	private boolean processInput() {
		return (currentTransition == null && currentFrame != null);
	}

	@Override
	public void mouseMoved() {
		if (processInput()) {
			currentFrame.mouseMoved();
		}
	}

	@Override
	public void mouseDragged() {
		if (processInput()) {
			currentFrame.mouseDragged();
		}
	}

	@Override
	public void mousePressed() {
		if (processInput()) {
			currentFrame.processMousePressed();
		}
	}

	@Override
	public void mouseReleased() {
		if (processInput()) {
			currentFrame.processMouseReleased();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (processInput()) {
			currentFrame.mouseWheelMoved(event.getWheelRotation());
		}
	}

	@Override
	public void keyPressed() {
		pressedKeys[keyCode] = true;
		if (processInput()) {
			currentFrame.keyPressed();
		}
		
		// don't let the escape key exit the game
		if (keyCode == KeyEvent.VK_ESCAPE)
			key = 0;
	}

	@Override
	public void keyReleased() {
		pressedKeys[keyCode] = false;
		if (processInput()) {
			currentFrame.keyReleased();
		}
	}

	@Override
	public void keyTyped() {
		// keyPressed is always called along keyTyped, so updating pressedKeys
		// is unnecessary
		if (processInput()) {
			currentFrame.keyTyped();
		}
	}
}
