package planet5;

import planet5.config.Fonts;
import planet5.frames.AboutFrame;
import planet5.frames.CampaignFrame;
import planet5.frames.GameFrame;
import planet5.frames.MenuFrame;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.framework.FullFadeTransition;
import planet5.framework.Transition;
import processing.core.PApplet;

public class Game extends Applet {
	// frames and transitions
	public static Frame menuFrame;
	public static Frame campaignFrame;
	public static Frame aboutFrame;
	public static Transition fullFadeTransition;
	
	@Override
	public void setup() {
		// set defaults
		smooth();
		size(1024, 768);
		
		// load configuration
		Fonts.load(this);
		
		// load frames and transitions
		menuFrame = new MenuFrame(this);
		campaignFrame = new CampaignFrame(this);
		aboutFrame = new AboutFrame(this);
		fullFadeTransition = new FullFadeTransition(this);
		
		// TODO: change back to menuFrame
		setFrame(campaignFrame);
		
		this.println(color(32, 128, 0, 128));
		println(color(255, 0, 0, 128));
	}

	public static void main(String[] args) {
		// windowed mode
		PApplet.main(new String[] { "planet5.Game" });
		
		// presentation mode
		// PApplet.main(new String[] { "--present", "planet5.Game" });
	}
}
