package me.anthuony.birbs;

import com.aparapi.Kernel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class EntityKernel extends Kernel
{
	private final BirbsContainer bc;
	private final ArrayList<Entity> entityList;
	private ArrayList<Chunk> chunkList;
	
	private boolean paused;
	private int windowWidth, windowHeight, worldWidth, worldHeight, chunkSize, chunkWidth, chunkHeight;
	private float cameraOffsetX, cameraOffsetY, cameraScale, deltaTime;
	
	private int birbWidth, birbHeight;
	
	EntityKernel(BirbsContainer bc)
	{
		this.bc = bc;
		this.entityList = bc.getEntityList();
		
		updateVars(bc);
	}
	
	public void updateVars (BirbsContainer bc)
	{
		this.windowWidth = bc.getWindowWidth();
		this.windowHeight = bc.getWindowHeight();
		this.worldWidth = bc.getWorldWidth();
		this.worldHeight = bc.getWorldHeight();
		this.cameraOffsetX = (float) bc.getCameraOffsetX();
		this.cameraOffsetY = (float) bc.getCameraOffsetY();
		this.cameraScale = (float) bc.getScale();
		this.deltaTime = (float) bc.getUPDATE_CAP();
		this.paused = bc.isPaused();
		this.birbWidth = Birb.getBaseWidth();
		this.birbHeight = Birb.getBaseHeight();
		this.chunkSize = bc.getChunkSize();
		this.chunkWidth = bc.getChunkWidth();
		this.chunkHeight = bc.getChunkHeight();
	}
	
	@Override
	public void run()
	{
		int entityID = getGlobalId();
		Entity entity = entityList.get(entityID);
		
		float xW = entity.getXWorld();
		float yW = entity.getYWorld();
		float speed = entity.getSpeed();
		float direction = entity.getDirection();
		float scale = entity.getScale();

		//Find New Position
		if(!paused)
		{
			xW = (float) (xW + speed * Math.cos(direction) * deltaTime);
			yW = (float) (yW + speed * Math.sin(direction) * deltaTime);
		}
		
		//Adjust for World Boundaries
		xW = (float) ((Math.abs((xW + worldWidth + birbWidth / 2.0) % worldWidth)) - birbWidth / 2.0);
		yW = (float) ((Math.abs((yW + worldHeight + birbHeight / 2.0) % worldHeight)) - birbHeight / 2.0);

		//Calculate Screen Position
		float sX = (xW + cameraOffsetX) * cameraScale;
		float sY = (yW + cameraOffsetY) * cameraScale;

		//Update Entity Location
		entity.setXWorld(xW);
		entity.setYWorld(yW);

		entity.setXScreen(sX);
		entity.setYScreen(sY);
		
		//Find if Entity Should be Rendered
		entity.setOnScreen(sX > -birbWidth * cameraScale && sX < windowWidth + birbWidth * cameraScale && sY > -birbHeight * cameraScale && sY < windowHeight + birbHeight * cameraScale);
		
		//If on Screen
		if(entity.isOnScreen())
		{
			//Update Birb Color
			int b = (int) Math.abs((xW / worldWidth) * 255);
			int g = (int) Math.abs((yW / worldHeight) * 255);
			int r = 255 - b;
			
			entity.setColor(new Color(r, g, b, 255));
		}
		
		//Calculate Chunk
//		int previousChunk = entity.getChunk().getID();
		
//		int xC = (int) (xW / chunkSize);
//		int yC = (int) (yW / chunkSize);
//		int currentChunk = xC + yC * chunkWidth;
		int currentChunk = Chunk.calculateChunk(xW, yW, chunkSize, chunkWidth);
//		int currentChunk = 1;
		
		if(currentChunk < 0)
		{
			System.out.println("error chunk is negative?!");
		}
		
		entity.setChunk(currentChunk);
		
//		//If There Is Previous Assigned Chunk
//		if(previousChunk != -1)
//		{
//			if(previousChunk != currentChunk)
//			{
//				chunkEntityCount[previousChunk] -= 1;
//				chunkEntityCount[currentChunk] += 1;
//			}
//		}
//		else
//		{
//			chunkEntityCount[currentChunk] += 1;
//		}
//
//		int sum = 0;
//		//Calculate Chunk Positions
//		for(int i=0; i<chunkWidth * chunkHeight; i++)
//		{
//			chunkPos[i] = sum;
//			sum += chunkEntityCount[i];
//		}
	}
}
