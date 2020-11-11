package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public class Birb
{
	private static final int baseWidth = 70, baseHeight = 70;
	private final static double maxTurnSpeed = .1, turnNoise = 0;
	private static boolean hitboxVisible;
	private final String ID;
	private Vector vel, acc;
	private Point2D.Double worldPoint, screenPoint, formationPoint;
	private Color birbColor;
	private boolean onScreen;
	private double speedMultiplier = 1;
	
	public Birb(String ID, Point2D.Double worldPoint)
	{
		this.ID = ID;
		this.worldPoint = worldPoint;
		
		double velMag = Math.random() * 2 + 4;
		velMag = 20;
		vel = new Vector(velMag, Math.random() * 2 * Math.PI);
//		vel = new Vector(0, 3 * Math.PI / 2);
		birbColor = new Color(254, 105, 3, /*50 + 30 * (int) velMag*/ 0);
	}
	
	public static double getMaxTurnSpeed()
	{
		return maxTurnSpeed;
	}
	
	public static double getTurnNoise()
	{
		return turnNoise;
	}
	
	public static void toggleHitboxVisible()
	{
		hitboxVisible = !hitboxVisible;
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
}
