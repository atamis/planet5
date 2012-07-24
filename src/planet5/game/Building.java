package planet5.game;

import planet5.Game;
import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import processing.core.PApplet;

public class Building {
	public static final int TILE_SIZE = Globals.TILE_SIZE;
	
	//public static final int 
	
	int type;
	int row, col;
	int width, height;
	
	int hp;
	int maxHp;
	int lastFireTime = 0;
	
	boolean powered = false;
	Building powerSource = null;
	
	public Building(int type, int x, int y) {
		this.type = type;
		this.row = y;
		this.col = x;
		this.width = BuildingStats.cols[type];
		this.height = BuildingStats.rows[type];
		maxHp = BuildingStats.healths[type];
		hp = maxHp;
	}
	
	public void draw(PApplet p, int x, int y, int selectedTime) {
		final int hpHeight = 8;
		
		p.noStroke();
		
		// background
		p.stroke(0xFF404040);
		p.strokeWeight(1);
		p.textFont(Fonts.consolas16);
		p.fill(0xFF808080);
		
		p.rect(x, y + hpHeight, width * TILE_SIZE - 1, height * TILE_SIZE - hpHeight - 1);
		if (selectedTime != -1) {
			int alpha = (selectedTime / 2) % 511;
			if (alpha > 255)// TODO: fine tuning
				alpha = 511 - alpha;
			p.fill(p.color(32, 128, 0, alpha / 2 + 128));
			p.rect(x, y + hpHeight, width * TILE_SIZE - 1, height * TILE_SIZE - hpHeight - 1);
			p.textAlign(p.CENTER, p.CENTER);
			p.textSize(12);
			p.fill(0);
			p.text("sell", x, y, width * TILE_SIZE - 1, height * TILE_SIZE - 1);
		}
		
		// hp bar background
		p.noStroke();
		p.fill(0);
		p.rect(x, y, width * TILE_SIZE, hpHeight);
		
		// hp bar
		int fill = (width * TILE_SIZE - 2) * hp / maxHp;
		p.fill(0xFFC00000);
		p.rect(x + 1, y + 1, fill, hpHeight - 2);
		
		// text
		p.textAlign(p.LEFT, p.TOP);
		p.fill(0);
		p.textSize(16);
		//p.text("" + type, x + 8, y + 8);
		
		// if building is not powered draw the sign
		// otherwise draw the connection
		if (!powered) {
			// TODO: optimize
			p.text("!", x + 8, y + 8);
			
		} else if (powerSource != null) {
			
		}
	}
}
