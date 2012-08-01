package planet5.config;

public class EnemyLevel {
	private static float level = 0;
	
	public static float level() {
		// TODO: reset
		// return level;
		return level;
	}
	
	public static float add(float x) {
		level += x;
		
		return level;
	}
	
	public static void reset() {
		level = 0;
	}
}
