/*
 * Transition.java
 * James Zhang
 * July 17 2012
 * The Transition class is used by the Applet to transition between two Frames.
 */

package planet5.framework;

public abstract class Transition {
	// keep track of passed time
	private int startTime;

	private Applet parent;

	// the frame to transition into
	private Frame nextFrame = null;

	// arrays that store the frame images
	private int[] currentFrameImagePixels;
	private int[] nextFrameImagePixels;

	// starts a transition
	public void start(Frame nextFrame) {
		// save the next frame first so the current frame will draw over it
		nextFrameImagePixels = captureFrame(nextFrame);
		currentFrameImagePixels = captureFrame(parent.currentFrame);

		// set variables
		parent.currentTransition = this;
		startTime = parent.millis();
		this.nextFrame = nextFrame;
	}

	// custom drawing method
	public abstract void draw();

	// gets the current frame image
	protected int[] getCurrentFrameImage() {
		return currentFrameImagePixels;
	}

	// gets the next frame image
	protected int[] getNextFrameImage() {
		return nextFrameImagePixels;
	}

	// draws the current frame image
	protected void drawCurrentFrameImage() {
		drawImage(currentFrameImagePixels);
	}

	// draws the image of the frame to transition to
	protected void drawNextFrameImage() {
		drawImage(nextFrameImagePixels);
	}

	// returns the elapsed milliseconds this transition has been going
	protected int getElapsedMillis() {
		return parent.millis() - startTime;
	}

	// stops the transition and sets the next screen
	protected void finish() {
		parent.currentFrame = nextFrame;
		parent.currentTransition = null;
	}

	// sets the frame to a color array
	private void drawImage(int[] imagePixels) {
		parent.loadPixels();
		for (int i = 0; i < imagePixels.length; i++) {
			parent.pixels[i] = imagePixels[i];
		}
		parent.updatePixels();
	}

	// returns a color array of a frame. will also draw the frame
	private int[] captureFrame(Frame frame) {
		frame.paint();
		parent.loadPixels();
		int[] result = parent.pixels.clone();
		parent.updatePixels();
		return result;
	}
}
