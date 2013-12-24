package mineplex;

import java.util.HashMap;

import com.jme3.material.Material;

public class TileRegistry {
	public HashMap<String, Material> materials;
	
	public TileRegistry() {
		materials = new HashMap<String, Material>();
	}
	
	public void registerTile(Tile tile) {
		materials.put(tile.getName(), tile.mat);
	}
	
	public Material getMaterial(Tile tile) {
		return materials.get(tile.getName());
	}
}
