package planet5;

import planet5.config.BaseBuildingStats;
import planet5.config.BuildingStats;
import planet5.config.Fonts;
import planet5.frames.AboutFrame;
import planet5.frames.CampaignFrame;
import planet5.frames.GameFrame;
import planet5.frames.MenuFrame;
import planet5.frames.SettingsFrame;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.framework.FullFadeTransition;
import planet5.framework.Transition;
import processing.core.PApplet;

public class Main extends Applet {
	public static Main instance;
	
	// frames and transitions
	public static MenuFrame menuFrame;
	public static CampaignFrame campaignFrame;
	public static SettingsFrame settingsFrame;
	public static AboutFrame aboutFrame;
	public static FullFadeTransition fullFadeTransition;
	
	@Override
	public void setup() {
		// set defaults
		smooth();
		size(1024, 768);
		instance = this;
		
		// load configuration
		Fonts.load(this);
		
		// load frames and transitions
		menuFrame = new MenuFrame(this);
		campaignFrame = new CampaignFrame(this);
		settingsFrame = new SettingsFrame(this);
		aboutFrame = new AboutFrame(this);
		fullFadeTransition = new FullFadeTransition(this);
		
		// TODO: change back to menuFrame
		setFrame(campaignFrame);
	}

	public static void main(String[] args) {
		// windowed mode
		PApplet.main(new String[] { "planet5.Main" });
		
		// presentation mode
		// PApplet.main(new String[] { "--present", "planet5.Main" });
	}
}
