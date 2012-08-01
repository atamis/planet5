package planet5.frames;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import planet5.Main;
import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.config.Globals;
import planet5.framework.Applet;
import planet5.framework.Button;
import planet5.framework.Frame;
import planet5.game.Building;
import planet5.game.Game;
import planet5.game.GameListener;
import planet5.game.GameRenderer;
import planet5.game.gen.CaveGenerator;
import planet5.game.gen.VoronoiPerlinNoiseGenerator;

public class GameFrame extends Frame implements GameListener {
	Game game;
	
	// constructors and initialization
	public GameFrame(Applet parent, int width, int height) {
		super(parent);
		
		GameRenderer.init(p);
		game = new Game(this, p, width, height);
	}

	// updating methods
	@Override
	public void update() {
		game.update();
	}

	@Override
	protected void draw() {
		game.draw();
	}
	
	// interface implementation
	@Override
	public void quit() {
		p.transitionFrame(Main.menuFrame, Main.fullFadeTransition);
	}
	
	// key event handlers
	@Override
	public void keyPressed() {
		game.keyPressed(p.keyCode);
	}
	
	@Override
	public void keyReleased() {
		game.keyReleased(p.keyCode);
	}

	// mouse event handlers
	@Override
	public void mousePressed() {
		int x = p.mouseX, y = p.mouseY;
		if (p.mouseButton == p.LEFT) {
			game.mousePressed(x, y, MouseEvent.BUTTON1);
		} else if (p.mouseButton == p.CENTER) {
			game.mousePressed(x, y, MouseEvent.BUTTON2);
		} else if (p.mouseButton == p.RIGHT) {
			game.mousePressed(x, y, MouseEvent.BUTTON3);
		}
	}
	
	@Override
	public void mouseReleased() {
		int x = p.mouseX, y = p.mouseY;
		if (p.mouseButton == p.LEFT) {
			game.mouseReleased(x, y, MouseEvent.BUTTON1);
		} else if (p.mouseButton == p.CENTER) {
			game.mouseReleased(x, y, MouseEvent.BUTTON2);
		} else if (p.mouseButton == p.RIGHT) {
			game.mouseReleased(x, y, MouseEvent.BUTTON3);
		}
	}

}
