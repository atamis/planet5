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
	//private static int[] hp = {30*16, 60*16, 20*16};
	// Damage per second
	private static int[] damage = {3, 6, 4};
	// Speed per second.
	private static int[] speed = {10, 5, 20};
	private static int[] spawn = {3, 2, 1};
	
	public static float hp_delta = 0.001f;
	public static float damage_delta = 0.0001f;
	public static float speed_delta = 0.00001f;
	public static float spawn_delta = 0.00001f;
	
	public static int getHP(int i) {
		return (int) (hp[i] + hp_delta * EnemyLevel.level());
	}
	
	public static int getDamage(int i) {
		return (int) (damage[i] + damage_delta * EnemyLevel.level());
	}
	
	public static int getSpeed(int i) {
		return (int) (speed[i] + speed_delta * EnemyLevel.level());
	}
	
	public static int getSpawn(int i) {
		return (int) (spawn[i] + spawn_delta * EnemyLevel.level());
	}

	public static float getSpawn() {
		return 1 + spawn_delta * EnemyLevel.level();
	}
}
