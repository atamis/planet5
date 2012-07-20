package planet5.config;

public final class EnemyStats {

	/*
	 * Enemies
	 * #0: Standard. Normal.
	 * #1: Heavy armor. Slow, but lots of health and damage.
	 * #3: Fast. Very fast, lots of damage, but very little health.
	 */
	// HP. Damage until it dies.
	static float[] hp = {30, 60, 20};
	// Damage per second
	static float[] damage = {150, 300, 200};
	// Speed per second.
	static float[] speed = {1, 0.5f, 4};
}
