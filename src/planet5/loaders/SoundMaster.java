package planet5.loaders;

import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;

public class SoundMaster {
	public static AudioPlayer theme_song;
	public static AudioSample mortar_fire;
	public static AudioSample mortar_explosion;
	public static AudioSample laser_fire;
	
	public static void load(Minim m){
		theme_song = m.loadFile("Eric_Skiff_-_01_-_A_Night_Of_Dizzy_Spells.mp3");
		mortar_fire = m.loadSample("mortar_fire.wav");
		mortar_explosion = m.loadSample("mortar_fire.wav");
		laser_fire = m.loadSample("laser_fire.wav");
	}
	
	public static void mute() {
		theme_song.mute();
		mortar_fire.mute();
		mortar_explosion.mute();
		laser_fire.mute();
	}
	
	public static void unmute() {
		theme_song.unmute();
		mortar_fire.unmute();
		mortar_explosion.unmute();
		laser_fire.unmute();
	}
}
