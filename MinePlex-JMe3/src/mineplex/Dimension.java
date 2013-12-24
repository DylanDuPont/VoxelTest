package mineplex;

import java.util.ArrayList;
import java.util.Iterator;

import com.jme3.math.Vector3f;
import com.turtlesort.hmapgen.HeightMapGenerator;

public class Dimension {
	public ArrayList3d<Tile> tiles;
	public Main main;
	
	public ChunkManager chunks;
	
	public int updatesPassed;
	public int updatesUntilLoad;
	
	public ArrayList<Location> chunkQueue;
	public boolean generated;
	
	public Dimension(Main main) {
		this.tiles = new ArrayList3d<Tile>();
		this.main = main;
		this.chunks = new ChunkManager(this);
		
		this.chunkQueue = new ArrayList<Location>();
		
		updatesPassed = 0;
		updatesUntilLoad = 0;
		generated = false;
	}
	
	public void update() {
		chunks.updateChunks();
		chunks.update();
		
		if(updatesUntilLoad == updatesPassed) {
			Vector3f l = main.getCamera().getLocation();
			
			int x = (int) (l.x - (l.x % chunks.CHUNK_SIZE)) / chunks.CHUNK_SIZE;
			int y = (int) (l.y - (l.y % chunks.CHUNK_SIZE)) / chunks.CHUNK_SIZE;
			int z = (int) (l.z - (l.z % chunks.CHUNK_SIZE)) / chunks.CHUNK_SIZE;
			
			chunks.loadChunk(new Location(x, y, z));
			
			ArrayList<Location> calculated = new ArrayList<Location>();
			
			for(Location lololol : chunkQueue)
				if(!calculated.contains(lololol)) {
					chunks.recalculateChunk(lololol);
					calculated.add(lololol);
				}
			
			chunkQueue.clear();
			
			//System.out.println(x + " : " + y + " : " + z);
			//System.out.println(l.x + " : " + l.y + " : " + l.z);
			
			updatesUntilLoad = updatesPassed+5;
		}
		
		updatesPassed++;
	}
	
	public void placeBlock(Tile tile) {
		if(generated)
			chunkQueue.add(tile.getChunk().location);
		
		if(tiles.get(tile.x, tile.y, tile.z) != null)
			deleteBlock(tiles.get(tile.x, tile.y, tile.z));
		
		tile.setLoaded(tile.getChunk().loaded);
		tiles.put(tile.x, tile.y, tile.z, tile);
	}
	
	public void deleteBlock(Tile tile) {
		if(generated)
			chunkQueue.add(tile.getChunk().location);
		
		tile.setLoaded(false);
		tile.delete();
		tiles.remove(tile.x, tile.y, tile.z);
		tile = null;
	}
	
	public void generateLevel() {
		System.out.println("Generating...");

		System.out.println("Creating heightmap...");
		HeightMapGenerator g = new HeightMapGenerator();
		g.setSize(128,128);
		
		double[][] data = g.generate();
		System.out.println("Creating heightmap... Done!");
		System.out.println("Creating world using heightmap...");

		int intensity = 1; //anything from 1-16
        int ocean_level = 4;
		
		System.out.println("Placing surface...");
        for(int i = 0; i < data.length;i++) {
            for(int j = 0; j < data[0].length;j++) {
                int height = (int)(((double)data[i][j]) * (10^intensity));
                System.out.print(height);
                System.out.print(",");
            	
                if(height == ocean_level)
                	placeBlock(new TileSand(i, height, j, this));
                else
                	placeBlock(new TileGrass(i, height, j, this));
            }
            System.out.println();
        }
        
        System.out.println("Placing liquids...");
        for(int i = 0; i < data.length;i++) {
            for(int j = 0; j < data[0].length;j++) {
            	if(getBlock(i, ocean_level, j) == null)
            		placeBlock(new TileWater(i, ocean_level, j, this));
            }
        }
		
        generated = true;
		System.out.println("Done!");
		recalculateQuads();
	}
	
	public void recalculateQuads() {
		System.out.println("Calculating quads...");
		
		int quadsRemoved = 0;
		
		Iterator<Tile> it = tiles.getIteratable();
		while(it.hasNext()) {
			Tile tile = it.next();
			
			quadsRemoved+=tile.recalculateQuads();
		}
		
		System.out.println("Done! " + quadsRemoved + " removed");
	}

	public Tile getBlock(int x, int y, int z) {
		if(tiles.contains(x, y, z))
			return tiles.get(x, y, z);
		else
			return null;
	}
}
