package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Entity
{
	private final String ID;
	private double scale = 1;
	private Vector velocity, acceleration;
	private Point2D.Double worldPoint, screenPoint;
	private Color color;
	private boolean onScreen;
	
	public Entity(String ID, Point2D.Double worldPoint)
	{
		this.ID = ID;
		this.worldPoint = worldPoint;
	}
	
	public String getID()
	{
		return ID;
	}
	
	public double getScale()
	{
		return scale;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
	}
	
	public Vector getVelocity()
	{
		return velocity;
	}
	
	public double getSpeed()
	{
		return velocity.getMagnitude();
	}
	
	public double getDirection()
	{
		return velocity.getDirection();
	}
	
	public void setVelocity(Vector velocity)
	{
		this.velocity = velocity;
	}
	
	public Vector getAcceleration()
	{
		return acceleration;
	}
	
	public void setAcceleration(Vector acceleration)
	{
		this.acceleration = acceleration;
	}
	
	public Point2D.Double getWorldPoint()
	{
		return worldPoint;
	}
	
	public void setWorldPoint(Point2D.Double worldPoint)
	{
		this.worldPoint = worldPoint;
	}
	
	public Point2D.Double getScreenPoint()
	{
		return screenPoint;
	}
	
	public void setScreenPoint(Point2D.Double screenPoint)
	{
		this.screenPoint = screenPoint;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public boolean isOnScreen()
	{
		return onScreen;
	}
	
	public void setOnScreen(boolean onScreen)
	{
		this.onScreen = onScreen;
	}
}
