package planet5.config;

public final class EnemyStats {

	/*
	 * Enemies
	 * #0: Standard. Normal.
	 * #1: Heavy armor. Slow, but lots of health and damage.
	 * #3: Fast. Very fast, lots of damage, but very little health.
	 */
	// HP. Damage until it dies.
	private static int[] hp = {30, 60, 20};
	// Damage per second
	private static int[] damage = {3, 6, 4};
	// Speed per second.
	private static int[] speed = {10, 5, 20};
	
	public static float hp_delta = 0.001f;
	public static float damage_delta = 0.001f;
	public static float speed_delta = 0.0001f;
	
	public static int getHP(int i) {
		return (int) (hp[i] + hp_delta * EnemyLevel.level);
	}
	
	public static int getDamage(int i) {
		return (int) (damage[i] + damage_delta * EnemyLevel.level);
	}
	
	public static int getSpeed(int i) {
		return (int) (speed[i] + speed_delta * EnemyLevel.level);
	}
}
