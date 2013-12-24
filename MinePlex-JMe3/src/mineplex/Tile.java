package mineplex;

import java.util.HashMap;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public abstract class Tile {
	public int x;
	public int y;
	public int z;
	
	public Dimension world;
	
	public Material mat;
	private Main m;
	
	public int FACE_UP = 1;
	public int FACE_DOWN = 2;
	public int FACE_NORTH = 3;
	public int FACE_EAST = 4;
	public int FACE_SOUTH = 5;
	public int FACE_WEST = 6;
	
	private HashMap<Integer, Geometry> quads;
	
	private float size;
	
	private int quadsRemoved;
	private boolean loaded;
	boolean optimized;
	
	public Tile(ColorRGBA color, int x, int y, int z, Dimension world) {
		m = Main.getInstance();

        mat = m.tileRegistry.getMaterial(this);
        if(mat == null) {
            mat = new Material(m.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Ambient", color);
            mat.setColor("Diffuse", color);
            
            return;
        }
        
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.world = world;
		this.loaded = true;
		
		this.quads = new HashMap<Integer, Geometry>();

		world.chunks.addChunk(getChunkLocation());
		
		size = 0.5f;
        
        renderQuad(FACE_EAST, mat);
        renderQuad(FACE_WEST, mat);
        renderQuad(FACE_NORTH, mat);
        renderQuad(FACE_SOUTH, mat);
        renderQuad(FACE_UP, mat);
        renderQuad(FACE_DOWN, mat);
	}
	
	public void renderQuad(int side, Material mat) {
		if(quads.containsKey(side)) {
			getChunk().rootNode.attachChild(quads.get(side));
		} else {
			Geometry geo = new Geometry("Quad", getQuad(side, size)); // using our custom mesh object
			
			/*ColorRGBA color = ColorRGBA.Yellow;
	
	        mat = new Material(m.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
	        mat.setBoolean("UseMaterialColors", true);  // Set some parameters, e.g. blue.
	        
	        if(side == FACE_UP)
	        	color = ColorRGBA.Blue;
	        if(side == FACE_DOWN)
	        	color = ColorRGBA.Brown;
	        if(side == FACE_NORTH)
	        	color = ColorRGBA.Orange;
	        if(side == FACE_EAST)
	        	color = ColorRGBA.DarkGray;
	        if(side == FACE_SOUTH)
	        	color = ColorRGBA.Green;
	        if(side == FACE_WEST)
	        	color = ColorRGBA.Magenta;
	
	        mat.setColor("Ambient", color);   // ... color of this object
	        mat.setColor("Diffuse", color);   // ... color of light being reflected*/
			geo.setMaterial(mat);
			
			getChunk().rootNode.attachChild(geo);
			quads.put(side, geo);
		}
	}
	
	public void hideQuad(int side) {
		if(!quads.containsKey(side))
			return;
		
		if(quads.get(side).getParent() == getChunk().rootNode) {
			quads.get(side).removeFromParent();
			quadsRemoved++;
		}
	}
	
	/**
	 * Returns amount of quads visible (0 if none)
	 * 
	 * (Must run recalculateQuads to update)
	 * @return
	 * Quads visible
	 */
	public int getQuadsVisible() {
		int visible = 0;
		
		for(int i = 0; i <= 6; i++) {
			if(quads.containsKey(i) && quads.get(i).getParent() == getChunk().rootNode) {
				visible++;
			}
		}
		
		return visible;
	}
	
	public boolean canMergeWith(int side, int x, int y, int z) {
		Tile t = world.getBlock(x, y, z);
		if(t == null)
			return false;
		
		if(t.getName() != getName())
			return false;
		if(!quads.containsKey(side))
			return false;
		if(t.getChunk() != getChunk())
			return false;
		if(quads.get(side).getParent() != getChunk().rootNode)
			return false;
		if(t.quads.get(side).getParent() != getChunk().rootNode)
			return false;
		
		return true;
	}
	
	public Mesh getQuad(int side, float size) {
		Mesh mesh = new Mesh();
		
		float x_ = x - size;
		float y_ = y - size;
		float z_ = z - size;
		
        Vector3f [] vertices = new Vector3f[4];
        int [] indexes = { 2,0,1, 1,3,2 };
        int [] indexes_ = { 1,0,2, 2,3,1 };
        
        float[] normals = new float[12];

        //FIXME: make sides actually correspond to their name (ie face_north might not be north)
        if(side == FACE_UP) {
            vertices[0] = new Vector3f(x_,y_+1,z_);
            vertices[1] = new Vector3f(x_+1,y_+1,z_);
            vertices[2] = new Vector3f(x_,y_+1,z_+1);
            vertices[3] = new Vector3f(x_+1,y_+1,z_+1);

            normals = new float[]{0,1,0, 0,1,0, 0,1,0, 0,1,0};
            
            indexes = indexes_;
        } else if(side == FACE_DOWN) {
            vertices[0] = new Vector3f(x_,y_,z_);
            vertices[1] = new Vector3f(x_+1,y_,z_);
            vertices[2] = new Vector3f(x_,y_,z_+1);
            vertices[3] = new Vector3f(x_+1,y_,z_+1);

            normals = new float[]{0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0};
        } else if(side == FACE_NORTH) {
            vertices[0] = new Vector3f(x_,y_,z_);
            vertices[1] = new Vector3f(x_,y_,z_+1);
            vertices[2] = new Vector3f(x_,y_+1,z_);
            vertices[3] = new Vector3f(x_,y_+1,z_+1);

            normals = new float[]{0,0,1, 0,0,1, 0,0,1, 0,0,1};
        } else if(side == FACE_EAST) {
            vertices[0] = new Vector3f(x_+1,y_,z_);
            vertices[1] = new Vector3f(x_+1,y_,z_+1);
            vertices[2] = new Vector3f(x_+1,y_+1,z_);
            vertices[3] = new Vector3f(x_+1,y_+1,z_+1);

            normals = new float[]{-1,0,0, -1,0,0, -1,0,0, -1,0,0};
            
            indexes = indexes_;
        } else if(side == FACE_SOUTH) {
            vertices[0] = new Vector3f(x_,y_,z_+1);
            vertices[1] = new Vector3f(x_+1,y_,z_+1);
            vertices[2] = new Vector3f(x_,y_+1,z_+1);
            vertices[3] = new Vector3f(x_+1,y_+1,z_+1);

            normals = new float[]{0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1};
        } else if(side == FACE_WEST) {
            vertices[0] = new Vector3f(x_,y_,z_);
            vertices[1] = new Vector3f(x_+1,y_,z_);
            vertices[2] = new Vector3f(x_,y_+1,z_);
            vertices[3] = new Vector3f(x_+1,y_+1,z_);

            normals = new float[]{1,0,0, 1,0,0, 1,0,0, 1,0,0};
            
            indexes = indexes_;
        }
        
        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);
        
        mesh.setBuffer(Type.Normal,   3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();
        
        return mesh;
	}
	
	public int recalculateQuads() {
		if(!loaded) {
			for(int i = 1; i <= 6; i++)
				hideQuad(i);
			
			return 6;
		}
		
        renderQuad(FACE_EAST, mat);
        renderQuad(FACE_WEST, mat);
        renderQuad(FACE_NORTH, mat);
        renderQuad(FACE_SOUTH, mat);
        renderQuad(FACE_UP, mat);
        renderQuad(FACE_DOWN, mat);
        
        quadsRemoved = 0;
        
		if(world.getBlock(x, y+1, z) != null)
			hideQuad(FACE_UP);
		if(world.getBlock(x, y-1, z) != null)
			hideQuad(FACE_DOWN);
		if(world.getBlock(x, y, z+1) != null)
			hideQuad(FACE_SOUTH);
		if(world.getBlock(x, y, z-1) != null)
			hideQuad(FACE_WEST);
		if(world.getBlock(x+1, y, z) != null)
			hideQuad(FACE_EAST);
		if(world.getBlock(x-1, y, z) != null)
			hideQuad(FACE_NORTH);
		
		return quadsRemoved;
	}
	
	public Location getChunkStart() {
		int chunk_size = world.chunks.CHUNK_SIZE;
		
		return new Location(x - ( x % chunk_size ), y - ( y % chunk_size ), z - ( z % chunk_size ));
	}
	
	public Location getChunkLocation() {
		Location chunkStart = getChunkStart();
		int chunk_size = world.chunks.CHUNK_SIZE;
		
		return new Location(chunkStart.x / chunk_size, chunkStart.y / chunk_size, chunkStart.z / chunk_size);
	}
	
	public Chunk getChunk() {
		return world.chunks.getChunk(getChunkLocation().x, getChunkLocation().y, getChunkLocation().z);
	}
	
	public void setLoaded(boolean loaded) {
		if(loaded != this.loaded) {
			this.loaded = loaded;
			if(loaded) {
				//System.out.println("recalculating...");
				recalculateQuads();
			} else {
				for(int i = 1; i <= 6; i++)
					hideQuad(i);
			}
		}
	}
	
	public void _() {
		if(!loaded)
			return;
		
		update();
	}
	
	public abstract void update();
	public abstract String getName();

	public void delete() {
		
	}
}
