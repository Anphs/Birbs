package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Entity implements Comparable<Entity>
{
	//Entity Types:
	//1 = Birb
	private final BirbsContainer bc;
	private	final int entityID;
	private final int type;
	private float xWorld, yWorld, xScreen, yScreen, xForce, yForce, speed, direction, angularAcceleration, scale;
	private Chunk chunk;
	private Color color;
	private boolean onScreen;
	
	public Entity(BirbsContainer bc, int entityID, int type, float xWorld, float yWorld, float scale)
	{
		this.bc = bc;
		this.entityID = entityID;
		this.type = type;
		this.xWorld = ExtraMath.boundNumber(xWorld, 0, bc.getWorldWidth(), bc.getWorldWidth());
		this.yWorld = ExtraMath.boundNumber(yWorld, 0, bc.getWorldHeight(), bc.getWorldHeight());
		this.scale = scale;
		this.setChunk(Chunk.calculateChunk(this.xWorld, this.yWorld, bc.getChunkSize(), bc.getChunkWidth()));
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
	
	public float getSpeed()
	{
		return this.speed;
	}
	
	public void setDirection(float direction)
	{
		this.direction = direction;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public float getXWorld()
	{
		return this.xWorld;
	}
	
	public float getYWorld()
	{
		return this.yWorld;
	}
	
	public void setXWorld(float xWorld)
	{
		this.xWorld = xWorld;
	}
	
	public void setYWorld(float yWorld)
	{
		this.yWorld = yWorld;
	}
	
	public float getXScreen()
	{
		return this.xScreen;
	}
	
	public float getYScreen()
	{
		return this.yScreen;
	}
	
	public void setXScreen(float xScreen)
	{
		this.xScreen = xScreen;
	}
	
	public void setYScreen(float yScreen)
	{
		this.yScreen = yScreen;
	}
	
	public boolean isOnScreen()
	{
		return this.onScreen;
	}
	
	public void setOnScreen(boolean onScreen)
	{
		this.onScreen = onScreen;
	}
	
	public float getDirection()
	{
		return this.direction;
	}
	
	public float getScale()
	{
		return this.scale;
	}
	
	public Point2D.Float getWorldPoint()
	{
		return new Point2D.Float(getXWorld(), getYWorld());
	}
	
	public int getEntityID()
	{
		return entityID;
	}
	
	public void addXForce(float magnitude)
	{
		this.xForce += magnitude;
	}
	
	public void addYForce(float magnitude)
	{
		this.yForce += magnitude;
	}
	
	public void resetForces()
	{
		this.xForce = 0;
		this.yForce = 0;
	}
	
	public void applyForce(Entity e)
	{
	
	}
	
	public int getType()
	{
		return type;
	}
	
	public Chunk getChunk()
	{
		return chunk;
	}
	
	public void setChunk(int chunk)
	{
		if(chunk >= 0)
		{
			Chunk.assignChunk(this, this.chunk, bc.getChunkList().get(chunk));
			this.chunk = bc.getChunkList().get(chunk);
		}
		else
		{
			System.out.println("Negative chunk x:" + xWorld + " y: " + yWorld);
		}
	}
	
	public void clearChunk()
	{
		this.chunk = null;
	}
	
	@Override
	public int compareTo(Entity otherEntity)
	{
		if(this.getChunk().getID() <= otherEntity.getChunk().getID())
		{
			return -1;
		}
		else if (this.getChunk().getID() > otherEntity.getChunk().getID())
		{
			return 1;
		}
		return 0;
	}
}
