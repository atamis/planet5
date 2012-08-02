package planet5.framework;

import planet5.Main;


public class FullFadeTransition extends Transition {
	public FullFadeTransition(Applet parent) {
		super(parent);
	}

	@Override
	public void draw() {
        int time = getElapsedMillis();

        if (time > 510) {
        	if (super.nextFrame != Main.instance.menuFrame)
        		drawNextFrameImage();
            finish();
            return;
        }

        // draw frame image
        if (time > 255) {
        	if (super.nextFrame == Main.instance.menuFrame)
        		Main.instance.menuFrame.paint();
        	else
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
