package planet5;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import planet5.config.BaseBuildingStats;
import planet5.config.BuildingStats;
import planet5.frames.AboutFrame;
import planet5.frames.CampaignFrame;
import planet5.frames.GameFrame;
import planet5.frames.LevelSelectFrame;
import planet5.frames.MenuFrame;
import planet5.frames.SettingsFrame;
import planet5.frames.SurvivalFrame;
import planet5.framework.Applet;
import planet5.framework.Frame;
import planet5.framework.FullFadeTransition;
import planet5.framework.Transition;
import planet5.gfx.ParticleTestFrame;
import planet5.loaders.Fonts;
import planet5.loaders.SoundMaster;
import processing.core.PApplet;

public class Main extends Applet {
	public static Main instance;
	public static Minim minim;

	// frames and transitions
	public static MenuFrame menuFrame;
	public static CampaignFrame campaignFrame;
	public static SettingsFrame settingsFrame;
	public static AboutFrame aboutFrame;
	public static FullFadeTransition fullFadeTransition;
	public static LevelSelectFrame levelSelectFrame;
	public static SurvivalFrame survivalFrame;
	public static ParticleTestFrame test_frame;

	@Override
	public void setup() {
		// set defaults
		minim = new Minim(this);
		SoundMaster.load(minim);
		
		SoundMaster.theme_song.loop();

		smooth();
		size(1024, 768);
		instance = this;

		// load configuration
		Fonts.load(this);

		// load frames and transitions
		menuFrame = new MenuFrame(this);
		settingsFrame = new SettingsFrame(this);
		aboutFrame = new AboutFrame(this);
		fullFadeTransition = new FullFadeTransition(this);
		levelSelectFrame = new LevelSelectFrame(this);
		survivalFrame = new SurvivalFrame(this);
		test_frame = new ParticleTestFrame(this);

		setFrame(menuFrame);
	}

	public static void main(String[] args) {
		// windowed mode
		PApplet.main(new String[] { "planet5.Main" });

		// presentation mode
		// PApplet.main(new String[] { "--present", "planet5.Main" });
	}
}
