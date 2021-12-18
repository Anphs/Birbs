package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public class Birb extends Entity
{
	private final BirbsContainer bc;
	private static final int baseWidth = 70, baseHeight = 70, baseSpeed = 750;
	private final static float maxTurnSpeed = (float) .025, interactionRange = baseWidth * 3;
	private final String name;
	private Point2D.Float formationPoint;
	
	public Birb(BirbsContainer bc, int entityID, int type, String name, float xWorld, float yWorld, float scale)
	{
		super(bc, entityID, type, xWorld, yWorld, scale);
		this.bc = bc;
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
	
	public void applyMouseForce(boolean seek)
	{
		float distanceX = (float) (bc.getInput().getScaledMousePoint().getX() - this.getXWorld());
		float distanceY = (float) (bc.getInput().getScaledMousePoint().getY() - this.getYWorld());
		if(seek)
		{
			this.addXForce(distanceX);
			this.addYForce(distanceY);
		}
		else
		{
			this.addXForce(-distanceX);
			this.addYForce(-distanceY);
		}
	}
}
