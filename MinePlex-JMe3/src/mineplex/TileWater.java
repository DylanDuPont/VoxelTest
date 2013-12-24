package mineplex;

import com.jme3.math.ColorRGBA;

public class TileWater extends Tile {
	public TileWater(int x, int y, int z, Dimension world) {
		super(ColorRGBA.Blue, x, y, z, world);
	}

	@Override
	public void update() {

	}

	@Override
	public String getName() {
		return "Water";
	}
}
