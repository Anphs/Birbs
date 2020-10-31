package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Birb
{
	private static final int baseWidth = 70, baseHeight = 70;
	private final static double maxTurnSpeed = .1, turnNoise = 0;
	private final String ID;
	private Vector vel, acc;
	private Point2D.Double point, formationPoint;
	private static double offsetX = 0, offsetY = 0;
	private Color birbColor;
	private static boolean hitboxVisible;
	private double speedMultiplier = 1;
	
	public Birb(String ID, Point2D.Double point)
	{
		this.ID = ID;
		this.point = point;
		
		double velMag = Math.random() * 2 + 4;
		velMag = 20;
		vel = new Vector(velMag, Math.random() * 2 * Math.PI);
//		vel = new Vector(0, 3 * Math.PI / 2);
		birbColor = new Color(254, 105, 3, /*50 + 30 * (int) velMag*/ 0);
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
	
	public Point2D.Double getPoint()
	{
		return point;
	}
	
	public void setWorldPoint(Point2D.Double p)
	{
		this.point = p;
	}
	
	public double getAvoidRadius()
	{
		return (int)vel.getMagnitude()*15+150;
	}
	
	public Color getBirbColor()
	{
		return birbColor;
	}
	
	public void setBirbColor(Color birbColor)
	{
		this.birbColor = birbColor;
	}
	
	public static double getMaxTurnSpeed()
	{
		return maxTurnSpeed;
	}
	
	public static double getTurnNoise()
	{
		return turnNoise;
	}
	
	public String getID()
	{
		return ID;
	}
	
	public static void toggleHitboxVisible()
	{
		hitboxVisible = !hitboxVisible;
	}
	
	public double getSpeedMultiplier()
	{
		return speedMultiplier;
	}
	
	public void setSpeedMultiplier(double speedMultiplier)
	{
		this.speedMultiplier = speedMultiplier;
	}
	
	public static void setOffsetX(double offsetX)
	{
		Birb.offsetX = offsetX;
	}
	
	public static void setOffsetY(double offsetY)
	{
		Birb.offsetY = offsetY;
	}
	
	public Point2D.Double getFormationPoint()
	{
		return formationPoint;
	}
	
	public void setFormationPoint(Point2D.Double seekPoint)
	{
		this.formationPoint = seekPoint;
	}
	
	public static int getBaseWidth()
	{
		return baseWidth;
	}
	
	public static int getBaseHeight()
	{
		return baseHeight;
	}
}
