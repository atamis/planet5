package planet5.frames;

import java.awt.Rectangle;

import planet5.Main;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.loaders.Fonts;

public class InstructionsFrame extends Frame {
	public InstructionsFrame(Applet parent) {
		super(parent);

		// add back button
		addButton(new MenuButton(new Rectangle(32, 32, 110, 63), "Back",
				Fonts.consolas32, true));
	}

	@Override
	protected void draw() {
		p.background(0);

		p.noStroke();
		p.fill(32);
		p.rect(32, 96, p.width - 32 - 32, p.height - 64 - 32 - 32);

		p.rect(32 + 111, 32, p.width - 32 - 32 - 111, 63);

		p.fill(255);
		p.textAlign(p.CENTER);
		p.textFont(Fonts.consolas32);
		p.text("Instructions", 32, 32 + (63 - p.textDescent() - p.textAscent()) / 2,
				p.width - 32 - 32, 63);

		p.fill(255);
		p.textFont(Fonts.consolas16);
		p.textAlign(p.LEFT, p.CENTER);
		p.textLeading(p.textAscent() + p.textDescent());
		p.text("[WASD] to move the hero\n" +
				"[ESC] or [SPACE] to cancel a selection\n" +
				"[123456] to build a building\n" +
				"RIGHT CLICK to shoot\n" +
				"\n" +
				"1 - relay: relays energy field. buildings need energy to work\n" +
				"2 - solar farm: produces solar energy during the day\n" +
				"3 - capacitor: stores energy\n" +
				"4 - science lab: gives upgrades\n" +
				"5 - laser: single target constant damage\n" +
				"6 - mortar: rocket AOE damage\n" +
				"\n" +
				"Use the buildings at your disposal to survive the ever growing hordes of " +
				"aliens. If you or your main base dies, you lose. Enemies will grow in health, " +
				"damage, speed, and number. They will destroy everything in their path to reach " +
				"your main base.\n" +
				"\n" +
				"At the science lab, you can improve your buildings in a variety of ways.\n" +
				"\n" +
				"1 - HP - health of buildings\n" +
				"2 - GEN - energy generation\n" +
				"3 - CAP - energy capacitance\n" +
				"4 - DMG - turret damage\n" +
				"5 - ??? - mystery upgrade\n" +
				"\n" +
				"Monsters too will continually improve. Monsters spawn in darkness only, 8pm to " +
				"7:59 am. During the day, monsters that spawned during the night may still " +
				"attack, but no new monsters will spawn.\n" +
				"\n" +
				"To win, there must be no monsters on the map at midnight.",
				32 + 16, 96 + 16, p.width - 32 - 32 - 32, p.height - 32 - 32
						- 32 - 96);
	}

	@Override
	public void buttonClicked(String command) {
		if (command.equals("Back")) {
			p.transitionFrame(Main.menuFrame, Main.fullFadeTransition);
		}
	}
}
