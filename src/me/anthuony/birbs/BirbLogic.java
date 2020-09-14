package me.anthuony.birbs;

import java.awt.*;
import java.util.ArrayList;

public class BirbLogic extends Thread
{
	private Birb birb;
	private ArrayList<Birb> birbsList;
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
			updateBirbLocation();
		}
	}
	
	public void updateBirbLocation()
	{
		double speedMultiplier = 1;
		int x = birb.getPoint().x;
		int y = birb.getPoint().y;
		Vector vel = birb.getVel();
		x = (int) (x + vel.getMagnitude() * speedMultiplier * Math.cos(vel.getDirection()));
		y = (int) (y + vel.getMagnitude() * speedMultiplier * Math.sin(vel.getDirection()));
		
		//Adjust for world boundaries and birb boundaries
		x = (Math.abs((x + bc.getWorldWidth() + birb.getWidth() / 2) % bc.getWorldWidth())) - birb.getWidth() / 2;
		y = (Math.abs((y + bc.getWorldHeight() + birb.getHeight() / 2) % bc.getWorldHeight())) - birb.getHeight() / 2;
		
		//Update location
		Point newPoint = new Point(x, y);
		birb.setPoint(newPoint);
	}
	
	public void updateBirbColor()
	{
		Color color = birb.getBirbColor();
		int r = (color.getRed()+3)%255;
		int g = (color.getGreen()+1)%255;
		int bl = (color.getBlue()+2)%255;
		Vector vel = birb.getVel();
		int velMag = (int)vel.getMagnitude();
		Color newColor = new Color(r,g,bl,50 + 30 * velMag);
		birb.setBirbColor(newColor);
	}
	
	public static void setBc(BirbsContainer bc)
	{
		BirbLogic.bc = bc;
	}
}
