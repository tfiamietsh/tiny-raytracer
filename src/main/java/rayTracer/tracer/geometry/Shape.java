package rayTracer.tracer.geometry;

import rayTracer.tracer.misc.Vertex;
import rayTracer.util.Vec3;
import rayTracer.tracer.misc.Material;

import javafx.util.Pair;

public abstract class Shape {
	public abstract Pair<Double, Vertex> rayIntersection(Vec3 origin, Vec3 dir);

	protected void setMaterial(Material material) {
		this.material = material;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	protected Material material;
}
