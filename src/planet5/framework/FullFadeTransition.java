/*
 * FullFadeTransition.java
 * James Zhang
 * July 19 2012
 * Displays a 255 millisecond fade to a black screen, then a 255 millisecond
 * fade to the next screen.
 */

package planet5.framework;


public class FullFadeTransition extends Transition {
	public FullFadeTransition(Applet parent) {
		super(parent);
	}

	@Override
	public void draw() {
        int time = getElapsedMillis();

        if (time > 510) {
            drawNextFrameImage();
            finish();
            return;
        }

        // draw frame image
        if (time > 255) {
            drawNextFrameImage();
            time = 510 - time;
        }
        else {
            drawCurrentFrameImage();
        }

        // draw blackness
        p.fill(0, 0, 0, time);
        p.noStroke();
        p.rect(0, 0, p.width, p.height);
    }
}
