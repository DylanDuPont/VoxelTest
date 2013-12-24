package mineplex;

import com.jme3.math.ColorRGBA;

public class TileGrass extends Tile {
	public TileGrass(int x, int y, int z, Dimension world) {
		super(ColorRGBA.Green, x, y, z, world);
	}

	@Override
	public void update() {
		
	}

	@Override
	public String getName() {
		return "Grass";
	}
}
