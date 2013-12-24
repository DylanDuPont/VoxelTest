package mineplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ArrayList3d<Type> {
	private HashMap<Double, HashMap<Double, HashMap<Double, Type>>> array;
	private ArrayList<Type> array_; //FIXME: combine into a single array
	
	public ArrayList3d() {
		array = new HashMap<Double, HashMap<Double, HashMap<Double, Type>>>();
		array_ = new ArrayList<Type>();
	}
	
	private HashMap<Double, Type> g(double x, double y) {
		if(!array.containsKey(x))
			array.put(x, new HashMap<Double,HashMap<Double,Type>>());
		
		if(!array.get(x).containsKey(y))
			array.get(x).put(y, new HashMap<Double,Type>());
		
		return array.get(x).get(y);
	}
	
	public Type put(double x, double y, double z, Type type) {
		array_.add(type);
		return g(x, y).put(z, type);
	}
	
	public Type get(double x, double y, double z) {
		return g(x, y).get(z);
	}
	
	public void remove(double x, double y, double z) {
		array_.remove(get(x, y, z));
		g(x, y).remove(z);
	}
	
	public Iterator<Type> getIteratable() {
		return array_.iterator();
	}

	public boolean contains(double x, double y, double z) {
		return g(x, y).containsKey(z);
	}
}
