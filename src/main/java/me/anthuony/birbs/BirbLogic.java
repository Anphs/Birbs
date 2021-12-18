package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

@Deprecated
public class BirbLogic extends Thread
{
	private final ArrayList<Birb> logicBirbsList;
	private final BirbsContainer bc;
	private Birb birb;
	
	//Multi-threading
	public BirbLogic(ArrayList<Birb> birbsList, String name, ThreadGroup tg, BirbsContainer bc)
	{
		super(tg, name);
		this.logicBirbsList = birbsList;
		this.bc = bc;
	}
	
	public static float getPointDistance(Point2D.Float p1, Point2D.Float p2)
	{
		float p1x = (float) p1.getX();
		float p1y = (float) p1.getY();
		float p2x = (float) p2.getX();
		float p2y = (float) p2.getY();
		return (float) Point2D.distance(p1x, p1y, p2x, p2y);
	}
	
	public static double getBirbDistance(Birb b1, Birb b2)
	{
		return getPointDistance(b1.getWorldPoint(), b2.getWorldPoint());
	}
	
	public void run()
	{
		for (Birb birb : logicBirbsList)
		{
			this.birb = birb;
			if (!bc.isPaused())
			{
				if (bc.isDeleteClose())
				{
					doDeleteClose();
				}
				if (doAvoidOthers())
				{
					if (doAvoidOthers() && bc.isDoAlignment())
					{
						doAlignment();
					}
				}
				else
				{
					if (birb.getFormationPoint() != null)
					{
//						adjustFormationSpeed(birb.getFormationPoint());
						seekPoint(birb.getFormationPoint(), true);
					}
					else
					{
//						seekPoint(bc.getInput().getScaledMousePoint(), true);
					}
				}
			}
			updateBirbLocation();
			updateBirbColor();
		}
	}
	
	private void doDeleteClose()
	{
		int deleteRange = 10;
		ArrayList<Birb> birbsInRadius = getBirbsInRadius(deleteRange);
		for (Birb otherBirb : birbsInRadius)
		{
			bc.removeEntity(otherBirb);
		}
	}
	
	private void doAlignment()
	{
		ArrayList<Birb> birbsInRadius = getBirbsInRadius(25 * birb.getSpeed());
		if (birbsInRadius.size() > 0)
		{
			float avgMag = 0;
			float avgDir = 0;
			float avgX = 0;
			float avgY = 0;
			for (Birb otherBirb : birbsInRadius)
			{
				avgMag += otherBirb.getSpeed();
				avgDir += otherBirb.getDirection();
				avgX += otherBirb.getWorldPoint().getX();
				avgY += otherBirb.getWorldPoint().getY();
			}
			avgMag /= birbsInRadius.size();
			avgDir /= birbsInRadius.size();
			avgX /= birbsInRadius.size();
			avgY /= birbsInRadius.size();
			Point2D.Float avgPoint = new Point2D.Float(avgX, avgY);
			
			float currentDirection = birb.getDirection();
			float adjustment = getDirectionAdjustment(currentDirection, avgDir);
			float newDirection = (float) ((adjustment + currentDirection) % (2 * Math.PI));
			birb.setSpeed(avgMag);
			birb.setDirection(newDirection);
			
			seekPoint(avgPoint, true);
		}
	}
	
	public float getDirectionAdjustment(double currentDirection, double desiredDirection)
	{
		float adjustment = (float) (desiredDirection - currentDirection);
//		System.out.println(desiredDirection+" "+currentDirection+" "+adjustment);
		float change = (float) (adjustment % (2 * Math.PI));
		
		if (change > Math.PI && currentDirection < Math.PI)
		{
			change -= 2 * Math.PI;
		}
		
		if (change < -Math.PI && currentDirection > Math.PI)
		{
			change += 2 * Math.PI;
		}
		
		if (Math.abs(change) > Birb.getMaxTurnSpeed())
		{
			if (change > 0)
			{
				change = Birb.getMaxTurnSpeed();
			} else
			{
				change = -Birb.getMaxTurnSpeed();
			}
		}
		return change;
	}
	
	public void seekPoint(Point2D.Float desiredPoint, boolean seek)
	{
		float desiredX = desiredPoint.x;
		float desiredY = desiredPoint.y;
		float currentX = birb.getXWorld();
		float currentY = birb.getYWorld();
		
		float currentDirection = birb.getDirection();
		float desiredDirection = (float) Math.atan2(desiredY - currentY, desiredX - currentX);
		if (!seek)
		{
			desiredDirection += Math.PI;
		}
		if (desiredDirection < 0)
		{
			desiredDirection += 2 * Math.PI;
		}
		
		float adjustment = getDirectionAdjustment(currentDirection, desiredDirection);
		
		float newDirection = (float) ((adjustment + currentDirection) % (2 * Math.PI));
		birb.setDirection(newDirection);
	}
	
//	public void adjustFormationSpeed(Point2D.Float formationPoint)
//	{
//		double distance = getPointDistance(formationPoint, birb.getWorldPoint());
//		double slowDownRange = 300;
//		if (distance < slowDownRange)
//		{
//			birb.setSpeedMultiplier(Math.min(distance / slowDownRange, 20));
//		} else
//		{
//			birb.setSpeedMultiplier(1);
//		}
//	}
	
	public double getBirbDistance(Birb otherBirb)
	{
		return getPointDistance(birb.getWorldPoint(), otherBirb.getWorldPoint());
	}
	
	public ArrayList<Birb> getBirbsInRadius(double radius)
	{
		ArrayList<Birb> birbsInRadius = new ArrayList<>();
		for (Entity otherBirb : bc.getEntityList())
		{
			if (otherBirb != birb)
			{
				double distance = getBirbDistance((Birb) otherBirb);
				if (distance <= radius)
				{
					birbsInRadius.add((Birb) otherBirb);
				}
//				System.out.println(distance);
			}
		}
		return birbsInRadius;
	}
	
	public boolean doAvoidOthers()
	{
		int avoidRange = (int) birb.getSpeed() * 15 + 25;
		ArrayList<Birb> birbsInRadius = getBirbsInRadius(avoidRange);
		Birb closestOther = null;
		if (birbsInRadius.size() > 0)
		{
			closestOther = birbsInRadius.get(0);
		}
		for (Birb otherBirb : birbsInRadius)
		{
			if (getBirbDistance(closestOther, otherBirb) < getBirbDistance(closestOther, birb))
			{
				closestOther = otherBirb;
			}
		}
		if (closestOther != null)
		{
			seekPoint(closestOther.getWorldPoint(), false);
//			birb.setSpeedMultiplier(getBirbDistance(closestOther) / (avoidRange * 1.5));
			return false;
//			System.out.println(getBirbDistance(closestOther));
		}
		return true;
	}
	
	public void updateBirbLocation()
	{
		float x = birb.getXWorld();
		float y = birb.getYWorld();
		if (!bc.isPaused())
		{
			x = (float) (x + birb.getSpeed() * Math.cos(birb.getDirection()));
			y = (float) (y + birb.getSpeed() * Math.sin(birb.getDirection()));
		}
		
		//Adjust for world boundaries and birb boundaries
//		x = (Math.abs((x + bc.getWorldWidth() + birb.getWidth() / 2.0) % bc.getWorldWidth())) - birb.getWidth() / 2.0;
//		y = (Math.abs((y + bc.getWorldHeight() + birb.getHeight() / 2.0) % bc.getWorldHeight())) - birb.getHeight() / 2.0;
		x = (float) ((Math.abs((x + bc.getWorldWidth() + 70 / 2.0) % bc.getWorldWidth())) - 70 / 2.0);
		y = (float) ((Math.abs((y + bc.getWorldHeight() + 70 / 2.0) % bc.getWorldHeight())) - 70 / 2.0);
		
		//Update location
		birb.setXWorld(x);
		birb.setYWorld(y);
		
		float sX = (float) ((x + bc.getCameraOffsetX()) * bc.getScale());
		float sY = (float) ((y + bc.getCameraOffsetY()) * bc.getScale());
		
		birb.setXScreen(sX);
		birb.setYScreen(sY);
		birb.setOnScreen(sX > -70 * bc.getScale() && sX < bc.getWindowWidth() + 70 * bc.getScale() && sY > -70 * bc.getScale() && sY < bc.getWindowHeight() + 70 * bc.getScale());
	}
	
	public void updateBirbColor()
	{
		double x = birb.getWorldPoint().getX();
		double y = birb.getWorldPoint().getY();
		int b = (int) (x / bc.getWorldWidth() * 255);
		int g = (int) (y / bc.getWorldHeight() * 255);
		int r = 255 - b;
		
//		double angle = Math.abs(Math.toDegrees(birb.getDirection()));
//		int r = (int)(angle * 255 / 360);
//		int g = (int)(angle * 255 / 360);
//		int b = (int)(angle * 255 / 360);
		
		b = Math.min(Math.max(b, 50), 255);
		g = Math.min(Math.max(g, 50), 255);
		r = Math.min(Math.max(r, 50), 255);
		
		Color newColor = new Color(r, g, b, 255);
//		birb.setColor(newColor);
	}
}
