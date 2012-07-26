package planet5.config;

public class UpgradeStats {
	// health, gen, cap, damage, draw
	public static float level[] = {0, 0, 0, 0, 0};

	public static void reset() {
		for(int i = 0; i < level.length; i++)
			level[i] = 0;
		
	}
}
