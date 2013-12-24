package mineplex;

import java.util.Iterator;

import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.scene.Node;

public class ChunkManager {
	private ArrayList3d<Chunk> chunks;
	private ArrayList3d<Chunk> loadedChunks;
	private ArrayList3d<Chunk> unloadedChunks;
	private Dimension world;
	
	public int CHUNK_SIZE = 32;
	
	public ChunkManager(Dimension world, ArrayList3d<Chunk> chunks) {
		this.chunks = chunks;
		this.unloadedChunks = chunks;
		this.loadedChunks = new ArrayList3d<Chunk>();
		this.world = world;
	}
	
	public ChunkManager(Dimension world) {
		this(world, new ArrayList3d<Chunk>());
	}
	
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x, y, z);
	}
	
	public void addChunk(int x, int y, int z) {
		if(chunks.contains(x, y, z))
			return;
		
		chunks.put(x, y, z, new Chunk(x, y, z, world));
		unloadChunk(new Location(x, y, z));
	}
	
	public void mergeVoxels(Chunk chunk) {
		System.out.println(chunk.rootNode.getTriangleCount() + " / " + chunk.rootNode.getVertexCount());
		chunk.optimized_node = (Node) GeometryBatchFactory.optimize(chunk.rootNode);
		//chunk.optimized_node.batch();
		
		System.out.println("to:" + chunk.optimized_node.getTriangleCount() + " / " + chunk.optimized_node.getVertexCount());
	}
	
	public void deleteChunk(Location l) {
		System.out.println("DELETING CHUNK...");
		System.out.println("(may lag, must manually recalculate quads"); //FIXME: recalculate quads
		
		int x = l.x;
		int y = l.y;
		int z = l.z;
		
		Chunk chunk = getChunk(l.x, l.y, l.z);
		
		Iterator<Tile> it = chunk.getTiles().getIteratable();
		while(it.hasNext()) {
			Tile tile = it.next();
			if(tile != null) {
				world.deleteBlock(tile);
				it.remove();
			}
		}
		
		chunks.remove(x, y, z);
		unloadedChunks.remove(x, y, z);
		loadedChunks.remove(x, y, z);
		
		System.out.println("COMPLETE");
	}

	public void addChunk(Location l) {
		addChunk(l.x, l.y, l.z);
	}
	
	public void recalculateChunk(Location l) {
		Chunk chunk = getChunk(l.x, l.y, l.z);
		
		if(chunk == null)
			return;
		
		Iterator<Tile> it = chunk.getTiles().getIteratable();
		while(it.hasNext()) {
			Tile tile = it.next();
			if(tile != null)
				tile.recalculateQuads();
		}
		mergeVoxels(chunk);
	}
	
	public void updateChunk(Location l) {
		Chunk chunk = getChunk(l.x, l.y, l.z);
		
		if(chunk == null)
			return;
		
		Iterator<Tile> it = chunk.getTiles().getIteratable();
		while(it.hasNext()) {
			Tile tile = it.next();
			if(tile != null)
				tile._();
		}
	}
	
	public void updateChunks() {
		Iterator<Chunk> it = loadedChunks.getIteratable();
		while(it.hasNext()) {
			Chunk c = it.next();
			updateChunk(new Location(c.x, c.y, c.z));
		}
	}
	
	public void unloadChunk(Location l) {
		Chunk chunk = getChunk(l.x, l.y, l.z);
		if(!chunk.loaded)
			return;
		
		chunk.loaded = false;
		
		//int blocks = 0;
		
		Iterator<Tile> it = chunk.getTiles().getIteratable();
		while(it.hasNext()) {
			Tile tile = it.next();
			if(tile != null) {
				tile.setLoaded(false);
				//blocks++;
			}
		}

		if(chunk.optimized_node.getParent() == Main.getInstance().getRootNode())
			chunk.optimized_node.removeFromParent();
		
		//System.out.println("Unloaded chunk with " + blocks + " blocks at " + l.x + " : " + l.y + " : " + l.z);
		
		if(!unloadedChunks.contains(l.x, l.y, l.z)) {
			if(loadedChunks.contains(l.x, l.y, l.z))
				loadedChunks.remove(l.x, l.y, l.z);
			
			unloadedChunks.put(l.x, l.y, l.z, chunk);
		}
	}
	
	public void loadChunk(Location l) {
		Chunk chunk = getChunk(l.x, l.y, l.z);
		if(chunk == null)
			return;
		else if(chunk.loaded)
			return;
		
		chunk.loaded = true;
		
		int blocks = 0;
		
		Iterator<Tile> it = chunk.getTiles().getIteratable();
		while(it.hasNext()) {
			Tile tile = it.next();
			if(tile != null) {
				tile.setLoaded(true);
				blocks++;
			}
		}

		if(chunk.optimized_node.getParent() != Main.getInstance().getRootNode()) {
			System.out.println("Loading chunk: " + l.x + " : " + l.y + " : " + l.z);
			mergeVoxels(chunk);
			Main.getInstance().getRootNode().attachChild(chunk.optimized_node);
			System.out.println("Loaded chunk with " + blocks + " blocks");
		}
		
		if(!loadedChunks.contains(l.x, l.y, l.z)) {
			if(unloadedChunks.contains(l.x, l.y, l.z))
				unloadedChunks.remove(l.x, l.y, l.z);
			
			loadedChunks.put(l.x, l.y, l.z, chunk);
		}
	}
	
	public void update() {
		Iterator<Chunk> it = loadedChunks.getIteratable();
		while(it.hasNext()) {
			Chunk c = it.next();
			
			if(unloadedChunks.contains(c.x, c.y, c.z)){
				System.out.println("Silly chunk tried to be loaded and unloaded at the same time (" + c.x + ", " + c.y + ", " + c.z + ")");
				it.remove();
			}
		}
	}
	
	public void unloadedAll() { //dont add a load all, cuz i might add a infinite world, and if i do then hello future me (11:24AM 12/23/2013 incase u decide to keep this comment)
		Iterator<Chunk> it = unloadedChunks.getIteratable();
		while(it.hasNext()) {
			Chunk c = it.next();
			unloadChunk(new Location(c.x, c.y, c.z));
		}
	}
}
