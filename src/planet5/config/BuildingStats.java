package planet5.config;

public class BuildingStats {

	// All
	public static String[] names = { "Main Base", "Relay", "Solar Farm",
			"Capacitor", "Science Lab", "Laser Turret", "Mortar" };
	public static String[] descriptions = {
			"The Main Base forms the core of your network and if it falls, you lose.",
			"The Relay projects an energy field and supplies power to other buildings.",
			"The Solar Farm harvests solar energy for your network. Doesn't generate power at night.",
			"The Capacitor stores energy for use during the night. Increases your maximum power pool.",
			"The Science Lab upgrades a variety of buildings and weapons you use.",
			"The Laser Turret provides high damage short range defense. Fires continuously.",
			"The Mortar fires explosive energy that damage enemies in a wide radius. Recharges between shots." };
	public static int[] costs = { 0, 20, 30, 100, 500, 200, 500 };
	public static int[] rows = { 3, 1, 1, 1, 2, 1, 2 };
	public static int[] cols = { 3, 1, 1, 1, 2, 1, 2 };
	public static int[] light = { 256, 256, 64, 64, 256, 96, 64 };
	private static float[] healths = { 1500, 100, 100, 100, 500, 1000, 350 };
	private static float[] cap = { 4800, 0, 0, 1000, 0, 0, 0 };
	private static float[] gen = { 0.08f, 0, 0.08f, 0, 0, 0, 0 };
	private static float[] draw = { 0, 0, 0, 0, 10, 0.5f, 0 };
	private static float[] damage = { 0, 0, 0, 0, 0, 30, 200 };

	public static float health_delta = 0.0001f;
	public static float cap_delta = 0.0001f;
	public static float gen_delta = 0.000001f;
	public static float damage_delta = 0.001f;
	public static float draw_delta = 0.01f;

	public static float getHealth(int i) {
		return healths[i] + health_delta * UpgradeStats.level[Upgrades.health];
	}

	public static float getCap(int i) {
		if (cap[i] == 0)
			return 0;
		else
			return cap[i] + cap_delta * UpgradeStats.level[Upgrades.cap];
	}

	public static float getGen(int i) {
		if (gen[i] == 0)
			return 0;
		else
			return gen[i] + gen_delta * UpgradeStats.level[Upgrades.gen];
	}

	public static float getDamage(int i) {
		if (i == 5 || i == 6)
			return damage[i] + damage_delta
					* UpgradeStats.level[Upgrades.damage];
		else
			return 0;
	}

	public static float getDraw(int i) {
		if (i == 4 || i == 5 || i == 6)
			return draw[i] + draw_delta * UpgradeStats.level[Upgrades.draw];
		else
			return 0;

	}

	// Main Base. #0
	static float base_health = 1500f;
	static float base_cost = 0;
	static float base_rows = 3;
	static float base_cols = 3;
	static float base_light = 8;
	static float base_capacitance = 4800;
	static float base_energy_gen = 20;
	static float base_draw = 0;

	// Relay. #1
	static float relay_health = 100;
	static float relay_cost = 20;
	static float relay_rows = 1;
	static float relay_cols = 1;
	static float relay_light = 2;
	static float relay_capacitance = 0;
	static float relay_energy_gen = 0;
	static float relay_draw = 0;

	// Solar Farm. #2
	static float farm_health = 100;
	static float farm_cost = 30;
	static float farm_rows = 1;
	static float farm_cols = 1;
	static float farm_light = 2;
	static float farm_capacitance = 0;
	static float farm_energy_gen = 50;
	static float farm_draw = 0;

	// Capacitor. #3
	static float capacitor_health = 100;
	static float capacitor_cost = 100;
	static float capacitor_rows = 1;
	static float capacitor_cols = 1;
	static float capacitor_light = 2;
	static float capacitor_capacitance = 500;
	static float capacitor_energy_gen = 0;
	static float capacitor_draw = 0;

	// Science Lab. #4
	static float lab_health = 500;
	static float lab_cost = 500;
	static float lab_rows = 2;
	static float lab_cols = 2;
	static float lab_light = 8;
	static float lab_capacitance = 0;
	static float lab_energy_gen = 0;
	static float lab_draw = 10;

	// Laser Turret. #5
	static float laser_health = 500;
	static float laser_cost = 350;
	static float laser_rows = 1;
	static float laser_cols = 1;
	static float laser_light = 3;
	static float laser_capacitance = 0;
	static float laser_energy_gen = 0;
	static float laser_damage = 4;
	static float laser_draw = 10;

	// Mortar. #6
	static float mortar_health = 350;
	static float mortar_cost = 500;
	static float mortar_rows = 2;
	static float mortar_cols = 2;
	static float mortar_light = 2;
	static float mortar_capacitance = 0;
	static float mortar_energy_gen = 0;
	static float mortar_draw = 4;
	static float mortar_fire_cost = 20;
	static float mortar_damage = 100;

}
