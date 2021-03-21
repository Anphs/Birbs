package me.anthuony.birbs;

import com.aparapi.Kernel;

import java.awt.*;

public class EntityKernel extends Kernel
{
	private final byte[] eType;
	private final float[] eXWorld;
	private final float[] eYWorld;
	private final float[] eXScreen;
	private final float[] eYScreen;
	private final float[] eSpeed;
	private final float[] eDirection;
	private final float[] eAngularAcceleration;
	private final float[] eScale;
	private final int[] eChunk;
	private final Color[] eColor;
	private final boolean[] eOnScreen;
	
	private final int[] chunkPos;
	private final int[] chunkEntityCount;
	
	private boolean paused;
	private int windowWidth, windowHeight, worldWidth, worldHeight, chunkSize, chunkWidth, chunkHeight;
	private float cameraOffsetX, cameraOffsetY, cameraScale, deltaTime;
	
	private int birbWidth, birbHeight;
	
	EntityKernel(BirbsContainer bc)
	{
		this.eType = bc.geteType();
		this.eXWorld = bc.geteXWorld();
		this.eYWorld = bc.geteYWorld();
		this.eXScreen = bc.geteXScreen();
		this.eYScreen = bc.geteYScreen();
		this.eSpeed = bc.geteSpeed();
		this.eDirection = bc.geteDirection();
		this.eAngularAcceleration = bc.geteAngularAcceleration();
		this.eScale = bc.geteScale();
		this.eChunk = bc.geteChunk();
		this.eColor = bc.geteColor();
		this.eOnScreen = bc.geteOnScreen();
		
		this.chunkPos = bc.getChunkPos();
		this.chunkEntityCount = bc.getChunkEntityCount();
		
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
		
		float xW = eXWorld[entityID];
		float yW = eYWorld[entityID];
		float speed = eSpeed[entityID];
		float direction = eDirection[entityID];
		float scale = eScale[entityID];

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
		eXWorld[entityID] = xW;
		eYWorld[entityID] = yW;

		eXScreen[entityID] = sX;
		eYScreen[entityID] = sY;
		
		//Find if Entity Should be Rendered
		eOnScreen[entityID] = sX > -birbWidth * cameraScale && sX < windowWidth + birbWidth * cameraScale && sY > -birbHeight * cameraScale && sY < windowHeight + birbHeight * cameraScale;
		
		//If on Screen
		if(eOnScreen[entityID])
		{
			//Update Birb Color
			int b = (int) Math.abs((xW / worldWidth) * 255);
			int g = (int) Math.abs((yW / worldHeight) * 255);
			int r = 255 - b;
			
			eColor[entityID] = new Color(r, g, b, 255);
		}
		
		//Calculate Chunk
		int previousChunk = eChunk[entityID];
		
		int xC = (int) (xW / chunkSize);
		int yC = (int) (yW / chunkSize);
		int currentChunk = xC + yC * chunkWidth;
		
		if(currentChunk < 0)
		{
			System.out.println("error chunk is negative?!");
		}
		
		eChunk[entityID] = currentChunk;
		
		//If There Is Previous Assigned Chunk
		if(previousChunk != -100)
		{
			if(previousChunk != currentChunk)
			{
				chunkEntityCount[previousChunk] -= 1;
				chunkEntityCount[currentChunk] += 1;
			}
		}
		else
		{
			chunkEntityCount[currentChunk] += 1;
		}
		
		int sum = 0;
		//Calculate Chunk Positions
		for(int i=0; i<chunkWidth * chunkHeight; i++)
		{
			chunkPos[i] = sum;
			sum += chunkEntityCount[i];
		}
	}
}
