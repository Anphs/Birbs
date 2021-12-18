package me.anthuony.birbs;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Formation
{
	private final String type;
	private ArrayList<Birb> birbsList;
	
	public Formation(String type)
	{
		this.type = type;
	}
	
	public Formation(String type, ArrayList<Birb> birbsList)
	{
		this.type = type;
		this.birbsList = birbsList;
	}
	
	public void setBirbsList(ArrayList<Birb> birbsList)
	{
		this.birbsList = birbsList;
	}
	
	public void updateFormationPoints(BirbsContainer bc)
	{
		
		if (type.equalsIgnoreCase("Line"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				float x = (float) ((bc.getWorldWidth() - 1000) * ((double) i / birbsList.size()) + 500);
				Point2D.Float formationPoint = new Point2D.Float(x, (float) (bc.getWorldHeight() / 2.0));
				currentBirb.setFormationPoint(formationPoint);
			}
		} else if (type.equalsIgnoreCase("Circle"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				float radius = (float) (0.4 * Math.min(bc.getWorldWidth(), bc.getWorldHeight()));
				float degree = (float) (360 * ((double) i / birbsList.size()));
				float radian = (float) Math.toRadians(degree);
				float x = (float) (radius * Math.cos(radian) + bc.getWorldWidth() / 2.0);
				float y = (float) (radius * Math.sin(radian) + bc.getWorldHeight() / 2.0);
				Point2D.Float formationPoint = new Point2D.Float(x, y);
				currentBirb.setFormationPoint(formationPoint);
			}
		} else if (type.equalsIgnoreCase("Circle2"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				float radius = (float) (0.2 * Math.min(bc.getWorldWidth(), bc.getWorldHeight()));
				float degree = (float) (360 * ((double) i / birbsList.size()));
				float radian = (float) Math.toRadians(degree);
				float x = (float) (radius * Math.cos(radian) + bc.getWorldWidth() / 2.0);
				float y = (float) (radius * Math.sin(radian) + bc.getWorldHeight() / 2.0);
				Point2D.Float formationPoint = new Point2D.Float(x, y);
				currentBirb.setFormationPoint(formationPoint);
			}
		} else if (type.equalsIgnoreCase("Cubic"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				float maxY = (float) Math.pow(birbsList.size() / 2, 3);
				float temp = (bc.getWorldHeight() - 1000) / maxY / 2;
				float x = (float) ((bc.getWorldWidth() - 1000) * ((double) i / birbsList.size()));
				x = bc.getWorldWidth() - x - 500;
				float y = (float) (bc.getWorldHeight() / 2.0 + (Math.pow(i - birbsList.size() / 2, 3) * temp));
				Point2D.Float formationPoint = new Point2D.Float(x, y);
				currentBirb.setFormationPoint(formationPoint);
			}
		} else if (type.equalsIgnoreCase("Cubic2"))
		{
			for (int i = 0; i < birbsList.size(); i++)
			{
				Birb currentBirb = birbsList.get(i);
				float maxY = (float) Math.pow(birbsList.size() / 2.0, 3);
				float temp = (bc.getWorldHeight() - 1000) / maxY / 2;
				float x = (float) ((bc.getWorldWidth() - 1000) * ((double) i / birbsList.size()));
				x = bc.getWorldWidth() - x - 500;
				float y = (float) ((Math.pow(i - birbsList.size() / 2, 3) * -temp) + bc.getWorldHeight() / 2.0);
				
				Point2D.Float formationPoint = new Point2D.Float(x, y);
				currentBirb.setFormationPoint(formationPoint);
			}
		}
		
		
	}
}
