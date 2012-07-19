package planet5.frames;

import java.awt.Rectangle;

import planet5.config.Fonts;
import planet5.framework.Button;

public class MenuButton extends Button {
	public MenuButton(Rectangle bounds, String text) {
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
	
	// helper painting methods
	private void paintButton(int color) {
		p.noStroke();
		p.fill(p.color(color - 160));
		p.rect(getX(), getY(), getWidth(), getHeight());
		
		p.noStroke();
		p.fill(p.color(color + 16));
		p.textFont(Fonts.consolas32);
		p.textAlign(p.LEFT, p.CENTER);
		p.text(" " + getText(), getX(), getY() - p.textDescent() / 2, getWidth(), getHeight());
	}
}
