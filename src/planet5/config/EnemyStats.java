package planet5.config;

public final class EnemyStats {

	/*
	 * Enemies
	 * #0: Standard. Normal.
	 * #1: Heavy armor. Slow, but lots of health and damage.
	 * #3: Fast. Very fast, lots of damage, but very little health.
	 */
	// HP. Damage until it dies.
	public static int[] hp = {30, 60, 20};
	// Damage per second
	public static int[] damage = {3, 6, 4};
	// Speed per second.
	public static int[] speed = {10, 5, 20};
}
