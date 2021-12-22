package me.anthuony.birbs;

import com.aparapi.Kernel;


import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

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

	private final List<String> keybindsHint = new LinkedList<>();
	private final List<String> names = new LinkedList<>();
	
//	private int entityCount = 0;
	
	private final ArrayList<Entity> entityList = new ArrayList<>();
	private final ArrayList<Birb> birbsList = new ArrayList<>();
	
	private final double cameraPanningInterval = 5000 * UPDATE_CAP;
	private final double minScale = .1;
	private final double maxScale = 1.5;
	private final int worldWidth = windowWidth * 10, worldHeight = windowHeight * 10;
	private double cameraOffsetX = (worldWidth - windowWidth * 10) / -2.0;
	private double cameraOffsetY = (worldHeight - windowHeight * 10) / -2.0;
	private double cameraTempOffsetX = 0;
	private double cameraTempOffsetY = 0;
	private double scale = .1;

	private final ArrayList<Chunk> chunkList = new ArrayList<>();
	private final int chunkSize = (int) Birb.getInteractionRange() * 9;
	private final int chunkWidth = (worldWidth % chunkSize == 0) ? worldWidth/chunkSize : worldWidth/chunkSize + 1;
	private final int chunkHeight = (worldHeight % chunkSize == 0) ? worldHeight/chunkSize : worldHeight/chunkSize + 1;
	
	private boolean paused = false, drawHitbox = false, drawName = true, drawUI = true;

	private final List<Entity> pursuitList = new LinkedList<>();
	private int pursuitIndex = -1;
	private Entity pursuitEntity;
	
	private boolean avoidOthers = true;
	private boolean doAlignment = true;
	private boolean doCohesion = true;
	
	private final Color worldBackgroundColor = new Color(0, 0,0, 255);
	private final Color windowBackgroundColor = new Color(50, 50, 50, 255);
	private final Color textUIColor = new Color(0, 128, 128, 255);
	
	public BirbsContainer(AbstractBirbsManager world)
	{
		this.world = world;

		int numChunks = chunkWidth * chunkHeight;
		for(int i=0; i<numChunks; i++)
		{
			chunkList.add(new Chunk(i));
		}
	}

	private void fileToStringList(String fileName, List<String> list)
	{
		ClassLoader classLoader = getClass().getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(fileName))
		{
			assert inputStream != null;
			Scanner scan = new Scanner(inputStream);
			while (scan.hasNext())
			{
				list.add(scan.nextLine());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		kernel = new EntityKernel(this);
		
		kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);

		String keybindsFile = "Keybinds.txt";
		String namesFile = "Names.txt";
		ClassLoader classLoader = getClass().getClassLoader();

		fileToStringList(keybindsFile, keybindsHint);
		fileToStringList(namesFile, names);
		
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
	
	public int getWorldHeight()
	{
		return worldHeight;
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
	
	public ArrayList<Entity> getEntityList()
	{
		return entityList;
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
	
	public List<String> getKeybindsHint()
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
	
	public String getRandomName()
	{
		return names.get((int) (Math.random() * names.size()));
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public Entity getPursuitEntity()
	{
		return pursuitEntity;
	}
	
	public void setPursuitEntity(Entity pursuitEntity)
	{
		this.pursuitEntity = pursuitEntity;
		if(!pursuitList.contains(pursuitEntity))
		{
			pursuitList.add(pursuitEntity);
			pursuitIndex = pursuitList.size() - 1;
		}
	}

	public boolean hasNextPursuit()
	{
		return pursuitIndex < pursuitList.size() - 1;
	}

	public boolean hasPreviousPursuit()
	{
		return pursuitIndex > 0;
	}

	public Entity nextPursuit()
	{
		return pursuitList.get(++pursuitIndex);
	}

	public Entity previousPursuit()
	{
		return pursuitList.get(--pursuitIndex);
	}
	
	public Entity getRandomUniqueEntity(ArrayList<Birb> birbsList, List<Entity> exclusionList)
	{
		Entity randomEntity = birbsList.get((int) (Math.random() * birbsList.size()));
		while(exclusionList.contains(randomEntity) && randomEntity.getType() == 1)
		{
			randomEntity = (Birb) birbsList.get((int) (Math.random() * birbsList.size()));
		}
		return randomEntity;
	}
	
	public boolean isDrawUI()
	{
		return drawUI;
	}
	
	public List<Entity> getPursuitList()
	{
		return pursuitList;
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
	
	public void toggleAlignment()
	{
		doAlignment = !doAlignment;
	}
	public void toggleCohesion()
	{
		doCohesion = !doCohesion;
	}
	public void toggleSeparation()
	{
		avoidOthers = !avoidOthers;
	}
}
