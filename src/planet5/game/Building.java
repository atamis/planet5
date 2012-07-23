package planet5.game;

import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import processing.core.PApplet;

public class Building {
	int type;
	int row, col;
	int width, height;
	
	int hp;
	int maxHp;
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	
	public Building(int type, int x, int y) {
		this.type = type;
		this.row = y;
		this.col = x;
		this.width = BuildingStats.cols[type];
		this.height = BuildingStats.rows[type];
		maxHp = BuildingStats.healths[type];
		hp = maxHp;
	}
	
	public void draw(PApplet p, int x, int y) {
		final int hpHeight = 8;
		
		p.noStroke();
		
		// hero background
		p.stroke(0xFF404040);
		p.strokeWeight(1);
		p.textFont(Fonts.consolas16);
		p.textAlign(p.LEFT, p.TOP);
		p.fill(0xFF808080);
		p.rect(x, y + hpHeight, width * TILE_SIZE - 1, height * TILE_SIZE - hpHeight - 1);
		
		// hp bar background
		p.noStroke();
		p.fill(0);
		p.rect(x, y, width * TILE_SIZE, hpHeight);
		
		// hp bar
		int fill = (width * TILE_SIZE - 2) * hp / maxHp;
		p.fill(0xFFC00000);
		p.rect(x + 1, y + 1, fill, hpHeight - 2);
		
		// text
		p.fill(0);
		p.text("" + type, x + 8, y + 8);
	}
}
