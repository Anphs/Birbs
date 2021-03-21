package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Entity
{
	//Entity Types:
	//1 = Birb
	private	final int entityID;
	private final BirbsContainer bc;
	
	public Entity(BirbsContainer bc, int entityID, float xWorld, float yWorld, float scale)
	{
		this.entityID = entityID;
		this.bc = bc;
		bc.seteXWorld(entityID, xWorld);
		bc.seteYWorld(entityID, yWorld);
		bc.seteScale(entityID, scale);
	}
	
	public void setSpeed(float velocity)
	{
		bc.geteSpeed()[entityID] = velocity;
	}
	
	public float getSpeed()
	{
		return bc.geteSpeed(entityID);
	}
	
	public void setDirection(float direction)
	{
		bc.seteDirection(entityID, direction);
	}
	
	public void setScale(float scale)
	{
		bc.seteScale(entityID, scale);
	}
	
	public void setColor(Color color)
	{
		bc.seteColor(entityID, color);
	}
	
	public Color getColor()
	{
		return bc.geteColor(entityID);
	}
	
	public float getXWorld()
	{
		return bc.geteXWorld(entityID);
	}
	
	public float getYWorld()
	{
		return bc.geteYWorld(entityID);
	}
	
	public void setXWorld(float xWorld)
	{
		bc.seteXWorld(entityID, xWorld);
	}
	
	public void setYWorld(float yWorld)
	{
		bc.seteYWorld(entityID, yWorld);
	}
	
	public float getXScreen()
	{
		return bc.geteXScreen(entityID);
	}
	
	public float getYScreen()
	{
		return bc.geteYScreen(entityID);
	}
	
	public void setXScreen(float xScreen)
	{
		bc.seteXScreen(entityID, xScreen);
	}
	
	public void setYScreen(float yScreen)
	{
		bc.seteYScreen(entityID, yScreen);
	}
	
	public boolean isOnScreen()
	{
		return bc.geteOnScreen(entityID);
	}
	
	public void setOnScreen(boolean onScreen)
	{
		bc.seteOnScreen(entityID, onScreen);
	}
	
	public float getDirection()
	{
		return bc.geteDirection(entityID);
	}
	
	public float getScale()
	{
		return bc.geteScale(entityID);
	}
	
	public Point2D.Float getWorldPoint()
	{
		return new Point2D.Float(getXWorld(), getYWorld());
	}
	
	public int getEntityID()
	{
		return entityID;
	}
	
	public int getChunkID() { return bc.geteChunk(entityID); }
}
