package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public class Birb
{
	private static final int baseWidth = 70, baseHeight = 70;
	private final static double maxTurnSpeed = .1;
	private final static double turnNoise = 0;
	private double scale = 1;
	private final String ID, name;
	private Vector vel, acc;
	private Point2D.Double worldPoint, screenPoint, formationPoint;
	private Color birbColor;
	private boolean onScreen;
	private double speedMultiplier = 1;
	
	public Birb(String ID, String name, Point2D.Double worldPoint)
	{
		this.ID = ID;
		this.name = name;
		this.worldPoint = worldPoint;
		
		double velMag = 20;
		this.vel = new Vector(velMag, Math.random() * 2 * Math.PI);
//		vel = new Vector(0, 3 * Math.PI / 2);
		this.birbColor = new Color(0, 0, 0,0);
		this.scale = Math.random() + .5 + .25;
	}
	
	public static double getMaxTurnSpeed()
	{
		return maxTurnSpeed;
	}
	
	public static double getTurnNoise()
	{
		return turnNoise;
	}
	
	public static int getBaseWidth()
	{
		return baseWidth;
	}
	
	public static int getBaseHeight()
	{
		return baseHeight;
	}
	
	public Vector getVel()
	{
		return vel;
	}
	
	public void setVel(Vector vel)
	{
		this.vel = vel;
	}
	
	public Vector getAcc()
	{
		return acc;
	}
	
	public void setAcc(Vector acc)
	{
		this.acc = acc;
	}
	
	public Point2D.Double getWorldPoint()
	{
		return worldPoint;
	}
	
	public void setWorldPoint(Point2D.Double p)
	{
		this.worldPoint = p;
	}
	
	public Point2D.Double getScreenPoint()
	{
		return screenPoint;
	}
	
	public void setScreenPoint(Point2D.Double screenPoint)
	{
		this.screenPoint = screenPoint;
	}
	
	public double getAvoidRadius()
	{
		return (int) vel.getMagnitude() * 15 + 150;
	}
	
	public Color getBirbColor()
	{
		return birbColor;
	}
	
	public void setBirbColor(Color birbColor)
	{
		this.birbColor = birbColor;
	}
	
	public String getID()
	{
		return ID;
	}
	
	public double getSpeedMultiplier()
	{
		return speedMultiplier;
	}
	
	public void setSpeedMultiplier(double speedMultiplier)
	{
		this.speedMultiplier = speedMultiplier;
	}
	
	public Point2D.Double getFormationPoint()
	{
		return formationPoint;
	}
	
	public void setFormationPoint(Point2D.Double seekPoint)
	{
		this.formationPoint = seekPoint;
	}
	
	public boolean isOnScreen()
	{
		return onScreen;
	}
	
	public void setOnScreen(boolean onScreen)
	{
		this.onScreen = onScreen;
	}
	
	public double getScale()
	{
		return scale;
	}
	
	public String getName()
	{
		return name;
	}
}
