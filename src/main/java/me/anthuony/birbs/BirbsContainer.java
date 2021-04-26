package me.anthuony.birbs;

import com.aparapi.Kernel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BirbsContainer implements Runnable
{
	
	private final AbstractBirbsManager world;
	private Window window;
	private Renderer renderer;
	private Input input;
	private EntityKernel kernel;
	
	private String title = "Birbs";
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice mainScreen = env.getScreenDevices()[0];
	
	private final int windowWidth = mainScreen.getDisplayMode().getWidth();
	private final int windowHeight = mainScreen.getDisplayMode().getHeight();
	private boolean running = true;
	
	private final double UPDATE_CAP = 1.0 / mainScreen.getDisplayMode().getRefreshRate();
	private double frameTime = 0;
	private int frames = 0;
	private int fps = 0;
	private long kernelTime = 0, renderTime = 0;
	
	private final ArrayList<String> changelog = new ArrayList<>();
	private final ArrayList<String> keybindsHint = new ArrayList<>();
	private final ArrayList<String> names = new ArrayList<>();
	
//	private int entityCount = 0;
	
	private final ArrayList<Entity> entityList = new ArrayList<Entity>();
	private final ArrayList<Birb> birbsList = new ArrayList<Birb>();
	private final ArrayList<Birb> pursuitBirbHistoryList = new ArrayList<Birb>();
	
	private final ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
	private final int chunkSize = (int) Birb.getInteractionRange() * 9;
	private final int chunkWidth, chunkHeight;
	
	private final double cameraPanningInterval = 5000 * UPDATE_CAP;
	private final double minScale = .1;
	private final double maxScale = 1.5;
	private int worldWidth = windowWidth * 10, worldHeight = windowHeight * 10;
//	private int worldWidth = chunkSize * 3, worldHeight = chunkSize * 3;
	private double cameraOffsetX = (worldWidth - windowWidth * 10) / -2.0;
	private double cameraOffsetY = (worldHeight - windowHeight * 10) / -2.0;
	private double cameraTempOffsetX = 0;
	private double cameraTempOffsetY = 0;
	private double scale = .1;
	
	int capacity = /*getWindowWidth() * getWindowHeight() / 1000;*/ 10;
	private double dpdt = 0;
	
	private boolean paused = false, drawHitbox = false, drawName = false, drawUI = true;
	private Birb pursuitBirb;
	private int pursuitBirbHistoryIndex = 0;
	
	private final boolean avoidOthers = true;
	private final boolean doAlignment = true;
	private final boolean doCohesion = true;
	private final boolean deleteClose = false;
	
	private final Color worldBackgroundColor = new Color(0, 0,0, 255);
	private final Color windowBackgroundColor = new Color(50, 50, 50, 255);
	private final Color textUIColor = new Color(255, 105, 3, 255);
	
	public BirbsContainer(AbstractBirbsManager world)
	{
		this.world = world;
		
		if(worldWidth % chunkSize == 0)
		{
			chunkWidth = worldWidth/chunkSize;
		}
		else
		{
			chunkWidth = worldWidth/chunkSize + 1;
		}
		if(worldHeight % chunkSize == 0)
		{
			chunkHeight = worldHeight/chunkSize;
		}
		else
		{
			chunkHeight = worldHeight/chunkSize + 1;
		}
		int numChunks = chunkWidth * chunkHeight;
		for(int i=0; i<numChunks; i++)
		{
			chunkList.add(new Chunk(i));
		}
//		Arrays.fill(eChunk, -100);
	}
	
	public void start()
	{
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		kernel = new EntityKernel(this);
		
		kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
		
		String changelogFile = "Changelog.txt";
		String keybindsFile = "Keybinds.txt";
		String namesFile = "Names.txt";
		ClassLoader classLoader = getClass().getClassLoader();
		
		try (InputStream inputStream = classLoader.getResourceAsStream(changelogFile))
		{
			assert inputStream != null;
			Scanner scan = new Scanner(inputStream);
			while (scan.hasNext())
			{
				changelog.add(scan.nextLine());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try (InputStream inputStream = classLoader.getResourceAsStream(keybindsFile))
		{
			assert inputStream != null;
			Scanner scan = new Scanner(inputStream);
			while (scan.hasNext())
			{
				keybindsHint.add(scan.nextLine());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try (InputStream inputStream = classLoader.getResourceAsStream(namesFile))
		{
			assert inputStream != null;
			Scanner scan = new Scanner(inputStream);
			while (scan.hasNext())
			{
				names.add(scan.nextLine());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop()
	{
	
	}
	
	public void run()
	{
		boolean render = false;
		double startTime = 0;
		double endTime = System.nanoTime() / 1.0e9;
		double processedTime = 0;
		double unProcessedTime = 0;
		
		while (running)
		{
			render = false;
			
			startTime = System.nanoTime() / 1.0e9;
			processedTime = startTime - endTime;
			endTime = startTime;
			
			unProcessedTime += processedTime;
			frameTime += processedTime;
			
			while (unProcessedTime >= UPDATE_CAP)
			{
				unProcessedTime -= UPDATE_CAP;
				render = true;
				world.update(this);
				input.update();
				
				if (frameTime >= 1.0)
				{
					frameTime = 0;
					fps = frames;
					frames = 0;
//					System.out.println("FPS: " + fps);
				}
			}
			
			if (render)
			{
//				renderer.clear();
				long t1 = System.currentTimeMillis();
				
				world.render(this, renderer);
				
				long t2 = System.currentTimeMillis();
				renderTime = t2 - t1;
				
				window.update();
				frames++;
			} else
			{
				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		dispose();
		System.exit(0);
	}
	
	public void dispose()
	{
		kernel.dispose();
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public int getWorldWidth()
	{
		return worldWidth;
	}
	
	public void setWorldWidth(int worldWidth)
	{
		this.worldWidth = worldWidth;
	}
	
	public int getWorldHeight()
	{
		return worldHeight;
	}
	
	public void setWorldHeight(int worldHeight)
	{
		this.worldHeight = worldHeight;
	}
	
	public double getScale()
	{
		return scale;
	}
	
	public void setScale(double scale)
	{
		Point2D.Float zoomPoint = input.getScaledMousePoint();
		
		scale = Math.round(scale * 10) / 10.0;
		//Zoom In
		if (getInput().getScroll() < 0 && scale >= minScale && scale <= maxScale)
		{
			double diffX = (zoomPoint.getX() + getCameraOffsetX()) * -.1 / scale;
			double diffY = (zoomPoint.getY() + getCameraOffsetY()) * -.1 / scale;
			setCameraOffsetX(getCameraOffsetX() + diffX);
			setCameraOffsetY(getCameraOffsetY() + diffY);
		}
		//Zoom out
		else if (getInput().getScroll() > 0 && scale >= minScale && scale <= maxScale)
		{
			double diffX = (zoomPoint.getX() + getCameraOffsetX()) * -.1 / scale;
			double diffY = (zoomPoint.getY() + getCameraOffsetY()) * -.1 / scale;
			setCameraOffsetX(getCameraOffsetX() - diffX);
			setCameraOffsetY(getCameraOffsetY() - diffY);
		}
		this.scale = Math.max(minScale, scale);
		this.scale = Math.min(maxScale, this.scale);
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public double getUPDATE_CAP()
	{
		return UPDATE_CAP;
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public Input getInput()
	{
		return input;
	}
	
	public EntityKernel getKernel()
	{
		return kernel;
	}
	
	public long getKernelTime()
	{
		return kernelTime;
	}
	
	public void setKernelTime(long kernelTime)
	{
		this.kernelTime = kernelTime;
	}
	
	public long getRenderTime()
	{
		return renderTime;
	}
	
	public void setRenderTime(long renderTime)
	{
		this.renderTime = renderTime;
	}
	
	public ArrayList<Entity> getEntityList()
	{
		return entityList;
	}
	
	public int getFrames()
	{
		return frames;
	}
	
	public void removeEntity(Entity entity)
	{
		entityList.remove(entity);
	}
	
	public void removeAllEntities()
	{
		entityList.clear();
		birbsList.clear();
	}
	
	public AbstractBirbsManager getWorld()
	{
		return world;
	}
	
	public double getCameraOffsetX()
	{
		return cameraOffsetX;
	}
	
	public void setCameraOffsetX(double x)
	{
		cameraOffsetX = x;
	}
	
	public double getCameraOffsetY()
	{
		return cameraOffsetY;
	}
	
	public void setCameraOffsetY(double y)
	{
		cameraOffsetY = y;
	}
	
	public void changeCameraOffsetX(double x)
	{
		cameraOffsetX += x;
	}
	
	public void changeCameraOffsetY(double y)
	{
		cameraOffsetY += y;
	}
	
	public double getCameraTempOffsetX()
	{
		return cameraTempOffsetX;
	}
	
	public void setCameraTempOffsetX(double cameraTempOffsetX)
	{
		this.cameraTempOffsetX = cameraTempOffsetX;
	}
	
	public double getCameraTempOffsetY()
	{
		return cameraTempOffsetY;
	}
	
	public void setCameraTempOffsetY(double cameraTempOffsetY)
	{
		this.cameraTempOffsetY = cameraTempOffsetY;
	}
	
	public int getWindowWidth()
	{
		return windowWidth;
	}
	
	public int getWindowHeight()
	{
		return windowHeight;
	}
	
	public double getCameraPanningInterval()
	{
		return cameraPanningInterval;
	}
	
	public ArrayList<String> getChangelog()
	{
		return changelog;
	}
	
	public ArrayList<String> getKeybindsHint()
	{
		return keybindsHint;
	}
	
	public int getFps()
	{
		return fps;
	}
	
	public void toggleHitboxes()
	{
		drawHitbox = !drawHitbox;
	}
	
	public void togglePause()
	{
		paused = !paused;
	}
	
	public void toggleNames()
	{
		drawName = !drawName;
	}
	
	public void toggleUI()
	{
		drawUI = !drawUI;
	}
	
	public boolean isHitboxVisible()
	{
		return drawHitbox;
	}
	
	public boolean isDrawName()
	{
		return drawName;
	}
	
	public void setDrawName(boolean drawName)
	{
		this.drawName = drawName;
	}
	
	public String getRandomName()
	{
		return names.get((int) (Math.random() * names.size()));
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public Birb getPursuitBirb()
	{
		return pursuitBirb;
	}
	
	public void setPursuitBirb(Birb pursuitBirb)
	{
		this.pursuitBirb = pursuitBirb;
		if(!pursuitBirbHistoryList.contains(pursuitBirb))
		{
			pursuitBirbHistoryList.add(pursuitBirb);
			pursuitBirbHistoryIndex = pursuitBirbHistoryList.size() - 1;
		}
	}
	
	public Birb getRandomBirb(ArrayList<Birb> birbsList)
	{
		return birbsList.get((int) (Math.random() * birbsList.size()));
	}
	
	public Birb getRandomUniqueBirb(ArrayList<Birb> birbsList, ArrayList<Birb> exclusionList)
	{
		Birb randomBirb = (Birb) birbsList.get((int) (Math.random() * birbsList.size()));
		while(exclusionList.contains(randomBirb) && randomBirb.getType() == 1)
		{
			randomBirb = (Birb) birbsList.get((int) (Math.random() * birbsList.size()));
		}
		return randomBirb;
	}
	
	public boolean isDrawUI()
	{
		return drawUI;
	}
	
	public ArrayList<Birb> getPursuitBirbHistoryList()
	{
		return pursuitBirbHistoryList;
	}
	
	public int incrementBirbHistoryIndex()
	{
		pursuitBirbHistoryIndex++;
		return pursuitBirbHistoryIndex;
	}
	
	public int decrementBirbHistoryIndex()
	{
		pursuitBirbHistoryIndex--;
		return pursuitBirbHistoryIndex;
	}
	
	public int getPursuitBirbHistoryIndex()
	{
		return pursuitBirbHistoryIndex;
	}
	
	public boolean isAvoidOthers()
	{
		return avoidOthers;
	}
	
	public boolean isDoAlignment()
	{
		return doAlignment;
	}
	
	public boolean isDoCohesion()
	{
		return doCohesion;
	}
	
	public boolean isDeleteClose()
	{
		return deleteClose;
	}
	
	
	public int getEntityCount()
	{
		return entityList.size();
	}
	
	public int getChunkSize()
	{
		return chunkSize;
	}
	
	public int getChunkWidth()
	{
		return chunkWidth;
	}
	
	public int getChunkHeight()
	{
		return chunkHeight;
	}
	
	public double getDpdt()
	{
		return dpdt;
	}
	
	public void setDpdt(double dpdt)
	{
		this.dpdt = dpdt;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public ArrayList<Birb> getBirbsList()
	{
		return birbsList;
	}
	
	public ArrayList<Chunk> getChunkList()
	{
		return chunkList;
	}
	
	public void clearChunks()
	{
		for(Chunk c: this.getChunkList())
		{
			c.clearChunk();
		}
	}
	
	public Color getWorldBackgroundColor()
	{
		return worldBackgroundColor;
	}
	
	public Color getTextUIColor()
	{
		return textUIColor;
	}
	
	public Color getWindowBackgroundColor()
	{
		return windowBackgroundColor;
	}
	
	public ArrayList<Entity> getNearbyEntities(BirbsContainer bc, Chunk c, int radius)
	{
		ArrayList<Chunk> chunkList = bc.getChunkList();
		int chunkWidth = bc.getChunkWidth();
		
		ArrayList<Entity> nearby = new ArrayList<>();
		
		int startPos = c.getID() - (radius * (chunkWidth + 1));
		for(int i = 0; i < radius * 2 + 1; i++)
		{
			for(int j = 0; j < radius * 2 + 1; j++)
			{
				int currentPos = startPos + i * chunkWidth + j;
				if(currentPos < 0)
				{
					currentPos += chunkList.size();
				}
				if(currentPos >= chunkList.size())
				{
					currentPos -= chunkList.size();
				}
				Chunk currentChunk = chunkList.get(currentPos);
//				System.out.println("c: " + c + " chunk: " + currentChunk);
				nearby.addAll(currentChunk.getEntityList());
//				System.out.println("c: " + currentChunk + nearby.toString());
			}
		}
		return nearby;
	}
}
