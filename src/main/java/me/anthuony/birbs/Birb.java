package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public class Birb extends Entity
{
	private static final int baseWidth = 70, baseHeight = 70;
	private final static double maxTurnSpeed = .1;
	private final String name;
	private Point2D.Double formationPoint;
	private double speedMultiplier = 1;
	
	public Birb(String ID, String name, Point2D.Double worldPoint)
	{
		super(ID, worldPoint);
		this.name = name;
		
		double velMag = 20;
		setVelocity(new Vector(velMag, Math.random() * 2 * Math.PI));
//		vel = new Vector(0, 3 * Math.PI / 2);
		setColor(new Color(0, 0, 0, 0));
		setScale(Math.random() + .5 + .25);
	}
	
	public static double getMaxTurnSpeed()
	{
		return maxTurnSpeed;
	}
	
	public static int getBaseWidth()
	{
		return baseWidth;
	}
	
	public static int getBaseHeight() { return baseHeight; }
	
	public double getAvoidRadius()
	{
		return (int) getSpeed() * 15 + 150;
	}
	
	public Point2D.Double getFormationPoint()
	{
		return formationPoint;
	}
	
	public void setFormationPoint(Point2D.Double seekPoint)
	{
		this.formationPoint = seekPoint;
	}
	
	public String getName() { return name; }
	
	public double getSpeedMultiplier()
	{
		return speedMultiplier;
	}
	
	public void setSpeedMultiplier(double speedMultiplier)
	{
		this.speedMultiplier = speedMultiplier;
	}
}
