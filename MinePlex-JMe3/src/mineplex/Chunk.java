package mineplex;

import com.jme3.scene.Node;

public class Chunk {
	public int x;
	public int y;
	public int z;
	
	public boolean loaded;
	
	public Dimension world;
	public Node rootNode;
	
	public Node optimized_node;
	public Location location;
	
	public Chunk(int x, int y, int z, Dimension world) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.location = new Location(x, y, z);
		
		this.world = world;
		this.rootNode = new Node();
		this.optimized_node = new Node();
	}
	
	public ArrayList3d<Tile> getTiles() {
		int x_ = x * world.chunks.CHUNK_SIZE;
		int y_ = y * world.chunks.CHUNK_SIZE;
		int z_ = z * world.chunks.CHUNK_SIZE;
		
		ArrayList3d<Tile> tiles = new ArrayList3d<Tile>();
		
		for(int i = x_; i <= x_ + world.chunks.CHUNK_SIZE; i++)
			for(int j = y_; j <= y_ + world.chunks.CHUNK_SIZE; j++)
				for(int k = z_; k <= z_ + world.chunks.CHUNK_SIZE; k++)
					tiles.put(i, j, k, world.getBlock(i, j, k));
		
		return tiles;
	}
}
