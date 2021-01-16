package me.anthuony.birbs;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class BirbsContainer implements Runnable
{
	
	private final AbstractBirbsManager world;
	private final double UPDATE_CAP = 1.0 / 60.0;
	private final double cameraPanningInterval = 100;
	private final double minScale = .1;
	private final double maxScale = 1.5;
	private final ArrayList<Birb> birbsList = new ArrayList<>();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private final int windowWidth = screenSize.width;
	private final int windowHeight = screenSize.height;
	double frameTime = 0;
	int frames = 0;
	int fps = 0;
	ArrayList<String> changelog = new ArrayList<>();
	ArrayList<String> keybindsHint = new ArrayList<>();
	ArrayList<String> names = new ArrayList<>();
	private Window window;
	private Renderer renderer;
	private Input input;
	//	private int windowWidth = 1920, windowHeight = 1080;
	private int worldWidth = 19200, worldHeight = 10800;
	private double cameraOffsetX = (worldWidth - windowWidth * 10) / -2.0;
	private double cameraOffsetY = (worldHeight - windowHeight * 10) / -2.0;
	private double cameraTempOffsetX = 0;
	private double cameraTempOffsetY = 0;
	private double scale = .1;
	private String title = "Birbs";
	private int birbTotalSpawned;
	private boolean paused = false, drawHitbox = false, drawName = true, drawUI = true;
	private Birb pursuitBirb;
	
	public BirbsContainer(AbstractBirbsManager world)
	{
		this.world = world;
	}
	
	public void start()
	{
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		
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
		boolean running = true;
		
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
				world.update(this, (float) UPDATE_CAP);
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
				world.render(this, renderer);
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
	}
	
	public void dispose()
	{
	
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
		Point2D.Double zoomPoint = input.getScaledMousePoint();
		
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
	
	public Window getWindow()
	{
		return window;
	}
	
	public Input getInput()
	{
		return input;
	}
	
	public ArrayList<Birb> getBirbsList()
	{
		return birbsList;
	}
	
	public int getBirbTotalSpawned()
	{
		return birbTotalSpawned;
	}
	
	public void setBirbTotalSpawned(int birbTotalSpawned)
	{
		this.birbTotalSpawned = birbTotalSpawned;
	}
	
	public int incrementBirbTotalSpawned()
	{
		birbTotalSpawned++;
		return birbTotalSpawned - 1;
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
	
	public void toggleHitboxes() { drawHitbox = !drawHitbox; }
	
	public void togglePause() { paused = !paused; }
	
	public void toggleNames() { drawName = !drawName; }
	
	public void toggleUI() { drawUI = !drawUI; }
	
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
		return names.get((int)(Math.random() * names.size()));
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
	}
	
	public boolean isDrawUI()
	{
		return drawUI;
	}
}
