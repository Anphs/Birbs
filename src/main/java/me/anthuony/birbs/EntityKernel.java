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
		float deltaXW;
		float deltaYW;
		float xF = entity.getXForce();
		float yF = entity.getYForce();
		float speed = entity.getSpeed();
		float direction = entity.getDirection();
		float avgHeading = entity.getDirection();
		float count = 1;
		float forceDirection;
		float newDirection;
		float scale = entity.getScale();
		
		if(!paused)
		{
			//Find New Position
			deltaXW = (float) (speed * Math.cos(direction) * deltaTime);
			deltaYW = (float) (speed * Math.sin(direction) * deltaTime);
			xW += deltaXW;
			yW += deltaYW;
			
			//Adjust for World Boundaries
			xW = (float) ((Math.abs((xW + worldWidth + birbWidth / 2.0) % worldWidth)) - birbWidth / 2.0);
			yW = (float) ((Math.abs((yW + worldHeight + birbHeight / 2.0) % worldHeight)) - birbHeight / 2.0);
			
			//Update Entity World Location
			entity.setXWorld(xW);
			entity.setYWorld(yW);
			
			//Reset Forces
			entity.resetForces();
			
			//Add momentum
			entity.addXForce(deltaXW * 10);
			entity.addYForce(deltaYW * 10);
		}

		//Calculate Screen Position
		float sX = (xW + cameraOffsetX) * cameraScale;
		float sY = (yW + cameraOffsetY) * cameraScale;

		//Update Entity Screen Location
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
			
//			float mouseDistance = (float) Point.distance(xW, yW, bc.getInput().getScaledMousePoint().getX(), bc.getInput().getScaledMousePoint().getY());
			int brighter = 0;
//			if(mouseDistance < 10000)
//			{
//				brighter = - (int) (120000 / (mouseDistance));
//			}
			
			b = ExtraMath.boundNumber(b + brighter, 0, 255);
			g = ExtraMath.boundNumber(g + brighter, 0, 255);
			r = ExtraMath.boundNumber(r + brighter, 0, 255);
			
			entity.setColor(new Color(r, g, b, 255));
		}
		
		//Calculate Chunk
		int currentChunk = Chunk.calculateChunk(xW, yW, chunkSize, chunkWidth);
		entity.setChunk(currentChunk);
		
		if(entity.getType() == 1)
		{
			Birb birb = (Birb) entity;
//			birb.applyMouseForce(true);
			for(Entity e: bc.getNearbyEntities(bc, entity.getChunk(), 1))
			{
				if(e != null && e != entity && entity.getDistance(e) < Birb.getInteractionRange())
				{
					entity.applyForceTo(e);
					avgHeading += entity.getDirection();
					count++;
				}
				else
				{
//					System.out.println("me");
				}
			}
		}
		
		//Do alignment
		if(bc.isDoAlignment())
		{
			avgHeading /= count;
			entity.addXForce((float) (1000 * Math.cos(avgHeading)));
			entity.addYForce((float) (1000 * Math.sin(avgHeading)));
		}
		
		if(xF != 0 || yF != 0)
		{
			//Find The Direction of The Net Force
			forceDirection = (float) Math.atan2(yF, xF);
			
			float adjustment = (forceDirection - direction);
			float change = (float) (adjustment % (2 * Math.PI));
			
			if (change > Math.PI && direction < Math.PI)
			{
				change -= 2 * Math.PI;
			}
			else if (change < -Math.PI && direction > Math.PI)
			{
				change += 2 * Math.PI;
			}
			
			if(Math.abs(change) > Birb.getMaxTurnSpeed())
			{
				if (change > 0)
				{
					change = Birb.getMaxTurnSpeed();
				} else
				{
					change = -Birb.getMaxTurnSpeed();
				}
			}
			
			newDirection = (float) ((direction + change) % (2 * Math.PI));
			
			//Set new direction
			entity.setDirection(newDirection);
		}
	}
}
