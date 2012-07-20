package planet5.game;

import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import processing.core.PApplet;

public class Building {
	int type;
	int row, col;
	int width, height;
	
	float hp;
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	
	public Building(int type, int x, int y) {
		this.type = type;
		this.row = y;
		this.col = x;
		this.width = BuildingStats.cols[type];
		this.height = BuildingStats.rows[type];
		// TODO
	}
	
	public void draw(PApplet p, int x, int y) {
		// TODO: use better drawing
		p.stroke(0);
		p.strokeWeight(1);
		p.textFont(Fonts.consolas16);
		p.fill(0xFF808080);
		p.rect(x, y, width * TILE_SIZE, height * TILE_SIZE);
		p.fill(0);
		p.text("" + type, x + 8, y + 8);
	}
}
