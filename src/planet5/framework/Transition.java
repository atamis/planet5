package planet5.framework;

public abstract class Transition {
	// keep track of passed time
	private int startTime;

	protected Applet p;

	// the frame to transition into
	public Frame nextFrame = null;

	// arrays that store the frame images
	private int[] currentFrameImagePixels;
	private int[] nextFrameImagePixels;
	
	public Transition(Applet parent) {
		p = parent;
	}

	// starts a transition
	public void start(Frame nextFrame) {
		// save the next frame first so the current frame will draw over it
		nextFrameImagePixels = captureFrame(nextFrame);
		currentFrameImagePixels = captureFrame(p.currentFrame);

		// set variables
		p.currentTransition = this;
		startTime = p.millis();
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
		return p.millis() - startTime;
	}

	// stops the transition and sets the next screen
	protected void finish() {
		p.currentFrame = nextFrame;
		p.currentTransition = null;
	}

	// sets the frame to a color array
	private void drawImage(int[] imagePixels) {
		p.loadPixels();
		for (int i = 0; i < imagePixels.length; i++) {
			p.pixels[i] = imagePixels[i];
		}
		p.updatePixels();
	}

	// returns a color array of a frame. will also draw the frame
	private int[] captureFrame(Frame frame) {
		frame.paint();
		p.loadPixels();
		int[] result = p.pixels.clone();
		p.updatePixels();
		return result;
	}
}
