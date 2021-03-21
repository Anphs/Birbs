package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public class Birb extends Entity
{
	private static final int baseWidth = 70, baseHeight = 70, baseSpeed = 1000;
	private final static float maxTurnSpeed = (float) .1, interactionRange = baseWidth;
	private final String name;
	private Point2D.Float formationPoint;
	
	public Birb(BirbsContainer bc, int entityID, String name, float xWorld, float yWorld, float scale)
	{
		super(bc, entityID, xWorld, yWorld, scale);
		this.name = name;
		
		setSpeed(baseSpeed);
		setDirection((float) (Math.random() * 2 * Math.PI));
//		setDirection((float) (3 * Math.PI / 2));
		
//		setColor(new Color((int)(255 * Math.random()), (int)(255 * Math.random()), (int)(255 * Math.random()), 255));
		setScale((float) (Math.random() + .5 + .25));
	}
	
	public static float getMaxTurnSpeed()
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
	
	public Point2D.Float getFormationPoint()
	{
		return formationPoint;
	}
	
	public void setFormationPoint(Point2D.Float seekPoint)
	{
		this.formationPoint = seekPoint;
	}
	
	public String getName() { return name; }
	
	public static float getInteractionRange()
	{
		return interactionRange;
	}
}
