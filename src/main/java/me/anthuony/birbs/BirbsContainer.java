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
	
	private final int maxEntityCount = 1000000;
	private final byte[] eType = new byte[maxEntityCount];
	private final float[] eXWorld = new float[maxEntityCount];
	private final float[] eYWorld = new float[maxEntityCount];
	private final float[] eXScreen = new float[maxEntityCount];
	private final float[] eYScreen = new float[maxEntityCount];
	private final float[] eXForce = new float[maxEntityCount];
	private final float[] eYForce = new float[maxEntityCount];
	private final float[] eSpeed = new float[maxEntityCount];
	private final float[] eDirection = new float[maxEntityCount];
	private final float[] eAngularAcceleration = new float[maxEntityCount];
	private final float[] eScale = new float[maxEntityCount];
	private final int[] eChunk = new int[maxEntityCount];
	private final Color[] eColor = new Color[maxEntityCount];
	private final boolean[] eOnScreen = new boolean[maxEntityCount];
	private int entityCount = 0;
	
	private final ArrayList<Birb> birbsList = new ArrayList<>();
	private final ArrayList<Birb> pursuitBirbHistoryList = new ArrayList<Birb>();
	
	private final double cameraPanningInterval = 5000 * UPDATE_CAP;
	private final double minScale = .1;
	private final double maxScale = 1.5;
	private int worldWidth = windowWidth * 10, worldHeight = windowHeight * 10;
	private double cameraOffsetX = (worldWidth - windowWidth * 10) / -2.0;
	private double cameraOffsetY = (worldHeight - windowHeight * 10) / -2.0;
	private double cameraTempOffsetX = 0;
	private double cameraTempOffsetY = 0;
	private double scale = .1;
	
	int capacity = getWindowWidth() * getWindowHeight() / 500;
	private double dpdt = 0;
	
	private boolean paused = false, drawHitbox = false, drawName = true, drawUI = true;
	private Birb pursuitBirb;
	private int pursuitBirbHistoryIndex = 0;
	
	private final int chunkSize = (int) Birb.getInteractionRange() * 9;
	private final int chunkWidth, chunkHeight;
	private final int[] chunkPos;
	private final int[] chunkEntityCount;
	
	private final boolean avoidOthers = true;
	private final boolean doAlignment = true;
	private final boolean doCohesion = true;
	private final boolean deleteClose = false;
	
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
		chunkPos = new int[chunkWidth * chunkHeight];
		chunkEntityCount = new int[chunkPos.length];
		Arrays.fill(eChunk, -100);
	}
	
	public void start()
	{
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		kernel = new EntityKernel(this);
		
		kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
		
		Thread thread = new Thread(this);
		thread.start();
		
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
					System.out.println("FPS: " + fps);
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
	
	public ArrayList<Birb> getBirbsList()
	{
		return birbsList;
	}
	
	public void setEntityCount(int entityCount)
	{
		this.entityCount = entityCount;
	}
	
	public int incrementEntityCount()
	{
		this.entityCount++;
		return this.entityCount - 1;
	}
	
	public int getFrames()
	{
		return frames;
	}
	
	public void removeBirb(Birb birb)
	{
		birbsList.remove(birb);
	}
	
	public void removeAllBirbs()
	{
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
		Birb randomBirb = birbsList.get((int) (Math.random() * birbsList.size()));
		while(exclusionList.contains(randomBirb))
		{
			randomBirb = birbsList.get((int) (Math.random() * birbsList.size()));
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
	
	public byte[] geteType()
	{
		return eType;
	}
	
	public float[] geteXWorld()
	{
		return eXWorld;
	}
	
	public float[] geteYWorld()
	{
		return eYWorld;
	}
	
	public float[] geteXScreen()
	{
		return eXScreen;
	}
	
	public float[] geteYScreen()
	{
		return eYScreen;
	}
	
	public float[] geteXForce()
	{
		return eXForce;
	}
	
	public float[] geteYForce()
	{
		return eYForce;
	}
	
	public float[] geteSpeed()
	{
		return eSpeed;
	}
	
	public float[] geteDirection()
	{
		return eDirection;
	}
	
	public float[] geteAngularAcceleration()
	{
		return eAngularAcceleration;
	}
	
	public boolean[] geteOnScreen()
	{
		return eOnScreen;
	}
	
	public float[] geteScale()
	{
		return eScale;
	}
	
	public int[] geteChunk() { return eChunk; }
	
	public Color[] geteColor() { return eColor; }
	
	public byte geteType(int entityID)
	{
		return eType[entityID];
	}
	
	public float geteXWorld(int entityID)
	{
		return eXWorld[entityID];
	}
	
	public float geteYWorld(int entityID)
	{
		return eYWorld[entityID];
	}
	
	public float geteXScreen(int entityID)
	{
		return eXScreen[entityID];
	}
	
	public float geteYScreen(int entityID)
	{
		return eYScreen[entityID];
	}
	
	public float geteXForce(int entityID)
	{
		return eXForce[entityID];
	}
	
	public float geteYForce(int entityID)
	{
		return eYForce[entityID];
	}
	
	public float geteSpeed(int entityID)
	{
		return eSpeed[entityID];
	}
	
	public float geteDirection(int entityID)
	{
		return eDirection[entityID];
	}
	
	public float geteAngularAcceleration(int entityID)
	{
		return eAngularAcceleration[entityID];
	}
	
	public boolean geteOnScreen(int entityID)
	{
		return eOnScreen[entityID];
	}
	
	public float geteScale(int entityID)
	{
		return eScale[entityID];
	}
	
	public int geteChunk(int entityID) { return eChunk[entityID]; }
	
	public Color geteColor(int entityID) { return eColor[entityID]; }
	
	public void seteType(int entityID, byte newEntityID)
	{
		eType[entityID] = newEntityID;
	}
	
	public void seteXWorld(int entityID, float xWorld)
	{
		eXWorld[entityID] = xWorld;
	}
	
	public void seteYWorld(int entityID, float yWorld)
	{
		eYWorld[entityID] = yWorld;
	}
	
	public void seteXScreen(int entityID, float xScreen)
	{
		eXScreen[entityID] = xScreen;
	}
	
	public void seteYScreen(int entityID, float yScreen)
	{
		eYScreen[entityID] = yScreen;
	}
	
	public void seteXForce(int entityID, float xForce)
	{
		eXScreen[entityID] = xForce;
	}
	
	public void seteYForce(int entityID, float yForce)
	{
		eYScreen[entityID] = yForce;
	}
	
	public void seteSpeed(int entityID, float speed)
	{
		eSpeed[entityID] = speed;
	}
	
	public void seteDirection(int entityID, float direction)
	{
		eDirection[entityID] = direction;
	}
	
	public void seteAngularAcceleration(int entityID, float angularAcceleration)
	{
		eAngularAcceleration[entityID] = angularAcceleration;
	}
	
	public void seteOnScreen(int entityID, boolean onScreen)
	{
		eOnScreen[entityID] = onScreen;
	}
	
	public void seteScale(int entityID, float scale)
	{
		eScale[entityID] = scale;
	}
	
	public void seteChunk(int entityID, int chunk) { eChunk[entityID] = chunk; }
	
	public void seteColor(int entityID, Color color) { eColor[entityID] = color; }
	
	public int getEntityCount()
	{
		return this.entityCount;
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
	
	public int[] getChunkPos()
	{
		return chunkPos;
	}
	
	public int getChunkPos(int chunkID)
	{
		return chunkPos[chunkID];
	}
	
	public void setChunkPos(int chunkID, int start)
	{
		chunkPos[chunkID] = start;
	}
	
	public int[] getChunkEntityCount()
	{
		return chunkEntityCount;
	}
	
	public int getChunkEntityCount(int chunkID)
	{
		return chunkEntityCount[chunkID];
	}
	
	public void setChunkEntityCount(int chunkID, int count)
	{
		chunkEntityCount[chunkID] = count;
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
}
