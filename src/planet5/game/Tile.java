package planet5.game;

public class Tile {
	int color;
	boolean wall;
	Building building;
	byte lighting;

	public Tile(int color, boolean wall) {
		super();
		this.color = color;
		this.wall = wall;
	}
}
