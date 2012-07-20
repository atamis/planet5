package planet5.frames;

import java.awt.Rectangle;

import planet5.config.Fonts;
import planet5.framework.Button;

public class ConfirmButton extends Button {
	public boolean confirm = false;
	
	public ConfirmButton(Rectangle bounds, String text) {
		super(bounds, text, text);
	}

	// painting methods
	@Override
	public void paintDisabledState() {
		paintButton(224);
	}

	@Override
	public void paintDefaultState() {
		paintButton(192);
	}

	@Override
	public void paintPressedState() {
		paintButton(176);
	}

	@Override
	public void paintHoveredState() {
		paintButton(208);
	}

	@Override
	public void update() {
		if (!bounds.contains(p.mouseX, p.mouseY))
			confirm = false;
		super.update();
	}
	
	// helper painting methods
	private void paintButton(int color) {
		p.noStroke();
		p.fill(p.color(color - 160));
		p.rect(getX(), getY(), getWidth(), getHeight());
		
		p.noStroke();
		p.fill(p.color(color + 16));
		p.textFont(Fonts.consolas32);
		p.textAlign(p.CENTER, p.CENTER);
		
		String text = getText();
		if (confirm) {
			text = "Sure?";
		}
		p.text(text, getX(), getY() - p.textDescent() / 2, getWidth(), getHeight());
	}

	@Override
	public void mouseReleased() {
		if (p.mouseButton == p.LEFT && pressedInside
				&& bounds.contains(p.mouseX, p.mouseY)) {
			if (confirm) {
				listener.buttonClicked(command);
				clicked();
				confirm = false;
			} else {
				confirm = true;
			}
		}
	}
}
