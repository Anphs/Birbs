package me.anthuony.birbs;

public class Vector
{
	private final double magnitude;
	private final double direction;
	
	public Vector(double d, double e)
	{
		magnitude = d;
		direction = e;
	}
	
	public double getMagnitude()
	{
		return magnitude;
	}
	
	public double getDirection()
	{
		return direction;
	}
}
