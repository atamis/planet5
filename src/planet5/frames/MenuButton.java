package planet5.frames;

import java.awt.Rectangle;

import planet5.config.Fonts;
import planet5.framework.Button;
import processing.core.PFont;

public class MenuButton extends Button {
	public PFont font;
	public boolean center;
	public boolean pressed;
	
	public MenuButton(Rectangle bounds, String text, PFont font, boolean center) {
		super(bounds, text, text);
		this.font = font;
		this.center = center;
		this.pressed = false;
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
		if (pressed) {
			color = 176;
		}
		
		p.noStroke();
		p.fill(p.color(color - 160));
		p.rect(getX(), getY(), getWidth(), getHeight());
		
		p.noStroke();
		p.fill(p.color(color + 16));
		p.textFont(font);
		
		if (center) {
			p.textAlign(p.CENTER, p.CENTER);
		} else {
			p.textAlign(p.LEFT, p.CENTER);
		}
		
		String text = getText();
		if (!center) {
			text = " " + text;
		}
		p.text(text, getX(), getY() - p.textDescent() / 2, getWidth(), getHeight());
	}
}
