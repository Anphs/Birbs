package me.anthuony.birbs;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Formation
{
	private final String type;
	private final ArrayList<Birb> birbsList;
	
	public Formation(String type, ArrayList<Birb> birbsList)
	{
		this.type = type;
		this.birbsList = birbsList;
	}
	
	public void updateFormationPoints(BirbsContainer bc)
	{
		
		if (type.equalsIgnoreCase("Line"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				double x = bc.getWorldWidth() * ((double) i / birbsList.size());
				Point2D.Double formationPoint = new Point2D.Double(x, bc.getWorldHeight() / 2.0);
				currentBirb.setFormationPoint(formationPoint);
			}
		} else if (type.equalsIgnoreCase("Circle"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				double radius = 0.4 * Math.min(bc.getWorldWidth(), bc.getWorldHeight());
				double degree = 360 * ((double) i / birbsList.size());
				double radian = Math.toRadians(degree);
				double x = radius * Math.cos(radian) + bc.getWorldWidth() / 2.0;
				double y = radius * Math.sin(radian) + bc.getWorldHeight() / 2.0;
				Point2D.Double formationPoint = new Point2D.Double(x, y);
				currentBirb.setFormationPoint(formationPoint);
			}
		} else if (type.equalsIgnoreCase("Cubic"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				double maxY = Math.pow(birbsList.size()/2, 3);
				double temp = (bc.getWorldHeight() - 1000) / maxY / 2;
				double x = (bc.getWorldWidth() - 1000) * ((double) i / birbsList.size());
				x = bc.getWorldWidth() - x - 500;
				double y = bc.getWorldHeight()/2.0 + (Math.pow(i - birbsList.size()/2, 3) * temp);
				Point2D.Double formationPoint = new Point2D.Double(x, y);
				currentBirb.setFormationPoint(formationPoint);
			}
		}
		
	}
}
