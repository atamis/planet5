package planet5.frames;

import java.awt.Rectangle;

import planet5.framework.Button;
import planet5.loaders.Fonts;

public class TextButton extends Button {
	public TextButton(Rectangle bounds, String text) {
		super(bounds, text, text);
	}

	// painting methods
	@Override
	public void paintDisabledState() {
		paintButton(255);
	}

	@Override
	public void paintDefaultState() {
		paintButton(192);
	}

	@Override
	public void paintPressedState() {
		paintButton(160);
	}

	@Override
	public void paintHoveredState() {
		paintButton(224);
	}
	
	// helper painting methods
	private void paintButton(int color) {
		p.noStroke();
		p.fill(p.color(color + 16));
		p.textFont(Fonts.consolas32);
		p.textAlign(p.LEFT, p.CENTER);
		p.text(getText(), getX(), getY() - p.textDescent() / 2, getWidth(), getHeight());
	}
}
