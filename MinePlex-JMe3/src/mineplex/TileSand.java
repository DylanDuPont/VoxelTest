package mineplex;

import com.jme3.math.ColorRGBA;

public class TileSand extends Tile {
	public TileSand(int x, int y, int z, Dimension world) {
		super(ColorRGBA.Yellow, x, y, z, world);
	}

	@Override
	public void update() {

	}

	@Override
	public String getName() {
		return "Sand";
	}
}
