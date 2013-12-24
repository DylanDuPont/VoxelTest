package mineplex;

import com.jme3.math.ColorRGBA;

public class TileStone extends Tile {
	public TileStone(int x, int y, int z, Dimension world) {
		super(ColorRGBA.Gray, x, y, z, world);
	}

	@Override
	public void update() {

	}

	@Override
	public String getName() {
		return "Stone";
	}
}
