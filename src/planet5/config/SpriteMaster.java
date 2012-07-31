package planet5.config;

import processing.core.PApplet;
import processing.core.PImage;

public class SpriteMaster {
	private final static int TILE_SIZE = Globals.TILE_SIZE;

	public PImage raw_building_sprite;

	
	// 0 - main base
	// 1 - relay
	// 2 - farm
	// 3 - capacitor
	// 4 - lab
	// 5 - laser
	// 6 - mortar
	public PImage[] building_sprites;
	public PImage laser_gun;
	public PImage mortar_gun;
	public PImage hero;
	public PImage enemy;
	public PImage mortar_bullet;
	public PImage blood_splat;

	public SpriteMaster(PApplet p) {
		int width, height;
		raw_building_sprite = p.loadImage(Globals.SPRITE_IMAGE);
		building_sprites = new PImage[BuildingStats.names.length];
		
		// base
		width = BuildingStats.cols[0] * TILE_SIZE;
		height = BuildingStats.rows[0] * TILE_SIZE;
		building_sprites[0] = p.createImage(width, height, p.ARGB);
		building_sprites[0].copy(raw_building_sprite, 0, 0, width, height, 0, 0, width, height);
		
		// relay
		width = BuildingStats.cols[1] * TILE_SIZE;
		height = BuildingStats.rows[1] * TILE_SIZE;
		building_sprites[1] = p.createImage(width, height, p.ARGB);
		building_sprites[1].copy(raw_building_sprite, 3*TILE_SIZE, 2*TILE_SIZE, width, height, 0, 0, width, height);

		// farm
		width = BuildingStats.cols[2] * TILE_SIZE;
		height = BuildingStats.rows[2] * TILE_SIZE;
		building_sprites[2] = p.createImage(width, height, p.ARGB);
		building_sprites[2].copy(raw_building_sprite, 4 * TILE_SIZE, 2*TILE_SIZE, width, height, 0, 0, width, height);

		// capacitor
		width = BuildingStats.cols[3] * TILE_SIZE;
		height = BuildingStats.rows[3] * TILE_SIZE;
		building_sprites[3] = p.createImage(width, height, p.ARGB);
		building_sprites[3].copy(raw_building_sprite, 5*TILE_SIZE, 2*TILE_SIZE, width, height, 0, 0, width, height);

		// lab
		width = BuildingStats.cols[4] * TILE_SIZE;
		height = BuildingStats.rows[4] * TILE_SIZE;
		building_sprites[4] = p.createImage(width, height, p.ARGB);
		building_sprites[4].copy(raw_building_sprite, 3 * TILE_SIZE, 0, width, height, 0, 0, width, height);

		// laser
		width = BuildingStats.cols[5] * TILE_SIZE;
		height = BuildingStats.rows[5] * TILE_SIZE;
		building_sprites[5] = p.createImage(width, height, p.ARGB);
		building_sprites[5].copy(raw_building_sprite, 6*TILE_SIZE, 2*TILE_SIZE, width, height, 0, 0, width, height);

		// mortar
		width = BuildingStats.cols[6] * TILE_SIZE;
		height = BuildingStats.rows[6] * TILE_SIZE;
		building_sprites[6] = p.createImage(width, height, p.ARGB);
		building_sprites[6].copy(raw_building_sprite, 5 * TILE_SIZE, 0, width, height, 0, 0, width, height);

		
		// laser gun
		width = BuildingStats.cols[5] * TILE_SIZE;
		height = BuildingStats.rows[5] * TILE_SIZE;
		laser_gun = p.createImage(width, height, p.ARGB);
		laser_gun.copy(raw_building_sprite, 0, 3 * TILE_SIZE, width, height, 0, 0, width, height);
		
		// mortar gun
		width = BuildingStats.cols[6] * TILE_SIZE;
		height = BuildingStats.rows[6] * TILE_SIZE;
		mortar_gun = p.createImage(width, height, p.ARGB);
		mortar_gun.copy(raw_building_sprite, 1 * TILE_SIZE, 3 * TILE_SIZE, width, height, 0, 0, width, height);
		
		// hero
		width = 1 * TILE_SIZE;
		height = 1 * TILE_SIZE;
		hero = p.createImage(width, height, p.ARGB);
		hero.copy(raw_building_sprite, 3 * TILE_SIZE, 3 * TILE_SIZE, width, height, 0, 0, width, height);
		
		// enemy
		width = (int) (0.5 * TILE_SIZE);
		height = (int) (0.5 * TILE_SIZE);
		enemy = p.createImage(width, height, p.ARGB);
		enemy.copy(raw_building_sprite, 4 * TILE_SIZE, 3 * TILE_SIZE, width, height, 0, 0, width, height);

		width = (int) (0.5 * TILE_SIZE);
		height = (int) (0.5 * TILE_SIZE);
		mortar_bullet = p.createImage(width, height, p.ARGB);
		mortar_bullet.copy(raw_building_sprite, (int) (4.6 * TILE_SIZE), 3 * TILE_SIZE, width, height, 0, 0, width, height);

		width = (int) (0.5 * TILE_SIZE);
		height = (int) (0.5 * TILE_SIZE);
		blood_splat = p.createImage(width, height, p.ARGB);
		blood_splat.copy(raw_building_sprite, (int) (5 * TILE_SIZE), 3 * TILE_SIZE, width, height, 0, 0, width, height);


	}

	public static PImage carvePImage(PApplet p, PImage i, int x, int y,
			int width, int height) {
		PImage result = p.createImage(width, height, p.ARGB);
		i.loadPixels();
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				int tx = x + w, ty = y + h;
				p.set(w, h, i.get(tx, ty));
			}

		}

		return result;
	}

	private static SpriteMaster instance = null;

	public static SpriteMaster instance(PApplet p) {
		if (instance == null)
			instance = new SpriteMaster(p);
		return instance;
	}
}
