package fr.masterfire.math;

public class Vector {
	public float x, y;

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void normalize() {
		float l = (float) Math.sqrt(x * x + y * y);
		x /= l;
		y /= l;
	}
}