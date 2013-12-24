package mineplex;

import com.jme3.math.Vector3f;

public class Location {
	public int x;
	public int y;
	public int z;
	
	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location(Vector3f v) {
		this((int) v.x, (int) v.y, (int) v.z);
	}
}
