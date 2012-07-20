package planet5.config;

public final class BuildingStats {

	// All
	public static float[] healths	= {1500, 100, 100, 100,	500, 500, 350};
	public static float[] costs	= {0,    20,  30,  100,	500, 350, 500};
	public static int[] rows		= {3,    1,   1,   1,	2,   1,   2};
	public static int[] cols		= {3,    1,   1,   1,	2,   1,   2};
	public static float[] light	= {8,    2,   2,   2,	8,   3,   2};
	public static float[] cap		= {4800, 0,   0,   500,	0,   0,   0};
	public static float[] gen		= {20,   0,   50,  0,	0,   0,   0};
	public static float[] draw		= {0,    0,   0,   0,	10,  10,   4};
	
	
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
