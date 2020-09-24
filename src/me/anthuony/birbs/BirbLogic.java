package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BirbLogic extends Thread
{
	private Birb birb;
	private final ArrayList<Birb> birbsList;
	private static BirbsContainer bc;
	
	//Multi-threading
	public BirbLogic(ArrayList<Birb> birbsList, String name, ThreadGroup tg)
	{
		super(tg, name);
		this.birbsList = birbsList;
	}
	
	public void run()
	{
		for (Birb birb : birbsList)
		{
			this.birb = birb;
			updateBirbColor();
			seekPoint(bc.getInput().getMousePoint(), true);
			updateBirbLocation();
		}
	}
	
	public void seekPoint(Point2D.Double desiredPoint, boolean seek)
	{
		double desiredX = desiredPoint.getX();
		double desiredY = desiredPoint.getY();
		double currentX = birb.getPoint().getX();
		double currentY = birb.getPoint().getY();
		
		double currentDirection = birb.getVel().getDirection();
		double desiredDirection = Math.atan2(desiredY - currentY, desiredX - currentX);
		if(!seek)
		{
			desiredDirection += Math.PI;
		}
		if(desiredDirection<0) {
			desiredDirection += 2*Math.PI;
		}
		double adjustment = desiredDirection - currentDirection;
//		System.out.println(desiredDirection+" "+currentDirection+" "+adjustment);
		double change = adjustment%(2*Math.PI);
		
		if(change > Math.PI && currentDirection < Math.PI) {
			change -= 2*Math.PI;
		}
		
		if(change < -Math.PI && currentDirection > Math.PI) {
			change += 2*Math.PI;
		}
		
		if(Math.abs(change) > Birb.getMaxTurnSpeed()) {
			if(change > 0) {
				change = Birb.getMaxTurnSpeed();
			}
			else {
				change = -Birb.getMaxTurnSpeed();
			}
		}
		
		double newDirection = (change+currentDirection)%(2*Math.PI);
		birb.setVel(new Vector(birb.getVel().getMagnitude(), newDirection));
	}
	
	public void updateBirbLocation()
	{
		double speedMultiplier = 1;
		double x = birb.getPoint().getX();
		double y = birb.getPoint().getY();
		Vector vel = birb.getVel();
		x = (x + vel.getMagnitude() * speedMultiplier * Math.cos(vel.getDirection()));
		y = (y + vel.getMagnitude() * speedMultiplier * Math.sin(vel.getDirection()));
		
		//Adjust for world boundaries and birb boundaries
		x = (Math.abs((x + bc.getWorldWidth() + birb.getWidth() / 2.0) % bc.getWorldWidth())) - birb.getWidth() / 2.0;
		y = (Math.abs((y + bc.getWorldHeight() + birb.getHeight() / 2.0) % bc.getWorldHeight())) - birb.getHeight() / 2.0;
		
		//Update location
		Point2D.Double newPoint = new Point2D.Double(x, y);
		birb.setPoint(newPoint);
	}
	
	public void updateBirbColor()
	{
		Color color = birb.getBirbColor();
		int r = (color.getRed() + 3) % 255;
		int g = (color.getGreen() + 1) % 255;
		int bl = (color.getBlue() + 2) % 255;
		Vector vel = birb.getVel();
		int velMag = (int) vel.getMagnitude();
		Color newColor = new Color(r, g, bl, 50 + 30 * velMag);
		birb.setBirbColor(newColor);
	}
	
	public static void setBc(BirbsContainer bc)
	{
		BirbLogic.bc = bc;
	}
}
