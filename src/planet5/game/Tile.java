package planet5.game;

public class Tile {
	public int color;
	public boolean wall;
	public Building building;

	public Tile(int color, boolean wall) {
		super();
		this.color = color;
		this.wall = wall;
	}
}
