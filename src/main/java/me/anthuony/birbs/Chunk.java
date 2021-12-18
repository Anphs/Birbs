package me.anthuony.birbs;

import java.util.ArrayList;
import java.util.List;

public class Chunk
{
	private final int ID;
	private final List<Entity> entityList = new ArrayList<Entity>();
	
	public Chunk(int ID)
	{
		this.ID = ID;
	}
	
	public static int calculateChunk(float xW, float yW, int chunkSize, int chunkWidth)
	{
		int xC = (int) (xW / chunkSize);
		int yC = (int) (yW / chunkSize);
		int chunk = xC + yC * chunkWidth;
		if(chunk >= 0)
		{
			return chunk;
		}
		else
		{
			System.out.println("Chunk is negative");
			return -1;
		}
	}
	
	public static void assignChunk(Entity e, Chunk previousChunk, Chunk newChunk)
	{
		if(previousChunk == null)
		{
			newChunk.addEntity(e);
		}
		else if(previousChunk.getID() != newChunk.getID())
		{
			previousChunk.removeEntity(e);
			newChunk.addEntity(e);
		}
	}
	
	public int getID() { return this.ID; }
	
	public synchronized void addEntity(Entity e)
	{
		entityList.add(e);
	}
	
	public synchronized void removeEntity(Entity e)
	{
		entityList.remove(e);
	}
	
	public List<Entity> getEntityList()
	{
		return this.entityList;
	}
	
	public int getSize() { return entityList.size(); }
	
	public void clearChunk()
	{
		entityList.clear();
	}
	
	public String toString()
	{
		return "" + this.ID;
	}
}
